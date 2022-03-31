package org.itxtech.mcl.module.builtin;

import org.apache.commons.cli.Option;
import org.itxtech.mcl.Utility;
import org.itxtech.mcl.component.Config;
import org.itxtech.mcl.component.Repository;
import org.itxtech.mcl.module.MclModule;
import org.itxtech.mcl.utils.AnsiMsg;

import java.io.File;

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
        loader.options.addOption(Option.builder("u").desc("Update packages").longOpt("update").build());
        loader.options.addOption(Option.builder("k").desc("Disable progress bar").longOpt("disable-progress-bar").build());
    }

    @Override
    public void load() {
        var packages = loader.config.packages;
        for (var pkg : packages) {
            try {
                check(pkg);
            } catch (Exception e) {
                loader.logger.error("Failed to verify package \"" + pkg.id + "\"");
                loader.logger.logException(e);
            }
        }
        if (showNotice) {
            loader.logger.warning(AnsiMsg.newMsg()
                    .lightYellow()
                    .a("Run ")
                    .reset().gold()
                    .a("./mcl -u")
                    .reset().lightYellow()
                    .a(" to update packages.")
            );
        }
    }

    public void check(Config.Package pack) throws Exception {
        // "Verifying \"" + pack.id + "\" version " + pack.version
        loader.logger.info(AnsiMsg.newMsg()
                .a("Verifying ")
                .gold().a("\"").a(pack.id).a("\"").reset()
                .a(" v").gold().a(pack.version)
        );
        var update = loader.cli.hasOption("u");
        var force = pack.isVersionLocked();
        var down = false;
        if (!Utility.checkLocalFile(pack)) {
            loader.logger.error("\"" + pack.id + "\" is corrupted.");
            down = true;
        }
        var ver = "";
        Repository.Package info = null;
        if (pack.channel.startsWith("maven")) {
            ver = loader.repo.getLatestVersionFromMaven(pack.id, pack.channel);
        } else {
            info = loader.repo.fetchPackage(pack.id);
            if (pack.type.equals("")) {
                pack.type = Config.Package.getType(info.type);
            }
            if (pack.channel.equals("")) {
                pack.channel = Config.Package.getChannel(info.defaultChannel);
            }
            if (!info.channels.containsKey(pack.channel)) {
                loader.logger.error(AnsiMsg.newMsg()
                        .lightRed()
                        .a("Invalid update channel ")
                        .lightBlue().append("\"").a(pack.channel).a("\"")
                        .lightRed()
                        .a(" for package ")
                        .gold().a("\"").a(pack.id).a("\"")
                );
                loader.saveConfig();
                return;
            }
            var target = info.channels.get(pack.channel);
            ver = target.get(target.size() - 1);
        }

        if ((update && !pack.version.equals(ver) && !force) || pack.version.trim().equals("")) {
            if (loader.cli.hasOption("q")) {
                pack.removeFiles();
            } else if (pack.type.equals(Config.Package.TYPE_PLUGIN)) {
                var dir = new File(pack.type);
                pack.getJarFile().renameTo(new File(dir, pack.getBasename() + ".jar.bak"));
            }
            pack.version = ver;
            down = true;
        }
        if (!down && !pack.version.equals(ver)) {
            loader.logger.warning(AnsiMsg.newMsg()
                    .lightRed()
                    .a("Package ")
                    .reset().gold().a("\"").a(pack.id).a("\"")
                    .reset().lightRed().a(" has newer version ")
                    .reset().gold().a("\"").a(ver).a("\"")
            );
            showNotice = true;
        }
        if (down) {
            downloadFile(pack, info);
            if (!Utility.checkLocalFile(pack)) {
                loader.logger.error(AnsiMsg.newMsg()
                        .lightRed()
                        .a("The local file ")
                        .gold().a("\"").a(pack.id).a("\"")
                        .lightRed()
                        .a(" is still corrupted, please check the network.")
                );
            }
        }
        loader.saveConfig();
    }

    public void downloadFile(Config.Package pack, Repository.Package info) {
        var dir = new File(pack.type);
        dir.mkdirs();
        var ver = pack.version;
        var jarUrl = loader.repo.getJarUrl(pack, info);
        if (!jarUrl.isEmpty()) {
            down(jarUrl, new File(dir, pack.getName() + "-" + ver + ".jar"));
            down(loader.repo.getSha1Url(pack, info, jarUrl), new File(dir, pack.getName() + "-" + ver + ".sha1"));
            var metadata = loader.repo.getMetadataUrl(pack, info);
            if (!metadata.equals("")) {
                down(metadata, new File(dir, pack.getName() + "-" + ver + ".metadata"));
            }
        } else {
            loader.logger.error(AnsiMsg.newMsg()
                    .a("Cannot download package ")
                    .gold().a("\"").a(pack.id).a("\"")
            );
        }
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
