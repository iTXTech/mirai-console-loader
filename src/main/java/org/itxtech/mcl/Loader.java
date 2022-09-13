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
import org.itxtech.mcl.pkg.PackageManager;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    public PackageManager packageManager;
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
            if (!Boolean.getBoolean("mcl.disable-ansi")) {
                if (!Boolean.getBoolean("mcl.no-ansi-console-init")) {
                    try {
                        AnsiConsole.systemInstall();
                    } catch (Exception ansiException) {
                        loader.logger.error("Fail to initialize JAnsi, set env mcl.no-ansi-console-init to true to disable the initialization.");
                        loader.logger.logException(ansiException);
                    }
                }
                Ansi.setEnabled(true);
            } else {
                Ansi.setEnabled(false);
            }
            loader.loadConfig();
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
                var stringWriter = new StringWriter();
                var printWriter = new PrintWriter(stringWriter);
                var formatter = new HelpFormatter();
                formatter.printHelp(printWriter, formatter.getWidth(), "mcl", null,
                        options, formatter.getLeftPadding(), formatter.getDescPadding(),
                        null, false);
                printWriter.flush();
                logger.info(stringWriter.toString());
                exit(1);
            }
            cli = new CommandLine.Builder().build();
        }
    }

    public void loadConfig() {
        config = Config.load(configFile);
        for (int i = 0; i < loader.config.mavenRepo.length; i ++) {
            if(loader.config.mavenRepo[i].endsWith("/")){
                loader.config.mavenRepo[i] = loader.config.mavenRepo[i].substring(0,loader.config.mavenRepo[i].length - 1);
            }
        }
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

    public boolean saveConfig() {
        return tryCatching(() -> config.save(configFile));
    }

    private boolean tryCatching(UnsafeRunnable r) {
        try {
            r.run();
            return true;
        } catch (Throwable e) {
            logger.logException(e);
            return false;
        }
    }

    private interface UnsafeRunnable {
        void run() throws Exception;
    }

    /**
     * 启动 Mirai Console Loader，并加载脚本
     */
    public void start(String[] args) throws Exception {
        logger.info(Ansi.ansi().fgBrightCyan().a("iTXTech Mirai Console Loader")
                .reset().a(" version ").fgBrightYellow().a(getVersion()));
        logger.info("https://github.com/iTXTech/mirai-console-loader");
        logger.info(Ansi.ansi().a("This program is licensed under ").fgBrightMagenta().a("GNU AGPL v3"));

        var bootGroup = new OptionGroup();
        bootGroup.addOption(Option.builder("z").desc("Skip boot phase").longOpt("dry-run").build());
        bootGroup.addOption(Option.builder().desc("Execute boot phase only").longOpt("boot-only").build());
        options.addOptionGroup(bootGroup);

        packageManager = new PackageManager(this);
        repo = new Repository(this);
        manager = new ModuleManager(this);
        downloader = new DefaultDownloader(this);

        parseCli(args, false);
        tryCatching(() -> manager.loadAllModules()); //此阶段脚本只能修改loader中变量
        parseCli(args, true);

        if (!cli.hasOption("boot-only")) {
            tryCatching(() -> manager.phaseCli()); //此阶段脚本处理命令行参数
            tryCatching(() -> manager.phaseLoad()); //此阶段脚本下载包
            saveConfig();
        }

        boot = true;
        if (!cli.hasOption("z")) {
            tryCatching(() -> manager.phaseBoot()); //此阶段脚本启动mirai，且应该只有一个脚本实现
        }
    }
}
