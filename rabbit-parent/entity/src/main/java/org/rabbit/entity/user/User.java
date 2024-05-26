package org.rabbit.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.rabbit.entity.base.BaseEntity;

/**
 * User entity
 *
 * @author ninerabbit
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@TableName("user")
public class User extends BaseEntity {

    private String userId;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String tenantId;

    private String source;

    /**
     * A: active
     * D: un-active
     */
    private String status;
}
