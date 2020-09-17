package org.itxtech.mcl.script;

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
    }

    public void readAllScripts() throws Exception {
        for (var file : baseDir.listFiles()) {
            if (file.isFile()) {
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
}
