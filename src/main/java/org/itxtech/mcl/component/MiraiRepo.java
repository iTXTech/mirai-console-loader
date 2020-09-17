package org.itxtech.mcl.component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
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
    public HttpClient client = HttpClient.newBuilder().build();

    private final String baseUrl;

    public MiraiRepo(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public HashMap<String, PackageInfo> fetchPackages() throws IOException, InterruptedException {
        var response = client.send(HttpRequest.newBuilder(URI.create(baseUrl + "/packages.json")).timeout(Duration.ofMinutes(1)).build(), HttpResponse.BodyHandlers.ofString());
        return new Gson().fromJson(response.body(), new TypeToken<Map<String, PackageInfo>>() {
        }.getType());
    }

    public static class PackageInfo {
        public String name;
        public String description;
    }

    public static class Package {
        public HashMap<String, ArrayList<String>> channels;
    }
}
