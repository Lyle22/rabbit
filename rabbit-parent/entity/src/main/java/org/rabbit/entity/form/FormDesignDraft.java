package org.rabbit.entity.form;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author nine rabbit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("form_design_draft")
public class FormDesignDraft {

    @TableId(type = IdType.AUTO)
    private String id;

    @TableField(value  = "name")
    @Getter
    @Setter
    private String name;

    @TableField(value  = "process_definition_key")
    @Getter
    @Setter
    private String processDefinitionKey;

    @TableField(value  = "permission")
    @Getter
    @Setter
    private String permission;

    @TableField(value  = "info_json")
    @Getter
    @Setter
    private String infoJson;

    @TableField(value  = "preview_style")
    @Getter
    @Setter
    private String previewStyle;

    @TableField(value  = "form_result")
    @Getter
    @Setter
    private String formResult;

}
