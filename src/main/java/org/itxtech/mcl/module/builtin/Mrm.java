package org.itxtech.mcl.module.builtin;

import org.apache.commons.cli.Option;
import org.fusesource.jansi.Ansi;
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
public class Mrm extends MclModule {

    // Mirai Repo Manager

    static class Repo {
        public String url;
        public String desc;

        public Repo(String u, String d) {
            url = u;
            desc = d;
        }
    }

    public final static HashMap<String, Repo> repos = new HashMap<>() {{
        put("itx", new Repo("https://repo.itxtech.org", "Hosted by iTX Technologies"));
        put("github", new Repo("https://mcl.repo.mamoe.net", "Hosted on GitHub Pages"));
        put("mamoeRepo", new Repo("https://repo.mirai.mamoe.net/keep/mcl", "Hosted by Mamoe Technologies; Mamoe Repo Server"));
        put("forum", new Repo("https://mirai.mamoe.net/assets/mcl", "Hosted by Mamoe Technologies; Mirai Forum"));
    }};

    @Override
    public String getName() {
        return "mrm";
    }

    @Override
    public void prepare() {
        loader.options.addOption(Option.builder().desc("List all builtin Mirai Repo")
                .longOpt("mrm-list").build());

        loader.options.addOption(Option.builder().desc("Change Mirai Repo")
                .longOpt("mrm-use").hasArg().argName("RepoId").build());
    }

    @Override
    public void cli() {
        if (loader.cli.hasOption("mrm-list")) {
            loader.logger.info("");
            for (var repo : repos.entrySet()) {
                loader.logger.info(Ansi.ansi().a(repo.getKey()).a(" - ").fgBrightCyan()
                        .a(repo.getValue().url).reset().a(" - ").a(repo.getValue().desc));
            }
            loader.exit(0);
            return;
        }
        if (loader.cli.hasOption("mrm-use")) {
            var id = loader.cli.getOptionValue("mrm-use");
            var r = repos.get(id);
            if (r == null) {
                loader.logger.error("Fail to find Mirai Repo \"" + id + "\"");
            } else {
                loader.config.miraiRepo = r.url;
                loader.saveConfig();
            }
        }
    }
}
