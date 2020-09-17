package org.itxtech.mcl;

import org.itxtech.mcl.component.*;
import org.itxtech.mcl.script.ScriptManager;

import java.io.File;

/*
 *
 * Mirai Console Loader
 *
 * Copyright (C) 2020 iTX Technologies
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
public class Loader {
    public Downloader downloader = new DefaultDownloader();
    public Logger logger = new DefaultLogger();
    public File configFile = new File("config.json");
    public Config config;
    public ScriptManager manager;

    public static void main(String[] args) {
        new Loader().start();
    }

    /**
     * 启动 Mirai Console Loader，并加载脚本
     */
    public void start() {
        try {
            config = Config.load(configFile);
            manager = new ScriptManager(this, new File("scripts"));
            manager.loadAllScripts();
            //TODO
            config.save(configFile);
        } catch (Throwable e) {
            logger.logException(e);
        }
    }

    /**
     * 支持启动 Mirai Console
     */
    public void boot() {

    }

}
