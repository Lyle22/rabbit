package org.rabbit.service.form.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.rabbit.common.base.BasePageRequest;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormDesignRequestDTO extends BasePageRequest {

    String id;

    
    String tableName;

    
    String status;

    
    List<Map<String, Object>> data;

    
    Map<String, Object> where;

    
    Map<String, Object> notEquals;

    
    Map<String, Object> equals;

    
    String q;

    public OrderField<?> getOrderField(String defaultColumnName) {
        if (null != getIsDesc() && getIsDesc()) {
            if (StringUtils.isNotBlank(getOrderBy())) {
                return DSL.field(getOrderBy()).desc();
            } else {
                return DSL.field(defaultColumnName).desc();
            }
        } else {
            if (StringUtils.isNotBlank(getOrderBy())) {
                return DSL.field(getOrderBy()).asc();
            } else {
                return DSL.field(defaultColumnName).asc();
            }
        }
    }
}
