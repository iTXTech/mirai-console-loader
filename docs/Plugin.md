# MCL Plugin

在`Mirai Console`插件中使用`MCL API`，本文档采用`Kotlin`编写。

## 在`build.gradle(.kts)` 中添加`MCL`依赖

```groovy
dependencies {
    //打包时排除mcl包，如果您的插件打包时不打包依赖，则可使用implementation
    compileOnly("org.itxtech:mcl:2.0.0")
}
```

## 在插件主类中检查`MCL`

如果不检查会导致加载调用了`MCL API`的类时导致崩溃。

```kotlin
try {
    Class.forName("org.itxtech.mcl.Loader")
} catch (e: Exception) {
    logger.error("Mirai Console 并未通过 iTXTech Mirai Console Loader 加载。")
    logger.error("请访问 https://github.com/iTXTech/mirai-console-loader")
    return
}
//载入调用了MCL API的类，切记不要用子类，不然会自动加载
```

## 执行`MCL`命令行命令

```kotlin
import org.itxtech.mcl.Loader

val mcl = Loader.getInstance()

fun runMclCommand(args: Array<String>) {
    mcl.parseCli(args, true) //调用mcl解析参数
    mcl.manager.phaseCli() //调用模块管理器执行cli阶段
}

//执行添加包指令
runMclCommand(arrayOf("--update-package", "包名", "--type", "plugin", "--channel", "stable"))
```

## 调用`MCL`包管理器

```kotlin
import org.itxtech.mcl.Loader
import org.itxtech.mcl.component.Config

val mcl = Loader.getInstance();

//添加 Mirai Native
mcl.config.packages.add(
    Config.Package("org.itxtech:mirai-native", "stable")
)

//执行 updater 模块，如果updater被禁用则无法调用
mcl.manager.getModule("updater")?.load() //执行模块的 load 阶段
```
