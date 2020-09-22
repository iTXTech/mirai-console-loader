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

importPackage(org.apache.commons.cli);
importPackage(java.io);
importPackage(org.itxtech.mcl);
importPackage(java.lang);
importPackage(java.math);

loader.options.addOption(Option.builder("u").desc("Disable auto update").longOpt("disable-update").build());

phase.load = () => {
    let packages = loader.config.packages;
    for (let i in packages) {
        check(packages[i]);
    }
};

function checkLocalFile(pack) {
    return Utility.check(new File(loader.libDir, pack.getBasename() + ".jar"), new File(loader.libDir, pack.getBasename() + ".md5"));
}

function check(pack) {
    logger.info("Verifying \"" + pack.id + "\" version " + pack.version);
    let download = false;
    let force = false;
    if (!checkLocalFile(pack)) {
        logger.info("\"" + pack.id + "\" is corrupted. Start downloading...");
        download = true;
        force = true;
    }
    if (!loader.cli.hasOption("u")) {
        download = true;
    }
    if (download) {
        let info = loader.repo.fetchPackage(pack.id);
        if (!info.channels.containsKey(pack.channel)) {
            logger.error("Invalid update channel \"" + pack.channel + "\" for Package \"" + pack.name + "\"");
        } else {
            let target = info.channels[pack.channel];
            let ver = target[target.size() - 1];
            if (force || !pack.version.equals(ver)) {
                downloadFile(pack, ver);
                pack.version = ver;
                if (!checkLocalFile(pack)) {
                    logger.warning("The local file \"" + pack.id + "\" is still corrupted, please check the network.");
                }
            }
        }
    }
}

function downloadFile(pack, ver) {
    down(loader.repo.getMavenJarUrl(pack.id, ver), new File(loader.libDir, pack.getName() + "-" + ver + ".jar"));
    down(loader.repo.getMavenMd5Url(pack.id, ver), new File(loader.libDir, pack.getName() + "-" + ver + ".md5"));
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
    let name = file.name
    var size = 0
    let ttl = "";
    loader.downloader.download(url, file, (total, current) => {
        ttl = Utility.humanReadableFileSize(total);
        var cur = Utility.humanReadableFileSize(current);

        let line = " Downloading " + name + " " + buildDownloadBar(total, current) + " " + (alignRight(cur, ttl) + " / " + ttl) + " (" + (Math.floor(current * 1000 / total) / 10) + "%)" + "   \r";
        System.out.print(line);
        size = line.length
    });
    System.out.print(emptyString.substr(0, size) + '\r');
    System.out.println(" Downloading " + name + " " + buildDownloadBar(1, 1) + " " + ttl);
}
