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

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("form_design")
public class FormDesign extends BaseEntity {

    @TableField(value = "name")
    String name;

    @TableField(value = "table_name")
    String tableName;

    @TableField(value = "publish_status")
    String publishStatus;

    @TableField(value = "process_definition_key")
    String processDefinitionKey;

    @TableField(value = "enable")
    boolean enable;

    @TableField(value  = "permission")
    private String permission;

    @TableField(value  = "info_json")
    private String infoJson;

    @TableField(value  = "preview_style")
    private String previewStyle;

    @TableField(value  = "form_result")
    private String formResult;
}
