package org.rabbit.entity.template;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import org.rabbit.entity.base.BaseEntity;

/**
 * Email Template class
 *
 * @author nine rabbit
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("email_template")
public class EmailTemplate extends BaseEntity {

    public static final String EMAIL_TEMPLATE_ID_PREFIX = "email.";

    @TableField("template_json")
    private String templateJson;

    @TableField("template_variable")
    private String variables;

    @TableField("tos")
    private String tos;

    @TableField("from_")
    private String from;

    private String label;

    @TableField("ccs")
    private String ccs;

    @TableField("bcc")
    private String bcc;

    private String subject;

    private String body;

    /**
     * A : Active
     * D : Delete
     */
    private String status;

}
