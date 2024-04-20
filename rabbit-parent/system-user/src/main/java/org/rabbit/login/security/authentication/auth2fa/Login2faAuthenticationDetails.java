package org.rabbit.login.security.authentication.auth2fa;

import lombok.Data;

import java.time.Instant;

@Data
public class Login2faAuthenticationDetails {

    private String loginSessionId;

    private Instant jwtTokenExpiredAt;

    private String userEmail;

}
