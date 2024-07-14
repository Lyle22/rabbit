package org.rabbit.entity.form;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * The type System setting.
 *
 * @author nine rabbit
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("form_field_mapping")
public class FormFieldMapping {

    @TableId(type = IdType.AUTO)
    private String id;

    @TableField(value = "form_info_id")
    String formInfoId;

    @TableField(value = "column_name")
    String columnName;

    @TableField(value = "field_name")
    String fieldName;

    @TableField(value = "data_type")
    String dataType;

    @TableField(value = "status")
    String status;

    @TableField(value = "required_")
    @Getter
    @Setter
    boolean required;

    @TableField(value = "unique_")
    @Getter
    @Setter
    boolean unique;

    @TableField(value = "primary_key_")
    @Getter
    @Setter
    boolean primaryKey;

}
