package org.itxtech.mcl.component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.itxtech.mcl.Loader;

import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
public class MiraiRepo {
    public HttpClient client;

    private final Loader loader;

    public MiraiRepo(Loader loader) {
        this.loader = loader;
        client = loader.proxy == null ? HttpClient.newBuilder().build() : HttpClient.newBuilder().proxy(ProxySelector.of(loader.proxy)).build();
    }

    public HashMap<String, PackageInfo> fetchPackages() throws Exception {
        return new Gson().fromJson(httpGet("/packages.json"), new TypeToken<Map<String, PackageInfo>>() {
        }.getType());
    }

    public Package fetchPackage(String identifier) throws Exception {
        return new Gson().fromJson(httpGet("/" + identifier + "/package.json"), new TypeToken<Package>() {
        }.getType());
    }

    public String getMavenJarUrl(String id, String ver) {
        return loader.config.mavenRepo + "/net/mamoe/" + id + "/" + ver + "/" + id + "-" + ver + "-all.jar";
    }

    public String getMavenMd5Url(String id, String ver) {
        return loader.config.mavenRepo + "/net/mamoe/" + id + "/" + ver + "/" + id + "-" + ver + ".md5";
    }

    private String httpGet(String url) throws Exception {
        return client.send(
                HttpRequest.newBuilder(URI.create(loader.config.miraiRepo + url))
                        .timeout(Duration.ofSeconds(30))
                        .setHeader("User-Agent", "iTX Technologies Mirai Console Loader")
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        ).body();
    }

    public static class PackageInfo {
        public String name;
        public String description;
    }

    public static class Package {
        public HashMap<String, ArrayList<String>> channels;
    }
}
