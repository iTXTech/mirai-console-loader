/*
 *
 * Mirai Console Loader
 *
 * Copyright (C) 2020-2021 iTX Technologies
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

importPackage(java.io);
importPackage(java.lang);
importPackage(java.math);
importPackage(org.itxtech.mcl);
importPackage(org.itxtech.mcl.component);
importPackage(org.itxtech.mcl.utils);
importPackage(org.apache.commons.cli);

loader.options.addOption(Option.builder("u").desc("Update packages").longOpt("update").build());
let showNotice = false;

phase.load = () => {
    let packages = loader.config.packages;
    for (let i in packages) {
        try {
            check(packages[i]);
        } catch (e) {
            loader.logger.error("Failed to verify package \"" + packages[i].id + "\"");
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
};

function check(pack) {
    // "Verifying \"" + pack.id + "\" version " + pack.version
    loader.logger.info(AnsiMsg.newMsg()
        .a("Verifying ")
        .gold().a("\"").a(pack.id).a("\"").reset()
        .a(" v").gold().a(pack.version)
    );
    let update = loader.cli.hasOption("u");
    let force = pack.isVersionLocked();
    let down = false;
    if (!Utility.checkLocalFile(pack)) {
        loader.logger.error("\"" + pack.id + "\" is corrupted.");
        down = true;
    }
    let info = loader.repo.fetchPackage(pack.id);
    if (!info.channels.containsKey(pack.channel)) {
        loader.logger.error(AnsiMsg.newMsg()
            .lightRed()
            .a("Invalid update channel ")
            .lightBlue().append("\"").a(pack.channel).a("\"")
            .lightRed()
            .a(" for package ")
            .gold().a("\"").a(pack.id).a("\"")
        );
    } else {
        let target = info.channels[pack.channel];
        let ver = target[target.size() - 1];
        if ((update && !pack.version.equals(ver) && !force) || pack.version.trim().equals("")) {
            if (loader.cli.hasOption("q")) {
                pack.removeFiles();
            } else if (pack.type.equals(Config.Package.TYPE_PLUGIN)) {
                let dir = new File(pack.type);
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
    }
    loader.saveConfig();
}

function downloadFile(pack, info) {
    let dir = new File(pack.type);
    dir.mkdirs();
    let ver = pack.version;
    let jarUrl = loader.repo.getJarUrl(pack, info);
    if (!jarUrl.equals("")) {
        down(jarUrl, new File(dir, pack.getName() + "-" + ver + ".jar"));
        down(jarUrl + ".sha1", new File(dir, pack.getName() + "-" + ver + ".sha1"));
        let metadata = loader.repo.getMetadataUrl(pack, info);
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

let emptyString = (function () {
    let buffer = "", counter = 1024;
    while (counter-- > 0) buffer += ' ';
    return buffer
})()

function alignRight(current, total) {
    let max = Math.max(current.length, total.length);
    return emptyString.substring(0, max - current.length) + current;
}

function buildDownloadBar(total, current) {
    let length = 30;
    let bar = Math.floor((current / total) * length);
    let buffer = "[";
    for (let i = 0; i < bar; i++) {
        buffer += '=';
    }
    if (bar < length) {
        buffer += '>';
        for (let i = bar; i < length; i++) {
            buffer += ' ';
        }
    }
    return buffer + "]";
}

function down(url, file) {
    let name = file.name;
    var size = 0;
    let ttl = "";
    loader.downloader.download(url, file, (total, current) => {
        ttl = Utility.humanReadableFileSize(total);
        var cur = Utility.humanReadableFileSize(current);

        let line = " Downloading " + name + " " + buildDownloadBar(total, current) + " " + (alignRight(cur, ttl) + " / " + ttl) + " (" + (Math.floor(current * 1000 / total) / 10) + "%)" + "   \r";
        loader.logger.print(line);
        size = line.length
    });
    loader.logger.print(emptyString.substr(0, size) + '\r');
    loader.logger.println(" Downloading " + name + " " + buildDownloadBar(1, 1) + " " + ttl);
}
