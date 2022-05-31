package org.itxtech.mcl.module.builtin;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.itxtech.mcl.Utility;
import org.itxtech.mcl.component.Repository;
import org.itxtech.mcl.module.MclModule;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

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
public class Repo extends MclModule {
    @Override
    public String getName() {
        return "repo";
    }

    @Override
    public void prepare() {
        var group = new OptionGroup();
        group.addOption(Option.builder("i").desc("Fetch info for specified package")
                .longOpt("package-info").hasArg().argName("PackageName").build());
        group.addOption(Option.builder("j").desc("List available packages in Mirai Repo")
                .longOpt("list-repo-packages").build());
        loader.options.addOptionGroup(group);
    }

    @Override
    public void cli() {
        try {
            if (loader.cli.hasOption("j")) {
                loader.logger.info("Fetching packages from " + loader.config.miraiRepo);
                var index = loader.repo.fetchPackageIndex();

                loader.logger.info("---------- Mirai Repo Index Metadata ----------");
                loader.logger.info("Name: " + index.metadata.name);
                loader.logger.info("Revision: " + index.metadata.commit);
                loader.logger.info("Time: " + DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format(new Timestamp(index.metadata.timestamp).toLocalDateTime()));
                loader.logger.info("");

                for (var pkg : index.packages.entrySet()) {
                    var info = pkg.getValue();
                    loader.logger.info("---------- Package: " + pkg.getKey() + " ----------");
                    loader.logger.info("Name: " + info.name);
                    loader.logger.info("Description: " + info.description);
                    loader.logger.info("Website: " + info.website);
                    loader.logger.info("Type: " + info.type);
                    loader.logger.info("Default Channel: " + info.defaultChannel);
                    loader.logger.info("");
                }
                loader.exit(0);
                return;
            }

            if (loader.cli.hasOption("i")) {
                var pkg = loader.cli.getOptionValue("i");

                loader.logger.info("Fetching channel info for package \"" + pkg + "\"");
                Repository.PackageInfo fetchedPackageInfo = loader.repo.fetchPackage(pkg);
                if (null == fetchedPackageInfo) {
                    loader.logger.error("Package \"" + pkg + "\" is not found in MiraiRepo");
                    loader.exit(1);
                    return;
                }

                for (var chan : fetchedPackageInfo.channels.entrySet()) {
                    loader.logger.info("---------- Channel: " + chan.getKey() + " ----------");
                    loader.logger.info("Version: " + Utility.join(", ", chan.getValue()));
                    loader.logger.info("");
                }
                loader.exit(0);
            }
        } catch (Exception e) {
            loader.logger.logException(e);
        }
    }
}
