package org.itxtech.mcl.script;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.itxtech.mcl.Loader;

import java.io.File;
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
public class ScriptManager {
    private final Loader loader;
    private final File baseDir;
    private final HashMap<String, Script> scripts = new HashMap<>();

    public ScriptManager(Loader loader, File baseDir) {
        this.loader = loader;
        this.baseDir = baseDir;
        this.baseDir.mkdirs();

        var group = new OptionGroup();
        group.addOption(Option.builder("l").longOpt("list-disabled-scripts").desc("List disabled scripts").build());
        group.addOption(Option.builder("e").longOpt("enable-script").desc("Enable script (exclude \".js\")").hasArg().argName("ScriptName").build());
        group.addOption(Option.builder("d").longOpt("disable-script").desc("Disable script (exclude \".js\")").hasArg().argName("ScriptName").build());
        loader.options.addOptionGroup(group);
    }

    public Script getScript(String name) {
        return scripts.get(name);
    }

    public void readAllScripts() throws Exception {
        if (loader.cli.hasOption("l")) {
            loader.logger.info("Disabled scripts: " + String.join(", ", loader.config.disabledScripts));
            return;
        }
        if (loader.cli.hasOption("d")) {
            var name = loader.cli.getOptionValue("d");
            if (!loader.config.disabledScripts.contains(name)) {
                loader.config.disabledScripts.add(name);
            }
            loader.logger.info("Script \"" + name + "\" has been disabled.");
            return;
        }
        if (loader.cli.hasOption("e")) {
            var name = loader.cli.getOptionValue("e");
            loader.config.disabledScripts.remove(name);
            loader.logger.info("Script \"" + name + "\" has been enabled.");
            return;
        }

        for (var file : baseDir.listFiles()) {
            var basename = file.getName().replace(".js", "");
            if (file.isFile() && file.getName().endsWith(".js") &&
                    !loader.config.disabledScripts.contains(basename)) {
                loader.logger.debug("Loading script: " + file.getName());
                scripts.put(basename, new Script(loader, file));
            }
        }
        if (scripts.size() == 0) {
            loader.logger.warning("No script has been loaded. Exiting.");
        }
    }

    public void phaseCli() {
        for (var script : scripts.values()) {
            if (script.phase.cli != null) {
                script.phase.cli.run();
            }
        }
    }

    public void phaseLoad() {
        for (var script : scripts.values()) {
            if (script.phase.load != null) {
                script.phase.load.run();
            }
        }
    }

    public void phaseBoot() {
        for (var script : scripts.values()) {
            if (script.phase.boot != null) {
                script.phase.boot.run();
            }
        }
    }
}
