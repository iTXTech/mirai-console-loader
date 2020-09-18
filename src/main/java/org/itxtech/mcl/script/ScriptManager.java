package org.itxtech.mcl.script;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.itxtech.mcl.Loader;

import java.io.File;
import java.util.ArrayList;

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
public class ScriptManager {
    private final Loader loader;
    private final File baseDir;
    private final ArrayList<Script> scripts = new ArrayList<>();

    public ScriptManager(Loader loader, File baseDir) {
        this.loader = loader;
        this.baseDir = baseDir;
        this.baseDir.mkdirs();

        var group = new OptionGroup();
        group.addOption(Option.builder("l").desc("列出禁用的脚本").longOpt("list").build());
        group.addOption(Option.builder("r").desc("启用脚本（不需要.js扩展名）").hasArg().argName("脚本名").build());
        group.addOption(Option.builder("d").desc("禁用脚本（不需要.js扩展名）").hasArg().argName("脚本名").build());
        loader.options.addOptionGroup(group);
    }

    public void readAllScripts() throws Exception {
        if (loader.cli.hasOption("l")) {
            loader.logger.info("禁用的脚本：" + String.join(", ", loader.config.disabledScripts));
            return;
        }
        if (loader.cli.hasOption("d")) {
            var name = loader.cli.getOptionValue("d");
            if (!loader.config.disabledScripts.contains(name)) {
                loader.config.disabledScripts.add(name);
            }
            loader.logger.info("已禁用脚本：" + name);
            return;
        }
        if (loader.cli.hasOption("r")) {
            var name = loader.cli.getOptionValue("r");
            loader.config.disabledScripts.remove(name);
            loader.logger.info("已启用脚本：" + name);
            return;
        }

        for (var file : baseDir.listFiles()) {
            if (file.isFile() && !loader.config.disabledScripts.contains(file.getName().replace(".js", ""))) {
                scripts.add(new Script(loader, file));
            }
        }
    }

    public void phaseCli() {
        for (var script : scripts) {
            if (script.phase.cli != null) {
                script.phase.cli.run();
            }
        }
    }

    public void phaseLoad() {
        for (var script : scripts) {
            if (script.phase.load != null) {
                script.phase.load.run();
            }
        }
    }

    public void phaseBoot() {
        for (var script : scripts) {
            if (script.phase.boot != null) {
                script.phase.boot.run();
            }
        }
    }
}
