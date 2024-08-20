package org.rabbit.tools;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Scanner;

/**
 * The class of generate code
 *
 * @author nine rabbit
 */
public class GenerateCode {

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    /**
     * RUN THIS
     */
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/nine?useSSL=true&useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8";
        String username = "docpal";
        String password = "password";
        String projectPath = System.getProperty("user.dir");

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("username") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir(projectPath + "/entity/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.baomidou.mybatisplus.samples.generator") // 设置父包名
                            .moduleName(scanner("设置父包模块名")) // 设置父包模块名
                            .pathInfo(Collections.singletonMap(
                                    OutputFile.xml, projectPath + "\\config-core\\src\\main\\java\\mapper")
                            ); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude(scanner("设置需要生成的表名")) // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
//		// 策略配置
//		StrategyConfig strategy = new StrategyConfig();
//		strategy.setNaming(NamingStrategy.underline_to_camel);
//		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//		strategy.setEntityLombokModel(true);
//		strategy.setInclude(scanner("表名"));
//		strategy.setSuperEntityColumns("id");
//		strategy.setControllerMappingHyphenStyle(true);
//		strategy.setTablePrefix(pc.getModuleName() + "_");
//		mpg.setStrategy(strategy);
//		// 选择 freemarker 引擎需要指定如下加，注意 pom 依赖必须有！
//		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
//		mpg.execute();
    }

}