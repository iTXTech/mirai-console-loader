# Mirai Console Loader

[![GitHub release](https://img.shields.io/github/v/release/itxtech/mirai-console-loader?label=stable)](https://github.com/iTXTech/mirai-console-loader/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.itxtech/mcl)](https://repo.maven.apache.org/maven2/org/itxtech/mcl/)
[![MiraiForum](https://img.shields.io/badge/post-on%20MiraiForum-yellow)](https://mirai.mamoe.net/topic/177)

模块化、轻量级且支持完全自定义的 [mirai](https://github.com/mamoe/mirai) 加载器。

开发者请参见 [MCL 开发文档](docs/README.md)。

## 简介

`iTX Technologies Mirai Console Loader`（下简称`MCL`）采用模块化设计，包含以下几个基础模块：

* `Module` 模块管理器，用于加载和执行模块，`MCL`的主要功能均由模块实现。模块执行有各个阶段，详见开发文档。
* `Config` 配置文件模块，用于配置的持久化。
* `Package` 包管理器。
* `Downloader` 下载器模块，用于下载文件，并实时返回进度。
* `Logger` 日志模块，用于向控制台输出日志。

## [`MCL` 命令行文档](cli.md)

该文档将教会您如何`安装插件`，`禁用和启用脚本`，`修改包的更新频道`等操作。

## 使用 `iTXTech MCL`

### 一键安装

[iTXTech MCL Installer](https://github.com/iTXTech/mcl-installer) 能在所有操作系统上一键安装 `iTXTech MCL`。

### 手动安装

1. 安装 Java 运行时（版本必须 >= 11）
2. 从 [Releases](https://github.com/iTXTech/mirai-console-loader/releases) 下载最新版本的`MCL`
3. 解压到某处
4. 在命令行中执行`.\mcl`以启动`MCL`

#### 在`*nix`下通过命令行安装

```bash
$ java -version # Check your java installation
java version "17.0.2" 2022-01-18 LTS
$ mkdir mcl
$ cd mcl
$ wget https://github.com/iTXTech/mirai-console-loader/releases/download/v1.2.2/mcl-1.2.2.zip
$ unzip mcl-1.2.2.zip
$ chmod +x mcl
$ ./mcl
```

## `Mirai Repo` 列表

* [iTXTech](https://repo.itxtech.org) - **默认** - Cloudflare Pages
* [Mamoe](https://mcl.repo.mamoe.net) - GitHub Pages
* [GitHub](https://github.com/project-mirai/mirai-repo-mirror) - 源仓库

## `Maven Repo` 列表

* [Maven Central](https://repo1.maven.org/maven2/) - `Maven Central`上游
* [Aliyun](https://maven.aliyun.com/repository/public) - **默认**，阿里云`Maven`镜像，国内访问速度快
* [HuaweiCloud](https://mirrors.huaweicloud.com/repository/maven) - 华为云`Maven`镜像，阿里云不可用时的备选方案

## 安装`MCL Module`扩展组件

1. 在 `mcl` 运行目录下新建 `modules` 目录
2. 将 目标Jar 放入该目录
3. 编辑 `config.json` 中 `module_packages` 字段，添加入 `jar文件名（不带扩展名）:包名`

如有扩展 `test.jar`，需要加载 `com.test` 包中的 `MclModule`，则添加的项为 `test:com.test`。

## 开源许可证

    iTXTech Mirai Console Loader
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
