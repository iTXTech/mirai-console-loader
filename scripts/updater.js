importPackage(org.apache.commons.cli);
importPackage(java.io);
importPackage(org.itxtech.mcl);
importPackage(java.lang);

phase.cli = () => {
    let update = Option.builder("u").desc("禁用自动更新").longOpt("disable-update").build();
    loader.options.addOption(update);
};

phase.load = () => {
    let packages = loader.config.packages;
    for (let i in packages) {
        check(packages[i]);
    }
};

function checkLocalFile(pack) {
    let baseName = pack.name + "-" + pack.localVersion;
    return FileUtil.check(new File(loader.libDir, baseName + ".jar"), new File(loader.libDir, baseName + ".md5"));
}

function check(pack) {
    logger.info("正在验证 " + pack.name + " 版本：" + pack.localVersion);
    let download = false;
    let force = false;
    if (!checkLocalFile(pack)) {
        logger.info(pack.name + " 文件校验失败，开始下载。");
        download = true;
        force = true;
    }
    if (!loader.cli.hasOption("u")) {
        download = true;
    }
    if (download) {
        let info = loader.repo.fetchPackage(pack.name);
        if (!info.channels.containsKey(pack.channel)) {
            logger.error("非法的更新频道：" + pack.channel + " 包：" + pack.name);
        } else {
            let target = info.channels[pack.channel];
            let ver = target[target.size() - 1];
            if (force || !pack.localVersion.equals(ver)) {
                downloadFile(pack.name, ver);
                pack.localVersion = ver;
                if (!checkLocalFile(pack)) {
                    logger.warning(pack.name + " 本地文件仍然校验失败，请检查网络。");
                }
            }
        }
    }
}

function downloadFile(name, ver) {
    down(loader.repo.getDownloadUrl(name, ver, "jar"), new File(loader.libDir, name + "-" + ver + ".jar"));
    down(loader.repo.getDownloadUrl(name, ver, "md5"), new File(loader.libDir, name + "-" + ver + ".md5"));
}

function down(url, file) {
    loader.downloader.download(url, file, (total, current) => {
        System.out.print("total: " + total + " cur: " + current + "\r");
    });
    System.out.println();
}
