package org.itxtech.mcl.module.builtin;

import org.apache.commons.cli.Option;
import org.fusesource.jansi.Ansi;
import org.itxtech.mcl.Utility;
import org.itxtech.mcl.component.Repository;
import org.itxtech.mcl.module.MclModule;
import org.itxtech.mcl.pkg.MclPackage;

import java.io.File;

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
public class Updater extends MclModule {
    private boolean showNotice = false;

    private int size = 0;
    private String ttl = "";

    @Override
    public String getName() {
        return "updater";
    }

    @Override
    public void prepare() {
        loader.options.addOption(Option.builder("u").desc("Update packages")
                .longOpt("update").build());
        loader.options.addOption(Option.builder("k").desc("Disable progress bar")
                .longOpt("disable-progress-bar").build());
//        loader.options.addOption(Option.builder("q").desc("Remove outdated files while updating")
//                .longOpt("delete").build());
    }

    @Override
    public void load() {
        for (var pkg : loader.packageManager.getPackages()) {
            try {
                check(pkg);
            } catch (Exception e) {
                loader.logger.error("Failed to verify package \"" + pkg.id + "\"");
                loader.logger.logException(e);
            }
        }
        if (showNotice) {
            loader.logger.warning(Ansi.ansi()
                    .fgYellow()
                    .a("Run ")
                    .reset().fgBrightYellow()
                    .a("./mcl -u")
                    .reset().fgYellow()
                    .a(" to update packages.")
            );
        }
    }

    public void check(MclPackage pack) throws Exception {
        var baseInfo = Ansi.ansi()
                .a("Verifying ")
                .fgBrightYellow()
                .a("\"").a(pack.id).a("\"");
        if (!"".equals(pack.version)) {
            baseInfo = baseInfo.reset().a(" v").fgBrightYellow().a(pack.version);
        }
        loader.logger.info(baseInfo);
        var update = loader.cli.hasOption("u");
        var force = pack.isVersionLocked();
        var down = false;
        if (!Utility.checkLocalFile(pack)) {
            if (!"".equals(pack.version)) {
                loader.logger.error("\"" + pack.id + "\" is corrupted.");
            }
            down = true;
        }
        var ver = "";
        Repository.PackageInfo info = null;
        if (pack.channel.startsWith("maven")) {
            ver = loader.repo.getLatestVersionFromMaven(pack.id, pack.channel);
        } else {
            info = loader.repo.fetchPackage(pack.id);
            if (pack.type.equals("")) {
                pack.type = MclPackage.getType(info.type);
            }
            if (pack.channel.equals("")) {
                pack.channel = MclPackage.getChannel(info.defaultChannel);
            }
            if (!info.channels.containsKey(pack.channel)) {
                loader.logger.error(Ansi.ansi()
                        .fgBrightRed()
                        .a("Invalid update channel ")
                        .fgBrightBlue().append("\"").a(pack.channel).a("\"")
                        .fgBrightRed()
                        .a(" for package ")
                        .fgBrightYellow().a("\"").a(pack.id).a("\"")
                );
                loader.saveConfig();
                return;
            }
            ver = info.getLatestVersion(pack.channel);
        }

        if ((update && !pack.version.equals(ver) && !force) || pack.version.trim().equals("")) {
//            if (loader.cli.hasOption("q")) {
            pack.removeFiles();
//            } else if (pack.type.equals(MclPackage.TYPE_PLUGIN)) {
//                var dir = new File(pack.type);
//                pack.getJarFile().renameTo(new File(dir, pack.getBasename() + ".jar.bak"));
//            }
            pack.version = ver;
            down = true;
        }
        if (!down && !pack.version.equals(ver)) {
            loader.logger.warning(Ansi.ansi()
                    .fgBrightRed()
                    .a("Package ")
                    .reset().fgBrightYellow().a("\"").a(pack.id).a("\"")
                    .reset().fgBrightRed().a(" has newer version ")
                    .reset().fgBrightYellow().a("\"").a(ver).a("\"")
            );
            showNotice = true;
        }
        if (down) {
            loader.logger.info(Ansi.ansi()
                    .a("Updating ")
                    .fgBrightYellow()
                    .a("\"").a(pack.id).a("\"").reset()
                    .a(" to v").fgBrightYellow().a(pack.version)
            );
            if (!Utility.checkLocalFile(pack)) {
                downloadFile(pack, info);
            }
            if (!Utility.checkLocalFile(pack)) {
                loader.logger.error(Ansi.ansi()
                        .fgBrightRed()
                        .a("The local file ")
                        .fgBrightYellow().a("\"").a(pack.id).a("\"")
                        .fgBrightRed()
                        .a(" is still corrupted, please check the network.")
                );
            }
        }
        loader.saveConfig();
    }

    public void downloadFile(MclPackage pack, Repository.PackageInfo info) {
        var dir = new File(pack.type);
        dir.mkdirs();
        var name = pack.getName();
        var jar = name + "-" + pack.version + ".jar";
        var metadata = name + "-" + pack.version + ".mirai.metadata";

        var jarUrl = loader.repo.getJarUrl(pack, info);
        if (jarUrl.isEmpty()) {
            loader.logger.error(Ansi.ansi()
                    .a("Cannot download package ")
                    .fgBrightYellow().a("\"").a(pack.id).a("\"")
            );
            return;
        }
        var index = jarUrl.lastIndexOf(name);
        if (index != -1) {
            jar = jarUrl.substring(index);
        }
        down(jarUrl, new File(dir, jar));

        var sha1Url = loader.repo.getSha1Url(pack, info, jarUrl);
        var sha1 = jar + ".sha1";
        down(sha1Url, new File(dir, sha1));

        var metadataUrl = loader.repo.getMetadataUrl(pack, info);
        if (metadataUrl.isEmpty()) return;
        down(metadataUrl, new File(dir, metadata));
    }

    public String alignRight(String current, String total) {
        var max = Math.max(current.length(), total.length());
        return " ".repeat(max - current.length()) + current;
    }

    public String buildDownloadBar(int total, int current) {
        var length = 30;
        var bar = Math.floor((current / 1.0 / total) * length);
        var buffer = new StringBuilder("[");
        for (var i = 0; i < bar; i++) {
            buffer.append('=');
        }
        if (bar < length) {
            buffer.append('>');
            for (var i = bar; i < length; i++) {
                buffer.append(' ');
            }
        }
        return buffer + "]";
    }

    public void down(String url, File file) {
        var name = file.getName();
        size = 0;
        ttl = "";
        loader.downloader.download(url, file, loader.cli.hasOption("k") ? null : (total, current) -> {
            ttl = Utility.humanReadableFileSize(total);
            var cur = Utility.humanReadableFileSize(current);

            var line = " Downloading " + name + " " + buildDownloadBar(total, current) + " " +
                    (alignRight(cur, ttl) + " / " + ttl) + " (" + (Math.floor(current * 1000.0 / total) / 10) + "%)" + "   \r";
            loader.logger.print(line);
            size = line.length();
        });
        if (!loader.cli.hasOption("k")) {
            loader.logger.print(" ".repeat(size) + '\r');
        }
        loader.logger.println(" Downloading " + name + " " + buildDownloadBar(1, 1) + " " + ttl);
    }
}
