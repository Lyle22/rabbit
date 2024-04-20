package org.rabbit.login.config;

import org.rabbit.common.contains.Constants;
import org.rabbit.exception.AuthException;
import org.rabbit.common.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

/**
 * Auth configurations.
 *
 * @author nine
 */
@Configuration
public class AuthConfigs {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthConfigs.class);

    /**
     * Whether auth enabled.
     */
    @Value("${" + Constants.Auth.AUTH_ENABLED + ":false}")
    private boolean authEnabled;

    @Value("${" + Constants.Auth.AUTH_2FA_ENABLED + ":false}")
    private boolean isRequired2FA;

    @Value("${" + Constants.Auth.AUTH_2FA_EXPIRATION_MINUTES + ":60}")
    private int auth2faExpirationMinutes;

    @Value("${" + Constants.Auth.AUTH_ROOT_URL + "}")
    private String authRootUrl;

    @Value("${" + Constants.Auth.AUTH_LOGIN_URI + "}")
    private String authLoginUrl;

    @Value("${" + Constants.Auth.AUTH_PUBLIC_URL + "}")
    private String authPublicUrl;

    @Value("${" + Constants.Auth.AUTH_REFRESH_TOKEN_URI + "}")
    private String authRefreshTokenUrl;

    @Value("${" + Constants.Jwt.SECRET + "}")
    private String jwtSecret;

    @Value("${" + Constants.Jwt.REFRESH_EXPIRATION_MINUTES + ":30}")
    private int jwtRefreshExpirationMinutes;

    @Value("${" + Constants.Jwt.EXPIRATION_MINUTES + ":60}")
    private int jwtExpirationMinutes;


    /**
     * Validate auth config.
     *
     * @throws LoginException If the config is not valid.
     */
    @PostConstruct
    public void validate() throws LoginException {
        if (!authEnabled) {
            return;
        }
        if (StringUtils.isEmpty(authRootUrl)) {
            throw new AuthException(ErrorCode.GLOBAL, "Missing parameters [ AUTH_LOGIN_URL ]");
        }
    }

    public boolean isAuthEnabled() {
        return authEnabled;
    }

    public Integer getAuth2faExpirationMinutes() {
        return auth2faExpirationMinutes;
    }

    public String getAuthRootUrl() {
        return authRootUrl;
    }

    public String getAuthLoginUrl() {
        return authLoginUrl;
    }

    public String getAuthPublicUrl() {
        return authPublicUrl;
    }

    public String getAuthRefreshTokenUrl() {
        return authRefreshTokenUrl;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public int getJwtRefreshExpirationMinutes() {
        return jwtRefreshExpirationMinutes;
    }

    public int getJwtExpirationMinutes() {
        return jwtExpirationMinutes;
    }

    public boolean isRequired2FA() {
        return isRequired2FA;
    }
}
