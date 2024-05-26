package org.rabbit.entity;

public class CodeGeneration {

    private String pathSplit = "/";

    private String templatePrefix = "/rabbit";

    private String outputDir = "./src/main/java";

    /**
     * 是否生成 CRUD 测试界面及对应的 controller 方法
     */
    private boolean crud = true;

    private String jdbcUrl = "jdbc:mysql://localhost:3306/rabbit?useUnicode=true&characterEncoding=utf-8";

    private String username = "nine";

    private String password = "password";

    private String driverName = "org.mariadb.jdbc.Driver";

    private String author = "nine";

    /**
     * 上级包名
     */
    private String parent = "org.rabbit.entity";

    /**
     * 末层子包名
     */
    private String moduleName = "mail";

    private String[] tablePrefix = {"rb_"};

    private String[] includeTable = {"email_layout",             "email_log" ,
            "email_template" ,
            "email_template_relationship"};

    /**
     * MySQL 生成演示
     */
    
   /* @Test
    public void generate() {
        AutoGenerator autoGenerator = new AutoGenerator();

        // 数据源
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL);
        dataSourceConfig.setTypeConvert(new MySqlTypeConvert() {
            // 自定义数据库表字段类型转换【可选】
            @Override
            public DbColumnType processTypeConvert(String fieldType) {
                return super.processTypeConvert(fieldType);
            }
        });
        dataSourceConfig.setDriverName(driverName);
        dataSourceConfig.setUsername(username);
        dataSourceConfig.setPassword(password);
        dataSourceConfig.setUrl(jdbcUrl);
        autoGenerator.setDataSource(dataSourceConfig);

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(outputDir);
        globalConfig.setFileOverride(true);
        globalConfig.setActiveRecord(true);
        globalConfig.setEnableCache(false);
        globalConfig.setBaseResultMap(true);
        globalConfig.setBaseColumnList(true);
        globalConfig.setOpen(false);
        globalConfig.setAuthor(author);

        globalConfig.setServiceName("%sService");
        autoGenerator.setGlobalConfig(globalConfig);

        // 策略
        StrategyConfig strategy = new StrategyConfig();
        strategy.setTablePrefix(tablePrefix);
        strategy.setInclude(includeTable);
        autoGenerator.setStrategy(strategy);

        // 包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(parent);
        packageConfig.setModuleName(moduleName);
        autoGenerator.setPackageInfo(packageConfig);

        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("crud", crud);
                map.put("package", parent + "." + moduleName);
                this.setMap(map);
            }
        };

        List<FileOutConfig> fileOutConfigs = new ArrayList<FileOutConfig>();
        // 调整 xml 生成目录到 resource
        fileOutConfigs.add(new FileOutConfig(templatePrefix + ConstVal.TEMPLATE_XML) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String packagePath = (parent + "." + moduleName).replaceAll("\\.", pathSplit).concat(pathSplit);
                return outputDir + "/../resources/" + packagePath + ConstVal.MAPPER.toLowerCase() + pathSplit + tableInfo.getEntityName() + ConstVal.MAPPER + ConstVal.XML_SUFFIX;
            }
        });
        fileOutConfigs.add(new FileOutConfig(templatePrefix + "/templates/domain.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String packagePath = (parent + "." + moduleName).replaceAll("\\.", pathSplit).concat(pathSplit);
                return outputDir + "/" + packagePath + "domain" + pathSplit + tableInfo.getEntityName() + "Domain" + ConstVal.JAVA_SUFFIX;
            }
        });
        if (crud) {
            fileOutConfigs.add(new FileOutConfig(templatePrefix + "/templates/test.html.vm") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return outputDir + "/../resources/static/view/" + moduleName + pathSplit + tableInfo.getEntityName() + "Test.html";
                }
            });
            fileOutConfigs.add(new FileOutConfig(templatePrefix + "/templates/list.html.vm") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return outputDir + "/../resources/static/view/" + moduleName + pathSplit + tableInfo.getEntityName() + "List.html";
                }
            });
        }
        injectionConfig.setFileOutConfigList(fileOutConfigs);
        autoGenerator.setCfg(injectionConfig);

        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        templateConfig.setEntity(templatePrefix + ConstVal.TEMPLATE_ENTITY);
        templateConfig.setMapper(templatePrefix + ConstVal.TEMPLATE_MAPPER);
        templateConfig.setService(templatePrefix + ConstVal.TEMPLATE_SERVICE);
        templateConfig.setServiceImpl(templatePrefix + ConstVal.TEMPLATE_SERVICEIMPL);
        templateConfig.setController(templatePrefix + ConstVal.TEMPLATE_CONTROLLER);

        autoGenerator.setTemplate(templateConfig);

        autoGenerator.execute();

    }*/

}
