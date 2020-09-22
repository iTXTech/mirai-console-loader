package org.itxtech.mcl.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
public class Config {
    @SerializedName("js_optimization_level")
    public int jsOptimizationLevel = -1;
    @SerializedName("mirai_repo")
    public String miraiRepo = "https://gitee.com/peratx/mirai-repo/raw/master";
    @SerializedName("maven_repo")
    public String mavenRepo = "https://maven.aliyun.com/repository/public";
    public ArrayList<Package> packages = new ArrayList<>() {{
        add(new Package("mirai-console", "nightly"));
        add(new Package("mirai-console-terminal", "nightly"));
        add(new Package("mirai-core-qqandroid", "stable"));
    }};
    @SerializedName("disabled_scripts")
    public ArrayList<String> disabledScripts = new ArrayList<>();
    public String proxy = "";
    @SerializedName("log_level")
    public int logLevel = Logger.LOG_DEBUG;
    @SerializedName("script_props")
    public HashMap<String, String> scriptProps = new HashMap<>();

    public static Config load(File file) {
        try {
            Config conf = new Gson().fromJson(new JsonReader(new FileReader(file)), new TypeToken<Config>() {
            }.getType());
            if (conf != null) {
                return conf;
            }
        } catch (Exception ignored) {
        }
        return new Config();
    }

    public void save(File file) throws IOException {
        var writer = new FileWriter(file);
        new GsonBuilder().setPrettyPrinting().create().toJson(this, writer);
        writer.close();
    }

    public static class Package {
        public String name;
        public String channel;
        public String version = "";

        public Package(String name, String channel) {
            this.name = name;
            this.channel = channel;
        }
    }
}
