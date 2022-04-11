package org.itxtech.mcl.module.builtin;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.itxtech.mcl.component.Repository;
import org.itxtech.mcl.module.MclModule;

import java.util.HashMap;

public class RepoCache extends MclModule {
    private static final String DISABLED_KEY = "repowithcache.disabled";
    private static final String AUTO_CLEAR_KEY = "repowithcache.auto-clear";

    @Override
    public String getName() {
        return "repowithcache";
    }

    @Override
    public void prepare() {
        var mainGroup = new OptionGroup();
        mainGroup.addOption(Option.builder().desc("Disable Repo With Cache")
                .longOpt("disable-repo-with-cache").build());
        mainGroup.addOption(Option.builder().desc("Enable Repo With Cache")
                .longOpt("enable-repo-with-cache").build());
        loader.options.addOptionGroup(mainGroup);


        var clearGroup = new OptionGroup();
        clearGroup.addOption(Option.builder().desc("Disable Repo With Cache auto clear")
                .longOpt("disable-auto-clear").build());
        clearGroup.addOption(Option.builder().desc("Enable Repo With Cache auto clear")
                .longOpt("enable-auto-clear").build());
        loader.options.addOptionGroup(clearGroup);

        if (loader.config.moduleProps.getOrDefault(DISABLED_KEY, "false").equals("false")) {
            loader.repo = new RepoWithCache(loader.repo);
            loader.logger.debug("RepoWithCache has been initialized");
        }
    }

    @Override
    public void cli() {
        if (loader.cli.hasOption("disable-repo-with-cache")) {
            loader.config.moduleProps.put(DISABLED_KEY, "true");
            loader.logger.info("RepoWithCache has been disabled. Restart MCL to take effect.");
        }
        if (loader.cli.hasOption("enable-repo-with-cache")) {
            loader.config.moduleProps.put(DISABLED_KEY, "false");
            loader.logger.info("RepoWithCache has been enabled. Restart MCL to take effect.");
        }

        if (loader.cli.hasOption("enable-auto-clear")) {
            loader.config.moduleProps.put(AUTO_CLEAR_KEY, "true");
        }
        if (loader.cli.hasOption("disable-auto-clear")) {
            loader.config.moduleProps.put(AUTO_CLEAR_KEY, "false");
        }
    }

    @Override
    public void boot() {
        if (loader.config.moduleProps.getOrDefault(AUTO_CLEAR_KEY, "true").equals("true")) {
            loader.logger.debug("RepoWithCache has been cleared");
        }
    }

    public static class RepoWithCache extends Repository {
        private final HashMap<String, PackageInfo> packageInfoCache = new HashMap<>();

        public RepoWithCache(Repository base) {
            super(base.loader);
        }

        @Override
        public PackageInfo fetchPackage(String id) throws Exception {
            if (packageInfoCache.containsKey(id)) {
                return packageInfoCache.get(id);
            }
            var info = super.fetchPackage(id);
            packageInfoCache.put(id, info);
            return info;
        }
    }
}
