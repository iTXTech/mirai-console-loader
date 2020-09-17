package org.itxtech.mcl.script;

import org.itxtech.mcl.Loader;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.io.FileReader;

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
public class Script {
    private Loader loader;
    private final File file;
    private Context cx;
    private org.mozilla.javascript.Script sc;
    private ImporterTopLevel scope;
    public final Phase phase = new Phase();

    public Script(Loader loader, File file) throws Exception {
        this.file = file;
        this.loader = loader;
        load();
    }

    private void loadLibs() {
        ScriptableObject.putProperty(scope, "loader", Context.javaToJS(loader, scope));
        ScriptableObject.putProperty(scope, "logger", Context.javaToJS(loader.logger, scope));
        ScriptableObject.putProperty(scope, "phase", Context.javaToJS(phase, scope));
    }

    public void load() throws Exception {
        cx = Context.enter();
        //Android 需要禁用编译
        cx.setLanguageVersion(Context.VERSION_ES6);
        scope = new ImporterTopLevel();
        scope.initStandardObjects(cx, false);
        loadLibs();
        var reader = new FileReader(file);
        sc = cx.compileReader(reader, file.getName(), 1, null);
        sc.exec(cx, scope);
    }
}
