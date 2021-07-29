package org.itxtech.mcl.utils;

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
 * @author Karlatemp
 * @website https://github.com/iTXTech/mirai-console-loader
 *
 */
public enum AnsiColor {
    RESET("\u001b[0m"),

    WHITE("\u001b[30m"),
    RED("\u001b[31m"),
    EMERALD_GREEN("\u001b[32m"),
    GOLD("\u001b[33m"),
    BLUE("\u001b[34m"),
    PURPLE("\u001b[35m"),
    GREEN("\u001b[36m"),

    GRAY("\u001b[90m"),
    LIGHT_RED("\u001b[91m"),
    LIGHT_GREEN("\u001b[92m"),
    LIGHT_YELLOW("\u001b[93m"),
    LIGHT_BLUE("\u001b[94m"),
    LIGHT_PURPLE("\u001b[95m"),
    LIGHT_CYAN("\u001b[96m");
    private final String format;

    private AnsiColor(String format) {
        this.format = format;
    }

    public String toString() {
        return format;
    }
}
