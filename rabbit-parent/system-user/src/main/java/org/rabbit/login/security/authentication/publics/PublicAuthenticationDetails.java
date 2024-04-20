package org.rabbit.login.security.authentication.publics;

import lombok.Data;

import java.time.Instant;

/**
 * The type public authentication details.
 *
 * @author nine
 */
@Data
public class PublicAuthenticationDetails {

    private String shareId;

    private Instant jwtTokenExpiredAt;

}
