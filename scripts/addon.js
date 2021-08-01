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
importPackage(org.itxtech.mcl.component);

//API Compatibility >= 1.2.2
let currentChannel = "c122";

let found = false;
let pkgs = loader.config.packages;
for (let i in pkgs) {
    let pkg = pkgs[i];
    if (pkg.id.equals("org.itxtech:mcl-addon")) {
        found = true;
        pkg.channel = currentChannel;
        break;
    }
}
if (!found) {
    let p = new Config.Package("org.itxtech:mcl-addon", currentChannel);
    p.type = Config.Package.TYPE_PLUGIN;
    loader.config.packages.add(p);
    loader.logger.info("MCL Addon is installed! Website: https://github.com/iTXTech/mcl-addon");
    loader.logger.warning("To remove MCL Addon, run \"./mcl --disable-script addon\" and \"./mcl --remove-package org.itxtech:mcl-addon --delete\"");
}
