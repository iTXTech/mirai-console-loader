# MCL Module

MCL 模块开发文档。

## 模块架构

`MCL Module` 中提供的基本对象：

* [Loader](../src/main/java/org/itxtech/mcl/Loader.java) - MCL 实例，可通过其访问各个组件

## `MclModule` 类

* 每个模块类都需继承 `org.itxtech.mcl.module.MclModule`
* 新版 MCL Module 加载将使用 Java SPI Service 的加载方式,   
  可参考 [META-INF.services](../src/main/resources/META-INF/services/org.itxtech.mcl.module.MclModule) 将类注册 

### `prepare`

模块最先执行的方法，可用于：

1. 添加命令行选项
2. 修改配置文件

### `cli`

脚本处理命令行参数。

### `load`

各脚本都处理完命令行参数后被调用。

### `boot`

启动`mirai`，应有且只有一个脚本实现此阶段。

## 示例

```java
package com.test.module;

import org.apache.commons.cli.Option;
import org.itxtech.mcl.module.MclModule;

public class Test extends MclModule {
    @Override
    public String getName() {
        return "Test"; // 此方法必须实现，名称是插件的唯一标识
    }

    @Override
    public void prepare() {
        loader //脚本中可直接访问 Loader 实例
                .options //命令行参数实例
                .addOption( //添加命令行参数
                        Option //该包已在最上面导入了
                                .builder("t")
                                .desc("Example")
                                .longOpt("example")
                                .build()
                );
        // MCL 使用 Apache Commons CLI，见 https://commons.apache.org/proper/commons-cli/
    }

    @Override
    public void cli() {
        if (loader.cli.hasOption("t")) { //如果存在-t参数，或--exmaple参数
            loader.logger.info("示例！！！");
        }
    }

    @Override
    public void load() {
        loader.logger.warning("示例：Load");
    }

    @Override
    public void boot() {
        loader.logger.warning("示例：Boot");
    }
}
```

## 注意事项

1. `Jar` 会直接加载入 `MCL` 的 `SystemClassLoader` 中，被所有包共享
2. `META-INF/services/org.itxtech.mcl.module.MclModule` 中一行对应一个 MclModule, 行的内容是 模块类 的完整类名
