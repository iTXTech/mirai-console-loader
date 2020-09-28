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
public class Repository {
    public HttpClient client;

    private final Loader loader;

    public Repository(Loader loader) {
        this.loader = loader;
        client = loader.getProxy() == null ? HttpClient.newBuilder().build() : HttpClient.newBuilder().proxy(ProxySelector.of(loader.getProxy())).build();
    }

    public HashMap<String, PackageInfo> fetchPackages() throws Exception {
        return new Gson().fromJson(httpGet("/packages.json"), new TypeToken<Map<String, PackageInfo>>() {
        }.getType());
    }

    private static String transformId(String id) {
        return id.replace(".", "/").replace(":", "/");
    }

    private static String getPackageFromId(String id) {
        return id.split(":", 2)[1];
    }

    public Package fetchPackage(String id) throws Exception {
        return new Gson().fromJson(httpGet("/" + transformId(id) + "/package.json"), new TypeToken<Package>() {
        }.getType());
    }

    public String getMavenJarUrl(String id, String ver, String verSuffix) {
        return loader.config.mavenRepo + "/" + transformId(id) + "/" + ver + "/" + getPackageFromId(id) + "-" + ver + verSuffix + ".jar";
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
