package org.rabbit.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.Instant;

/**
 * @author nine rabbit
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("email_template")
public class EmailTemplate {

    private String id;

    @TableField("email_layout_id")
    private Long emailLayoutId;

    @TableField("email_template_json")
    private String emailTemplateJson;

    @TableField("email_template_variable")
    private String emailTemplateVariable;

    @TableField("to_")
    private String to;

    @TableField("from_")
    private String from;

    private String label;

    @TableField("cc_")
    private String cc;
    @TableField("bcc_")
    private String bcc;

    private String subject;

    private String body;

    /**
     * A : Active
     * D : Delete
     */
    private String status;

    @TableField("created_by_")
    private String createdBy;

    @TableField("modified_by_")
    private String modifiedBy;

    @TableField("created_date_")
    private Instant createdDate;

    @TableField("modified_date_")
    private Instant modifiedDate;

}
