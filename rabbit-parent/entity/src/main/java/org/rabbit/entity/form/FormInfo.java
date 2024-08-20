package org.rabbit.entity.form;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import org.rabbit.entity.base.BaseEntity;

/**
 * The type System setting.
 *
 * @author nine rabbit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("form_info")
public class FormInfo extends BaseEntity {

    @TableField(value = "biz_id")
    String bizId;

    @TableField(value = "label")
    String label;

    @TableField(value = "table_name")
    String tableName;

    @TableField(value = "table_name_prefix")
    String tableNamePrefix;

    @TableField(value = "status")
    String status;

}
