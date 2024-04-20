package org.rabbit.login.security.jwtrefresh;

import lombok.Data;

import java.time.Instant;

@Data
public class JwtRefreshAuthenticationDetails {
    private String loginSessionId;
    private Instant jwtTokenExpiredAt;
}
