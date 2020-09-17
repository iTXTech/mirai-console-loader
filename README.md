# Mirai Console Loader

## 技术路线

* 主要程序架构采用`Java`编写，实现功能，比如连接更新服务器下载
* 具体逻辑实现采用`JavaScript`编写，实现逻辑，比如自定义启动参数，切换下载源，加载前置包等等

## 具体功能

* 下载和更新服务，支持channel
* 加载JAR进根ClassLoader
* 还有更多

## 下载服务架构

* 总体沿用现在的`mirai-repo`结构
```
mirai-console
|---mirai-console-1.0.0.jar
|---mirai-console-1.0.0.md5
mirai-console-pure
mirai-core-android
packages.json
```
`packages.json`内容
```JSON
{
    "mirai-console": {
        "name": "Mirai Console",
        "description": "Mirai Console后端"
    },
    "mirai-console-pure": {
        "name": "Mirai Console Pure",
        "description": "Mirai Console Pure前端实现"
    }
}
```

* 添加`package.json`

```JSON
{
    "name": "mirai-console",
    "channels": {
        "stable": ["1.0.0"],
        "beta": ["1.0-M4"],
        "nightly": ["1.0-M4-dev3"]
    }
}
```

* 添加`md5`校验码

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
