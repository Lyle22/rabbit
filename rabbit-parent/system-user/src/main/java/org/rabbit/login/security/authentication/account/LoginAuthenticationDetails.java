package org.rabbit.login.security.authentication.account;

import lombok.Data;

import java.time.Instant;

/**
 * The authentication details.
 *
 * @author nine rabbit
 */
@Data
public class LoginAuthenticationDetails {

    private String loginSessionId;

    private Instant jwtTokenExpiredAt;

    private String userEmail;

}
