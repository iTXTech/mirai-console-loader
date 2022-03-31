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
public class OracleJdk extends MclModule {
    @Override
    public String getName() {
        return "oraclejdk";
    }

    @Override
    public void prepare() {
        if (System.getProperty("java.vm.vendor").contains("Oracle")) {
            var pkgs = loader.config.packages;
            if (!loader.config.hasPackage("org.bouncycastle:bcprov-jdk15on")) {
                var p = new Config.Package("org.bouncycastle:bcprov-jdk15on");
                p.type = Config.Package.TYPE_CORE;
                pkgs.add(0, p);
                loader.logger.info("BouncyCastle is installed because OracleJDK is detected.");
            }
        }
    }
}
