package org.itxtech.mcl.impl;

import org.fusesource.jansi.Ansi;
import org.itxtech.mcl.component.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 *
 * Mirai Console Loader
 *
 * Copyright (C) 2020-2022 iTX Technologies
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
        if (e == null) e = oe;
        if (e instanceof Throwable) {
            error(getExceptionMessage((Throwable) e));
        } else {
            error(String.valueOf(e));
        }
    }

    public static String getExceptionMessage(Throwable e) {
        var stringWriter = new StringWriter();
        var printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    @Override
    public void log(Object info, int level) {
        if (level < logLevel) {
            return;
        }
        var ansi = Ansi.ansi().a(" ");
        String prefix;
        var date = new SimpleDateFormat("HH:mm:ss").format(new Date());
        switch (level) {
            case LOG_DEBUG:
                ansi = ansi.fgBrightBlack();
                prefix = "DEBUG";
                break;
            case LOG_WARNING:
                ansi = ansi.fgBrightYellow();
                prefix = "WARN";
                break;
            case LOG_ERROR:
                ansi = ansi.fgBrightRed();
                prefix = "ERROR";
                break;
            case LOG_INFO:
            default:
                ansi = ansi.fgBrightGreen();
                prefix = "INFO";
                break;
        }
        ansi.a(" ").a(date).a(" [").a(prefix).a("] ");
        if (level == LOG_INFO) ansi.reset();
        ansi.a(info);
        ansi.reset();
        if (level == LOG_ERROR) {
            System.err.println(ansi);
        } else {
            System.out.println(ansi);
        }
    }

    @Override
    public void print(Object s) {
        System.out.print(s);
    }

    @Override
    public void println(Object s) {
        System.out.println(s);
    }
}
