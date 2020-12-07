# 官方提供的脚本

* `config.js` - 通过命令行传入配置
* `updater.js` - 用于校验和下载`mirai`文件
* `boot.js` - 用于启动`mirai console`
* `repo.js` - 用于获取`mirai repo`仓库中的信息

## 使用样例

* 安装 `Mirai Native`

`.\mcl --update-package org.itxtech:mirai-native --type plugin --channel stable`

* 安装 `Chat Command`

`.\mcl --update-package net.mamoe:chat-command --type plugin --channel stable`

* 指定 `mirai-console` 版本（指定的版本必须为该`Channel`中存在的版本）

`.\mcl --update-package net.mamoe:mirai-console --channel stable --version 1.0.0`

* 禁用`updater`脚本

`.\mcl --disable-script updater`

* 启用 `updater` 脚本

`.\mcl --enable-script updater`

* 更新运行库但不启动

`.\mcl --dry-run`

* 查看帮助

```
PS > .\mcl -h

usage: mcl
 -a,--update-package <PackageName>   Add or update package
 -b,--show-boot-props                Show Mirai Console boot properties
 -c,--log-level <level>              Set log level
 -d,--disable-script <ScriptName>    Disable script (exclude ".js")
 -e,--enable-script <ScriptName>     Enable script (exclude ".js")
 -f,--set-boot-entry <EntryClass>    Set Mirai Console boot entry
 -g,--set-boot-args <Arguments>      Set Mirai Console boot arguments
 -i,--package-info <PackageName>     Fetch info for specified package
 -j,--list-repo-packages             List available packages in Mirai Repo
 -l,--list-disabled-scripts          List disabled scripts
 -m,--set-mirai-repo <Address>       Set Mirai Repo address
 -n,--channel <Channel>              Set update channel of package
 -o,--show-repos                     Show Mirai Repo and Maven Repo
 -p,--proxy <address>                Set HTTP proxy
 -r,--remove-package <PackageName>   Remove package
 -s,--list-packages                  List configured packages
 -t,--type <Type>                    Set type of package
 -u,--disable-update                 Disable auto update
 -v,--set-maven-repo <Address>       Set Maven Repo address
 -w,--version <Version>              Set version of package
 -x,--force-version                  Force download specified version
 -z,--dry-run                        Skip boot phase
```

## 开源许可证

    Copyright (C) 2020 iTX Technologies

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
