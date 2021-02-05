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
importPackage(org.apache.commons.cli);

loader.options.addOption(Option.builder("u").desc("Disable auto update").longOpt("disable-update").build());
loader.options.addOption(Option.builder("x").desc("Force download specified version").longOpt("force-version").build());

phase.load = () => {
    let packages = loader.config.packages;
    for (let i in packages) {
        check(packages[i]);
    }
};

function checkLocalFile(pack) {
    let dir = new File(pack.type);
    dir.mkdirs();
    return Utility.check(new File(dir, pack.getBasename() + ".jar"), new File(dir, pack.getBasename() + ".sha1"));
}

function check(pack) {
    logger.info("Verifying \"" + pack.id + "\" version " + pack.version);
    let update = loader.cli.hasOption("u");
    let force = loader.cli.hasOption("x");
    let down = false;
    if (!checkLocalFile(pack)) {
        logger.info("\"" + pack.id + ":" + pack.version + "\" is corrupted. Start downloading...");
        down = true;
    }
    let info = loader.repo.fetchPackage(pack.id);
    if (!info.channels.containsKey(pack.channel)) {
        logger.error("Invalid update channel \"" + pack.channel + "\" for Package \"" + pack.name + "\"");
    } else {
        let target = info.channels[pack.channel];
        let ver = target[target.size() - 1];
        if ((!update && !pack.version.equals(ver)) || (update && !target.contains(pack.version) && !force)) {
            if (pack.type.equals(Config.Package.TYPE_PLUGIN)) {
                let dir = new File(pack.type);
                new File(dir, pack.getBasename() + ".jar").renameTo(new File(dir, pack.getBasename() + ".jar.bak"));
            }
            pack.version = ver;
            down = true;
        }
        if (down) {
            downloadFile(pack, info);
            if (!checkLocalFile(pack)) {
                logger.warning("The local file \"" + pack.id + "\" is still corrupted, please check the network.");
            }
        }
    }
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
        logger.error("Cannot download package \"" + pack.id + "\".");
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
        logger.print(line);
        size = line.length
    });
    logger.print(emptyString.substr(0, size) + '\r');
    logger.println(" Downloading " + name + " " + buildDownloadBar(1, 1) + " " + ttl);
}
