# Mirai Console Loader

## 技术路线

* 主要程序架构采用`Kotlin`编写，实现功能，比如连接更新服务器下载
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