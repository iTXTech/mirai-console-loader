importPackage(java.io);
importPackage(org.itxtech.mcl);

phase.boot = () => {
    let files = [];
    let packages = loader.config.packages;
    for (let i in packages) {
        files.push(new File(loader.libDir, packages[i].name + "-" + packages[i].localVersion + ".jar"));
    }

    FileUtil.bootMirai(FileUtil.loadJars(files), "net.mamoe.mirai.console.pure.MiraiConsolePureLoader");
}
