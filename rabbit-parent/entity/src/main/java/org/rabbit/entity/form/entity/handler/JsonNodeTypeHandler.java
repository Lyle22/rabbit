package org.rabbit.entity.form.entity.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * includeNullJdbcType 在 Mybatis 3.4.0 开始 默认为true。
 * 想让mybatis 自动化处理映射关系，则必须保证 includeNullJdbcType =true,
 * 因为如果只是设置了  @MappedJdbcTypes(value = JdbcType.VARCHAR ) 则该处理器就无法自动处理 JdbcType是json 的情况。
 * 实际上，根据官方文档，mybatis 是把所有的返回值都当作 JdbcType = null 来自动 选择类型处理器的
 * 如果includeNullJdbcType =false，则必须在 sql中返回的字段上 明确标注 typeHandler= xxx.class
 *
 * @MappedJdbcTypes的value 设置为 JdbcType.LONGVARCHAR 或者 JdbcType.LONGVARCHAR 都可以。
 * 建议JdbcType.LONGVARCHAR，据测试，json 类型的返回结果的JdbcType = LONGVARCHAR
 *
 * @ColumnType when the reult is ResultMap
 */
// @MappedTypes(JsonNode.class) // 因为BaseTypeHandler<JsonNode> 泛型中指定了JsonNode 的话，这个注解也可以省略
@MappedJdbcTypes(value = JdbcType.VARCHAR, includeNullJdbcType = true)
@Component
public class JsonNodeTypeHandler extends BaseTypeHandler<JsonNode> implements InitializingBean {

    static JsonNodeTypeHandler j;

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 魔法 注入 单例bean objectMapper;
     * 在 @Controller 中注入ObjectMapper 不需要这么麻烦，直接 @Autowired 即可 。
     * 非Controller 注入原理：spring 启动过程中 实例化JsonNodeTypeHandler 的 bean 时，会自动把 objectMapper 携带过来；
     * spring 启动完成后的bean 又会被擦除 。所以，这个要及时赋值一下引用 objectMapper
     */
    @Override
    public void afterPropertiesSet() {
        j = this; // 初始化静态实例
        j.objectMapper = this.objectMapper; //及时拷贝引用
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode jsonNode, JdbcType jdbcType) throws SQLException {
        ps.setString(i, jsonNode != null ? jsonNode.toString() : null);
    }

    @SneakyThrows
    @Override
    public JsonNode getNullableResult(ResultSet rs, String colName) {
        return read(rs.getString(colName));
    }

    @SneakyThrows
    @Override
    public JsonNode getNullableResult(ResultSet rs, int colIndex) {
        return read(rs.getString(colIndex));
    }

    @SneakyThrows
    @Override
    public JsonNode getNullableResult(CallableStatement cs, int i) {
        return read(cs.getString(i));
    }

    @SneakyThrows
    private JsonNode read(String json) {
        return json != null ? j.objectMapper.readTree(json) : null;
    }
}
