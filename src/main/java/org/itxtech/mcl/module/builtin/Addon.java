package org.itxtech.mcl.module.builtin;

import org.itxtech.mcl.component.Config;
import org.itxtech.mcl.module.MclModule;

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
public class Addon extends MclModule {
    private static final String CURRENT_CHANNEL = "c122";

    @Override
    public String getName() {
        return "addon";
    }

    @Override
    public void prepare() {
        var found = false;
        var pkgs = loader.config.packages;
        for (var pkg : pkgs) {
            if (pkg.id.equals("org.itxtech:mcl-addon")) {
                found = true;
                pkg.channel = CURRENT_CHANNEL;
                break;
            }
        }
        if (!found) {
            var p = new Config.Package("org.itxtech:mcl-addon", CURRENT_CHANNEL);
            p.type = Config.Package.TYPE_PLUGIN;
            pkgs.add(p);
            loader.logger.info("MCL Addon is installed! Website: https://github.com/iTXTech/mcl-addon");
            loader.logger.warning("To remove MCL Addon, run \"./mcl --disable-module addon\" and \"./mcl --remove-package org.itxtech:mcl-addon --delete\"");
        }
    }
}
