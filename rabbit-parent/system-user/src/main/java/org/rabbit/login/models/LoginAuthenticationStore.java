package org.rabbit.login.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * the type of login auth store
 *
 * @author nine rabbit
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginAuthenticationStore {

    private String loginSessionId;

    private String authSessionId;

    private boolean enabled;

    private Date expiredDate;

    private String passcode;

    private String userId;
    
}
