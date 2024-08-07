package org.rabbit.entity.form.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.Instant;

/**
 * @author Lyle
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("email_log")
public class EmailLog {

    private Long id;

    @TableField("form_email")
    private String formEmail;

    private String tos;

    private String ccs;

    private String bcc;

    private String subject;

    private String body;

    @TableField("error_message")
    private String errorMessage;

    /**
     * A : Active
     * D : Delete
     */
    private String status;

    @TableField("created_date_")
    private Instant createdDate;

    @TableField("modified_date_")
    private Instant modifiedDate;
}
