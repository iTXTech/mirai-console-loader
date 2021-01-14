package org.itxtech.mcl.component;

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
public interface Logger {
    int LOG_DEBUG = 0;
    int LOG_INFO = 1;
    int LOG_WARNING = 2;
    int LOG_ERROR = 3;

    void setLogLevel(int level);

    void log(String info, int level);

    void debug(String info);

    void info(String info);

    void warning(String warning);

    void error(String error);

    void logException(Throwable e);
}
