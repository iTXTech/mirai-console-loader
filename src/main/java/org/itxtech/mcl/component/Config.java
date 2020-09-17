package org.itxtech.mcl.component;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Config {
    @SerializedName("mirai_repo")
    public String miraiRepo = "https://raw.githubusercontent.com/project-mirai/mirai-repo/master/shadow";
    public ArrayList<Package> packages = new ArrayList<>() {{
        add(new Package("mirai-console", "beta"));
        add(new Package("mirai-console-pure", "beta"));
        add(new Package("mirai-core-qqandroid", "stable"));
    }};

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
        new Gson().toJson(this, writer);
        writer.close();
    }

    //TODO: 插件中心可以复用
    public static class Package {
        public String name;
        public String channel;

        public Package(String name, String channel) {
            this.name = name;
            this.channel = channel;
        }
    }
}
