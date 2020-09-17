importPackage(org.apache.commons.cli);

logger.info("我被加载了");
phase.cli = () => {
    let test = Option.builder("t").argName("testArg").desc("Test desc").hasArg().longOpt("test").required().build();
    loader.options.addOption(test);
};
