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

importPackage(java.net);
importPackage(java.lang);
importPackage(org.itxtech.mcl.component);
importPackage(org.apache.commons.cli);

loader.options.addOption(Option.builder("p").desc("Set HTTP proxy")
    .longOpt("proxy").optionalArg(true).hasArg().argName("address").build());
loader.options.addOption(Option.builder("o").desc("Show Mirai Repo and Maven Repo")
    .longOpt("show-repos").build());
loader.options.addOption(Option.builder("m").desc("Set Mirai Repo address")
    .longOpt("set-mirai-repo").hasArg().argName("Address").build());
loader.options.addOption(Option.builder("v").desc("Set Maven Repo address")
    .longOpt("set-maven-repo").hasArg().argName("Address").build());
loader.options.addOption(Option.builder("c").desc("Set log level")
    .longOpt("log-level").hasArg().argName("level").build());
let group = new OptionGroup();
group.addOption(Option.builder("s").desc("List configured packages")
    .longOpt("list-packages").build());
group.addOption(Option.builder("r").desc("Remove package")
    .longOpt("remove-package").hasArg().argName("PackageName").build());
group.addOption(Option.builder("a").desc("Add or update package")
    .longOpt("update-package").hasArg().argName("PackageName").build());
loader.options.addOptionGroup(group);
loader.options.addOption(Option.builder("n").desc("Set update channel of package")
    .longOpt("channel").hasArg().argName("Channel").build());
loader.options.addOption(Option.builder("t").desc("Set type of package")
    .longOpt("type").hasArg().argName("Type").build());
loader.options.addOption(Option.builder("w").desc("Set version of package")
    .longOpt("version").hasArg().argName("Version").build());

phase.cli = () => {
    if (loader.cli.hasOption("p")) {
        loader.config.proxy = loader.cli.getOptionValue("p", "");
        loader.saveConfig();
    }
    if (loader.cli.hasOption("o")) {
        logger.info("Mirai Repo: " + loader.config.miraiRepo);
        logger.info("Maven Repo: " + loader.config.mavenRepo);
        System.exit(0);
    }
    if (loader.cli.hasOption("m")) {
        loader.config.miraiRepo = loader.cli.getOptionValue("m");
        loader.saveConfig();
    }
    if (loader.cli.hasOption("v")) {
        loader.config.mavenRepo = loader.cli.getOptionValue("v");
        loader.saveConfig();
    }
    if (loader.cli.hasOption("c")) {
        let lvl = Integer.parseInt(loader.cli.getOptionValue("c"));
        logger.setLogLevel(lvl);
    }
    if (loader.cli.hasOption("s")) {
        let pkgs = loader.config.packages;
        for (let i in pkgs) {
            let pkg = pkgs[i];
            logger.info("Package: " + pkg.id + "  Channel: " + pkg.channel + "  Type: " + pkg.type + "  Version: " + pkg.version);
        }
        System.exit(0);
    }
    if (loader.cli.hasOption("r")) {
        let name = loader.cli.getOptionValue("r");
        let pkgs = loader.config.packages;
        for (let i in pkgs) {
            let pkg = pkgs[i];
            if (pkg.id.equals(name)) {
                pkgs.remove(pkg);
                logger.info("Package \"" + pkg.id + "\" has been removed.");
                loader.saveConfig();
                System.exit(0);
            }
        }
        logger.error("Package \"" + name + "\" not found.");
        System.exit(1);
    }
    if (loader.cli.hasOption("a")) {
        let name = loader.cli.getOptionValue("a");
        let pkgs = loader.config.packages;
        for (let i in pkgs) {
            let pkg = pkgs[i];
            if (pkg.id.equals(name)) {
                updatePackage(pkg)
                logger.info("Package \"" + pkg.id + "\" has been updated.");
                loader.saveConfig();
                System.exit(0);
            }
        }
        let pkg = new Config.Package(name, "stable");
        updatePackage(pkg);
        pkgs.add(pkg);
        logger.info("Package \"" + pkg.id + "\" has been added.");
        loader.saveConfig();
        System.exit(0);
    }
}

function updatePackage(pkg) {
    if (loader.cli.hasOption("n")) {
        pkg.channel = loader.cli.getOptionValue("n");
    }
    if (loader.cli.hasOption("t")) {
        pkg.type = Config.Package.getType(loader.cli.getOptionValue("t"));
    }
    if (loader.cli.hasOption("w")) {
        pkg.version = loader.cli.getOptionValue("w");
    }
}
