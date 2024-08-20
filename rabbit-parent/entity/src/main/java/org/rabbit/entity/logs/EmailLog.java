package org.rabbit.entity.logs;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;
import org.rabbit.entity.base.BaseEntity;

/**
 * Email log
 *
 * @author nine rabbit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("log_email")
public class EmailLog extends BaseEntity {

    @TableField("email_server")
    private String emailServer;

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

}
