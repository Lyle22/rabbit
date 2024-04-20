package org.rabbit.code.service;

import org.rabbit.code.entity.TestDemo;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class CodeAutomation {

    public static void main(String[] args) {
        generateAllBeans(TestDemo.class, "org.rabbit.modulename", "测试");
    }

    /**
     * 时间格式化
     */
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 预设目录
     */
    private static String[] dirs = {
            "/dal",
            "/domain/bo",
            "/domain/fo",
            "/domain/query",
            "/domain/vo",
            "/service/impl",
            "/service/mapper",
            "/web/mapper",
            "/web/rest"
    };

    /**
     * 自动创建不存在的目录
     */
    private static void initDir(String basePackage) {
        for (String dir : dirs) {
            String path =
                    System.getProperty("user.dir")
                            + "/src/main/java/"
                            + getBasePathByPackage(basePackage)
                            + dir;
            File f = new File(path);
            if (!f.exists()) {
                f.mkdir();
            }
        }
    }

    /**
     * 替换@Column2fovoentity
     *
     * @param split
     * @return
     */
    private static String getBasePathByPackage2FVQ(String[] split) {

        return Arrays.stream(split)
                .filter(f -> StringUtils.isNotBlank(f))
                .map(
                        e -> {
                            String substring = e.substring(e.indexOf("COMMENT '"));
                            String replaceAll =
                                    substring.replaceAll("COMMENT '", "@ApiModelProperty(\"").replaceAll("'\"", "\"");
                            return replaceAll;
                        })
                .collect(Collectors.toList())
                .toString()
                .replaceAll("\\[", "")
                .replaceAll(",", "")
                .replaceAll("\\]", "");
    }

    /**
     * 替换@Column2bo
     *
     * @param split
     * @return
     */
    private static String getBasePathByPackage2Bo(String[] split) {
        return Arrays.stream(split)
                .filter(f -> StringUtils.isNotBlank(f))
                .map(
                        e -> {
                            String substring = e.substring(e.indexOf("COMMENT '"));
                            String replaceAll =
                                    substring.replaceAll("COMMENT '", "/** ").replaceAll("'\"\\)", " */");
                            return replaceAll;
                        })
                .collect(Collectors.toList())
                .toString()
                .replaceAll("\\[", "")
                .replaceAll(",", "")
                .replaceAll("\\]", "");
    }

    /**
     * 替换点
     *
     * @param basePackage
     * @return
     */
    private static String getBasePathByPackage(String basePackage) {
        return basePackage.replaceAll("\\.", "/");
    }

    private static String getClassPath(Class clazz) {
        return System.getProperty("user.dir")
                + "/src/main/java/"
                + getBasePathByPackage(clazz.getName().replace(".java", ""))
                + ".java";
    }

    /**
     * * 把第一个字母变为小写<br>
     * * 如：<br>
     * * <code>str = "UserDao";</code><br>
     * * <code>return "userDao";</code> 168 * @param str 169 * @return 170
     */
    private static String getLowercaseChar(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 显示信息
     *
     * @param info
     */
    private static void showInfo(String info) {
        System.out.println("创建文件：【" + info + "】成功！");
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public static String getDate() {

        return simpleDateFormat.format(new Date());
    }

    /**
     * 获取entity类中的属性
     *
     * @param entity
     * @return
     */
    private static String getEntityProperties(Class entity) {
        File entityFile = new File(getClassPath(entity));
        StringBuffer sb = new StringBuffer();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(entityFile);
            byte[] bytes = new byte[512];
            // 每次读取到的数据的长度
            int len = 0;
            while ((len = fis.read(bytes)) != -1) {
                // len值为-1时，表示没有数据了
                // append方法往sb对象里面添加数据
                sb.append(new String(bytes, 0, len, "utf-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                System.out.println("关闭流失败");
            }
        }
        String s = sb.toString();
        return s.substring(s.indexOf("{") + 1, s.indexOf("}"));
    }

    /**
     * 生成 Repository类
     *
     * @param clazz       User
     * @param basePackage com.demo.test -> com.demo.test.dal.UserRepository.java
     * @throws Exception
     */
    private static void generateRepository(Class clazz, String basePackage) throws Exception {
        String fileName =
                System.getProperty("user.dir")
                        + "/src/main/java/"
                        + getBasePathByPackage(basePackage)
                        + "/code/"
                        + clazz.getSimpleName()
                        + "Repository.java"; // 如：UserDao
        File f = new File(fileName);
        FileWriter fw = new FileWriter(f);
        fw.write(
                "package "
                        + basePackage
                        + ".code;\n"
                        + "\n"
                        + "import "
                        + basePackage
                        + ".code.common.DefaultDatabaseRepository;\n"
                        + "import "
                        + basePackage
                        + ".entity."
                        + clazz.getSimpleName()
                        + ";\n"
                        + "import org.springframework.stereotype.Repository;\n"
                        + "\n"
                        + "/**\n"
                        + " * @Author 自动 @Date "
                        + getDate()
                        + "\n"
                        + " */"
                        + "@Repository\n"
                        + "public interface "
                        + clazz.getSimpleName()
                        + "Repository extends DefaultDatabaseRepository<"
                        + clazz.getSimpleName()
                        + "> {}");
        fw.flush();
        fw.close();
        showInfo(fileName);
    }

    /**
     * 生成 BO 类
     *
     * @param clazz       User
     * @param basePackage com.demo.test -> com.demo.test.domain.bo.UserBo.java
     * @throws Exception
     */
    private static void generateBo(Class clazz, String basePackage) throws Exception {
        String fileName =
                System.getProperty("user.dir")
                        + "/src/main/java/"
                        + getBasePathByPackage(basePackage)
                        + "/domain/bo/"
                        + clazz.getSimpleName()
                        + "Bo.java"; // 如：UserBo
        File f = new File(fileName);
        FileWriter fw = new FileWriter(f);
        fw.write(
                "package "
                        + basePackage
                        + ".domain.bo;\n"
                        + "\n"
                        + "import "
                        + basePackage
                        + ".domain.bo.common.DefaultBo;\n"
                        + "import lombok.Data;\n"
                        + "\n"
                        + "/**\n"
                        + " * @Author 自动 @Date "
                        + getDate()
                        + "\n"
                        + " */"
                        + "@Data\n"
                        + "public class "
                        + clazz.getSimpleName()
                        + "Bo extends DefaultBo {\n"
                        + getBasePathByPackage2Bo(
                        getEntityProperties(clazz).trim().split("@Column")) // 获取entity中的属性，并复制到到Bo中
                        + "}");
        fw.flush();
        fw.close();
        showInfo(fileName);
    }

    /**
     * 生成 FO 类
     *
     * @param clazz       User
     * @param basePackage com.demo.test -> com.demo.test.domain.fo.UserFo.java
     * @throws Exception
     */
    private static void generateFo(Class clazz, String basePackage, String cnName) throws Exception {
        String fileName =
                System.getProperty("user.dir")
                        + "/src/main/java/"
                        + getBasePathByPackage(basePackage)
                        + "/domain/fo/"
                        + clazz.getSimpleName()
                        + "Fo.java"; // 如：UserBo
        File f = new File(fileName);
        FileWriter fw = new FileWriter(f);
        fw.write(
                "package "
                        + basePackage
                        + ".domain.fo;\n"
                        + "\n"
                        + "import io.swagger.annotations.ApiModel;\n"
                        + "import io.swagger.annotations.ApiModelProperty;\n"
                        + "import lombok.Data;\n"
                        + "\n"
                        + "/**\n"
                        + " * @Author 自动 @Date "
                        + getDate()
                        + "\n"
                        + " */\n"
                        + "@ApiModel(\""
                        + cnName
                        + "入参模型\")\n"
                        + "@Data\n"
                        + "public class "
                        + clazz.getSimpleName()
                        + "Fo{\n"
                        + getBasePathByPackage2FVQ(
                        getEntityProperties(clazz).trim().split("@Column")) // 获取entity中的属性，并复制到到fo中
                        + "}");
        fw.flush();
        fw.close();
        showInfo(fileName);
    }

    /**
     * 生成 VO 类
     *
     * @param clazz       User
     * @param basePackage com.demo.test -> com.demo.test.domain.vo.UserVo.java
     * @throws Exception
     */
    private static void generateVO(Class clazz, String basePackage, String cnName) throws Exception {
        String fileName =
                System.getProperty("user.dir")
                        + "/src/main/java/"
                        + getBasePathByPackage(basePackage)
                        + "/domain/vo/"
                        + clazz.getSimpleName()
                        + "Vo.java"; // 如：UserBo
        File f = new File(fileName);
        FileWriter fw = new FileWriter(f);
        fw.write(
                "package "
                        + basePackage
                        + ".domain.vo;\n"
                        + "import "
                        + basePackage
                        + ".domain.vo.common.DefaultVo;\n"
                        + "import io.swagger.annotations.ApiModel;\n"
                        + "import io.swagger.annotations.ApiModelProperty;\n"
                        + "import lombok.Data;\n"
                        + "\n"
                        + "/**\n"
                        + " * @Author 自动 @Date "
                        + getDate()
                        + "\n"
                        + " */\n"
                        + "@ApiModel(\""
                        + cnName
                        + "出参模型\")\n"
                        + "@Data\n"
                        + "public class "
                        + clazz.getSimpleName()
                        + "Vo extends DefaultVo {\n"
                        + getBasePathByPackage2FVQ(
                        getEntityProperties(clazz).trim().split("@Column")) // 获取entity中的属性，并复制到到Vo中
                        + "}");
        fw.flush();
        fw.close();
        showInfo(fileName);
    }

    /**
     * 生成 Query 类
     *
     * @param clazz       User
     * @param basePackage com.demo.test -> com.demo.test.domain.bo.UserQuery.java
     * @throws Exception
     */
    private static void generateQuery(Class clazz, String basePackage, String cnName)
            throws Exception {
        String fileName =
                System.getProperty("user.dir")
                        + "/src/main/java/"
                        + getBasePathByPackage(basePackage)
                        + "/domain/query/"
                        + clazz.getSimpleName()
                        + "Query.java"; // 如：UserBo
        File f = new File(fileName);
        FileWriter fw = new FileWriter(f);
        fw.write(
                "package "
                        + basePackage
                        + ".domain.query;\n"
                        + "\n"
                        + "import io.swagger.annotations.ApiModel;\n"
                        + "import io.swagger.annotations.ApiModelProperty;\n"
                        + "import lombok.Data;\n"
                        + "\n"
                        + "/**\n"
                        + " * @Author 自动 @Date "
                        + getDate()
                        + "\n"
                        + " */\n"
                        + "@ApiModel(\""
                        + cnName
                        + "查询模型\")\n"
                        + "@Data\n"
                        + "public class "
                        + clazz.getSimpleName()
                        + "Query {\n"
                        + " /** 数据id */\n"
                        + "  @ApiModelProperty(\"数据id\")\n"
                        + "  private String id;\n"
                        + "\n"
                        + getBasePathByPackage2FVQ(
                        getEntityProperties(clazz).trim().split("@Column")) // 获取entity中的属性，并复制到到Query中
                        + "\n"
                        + "  @ApiModelProperty(\"创建者ID\")\n"
                        + "  private String createUserId;\n"
                        + "\n"
                        + "  @ApiModelProperty(\"更新者ID\")\n"
                        + "  private String updateUserId;\n"
                        + "\n"
                        + "  @ApiModelProperty(\"创建时间开始\")\n"
                        + "  private String startCreateTime;\n"
                        + "\n"
                        + "  @ApiModelProperty(\"创建时间结束\")\n"
                        + "  private String endCreateTime;\n"
                        + "\n"
                        + "  @ApiModelProperty(\"更新时间开始\")\n"
                        + "  private String startUpdateTime;\n"
                        + "\n"
                        + "  @ApiModelProperty(\"更新时间结束\")\n"
                        + "  private String endUpdateTime;"
                        + "}");
        fw.flush();
        fw.close();
        showInfo(fileName);
    }

    /**
     * 生成Bo和entity的mapper类
     *
     * @param clazz       User
     * @param basePackage com.demo.test -> com.demo.test.mapper.UserMapper.java
     * @throws Exception
     */
    private static void generateServiceMapper(Class clazz, String basePackage) throws Exception {
        String fileName =
                System.getProperty("user.dir")
                        + "/src/main/java/"
                        + getBasePathByPackage(basePackage)
                        + "/service/mapper/"
                        + clazz.getSimpleName()
                        + "Mapper.java"; // 如：UserTransform
        File f = new File(fileName);
        FileWriter fw = new FileWriter(f);
        fw.write(
                "package "
                        + basePackage
                        + ".service.mapper;\n"
                        + "\n"
                        + "import "
                        + basePackage
                        + ".domain.bo."
                        + clazz.getSimpleName()
                        + "Bo;\n"
                        + "import "
                        + basePackage
                        + ".entity."
                        + clazz.getSimpleName()
                        + ";\n"
                        + "import "
                        + basePackage
                        + ".service.mapper.common.DefaultMapper;\n"
                        + "import lombok.extern.slf4j.Slf4j;\n"
                        + "import org.mapstruct.Mapper;\n"
                        + "\n"
                        + "/**\n"
                        + " * @Author 自动 @Date "
                        + getDate()
                        + "\n"
                        + " */"
                        + "@Slf4j\n"
                        + "@Mapper(componentModel = \"spring\")\n"
                        + "public abstract class "
                        + clazz.getSimpleName()
                        + "Mapper implements DefaultMapper<"
                        + clazz.getSimpleName()
                        + "Bo, "
                        + clazz.getSimpleName()
                        + "> {}");
        fw.flush();
        fw.close();
        showInfo(fileName);
    }

    /**
     * 生成web mapper类
     *
     * @param clazz       User
     * @param basePackage com.demo.test -> com.demo.test.mapper.UserWebMapper.java
     * @throws Exception
     */
    private static void generateWebMapper(Class clazz, String basePackage) throws Exception {
        String fileName =
                System.getProperty("user.dir")
                        + "/src/main/java/"
                        + getBasePathByPackage(basePackage)
                        + "/web/mapper/"
                        + clazz.getSimpleName()
                        + "WebMapper.java"; // 如：UserMapper
        File f = new File(fileName);
        FileWriter fw = new FileWriter(f);
        fw.write(
                "package "
                        + basePackage
                        + ".web.mapper;\n"
                        + "\n"
                        + "import "
                        + basePackage
                        + ".domain.bo."
                        + clazz.getSimpleName()
                        + "Bo;\n"
                        + "import "
                        + basePackage
                        + ".domain.fo."
                        + clazz.getSimpleName()
                        + "Fo;\n"
                        + "import "
                        + basePackage
                        + ".domain.vo."
                        + clazz.getSimpleName()
                        + "Vo;\n"
                        + "import "
                        + basePackage
                        + ".entity."
                        + clazz.getSimpleName()
                        + ";\n"
                        + "import "
                        + basePackage
                        + ".web.mapper.common.DefaultWebMapper;\n"
                        + "import lombok.extern.slf4j.Slf4j;\n"
                        + "import org.mapstruct.Mapper;\n"
                        + "import org.mapstruct.Mapping;\n"
                        + "import org.springframework.stereotype.Repository;\n"
                        + "\n"
                        + "/**\n"
                        + " * @Author 自动 @Date "
                        + getDate()
                        + "\n"
                        + " */"
                        + "@Slf4j\n"
                        + "@Repository\n"
                        + "@Mapper(componentModel = \"spring\")\n"
                        + "public abstract class "
                        + clazz.getSimpleName()
                        + "WebMapper\n"
                        + "    implements DefaultWebMapper<"
                        + clazz.getSimpleName()
                        + "Fo, "
                        + clazz.getSimpleName()
                        + "Bo, "
                        + clazz.getSimpleName()
                        + "Vo, "
                        + clazz.getSimpleName()
                        + "> {\n"
                        + "  @Override\n"
                        + "  @Mapping(target = \"updateTime\", source = \"updateTime\", dateFormat = \"yyyy-MM-dd HH:mm:ss\")\n"
                        + "  @Mapping(target = \"createTime\", source = \"createTime\", dateFormat = \"yyyy-MM-dd HH:mm:ss\")\n"
                        + "  public abstract "
                        + clazz.getSimpleName()
                        + "Vo bo2vo("
                        + clazz.getSimpleName()
                        + "Bo bo);\n"
                        + "\n"
                        + "  @Override\n"
                        + "  @Mapping(target = \"updateTime\", source = \"updateTime\", dateFormat = \"yyyy-MM-dd HH:mm:ss\")\n"
                        + "  @Mapping(target = \"createTime\", source = \"createTime\", dateFormat = \"yyyy-MM-dd HH:mm:ss\")\n"
                        + "  public abstract "
                        + clazz.getSimpleName()
                        + "Vo entity2vo("
                        + clazz.getSimpleName()
                        + " entity);\n"
                        + "}");
        fw.flush();
        fw.close();
        showInfo(fileName);
    }

    /**
     * 生成Service类
     *
     * @param clazz       User
     * @param basePackage com.demo.test -> com.demo.test.service.UserRepository.java
     * @throws Exception
     */
    private static void generateService(Class clazz, String basePackage) throws Exception {
        String fileName =
                System.getProperty("user.dir")
                        + "/src/main/java/"
                        + getBasePathByPackage(basePackage)
                        + "/service/"
                        + clazz.getSimpleName()
                        + "Service.java"; // 如：UserService
        File f = new File(fileName);
        FileWriter fw = new FileWriter(f);
        fw.write(
                "package "
                        + basePackage
                        + ".service;\n"
                        + "\n"
                        + "import "
                        + basePackage
                        + ".domain.bo."
                        + clazz.getSimpleName()
                        + "Bo;\n"
                        + "import "
                        + basePackage
                        + ".domain.query."
                        + clazz.getSimpleName()
                        + "Query;\n"
                        + "import "
                        + basePackage
                        + ".entity."
                        + clazz.getSimpleName()
                        + ";\n"
                        + "import org.springframework.data.domain.Page;\n"
                        + "import org.springframework.data.domain.Pageable;\n"
                        + "import org.springframework.data.domain.Sort;\n"
                        + "\n"
                        + "import java.util.List;\n"
                        + "import java.util.Optional;\n"
                        + "\n"
                        + "/**\n"
                        + " * @Author 自动 @Date "
                        + getDate()
                        + "\n"
                        + " */"
                        + "public interface "
                        + clazz.getSimpleName()
                        + "Service {\n"
                        + "\n"
                        + "  "
                        + clazz.getSimpleName()
                        + "Bo findOne("
                        + clazz.getSimpleName()
                        + "Query "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query);\n"
                        + "\n"
                        + "  Optional<"
                        + clazz.getSimpleName()
                        + "Bo> findOne(String id);\n"
                        + "\n"
                        + "  "
                        + clazz.getSimpleName()
                        + "Bo add("
                        + clazz.getSimpleName()
                        + "Bo "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Bo);\n"
                        + "\n"
                        + "  "
                        + clazz.getSimpleName()
                        + "Bo edit("
                        + clazz.getSimpleName()
                        + "Bo "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Bo);\n"
                        + "\n"
                        + "  void delete(String id);\n"
                        + "\n"
                        + "  Page<"
                        + clazz.getSimpleName()
                        + "> findAll("
                        + clazz.getSimpleName()
                        + "Query "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query, Pageable pageable);\n"
                        + "\n"
                        + "  List<"
                        + clazz.getSimpleName()
                        + "Bo> findAll("
                        + clazz.getSimpleName()
                        + "Query "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query, Sort sort);\n"
                        + "\n"
                        + "  List<"
                        + clazz.getSimpleName()
                        + "Bo> findAll("
                        + clazz.getSimpleName()
                        + "Query "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query);\n"
                        + "}");
        fw.flush();
        fw.close();
        showInfo(fileName);
    }

    /**
     * 生成ServiceImpl类
     *
     * @param clazz       User
     * @param basePackage com.demo.test -> com.demo.test.service.impl.UserServiceImpl.java
     * @throws Exception
     */
    private static void generateServiceImpl(Class clazz, String basePackage) throws Exception {
        String fileName =
                System.getProperty("user.dir")
                        + "/src/main/java/"
                        + getBasePathByPackage(basePackage)
                        + "/service/impl/"
                        + clazz.getSimpleName()
                        + "ServiceImpl.java"; // 如：UserServiceImpl
        File f = new File(fileName);
        FileWriter fw = new FileWriter(f);
        fw.write(
                "package "
                        + basePackage
                        + ".service.impl;\n"
                        + "import "
                        + basePackage
                        + ".dal."
                        + clazz.getSimpleName()
                        + "Repository;\n"
                        + "import "
                        + basePackage
                        + ".domain.bo."
                        + clazz.getSimpleName()
                        + "Bo;\n"
                        + "import "
                        + basePackage
                        + ".domain.query."
                        + clazz.getSimpleName()
                        + "Query;\n"
                        + "import "
                        + basePackage
                        + ".entity."
                        + clazz.getSimpleName()
                        + ";\n"
                        + "import "
                        + basePackage
                        + ".service."
                        + clazz.getSimpleName()
                        + "Service;\n"
                        + "import "
                        + basePackage
                        + ".service.common.DefaultModelService;\n"
                        + "import "
                        + basePackage
                        + ".service.mapper."
                        + clazz.getSimpleName()
                        + "Mapper;\n"
                        + "import "
                        + basePackage
                        + ".utils.DateFormatUtil;\n"
                        + "import org.apache.commons.lang3.StringUtils;\n"
                        + "import org.springframework.beans.factory.annotation.Autowired;\n"
                        + "import org.springframework.data.domain.Page;\n"
                        + "import org.springframework.data.domain.Pageable;\n"
                        + "import org.springframework.data.domain.Sort;\n"
                        + "import org.springframework.data.jpa.domain.Specification;\n"
                        + "import org.springframework.stereotype.Service;\n"
                        + "\n"
                        + "import javax.persistence.criteria.Predicate;\n"
                        + "import java.util.List;\n"
                        + "\n"
                        + "/**\n"
                        + " * @Author 自动 @Date "
                        + getDate()
                        + "\n"
                        + " */"
                        + "@Service\n"
                        + "public class "
                        + clazz.getSimpleName()
                        + "ServiceImpl extends DefaultModelService<"
                        + clazz.getSimpleName()
                        + "Bo, "
                        + clazz.getSimpleName()
                        + ">\n"
                        + "    implements "
                        + clazz.getSimpleName()
                        + "Service {\n"
                        + "  @Autowired private "
                        + clazz.getSimpleName()
                        + "Repository "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Repository;\n"
                        + "  @Autowired private "
                        + clazz.getSimpleName()
                        + "Mapper "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Mapper;\n"
                        + "\n"
                        + "  @Override\n"
                        + "  protected "
                        + clazz.getSimpleName()
                        + "Repository getDatabaseRepository() {\n"
                        + "    return "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Repository;\n"
                        + "  }\n"
                        + "\n"
                        + "  @Override\n"
                        + "  protected "
                        + clazz.getSimpleName()
                        + "Mapper getBeanMapper() {\n"
                        + "    return "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Mapper;\n"
                        + "  }\n"
                        + "\n"
                        + "  @Override\n"
                        + "  public "
                        + clazz.getSimpleName()
                        + "Bo findOne("
                        + clazz.getSimpleName()
                        + "Query "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query) {\n"
                        + "\n"
                        + "    return findOne(\n"
                        + "            (root, query, builder) -> {\n"
                        + "              Predicate and = builder.and();\n"
                        + "              if (StringUtils.isNotBlank("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getId())) {\n"
                        + "                and = builder.and(builder.equal(root.get(\"id\"), "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getId()));\n"
                        + "              }\n"
                        + "              return and;\n"
                        + "            })\n"
                        + "        .orElseGet(() -> null);\n"
                        + "  }\n"
                        + "\n"
                        + "  @Override\n"
                        + "  public "
                        + clazz.getSimpleName()
                        + "Bo add("
                        + clazz.getSimpleName()
                        + "Bo "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Bo) {\n"
                        + "    return insert("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Bo);\n"
                        + "  }\n"
                        + "\n"
                        + "  @Override\n"
                        + "  public "
                        + clazz.getSimpleName()
                        + "Bo edit("
                        + clazz.getSimpleName()
                        + "Bo "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Bo) {\n"
                        + "    return update("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Bo);\n"
                        + "  }\n"
                        + "\n"
                        + "  @Override\n"
                        + "  public Page<"
                        + clazz.getSimpleName()
                        + "> findAll("
                        + clazz.getSimpleName()
                        + "Query "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query, Pageable pageable) {\n"
                        + "\n"
                        + "    return findAll(getDefaultSpecification("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query), pageable);\n"
                        + "  }\n"
                        + "\n"
                        + "  @Override\n"
                        + "  public List<"
                        + clazz.getSimpleName()
                        + "Bo> findAll("
                        + clazz.getSimpleName()
                        + "Query "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query, Sort sort) {\n"
                        + "    return findAll(getDefaultSpecification("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query), sort);\n"
                        + "  }\n"
                        + "\n"
                        + "  @Override\n"
                        + "  public List<"
                        + clazz.getSimpleName()
                        + "Bo> findAll("
                        + clazz.getSimpleName()
                        + "Query "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query) {\n"
                        + "    return findAll(getDefaultSpecification("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query));\n"
                        + "  }\n"
                        + "\n"
                        + "  protected Specification<"
                        + clazz.getSimpleName()
                        + "> getDefaultSpecification("
                        + clazz.getSimpleName()
                        + "Query "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query) {\n"
                        + "    return (root, query, builder) -> {\n"
                        + "      Predicate and = builder.equal(root.get(\"deletedFlag\"), 0);\n"
                        + "      if (StringUtils.isNotBlank("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getId())) {\n"
                        + "        and = builder.and(and, builder.equal(root.get(\"id\"), "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getId()));\n"
                        + "      }\n"
                        + "      if (StringUtils.isNotBlank("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getId())) {\n"
                        + "        and =\n"
                        + "            builder.and(\n"
                        + "                and, builder.equal(root.get(\"createUserId\"), "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getCreateUserId()));\n"
                        + "      }\n"
                        + "      if (StringUtils.isNotBlank("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getId())) {\n"
                        + "        and =\n"
                        + "            builder.and(\n"
                        + "                and, builder.equal(root.get(\"updateUserId\"), "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getUpdateUserId()));\n"
                        + "      }\n"
                        + "      if (StringUtils.isNotBlank("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getStartCreateTime())) {\n"
                        + "        Predicate operatingTime =\n"
                        + "            builder.lessThanOrEqualTo(\n"
                        + "                root.get(\"createTime\"),\n"
                        + "                DateFormatUtil.formatDateTime(\n"
                        + "                    "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getStartCreateTime() + \" 00:00:00\", \"yyyy-MM-dd HH:mm:ss\"));\n"
                        + "        and = builder.and(and, operatingTime);\n"
                        + "      }\n"
                        + "      if (StringUtils.isNotBlank("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getEndCreateTime())) {\n"
                        + "        Predicate operatingTime =\n"
                        + "            builder.lessThanOrEqualTo(\n"
                        + "                root.get(\"createTime\"),\n"
                        + "                DateFormatUtil.formatDateTime(\n"
                        + "                    "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getEndCreateTime() + \" 23:59:59\", \"yyyy-MM-dd HH:mm:ss\"));\n"
                        + "        and = builder.and(and, operatingTime);\n"
                        + "      }\n"
                        + "      if (StringUtils.isNotBlank("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getStartUpdateTime())) {\n"
                        + "        Predicate operatingTime =\n"
                        + "            builder.lessThanOrEqualTo(\n"
                        + "                root.get(\"updateTime\"),\n"
                        + "                DateFormatUtil.formatDateTime(\n"
                        + "                    "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getStartUpdateTime() + \" 00:00:00\", \"yyyy-MM-dd HH:mm:ss\"));\n"
                        + "        and = builder.and(and, operatingTime);\n"
                        + "      }\n"
                        + "      if (StringUtils.isNotBlank("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getEndUpdateTime())) {\n"
                        + "        Predicate operatingTime =\n"
                        + "            builder.lessThanOrEqualTo(\n"
                        + "                root.get(\"updateTime\"),\n"
                        + "                DateFormatUtil.formatDateTime(\n"
                        + "                    "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query.getEndUpdateTime() + \" 23:59:59\", \"yyyy-MM-dd HH:mm:ss\"));\n"
                        + "        and = builder.and(and, operatingTime);\n"
                        + "      }\n"
                        + "      return and;\n"
                        + "    };\n"
                        + "  }\n"
                        + "}\n");
        fw.flush();
        fw.close();
        showInfo(fileName);
    }

    /**
     * 生成resource前台服务
     *
     * @param clazz       User
     * @param basePackage com.demo.test -> com.demo.test.dal.UserController.java
     * @throws Exception
     */
    private static void generateResource(Class clazz, String basePackage, String cnName)
            throws Exception {
        String fileName =
                System.getProperty("user.dir")
                        + "/src/main/java/"
                        + getBasePathByPackage(basePackage)
                        + "/web/rest/"
                        + clazz.getSimpleName()
                        + "Controller.java"; // 如：UserResource
        File f = new File(fileName);
        FileWriter fw = new FileWriter(f);
        fw.write(
                "package "
                        + basePackage
                        + ".web.rest;\n"
                        + "\n"
                        + "import "
                        + basePackage
                        + ".domain.SmzReaponse;\n"
                        + "import "
                        + basePackage
                        + ".domain.fo."
                        + clazz.getSimpleName()
                        + "Fo;\n"
                        + "import "
                        + basePackage
                        + ".domain.query."
                        + clazz.getSimpleName()
                        + "Query;\n"
                        + "import "
                        + basePackage
                        + ".domain.vo."
                        + clazz.getSimpleName()
                        + "Vo;\n"
                        + "import "
                        + basePackage
                        + ".entity."
                        + clazz.getSimpleName()
                        + ";\n"
                        + "import "
                        + basePackage
                        + ".service."
                        + clazz.getSimpleName()
                        + "Service;\n"
                        + "import "
                        + basePackage
                        + ".utils.PaginationUtil;\n"
                        + "import "
                        + basePackage
                        + ".web.mapper."
                        + clazz.getSimpleName()
                        + "WebMapper;\n"
                        + "import io.swagger.annotations.Api;\n"
                        + "import io.swagger.annotations.ApiOperation;\n"
                        + "import io.swagger.annotations.ApiParam;\n"
                        + "import lombok.extern.slf4j.Slf4j;\n"
                        + "import org.springframework.beans.factory.annotation.Autowired;\n"
                        + "import org.springframework.data.domain.Page;\n"
                        + "import org.springframework.data.domain.Pageable;\n"
                        + "import org.springframework.data.web.PageableDefault;\n"
                        + "import org.springframework.http.HttpHeaders;\n"
                        + "import org.springframework.http.HttpStatus;\n"
                        + "import org.springframework.http.MediaType;\n"
                        + "import org.springframework.http.ResponseEntity;\n"
                        + "import org.springframework.web.bind.annotation.*;\n"
                        + "\n"
                        + "import javax.inject.Inject;\n"
                        + "import javax.validation.Valid;\n"
                        + "import java.util.List;\n"
                        + "\n"
                        + "/**\n"
                        + " * @Author 自动 @Date "
                        + getDate()
                        + "\n"
                        + " */"
                        + "@Api(tags = {\""
                        + cnName
                        + "服务API\"})\n"
                        + "@Slf4j\n"
                        + "@RestController\n"
                        + "@RequestMapping(\"/api\")\n"
                        + "public class "
                        + clazz.getSimpleName()
                        + "Controller {\n"
                        + "  @Inject private "
                        + clazz.getSimpleName()
                        + "WebMapper "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "WebMapper;\n"
                        + "  @Autowired private "
                        + clazz.getSimpleName()
                        + "Service "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Service;\n"
                        + "\n"
                        + "  @ApiOperation(\n"
                        + "      value = \"查询所有\",\n"
                        + "      httpMethod = \"GET\",\n"
                        + "      nickname = \"findAll\",\n"
                        + "      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)\n"
                        + "  @GetMapping(\"/"
                        + getLowercaseChar(clazz.getSimpleName())
                        + "\")\n"
                        + "  public ResponseEntity<SmzReaponse<List<"
                        + clazz.getSimpleName()
                        + "Vo>>> findAll(\n"
                        + "      "
                        + clazz.getSimpleName()
                        + "Query "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query, @PageableDefault Pageable pageable) {\n"
                        + "    Page<"
                        + clazz.getSimpleName()
                        + "> page = "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Service.findAll("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Query, pageable);\n"
                        + "    HttpHeaders headers =\n"
                        + "        PaginationUtil.generatePaginationHttpHeaders(page, \"/api/ship/queryAllBoundShips\");\n"
                        + "    return new ResponseEntity(\n"
                        + "        SmzReaponse.createBySuccess("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "WebMapper.entitys2vos(page.getContent())),\n"
                        + "        headers,\n"
                        + "        HttpStatus.OK);\n"
                        + "  }\n"
                        + "\n"
                        + "  @ApiOperation(\n"
                        + "      value = \"根据id查询\",\n"
                        + "      httpMethod = \"GET\",\n"
                        + "      nickname = \"findOne\",\n"
                        + "      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)\n"
                        + "  @GetMapping(value = \"/"
                        + getLowercaseChar(clazz.getSimpleName())
                        + "/{id}\")\n"
                        + "  public ResponseEntity<SmzReaponse<"
                        + clazz.getSimpleName()
                        + "Vo>> findOne(\n"
                        + "      @PathVariable @ApiParam(value = \"数据id\") String id) {\n"
                        + "    return ResponseEntity.ok(\n"
                        + "        SmzReaponse.createBySuccess(\n"
                        + "            "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Service.findOne(id).map(e -> "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "WebMapper.bo2vo(e)).orElseGet(null)));\n"
                        + "  }\n"
                        + "\n"
                        + "  @ApiOperation(\n"
                        + "      value = \"添加数据\",\n"
                        + "      httpMethod = \"POST\",\n"
                        + "      nickname = \"add\",\n"
                        + "      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)\n"
                        + "  @PostMapping(value = \"/"
                        + clazz.getSimpleName()
                        + "\")\n"
                        + "  public ResponseEntity<SmzReaponse<"
                        + clazz.getSimpleName()
                        + "Vo>> add(@RequestBody @Valid "
                        + clazz.getSimpleName()
                        + "Fo fo) {\n"
                        + "\n"
                        + "    return ResponseEntity.ok(\n"
                        + "        SmzReaponse.createBySuccess(\n"
                        + "            "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "WebMapper.bo2vo("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Service.add("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "WebMapper.fo2bo(fo)))));\n"
                        + "  }\n"
                        + "\n"
                        + "  @ApiOperation(\n"
                        + "      value = \"修改数据\",\n"
                        + "      httpMethod = \"PUT\",\n"
                        + "      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)\n"
                        + "  @PutMapping(value = \"/"
                        + clazz.getSimpleName()
                        + "\")\n"
                        + "  public ResponseEntity<SmzReaponse<"
                        + clazz.getSimpleName()
                        + "Vo>> update(@RequestBody @Valid "
                        + clazz.getSimpleName()
                        + "Fo fo) {\n"
                        + "\n"
                        + "    return ResponseEntity.ok(\n"
                        + "        SmzReaponse.createBySuccess(\n"
                        + "            "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "WebMapper.bo2vo("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Service.edit("
                        + getLowercaseChar(clazz.getSimpleName())
                        + "WebMapper.fo2bo(fo)))));\n"
                        + "  }\n"
                        + "\n"
                        + "  @ApiOperation(\n"
                        + "      value = \"删除数据\",\n"
                        + "      httpMethod = \"DELETE\",\n"
                        + "      consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)\n"
                        + "  @DeleteMapping(value = \"/"
                        + clazz.getSimpleName()
                        + "/{id}\")\n"
                        + "  public ResponseEntity<SmzReaponse> delete(@PathVariable String id) {\n"
                        + "    "
                        + getLowercaseChar(clazz.getSimpleName())
                        + "Service.delete(id);\n"
                        + "    return new ResponseEntity<>(SmzReaponse.createBySuccess(\"删除成功\", \"删除成功\"), HttpStatus.OK);\n"
                        + "  }\n"
                        + "}");
        fw.flush();
        fw.close();
        showInfo(fileName);
    }

    /**
     * * 一键生成所有entity对应的类
     *
     * @param clazz       实体类
     * @param basePackage 基础包路径 如：com.demo.test
     */
    public static void generateAllBeans(Class clazz, String basePackage, String cnName) {
        try {
            initDir(basePackage);
            /** 生成bo */
            generateBo(clazz, basePackage);
            /** 生成fo vo bo query */
            generateFo(clazz, basePackage, cnName);
            generateVO(clazz, basePackage, cnName);
            generateQuery(clazz, basePackage, cnName);
            generateRepository(clazz, basePackage);

            /** 生成ServiceMapper */
            generateServiceMapper(clazz, basePackage);
            /** 生成webmapper */
            generateWebMapper(clazz, basePackage);

            /** 生成Service */
            generateService(clazz, basePackage);

            /** 生成ServiceImpl */
            generateServiceImpl(clazz, basePackage);

            /** 生成resource */
            generateResource(clazz, basePackage, cnName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
