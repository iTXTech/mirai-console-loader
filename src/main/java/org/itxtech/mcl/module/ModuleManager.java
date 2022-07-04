package org.itxtech.mcl.module;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.itxtech.mcl.Agent;
import org.itxtech.mcl.Loader;

import java.io.File;
import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.jar.JarFile;

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
public class ModuleManager {
    private final Loader loader;
    private final HashMap<String, MclModule> modules = new HashMap<>();

    public ModuleManager(Loader loader) {
        this.loader = loader;

        var group = new OptionGroup();
        group.addOption(Option.builder("l").longOpt("list-disabled-modules")
                .desc("List disabled modules").build());
        group.addOption(Option.builder("e").longOpt("enable-module")
                .desc("Enable module").hasArg().argName("ModuleName").build());
        group.addOption(Option.builder("d").longOpt("disable-module")
                .desc("Disable module").hasArg().argName("ModuleName").build());
        loader.options.addOptionGroup(group);
    }

    public MclModule getModule(String name) {
        return modules.get(name);
    }

    public void loadAllModules() throws Exception {
        if (loader.cli.hasOption("l")) {
            loader.logger.info("Disabled modules: " + String.join(", ", loader.config.disabledModules));
            return;
        }
        if (loader.cli.hasOption("d")) {
            var name = loader.cli.getOptionValue("d");
            if (!loader.config.disabledModules.contains(name)) {
                loader.config.disabledModules.add(name);
            }
            loader.logger.info("Module \"" + name + "\" has been disabled.");
            return;
        }
        if (loader.cli.hasOption("e")) {
            var name = loader.cli.getOptionValue("e");
            loader.config.disabledModules.remove(name);
            loader.logger.info("Module \"" + name + "\" has been enabled.");
            return;
        }

        var folder = new File("modules");
        folder.mkdir();

        var list = folder.listFiles(file -> file.getName().endsWith(".jar"));
        if (list != null) {
            for (var file : list) {
                var jar  = new JarFile(file);
                Agent.appendJarFile(jar);
            }
        }

        var serviceLoader = ServiceLoader.load(MclModule.class);

        serviceLoader.stream().forEach(provider -> {
            try {
                var module = provider.get();
                if (!loader.config.disabledModules.contains(module.getName())) {
                    loader.logger.debug("Loading module: \"" + module.getName() + "\". Class: " + module.getClass().getCanonicalName());
                    modules.put(module.getName(), module);

                    module.init(loader);
                    module.prepare();
                }
            } catch (Exception e) {
                loader.logger.logException(e);
            }
        });
        if (modules.size() == 0) {
            loader.logger.warning("No module has been loaded. Exiting.");
        }
    }

    public void phaseCli() {
        for (var module : modules.values()) {
            module.cli();
        }
    }

    public void phaseLoad() {
        for (var module : modules.values()) {
            module.load();
        }
    }

    public void phaseBoot() {
        for (var module : modules.values()) {
            module.boot();
        }
    }
}
