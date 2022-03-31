package org.itxtech.mcl.module.builtin;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.itxtech.mcl.Utility;
import org.itxtech.mcl.component.Repository;
import org.itxtech.mcl.module.MclModule;

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
            var repo = new Repository(loader);
            if (loader.cli.hasOption("j")) {
                loader.logger.info("Fetching packages from " + loader.config.miraiRepo);
                for (java.util.Map.Entry<String, Repository.PackageInfo> pkg : repo.fetchPackages().entrySet()) {
                    var info = pkg.getValue();
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
                var pkg = loader.cli.getOptionValue("i");
                loader.logger.info("Fetching channel info for package \"" + pkg + "\"");
                for (java.util.Map.Entry<String, java.util.ArrayList<String>> chan : repo.fetchPackage(pkg).channels.entrySet()) {
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
