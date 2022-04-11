package org.itxtech.mcl.module.builtin;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.itxtech.mcl.component.Repository;
import org.itxtech.mcl.module.MclModule;

import java.util.HashMap;

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
public class RepoCache extends MclModule {
    private static final String AUTO_CLEAR_KEY = "repowithcache.auto-clear";

    @Override
    public String getName() {
        return "repowithcache";
    }

    @Override
    public void prepare() {
        var clearGroup = new OptionGroup();
        clearGroup.addOption(Option.builder().desc("Disable Repo With Cache auto clear")
                .longOpt("disable-auto-clear").build());
        clearGroup.addOption(Option.builder().desc("Enable Repo With Cache auto clear")
                .longOpt("enable-auto-clear").build());
        loader.options.addOptionGroup(clearGroup);

        loader.repo = new RepoWithCache(loader.repo);
        loader.logger.debug("RepoWithCache has been initialized. Run \"./mcl --disable-module repowithcache\" to disable.");
    }

    @Override
    public void cli() {
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
