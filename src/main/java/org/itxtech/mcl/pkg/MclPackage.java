package org.itxtech.mcl.pkg;

import org.itxtech.mcl.Loader;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
public class MclPackage {
    public static final HashMap<String, String> TYPE_ALIAS = new HashMap<>() {{
        put("core", TYPE_CORE);
        put("plugin", TYPE_PLUGIN);
        put("mcl-module", TYPE_MODULE);
    }};

    public static final String TYPE_CORE = "libs";
    public static final String TYPE_PLUGIN = "plugins";
    public static final String TYPE_MODULE = "modules";

    public static final String CHAN_STABLE = "stable";
    public static final String CHAN_BETA = "beta";
    public static final String CHAN_NIGHTLY = "nightly";

    public static String getType(String t) {
        if (t == null) {
            return TYPE_PLUGIN;
        }
        var alias = TYPE_ALIAS.get(t);
        if (alias == null) {
            if (t.contains("-")) {
                t = t.split("-")[0];
            }
            return TYPE_ALIAS.getOrDefault(t, t);
        }
        return alias;
    }

    public static String getChannel(String c) {
        return c == null ? CHAN_STABLE : c;
    }

    public transient String id;
    public String channel;
    public String version = "";
    public String type = "";
    public boolean versionLocked = false;

    public MclPackage(String id) {
        this(id, "");
    }

    public MclPackage(String id, String channel) {
        this.id = id;
        this.channel = channel;
    }

    public boolean isVersionLocked() {
        return versionLocked;
    }

    public void addToMap(LinkedHashMap<String, MclPackage> map) {
        map.put(id, this);
    }

    public String getName() {
        return id.split(":", 2)[1];
    }

    public String getBasename() {
        return getName() + "-" + version;
    }

    public File getJarFile() {
        return new File(new File(type), getBasename() + ".jar");
    }

    public void removeFiles() {
        var dir = new File(type);
        deleteFile(dir, "jar");
        deleteFile(dir, "sha1");
        deleteFile(dir, "metadata");
    }

    public void deleteFile(File dir, String type) {
        var f = new File(dir, getBasename() + "." + type);
        if (f.exists()) {
            if (f.delete()) {
                Loader.getInstance().logger.info("File \"" + f.getName() + "\" has been deleted.");
            } else {
                Loader.getInstance().logger.error("Failed to delete \"" + f.getName() + "\". Please delete it manually.");
            }
        }
    }
}
