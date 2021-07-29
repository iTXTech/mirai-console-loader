# MCL Script

MCL 脚本开发文档。

## 脚本架构

`MCL Script` 中提供两个基本对象：

* [Loader](../src/main/java/org/itxtech/mcl/Loader.java) - MCL 实例，可通过其访问各个组件
* [Phase](../src/main/java/org/itxtech/mcl/script/Phase.java) - MCL 脚本执行阶段，脚本可按需实现

## `Phase`

### `read`

脚本被载入内存，此时执行脚本中不在任何`Phase`中的语句，例如：

1. 添加命令行选项
2. 修改配置文件

### `cli`

脚本处理命令行参数。

### `load`

各脚本都处理完命令行参数后被调用。

### `boot`

启动`mirai`，应有且只有一个脚本实现此阶段。

## 示例

```javascript
importPackage(org.apache.commons.cli); //同 Java 的 import
// MCL 使用 Apache Commons CLI，见 https://commons.apache.org/proper/commons-cli/

// read 阶段开始
loader //脚本中可直接访问 Loader 实例
    .options //命令行参数实例
    .addOption( //添加命令行参数
        Option //该包已在最上面导入了
            .builder("t")
            .desc("Example")
            .longOpt("example")
            .build()
    ); //
// read 阶段结束，只要不在任何phase中的语句都算read阶段

phase.cli = () => { //cli阶段，处理命令行参数
    if (loader.cli.hasOption("t")) { //如果存在-t参数，或--exmaple参数
        loader.logger.info("示例！！！");
    }
}

phase.load = () => {
    loader.logger.warning("示例：Load");
}

phase.boot = () => {
    loader.logger.warning("示例：Boot");
}
```
