package org.itxtech.mcl.impl;

import org.itxtech.mcl.component.Logger;
import org.itxtech.mcl.utils.AnsiMsg;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeJavaObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 *
 * Mirai Console Loader
 *
 * Copyright (C) 2020-2021 iTX Technologies
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author PeratX
 * @website https://github.com/iTXTech/mirai-console-loader
 *
 */
public class DefaultLogger implements Logger {
    private static final Class<?> C$NativeError;

    static {
        try {
            C$NativeError = Class.forName("org.mozilla.javascript.NativeError");
        } catch (Throwable throwable) {
            throw new ExceptionInInitializerError(throwable);
        }
    }

    protected int logLevel = LOG_DEBUG;

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public void log(Object info, int level) {
        if (level < logLevel) {
            return;
        }
        var date = new SimpleDateFormat("HH:mm:ss").format(new Date());
        var prefix = "INFO";
        switch (level) {
            case LOG_DEBUG:
                prefix = "DEBUG";
                break;
            case LOG_INFO:
                prefix = "INFO";
                break;
            case LOG_WARNING:
                prefix = "WARNING";
                break;
            case LOG_ERROR:
                prefix = "ERROR";
                break;
        }
        var log = " " + date + " [" + prefix + "] " + info;
        if (level == LOG_ERROR) {
            System.err.println(log);
        } else {
            System.out.println(log);
        }
    }

    @Override
    public void info(Object info) {
        log(info, LOG_INFO);
    }

    @Override
    public void debug(Object info) {
        log(info, LOG_DEBUG);
    }

    @Override
    public void warning(Object info) {
        log(info, LOG_WARNING);
    }

    @Override
    public void error(Object info) {
        log(info, LOG_ERROR);
    }

    @Override
    public void logException(Object e) {
        Object oe = e;
        if (C$NativeError.isInstance(e)) {
            e = ((IdScriptableObject) e).get("javaException");
            if (e instanceof NativeJavaObject) {
                e = ((NativeJavaObject) e).unwrap();
            }
        }
        if (e == null) e = oe;
        if (e instanceof Throwable) {
            error(getExceptionMessage((Throwable) e));
        } else {
            error(String.valueOf(e));
        }
    }

    @Override
    public void print(Object s) {
        System.out.print(AnsiMsg.renderNoAnsi(s));
    }

    @Override
    public void println(Object s) {
        System.out.println(AnsiMsg.renderNoAnsi(s));
    }

    public static String getExceptionMessage(Throwable e) {
        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
