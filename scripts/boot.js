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
importPackage(java.util);
importPackage(org.itxtech.mcl);
importPackage(org.itxtech.mcl.component);
importPackage(org.apache.commons.cli);

loader.options.addOption(Option.builder("b").desc("Show Mirai Console boot properties")
    .longOpt("show-boot-props").build());
loader.options.addOption(Option.builder("f").desc("Set Mirai Console boot entry")
    .longOpt("set-boot-entry").hasArg().argName("EntryClass").build());
loader.options.addOption(Option.builder("g").desc("Set Mirai Console boot arguments")
    .longOpt("set-boot-args").optionalArg(true).hasArg().argName("Arguments").build());

phase.cli = () => {
    if (loader.cli.hasOption("f")) {
        loader.config.scriptProps.put("boot.entry", loader.cli.getOptionValue("f"));
        loader.saveConfig();
    }
    if (loader.cli.hasOption("g")) {
        loader.config.scriptProps.put("boot.args", loader.cli.getOptionValue("g", ""));
        loader.saveConfig();
    }
    if (loader.cli.hasOption("b")) {
        loader.logger.info("Mirai Console boot entry: " + getBootEntry());
        loader.logger.info("Mirai Console boot arguments: " + getBootArgs());
        loader.exit(0);
        return;
    }
}

function getBootEntry() {
    return loader.config.scriptProps.getOrDefault("boot.entry", "net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader");
}

function getBootArgs() {
    return loader.config.scriptProps.getOrDefault("boot.args", "");
}

let depMap = new HashMap();
depMap.put("net.mamoe:mirai-core", "net.mamoe:mirai-core-all");

phase.boot = () => {
    let files = [];
    let pkgs = loader.config.packages;
    let pkgMap = new HashMap();
    for (let i in pkgs) {
        let pkg = pkgs[i];
        if (pkg.type.equals(Config.Package.TYPE_CORE)) {
            files.push(new File(new File(pkg.type), pkg.getBasename() + ".jar"));
            pkgMap.put(pkg.id, pkg.version);
        }
        if (pkg.type.equals(Config.Package.TYPE_PLUGIN)) {
            let file = new File(new File(pkg.type), pkg.getBasename() + ".metadata");
            if (file.exists()) {
                let deps = loader.repo.getMetadataFromFile(file).dependencies.iterator();
                while (deps.hasNext()) {
                    let dep = deps.next().split(":");
                    let name = dep[0] + ":" + dep[1];
                    let version = dep[2];
                    let realPkg = depMap.getOrDefault(name, name);
                    let it = pkgMap.entrySet().iterator();
                    while (it.hasNext()) {
                        let corePkg = it.next();
                        if (corePkg.getKey().equals(realPkg) && !corePkg.getValue().equals(version)) {
                            loader.logger.warning("Package \"" + pkg.id + "\" requires \"" + name + "\" version " + version + ". Current version is " + corePkg.getValue());
                        }
                    }
                }
            }
        }
    }

    Utility.bootMirai(files, getBootEntry(), getBootArgs());
}
