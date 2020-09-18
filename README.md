# Mirai Console Loader

模块化、轻量级且支持完全自定义的 [mirai](https://github.com/mamoe/mirai) 加载器。

## 简介

`iTX Technologies Mirai Console Loader`（下简称`MCL`）采用模块化设计，包含以下几个基础模块：

* `Script` 脚本执行模块，用于加载和执行脚本，`MCL`的主要功能均由脚本实现。脚本执行有各个阶段，详见注释。
* `Config` 配置文件模块，用于配置的持久化，暂不支持脚本写入。
* `Downloader` 下载器模块，用于下载文件，并实时返回进度。
* `Logger` 日志模块，用于向控制台输出日志。

-----

从某种程度上来说，`MCL`可以被称为一个平台，如果没有任何脚本被加载，`MCL`会直接退出。

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
