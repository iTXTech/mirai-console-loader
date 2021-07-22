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

importPackage(java.lang);
importPackage(org.itxtech.mcl);
importPackage(org.itxtech.mcl.component);
importPackage(org.apache.commons.cli);

let group = new OptionGroup();
group.addOption(Option.builder("i").desc("Fetch info for specified package")
    .longOpt("package-info").hasArg().argName("PackageName").build());
group.addOption(Option.builder("j").desc("List available packages in Mirai Repo")
    .longOpt("list-repo-packages").build());
loader.options.addOptionGroup(group);

phase.cli = () => {
    let repo = new Repository(loader);
    if (loader.cli.hasOption("j")) {
        loader.logger.info("Fetching packages from " + loader.config.miraiRepo);
        let pkgs = repo.fetchPackages().entrySet().iterator();
        while (pkgs.hasNext()) {
            let pkg = pkgs.next();
            let info = pkg.getValue();
            loader.logger.info("---------- Package: " + pkg.getKey() + " ----------");
            loader.logger.info("Name: " + info.name);
            loader.logger.info("Description: " + info.description);
            loader.logger.info("Website: " + info.website);
            loader.logger.info("Channels: " + Utility.join(", ", info.channels));
            loader.logger.info("");
        }
        loader.exit(0);
        return;
    }

    if (loader.cli.hasOption("i")) {
        let pkg = loader.cli.getOptionValue("i");
        loader.logger.info("Fetching channel info for package \"" + pkg + "\"");
        let info = repo.fetchPackage(pkg).channels.entrySet().iterator();
        while (info.hasNext()) {
            let chan = info.next();
            loader.logger.info("---------- Channel: " + chan.getKey() + " ----------");
            loader.logger.info("Version: " + Utility.join(", ", chan.getValue()));
            loader.logger.info("");
        }
        loader.exit(0);
        return;
    }
}
