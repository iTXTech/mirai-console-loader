# MCL 命令行参数

## 在`updater`中使用`maven`更新频道

此功能可以自动从`Maven Repo`获取最新版本。

```
./mcl --update-package 包名 --channel maven
```

### `maven` 支持两个子频道

* `stable` - 稳定版 - `./mcl --update-package 包名 --channel maven-stable`
* `prerelease` - 预发行版 - `./mcl --update-package 包名 --channel maven-prerelease`
* 留空或其他，则默认为最新版

## 禁用控制台颜色

配置`mcl.disable-ansi`环境变量为`true`。

```bash
$ java "-Dmcl.disable-ansi=true" -jar mcl.jar
```

仅禁用 `Windows CMD` 下 `ANSI` 初始化，请配置 `mcl.no-ansi-console-init` 环境变量为 `true`。

## 切换 `Mirai Repo`

`MCL` 内置 `Mirai Repo Manager`，可通过以下命令调用。

```bash
./mcl --mrm-list # 列出内置 Mirai Repo
./mcl --mrm-use forum # 使用 Mirai Forum 提供的 Mirai Repo 镜像
./mcl --set-mirai-repo https://repo.example.org # 使用自定义的 Mirai Repo
```

## 使用样例

* 修改某个包的更新频道

```
./mcl --update-package 包名 --channel 频道名
```

* 安装 `Mirai Native`

```
./mcl --update-package org.itxtech:mirai-native
```

* 安装 `Chat Command`

```
./mcl --update-package net.mamoe:chat-command
```

* 指定 `mirai-console` 版本（指定的版本必须为该`Channel`中存在的版本）

```
./mcl --update-package net.mamoe:mirai-console --channel stable --version 1.0.0
```

* 执行包更新

```
./mcl -u
```

* 禁用 `updater` 模块

```
./mcl --disable-module updater
```

* 启用 `updater` 模块

```
./mcl --enable-module updater
```

* 更新运行库但不启动

```
./mcl --dry-run
```

* 查看帮助

```bash
./mcl -h

usage: mcl
 -a,--update-package <PackageName>   Add or update package
 -b,--show-boot-props                Show Mirai Console boot properties
    --boot-only                      Execute boot phase only
 -c,--log-level <level>              Set log level
 -d,--disable-module <ModuleName>    Disable module
    --disable-auto-clear             Disable Repo Cache auto clear
 -e,--enable-module <ModuleName>     Enable module
    --enable-auto-clear              Enable Repo Cache auto clear
 -f,--set-boot-entry <EntryClass>    Set Mirai Console boot entry
 -g,--set-boot-args <Arguments>      Set Mirai Console boot arguments
 -i,--package-info <PackageName>     Fetch info for specified package
 -j,--list-repo-packages             List available packages in Mirai Repo
 -k,--disable-progress-bar           Disable progress bar
 -l,--list-disabled-modules          List disabled modules
 -m,--set-mirai-repo <Address>       Set Mirai Repo address
    --mrm-list                       List all builtin Mirai Repo
    --mrm-use <RepoId>               Change Mirai Repo
 -n,--channel <Channel>              Set update channel of package
 -o,--show-repos                     Show Mirai Repo and Maven Repo
 -p,--proxy <address>                Set HTTP proxy
 -q,--delete                         Remove outdated files while updating
 -r,--remove-package <PackageName>   Remove package
 -s,--list-packages                  List configured packages
    --set-max-threads <MaxThreads>   Set Max Threads of Multithreading
                                     Downloader
 -t,--type <Type>                    Set type of package
 -u,--update                         Update packages
 -w,--version <Version>              Set version of package
 -x,--lock                           Lock version of package
 -y,--unlock                         Unlock version of package
 -z,--dry-run                        Skip boot phase
```

## 开源许可证

    Copyright (C) 2020-2022 iTX Technologies

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
