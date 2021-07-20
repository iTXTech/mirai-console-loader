package org.itxtech.mcl.script;

import org.itxtech.mcl.Loader;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
 * @author PeratX
 * @website https://github.com/iTXTech/mirai-console-loader
 *
 */
public class Script {
    private final Loader loader;
    private final File file;
    private ImporterTopLevel scope;
    public final Phase phase = new Phase();

    public Script(Loader loader, File file) throws Exception {
        this.file = file;
        this.loader = loader;
        load();
    }

    private void loadLibs() {
        ScriptableObject.putProperty(scope, "loader", Context.javaToJS(loader, scope));
        ScriptableObject.putProperty(scope, "phase", Context.javaToJS(phase, scope));
    }

    public void load() throws Exception {
        var cx = Context.enter();
        cx.setOptimizationLevel(loader.config.jsOptimizationLevel);
        cx.setLanguageVersion(Context.VERSION_ES6);
        scope = new ImporterTopLevel();
        scope.initStandardObjects(cx, false);
        loadLibs();
        try (
                var stream = new FileInputStream(file);
                var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)
        ) {
            var sc = cx.compileReader(reader, file.getName(), 1, null);
            sc.exec(cx, scope);
        }
    }
}
