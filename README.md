# Mirai Console Loader

模块化、轻量级且支持完全自定义的 [mirai](https://github.com/mamoe/mirai) 加载器。

欢迎阅读自带脚本的[说明](scripts/README.md)，它将教会你如何`安装插件`，`禁用和启用脚本`，`修改包的更新频道`等基本操作。

## 简介

`iTX Technologies Mirai Console Loader`（下简称`MCL`）采用模块化设计，包含以下几个基础模块：

* `Script` 脚本执行模块，用于加载和执行脚本，`MCL`的主要功能均由脚本实现。脚本执行有各个阶段，详见注释。
* `Config` 配置文件模块，用于配置的持久化。
* `Downloader` 下载器模块，用于下载文件，并实时返回进度。
* `Logger` 日志模块，用于向控制台输出日志。

## 启动`MCL`

1. 安装 Java 运行时（版本必须 >= 11）
1. 从 [Releases](https://github.com/iTXTech/mirai-console-loader/releases) 下载最新版本的`MCL`
1. 解压到某处
1. 在命令行中执行`.\mcl`以启动`MCL`

## `Mirai Repo` 列表

* [Gitee](https://gitee.com/peratx/mirai-repo/raw/master) - **默认**，如要镜像请完整拷贝该仓库文件即可
* [GitHub](https://github.com/project-mirai/mirai-repo-mirror) - 位于`project-mirai`的镜像，首选`Gitee`

## `Maven Repo` 列表

* [Bintray - Him188moe](https://dl.bintray.com/him188moe/mirai) - `mamoe` 官方仓库，仅包含`mirai`相关包
* [JCenter](https://jcenter.bintray.com/) - `mamoe` 官方仓库会自动同步到 `JCenter`
* [Aliyun](https://maven.aliyun.com/repository/public) - **默认**，阿里云`JCenter`镜像，国内访问速度快
* [HuaweiCloud](https://mirrors.huaweicloud.com/repository/maven) - 华为云`JCenter`镜像，阿里云不可用时的备选方案

## 开源许可证

    Copyright (C) 2020-2021 iTX Technologies

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
