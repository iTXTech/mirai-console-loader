package org.itxtech.mcl.module.builtin;

import org.apache.commons.cli.Option;
import org.itxtech.mcl.Utility;
import org.itxtech.mcl.module.MclModule;
import org.itxtech.mcl.pkg.MclPackage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
public class Boot extends MclModule {
    public static final HashMap<String, String> depMap = new HashMap<>() {{
        put("net.mamoe:mirai-core", "net.mamoe:mirai-core-all");
    }};

    @Override
    public String getName() {
        return "boot";
    }

    public String getBootEntry() {
        return loader.config.moduleProps.getOrDefault("boot.entry",
                "net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader");
    }

    public String getBootArgs() {
        return loader.config.moduleProps.getOrDefault("boot.args", "");
    }

    @Override
    public void prepare() {
        loader.options.addOption(Option.builder("b").desc("Show Mirai Console boot properties")
                .longOpt("show-boot-props").build());
        loader.options.addOption(Option.builder("f").desc("Set Mirai Console boot entry")
                .longOpt("set-boot-entry").hasArg().argName("EntryClass").build());
        loader.options.addOption(Option.builder("g").desc("Set Mirai Console boot arguments")
                .longOpt("set-boot-args").optionalArg(true).hasArg().argName("Arguments").build());
    }

    @Override
    public void cli() {
        if (loader.cli.hasOption("f")) {
            loader.config.moduleProps.put("boot.entry", loader.cli.getOptionValue("f"));
            loader.saveConfig();
        }
        if (loader.cli.hasOption("g")) {
            loader.config.moduleProps.put("boot.args", loader.cli.getOptionValue("g", ""));
            loader.saveConfig();
        }
        if (loader.cli.hasOption("b")) {
            loader.logger.info("Mirai Console boot entry: " + getBootEntry());
            loader.logger.info("Mirai Console boot arguments: " + getBootArgs());
            loader.exit(0);
        }
    }

    @Override
    public void boot() {
        try {
            var files = new ArrayList<File>();
            var pkgMap = new HashMap<String, String>();
            for (var pkg : loader.packageManager.getPackages()) {
                if (pkg.type.equals(MclPackage.TYPE_CORE)) {
                    if (pkg.id.equals("org.bouncycastle:bcprov-jdk15on")) {
                        files.add(0, pkg.getJarFile());
                    } else {
                        files.add(pkg.getJarFile());
                    }
                    pkgMap.put(pkg.id, pkg.version);
                }
                if (pkg.type.equals(MclPackage.TYPE_PLUGIN)) {
                    var metadata = pkg.getMetadataFile();
                    if (metadata.exists()) {
                        for (var s : loader.repo.getMetadataFromFile(metadata).dependencies) {
                            var dep = s.split(":");
                            var name = dep[0] + ":" + dep[1];
                            var version = dep[2];
                            var realPkg = depMap.getOrDefault(name, name);
                            for (var corePkg : pkgMap.entrySet()) {
                                if (corePkg.getKey().equals(realPkg) && !corePkg.getValue().equals(version)) {
                                    loader.logger.warning("Package \"" + pkg.id + "\" requires \"" + name + "\" version " + version + ". Current version is " + corePkg.getValue());
                                }
                            }
                        }
                    }
                }
            }

            Utility.bootMirai(files, getBootEntry(), getBootArgs());
        } catch (Exception e) {
            loader.logger.logException(e);
        }
    }
}
