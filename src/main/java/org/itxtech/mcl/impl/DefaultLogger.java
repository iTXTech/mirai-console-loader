package org.itxtech.mcl.impl;

import org.itxtech.mcl.component.Logger;

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
    protected int logLevel = LOG_DEBUG;

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public void log(String info, int level) {
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
    public void info(String info) {
        log(info, LOG_INFO);
    }

    @Override
    public void debug(String info) {
        log(info, LOG_DEBUG);
    }

    @Override
    public void warning(String info) {
        log(info, LOG_WARNING);
    }

    @Override
    public void error(String info) {
        log(info, LOG_ERROR);
    }

    @Override
    public void logException(Throwable e) {
        error(getExceptionMessage(e));
    }

    @Override
    public void print(String s) {
        System.out.print(s);
    }

    @Override
    public void println(String s) {
        System.out.println(s);
    }

    public static String getExceptionMessage(Throwable e) {
        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
