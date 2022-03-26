package org.itxtech.mcl.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
 * @author Karlatemp
 * @website https://github.com/iTXTech/mirai-console-loader
 *
 */
public class AnsiMsg {
    public static AnsiMsg newMsg() {
        return new AnsiMsg();
    }

    private final Collection<Object> components;

    public AnsiMsg() {
        this(new ArrayList<>());
    }

    public AnsiMsg(Object... args) {
        this(new ArrayList<>(Arrays.asList(args)));
    }

    public AnsiMsg(Collection<Object> components) {
        this.components = components;
    }

    public String toString() {
        return renderNoAnsi();
    }

    public String renderWithAnsi() {
        StringBuilder sb = new StringBuilder();
        for (Object comp : components) {
            sb.append(comp);
        }
        return sb.toString();
    }

    public String renderNoAnsi() {
        StringBuilder sb = new StringBuilder();
        for (Object comp : components) {
            if (!(comp instanceof AnsiColor)) {
                sb.append(comp);
            }
        }
        return sb.toString();
    }

    public AnsiMsg append(Object msg) {
        components.add(msg);
        return this;
    }

    public AnsiMsg a(Object msg) {
        return append(msg);
    }

    public static String renderWithAnsi(Object msg) {
        if (msg instanceof AnsiMsg)
            return ((AnsiMsg) msg).renderWithAnsi();
        return String.valueOf(msg);
    }

    public static String renderNoAnsi(Object msg) {
        return String.valueOf(msg); // AnsiMsg.toString = renderNoAnsi
    }

    public AnsiMsg reset() {
        return append(AnsiColor.RESET);
    }

    public AnsiMsg white() {
        return append(AnsiColor.WHITE);
    }

    public AnsiMsg red() {
        return append(AnsiColor.RED);
    }

    public AnsiMsg emeraldGreen() {
        return append(AnsiColor.EMERALD_GREEN);
    }

    public AnsiMsg gold() {
        return append(AnsiColor.GOLD);
    }

    public AnsiMsg blue() {
        return append(AnsiColor.BLUE);
    }

    public AnsiMsg purple() {
        return append(AnsiColor.PURPLE);
    }

    public AnsiMsg green() {
        return append(AnsiColor.GREEN);
    }

    public AnsiMsg gray() {
        return append(AnsiColor.GRAY);
    }

    public AnsiMsg lightRed() {
        return append(AnsiColor.LIGHT_RED);
    }

    public AnsiMsg lightGreen() {
        return append(AnsiColor.LIGHT_GREEN);
    }

    public AnsiMsg lightYellow() {
        return append(AnsiColor.LIGHT_YELLOW);
    }

    public AnsiMsg lightBlue() {
        return append(AnsiColor.LIGHT_BLUE);
    }

    public AnsiMsg lightPurple() {
        return append(AnsiColor.LIGHT_PURPLE);
    }

    public AnsiMsg lightCyan() {
        return append(AnsiColor.LIGHT_CYAN);
    }
}
