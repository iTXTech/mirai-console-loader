package org.itxtech.mcl.component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.itxtech.mcl.Loader;
import org.itxtech.mcl.pkg.MclPackage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;

/*
 *
 * Mirai Console Loader
 *
 * Copyright (C) 2020-2023 iTX Technologies
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
    private static final String USER_AGENT = "iTX Technologies Mirai Console Loader";

    public HttpClient client;

    public Loader loader;

    public Repository(Loader loader) {
        this.loader = loader;
        client = (loader.getProxy() == null ?
                HttpClient.newBuilder() :
                HttpClient.newBuilder().proxy(ProxySelector.of(loader.getProxy())))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        if (loader.getProxy() != null) {
            loader.logger.debug("HTTP client initialized with HTTP proxy " + loader.config.proxy);
        }
    }

    public MclPackageIndex fetchPackageIndex() throws Exception {
        return new Gson().fromJson(httpGet("/packages.json"), new TypeToken<MclPackageIndex>() {
        }.getType());
    }

    private static String transformId(String id) {
        var arr =  id.split(":", 2);
        var group = arr[0];
        var name = arr[1];
        return group.replace(".", "/") + "/" + name;
    }

    private static String getPackageFromId(String id) {
        return id.split(":", 2)[1];
    }

    public PackageInfo fetchPackage(String id) throws Exception {
        return new Gson().fromJson(httpGet("/" + transformId(id) + "/package.json"), new TypeToken<PackageInfo>() {
        }.getType());
    }

    public Document fetchMavenMetadata(String id) throws Exception {
        for (var repo : loader.config.mavenRepo) {
            try {
                if (httpHead(repo + "/" + transformId(id)+ "/maven-metadata.xml").statusCode() != 200) continue;
                var content = httpGet("/" + transformId(id) + "/maven-metadata.xml", repo);
                var factory = DocumentBuilderFactory.newInstance();
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                return factory.newDocumentBuilder().parse(new InputSource(new StringReader(content)));
            } catch (Exception e) {
                loader.logger.logException(e);
            }
        }
        throw new Exception("Cannot find valid maven metadata");
    }

    public String getLatestVersionFromMaven(String id, String channel) throws Exception {
        var data = fetchMavenMetadata(id);
        if (channel.contains("-")) {
            var kind = SemVer.getVersionKindFromChannel(channel.split("-")[1]);
            if (kind != SemVer.VersionKind.Nightly) {
                var vers = data.getElementsByTagName("versions").item(0).getChildNodes();
                var map = new TreeMap<SemVer, String>();
                for (var i = 0; i < vers.getLength(); i++) {
                    var ver = vers.item(i).getTextContent().trim();
                    if (ver.length() > 0 && SemVer.isKind(ver, kind)) {
                        var semVer = SemVer.parseFromText(ver);
                        if (semVer != null) {
                            map.put(semVer, ver);
                            continue;
                        }

                        loader.logger.warning("Failed to parse version \"" + ver + "\" for \"" + id + "\"");
                    }
                }
                if (map.size() == 0) {
                    loader.logger.error("Cannot find any version matches channel \"" + channel + "\" for \"" + id + "\", using default version.");
                } else {
                    return map.lastEntry().getValue();
                }
            }
            else return data.getElementsByTagName("latest").item(0).getTextContent();
        }
        return data.getElementsByTagName("release").item(0).getTextContent();
    }

    public Metadata getMetadataFromFile(File file) throws Exception {
        return new Gson().fromJson(Files.readString(file.toPath()), new TypeToken<Metadata>() {
        }.getType());
    }

    public String getSha1Url(MclPackage pkg, PackageInfo info, String jarUrl) {
        if (info != null && info.repo != null) {
            var repoInfo = info.repo.get(pkg.version);
            if (repoInfo != null && repoInfo.sha1 != null && !repoInfo.sha1.isBlank()) {
                return repoInfo.sha1;
            }
        }
        return jarUrl + ".sha1";
    }

    public String getJarUrl(MclPackage pkg, PackageInfo info) {
        if (info != null && info.repo != null) {
            var repoInfo = info.repo.get(pkg.version);
            if (repoInfo != null && repoInfo.archive != null && !repoInfo.archive.isBlank()) {
                return repoInfo.archive;
            }
        }
        for (var repo : loader.config.mavenRepo) {
            var baseFolder = repo + "/" + transformId(pkg.id) + "/" + pkg.version + "/";
            var base = baseFolder + getPackageFromId(pkg.id) + "-" + pkg.version;
            if (pkg.version.endsWith("-SNAPSHOT")) {
                try {
                    var real = getSnapshotJarUrl(baseFolder, getPackageFromId(pkg.id), pkg.version);
                    if (!real.isEmpty()) return real;
                } catch (Exception e) {
                    loader.logger.logException(e);
                }
                continue;
            }
            for (var suf : loader.config.archiveSuffix) {
                var real = base + suf;
                try {
                    if (httpHead(real).statusCode() == 200) {
                        return real;
                    }
                } catch (Exception e) {
                    loader.logger.logException(e);
                }
            }
        }
        return "";
    }

    private String getSnapshotJarUrl(String baseFolder, String packageName, String packageVersion) throws Exception {
        if (httpHead(baseFolder + "maven-metadata.xml").statusCode() != 200) return "";
        var content = httpGet(baseFolder + "maven-metadata.xml", "");
        var factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        var document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(content)));
        var elements = document.getElementsByTagName("snapshotVersion");
        var versions = new HashMap<String,String>();
        for (int i = 0; i < elements.getLength(); i++) {
            var version = elements.item(i).getChildNodes();
            var classifier = findNodeValue(version, "classifier", "");
            var extension = findNodeValue(version, "extension", "");
            var value = findNodeValue(version, "value", "");
            var suffix = (classifier.isEmpty() ? "" : ("-" + classifier)) + "." + extension;

            var real = baseFolder + packageName + "-" + value + suffix;
            versions.put(suffix, packageName + "-" + packageVersion + suffix + "|" + real);
        }
        for (var suf : loader.config.archiveSuffix) {
            var real = versions.get(suf);
            if (real == null) continue;
            try {
                if (httpHead(real.split("\\|")[1]).statusCode() == 200) {
                    return real;
                }
            } catch (Exception e) {
                loader.logger.logException(e);
            }
        }
        return "";
    }

    public static String findNodeValue(NodeList nodes, String name, String defValue) {
        for (int i = 0; i < nodes.getLength(); i++) {
            var node = nodes.item(i);
            if (node.getNodeName().equals(name)) {
                return node.getTextContent().trim();
            }
        }
        return defValue;
    }

    public String getMetadataUrl(MclPackage pkg, PackageInfo info) {
        if (info != null && info.repo != null) {
            var repoInfo = info.repo.get(pkg.version);
            if (repoInfo != null && repoInfo.metadata != null && !repoInfo.metadata.isBlank()) {
                return repoInfo.metadata;
            }
        }
        for (var repo : loader.config.mavenRepo) {
            var url = repo + "/" + transformId(pkg.id) + "/" + pkg.version + "/"
                    + getPackageFromId(pkg.id) + "-" + pkg.version + ".mirai.metadata";
            try {
                if (httpHead(url).statusCode() == 200) {
                    return url;
                }
            } catch (Exception e) {
                loader.logger.logException(e);
            }
        }
        return "";
    }

    public HttpResponse<Void> httpHead(String url) throws Exception {
        loader.logger.debug("HTTP HEAD " + url);
        return client.send(
                HttpRequest.newBuilder(URI.create(url))
                        .method("HEAD", HttpRequest.BodyPublishers.noBody())
                        .timeout(Duration.ofSeconds(30))
                        .setHeader("User-Agent", USER_AGENT)
                        .build(),
                HttpResponse.BodyHandlers.discarding()
        );
    }

    public String httpGet(String url) throws Exception {
        return httpGet(url, loader.config.miraiRepo);
    }

    public String httpGet(String url, String server) throws Exception {
        loader.logger.debug("HTTP GET " + server + url);
        return client.send(
                HttpRequest.newBuilder(URI.create(server + url))
                        .timeout(Duration.ofSeconds(30))
                        .setHeader("User-Agent", USER_AGENT)
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        ).body();
    }

    public static class MclPackageIndex {
        public MclPackageIndexMetadata metadata;
        public Map<String, MclPackageIndexInfo> packages;
    }

    public static class MclPackageIndexMetadata {
        public String name;
        public long timestamp;
        public String commit;
    }

    public static class MclPackageIndexInfo {
        public String name;
        public String description;
        public String website;
        public String type;
        public String defaultChannel;
    }

    public static class PackageInfo {
        public String name;
        public String announcement;
        public String type;
        public String defaultChannel;
        public Map<String, ArrayList<String>> channels;
        public Map<String, RepoInfo> repo;

        public String getLatestVersion(String chan) {
            var c = channels.get(chan);
            return c.get(c.size() - 1);
        }

        public String getName(String id) {
            return name == null ? id : name;
        }
    }

    public static class RepoInfo {
        public String archive;
        public String metadata;
        public String sha1;
    }

    public static class Metadata {
        public String groupId;
        public String artifactId;
        public String version;
        public List<String> dependencies;
    }
}
