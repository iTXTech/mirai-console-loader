package org.itxtech.mcl;

import org.apache.commons.cli.*;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.itxtech.mcl.component.Config;
import org.itxtech.mcl.component.Downloader;
import org.itxtech.mcl.component.Logger;
import org.itxtech.mcl.component.Repository;
import org.itxtech.mcl.impl.DefaultDownloader;
import org.itxtech.mcl.impl.DefaultLogger;
import org.itxtech.mcl.module.ModuleManager;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.jar.Manifest;

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
public class Loader {
    private static Loader instance;

    public static Loader getInstance() {
        return instance;
    }

    public Downloader downloader;
    public Logger logger = new DefaultLogger();
    public File configFile = new File("config.json");
    public Config config;
    public ModuleManager manager;
    public Repository repo;
    public Options options = new Options();
    public CommandLine cli;

    public boolean boot = false;

    public Loader() {
        instance = this;
    }

    public static void main(String[] args) {
        var loader = new Loader();
        try {
            loader.loadConfig();
            if (!"true".equals(System.getProperties().getProperty("mcl.disable-ansi"))) {
                loader.detectLogger();
            } else {
                Ansi.setEnabled(false);
            }
            loader.start(args);
        } catch (Exception e) {
            loader.logger.logException(e);
        }
    }

    public void exit(int code) {
        if (!boot) {
            System.exit(code);
        }
    }

    public void parseCli(String[] args, boolean help) {
        try {
            cli = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            if (help) {
                logger.error(e.getMessage());
                new HelpFormatter().printHelp("mcl", options);
                exit(1);
            }
            try {
                cli = new DefaultParser().parse(new Options(), new String[0]);
            } catch (ParseException ignored) {
            }
        }
    }

    public void detectLogger() {
        if (!Boolean.getBoolean("mcl.no-anso-console-init")) {
            AnsiConsole.systemInstall();
        }
        Ansi.setEnabled(true);
    }

    public void loadConfig() {
        config = Config.load(configFile);
        logger.setLogLevel(config.logLevel);
    }

    public InetSocketAddress getProxy() {
        var p = config.proxy.split(":");
        try {
            return new InetSocketAddress(p[0], Integer.parseInt(p[1]));
        } catch (Exception e) {
            if (!"".equals(config.proxy)) {
                logger.error("Invalid proxy setting: " + config.proxy);
            }
        }
        return null;
    }

    public String getVersion() throws Exception {
        var version = "unknown";
        var mf = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (mf.hasMoreElements()) {
            var manifest = new Manifest(mf.nextElement().openStream());
            if ("org.itxtech.mcl.Loader".equals(manifest.getMainAttributes().getValue("Main-Class"))) {
                version = manifest.getMainAttributes().getValue("Version");
            }
        }
        return version;
    }

    public void saveConfig() throws Exception {
        config.save(configFile);
    }

    private void tryCatching(UnsafeRunnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            logger.logException(e);
        }
    }

    private interface UnsafeRunnable {
        void run() throws Exception;
    }

    /**
     * 启动 Mirai Console Loader，并加载脚本
     */
    public void start(String[] args) throws Exception {
        logger.info("iTXTech Mirai Console Loader version " + getVersion());
        logger.info("https://github.com/iTXTech/mirai-console-loader");
        logger.info("This program is licensed under GNU AGPL v3");

        options.addOption(Option.builder("z").desc("Skip boot phase").longOpt("dry-run").build());

        manager = new ModuleManager(this);
        parseCli(args, false);
        tryCatching(() -> manager.loadAllModules()); //此阶段脚本只能修改loader中变量
        parseCli(args, true);
        tryCatching(() -> manager.phaseCli()); //此阶段脚本处理命令行参数
        repo = new Repository(this);
        downloader = new DefaultDownloader(this);
        tryCatching(() -> manager.phaseLoad()); //此阶段脚本下载包
        saveConfig();
        boot = true;
        if (!cli.hasOption("z")) {
            tryCatching(() -> manager.phaseBoot()); //此阶段脚本启动mirai，且应该只有一个脚本实现
        }
    }
}
