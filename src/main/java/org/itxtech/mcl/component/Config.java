package org.itxtech.mcl.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.itxtech.mcl.Loader;
import org.itxtech.mcl.pkg.MclPackage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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
public class Config {
    @SerializedName("module_packages")
    public ArrayList<String> modulePackages = new ArrayList<>() {{
        add("mcl:org.itxtech.mcl.module.builtin");
    }};

    @SerializedName("mirai_repo")
    public String miraiRepo = "https://repo.itxtech.org";

    @SerializedName("maven_repo")
    public ArrayList<String> mavenRepo = new ArrayList<>() {{
        add("https://maven.aliyun.com/repository/public");
    }};

    public LinkedHashMap<String, MclPackage> packages = new LinkedHashMap<>() {{
        new MclPackage("net.mamoe:mirai-console").addToMap(this);
        new MclPackage("net.mamoe:mirai-console-terminal").addToMap(this);
        new MclPackage("net.mamoe:mirai-core-all").addToMap(this);
    }};

    @SerializedName("disabled_modules")
    public ArrayList<String> disabledModules = new ArrayList<>();

    public String proxy = "";

    @SerializedName("log_level")
    public int logLevel = Logger.LOG_INFO;

    @SerializedName("modules_props")
    public HashMap<String, String> modulesProps = new HashMap<>();

    public static Config load(File file) {
        try {
            Config conf = new Gson().fromJson(new JsonReader(new FileReader(file)), new TypeToken<Config>() {
            }.getType());
            if (conf != null) {
                for (var entry : conf.packages.entrySet()) {
                    entry.getValue().id = entry.getKey();
                }
                return conf;
            }
        } catch (Exception e) {
            if (file.isFile() && file.exists()) {
                Loader.getInstance().logger.logException(e);
                var bak = new File(file.getAbsolutePath() + "." + System.currentTimeMillis() + ".bak");
                try {
                    Files.copy(file.toPath(), bak.toPath());
                } catch (Exception ee) {
                    Loader.getInstance().logger.logException(ee);
                }
                Loader.getInstance().logger.error("Invalid configuration file. It has been renamed to " + bak.getAbsolutePath());
            }
        }
        return new Config();
    }

    public void save(File file) throws IOException {
        var writer = new FileWriter(file);
        new GsonBuilder().setPrettyPrinting().create().toJson(this, writer);
        writer.close();
    }
}
