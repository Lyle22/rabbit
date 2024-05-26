package org.rabbit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author nine rabbit
 */
@Configuration
public class SystemAuthConfiguration {

    public static final String SWAGGER_WHITELIST_URL = "/swagger-resources/**";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static String API_ROOT_URL;
    public static String AUTHENTICATION_PUBLIC_URL;
    public static String AUTHENTICATION_LOGIN_URL;
    public static String REFRESH_TOKEN_URL;
    public static String OUT_LINK_URL;

    public static String PASSWORD_FORGET_URI;
    public static String PASSWORD_CONFIRMRESET_URI;
    public static String PASSWORD_RESET_URI;


    @Value("${api.root.url:/api/**}")
    private void setApiRootUrl(String apiRootUrl) {
        API_ROOT_URL = apiRootUrl;
    }

    @Value("${authentication.login.uri:/api/auth/login}")
    private void setAuthenticationLoginUrl(String authenticationLoginUrl) {
        AUTHENTICATION_LOGIN_URL = authenticationLoginUrl;
    }

    @Value("${authentication.refresh.token.uri:/api/refresh/token}")
    public void setRefreshTokenUrl(String refreshTokenUrl) {
        REFRESH_TOKEN_URL = refreshTokenUrl;
    }

    @Value("${authentication.public.url:/public/**}")
    private void setAuthenticationPublicUrl(String authenticationPublicUrl) {
        AUTHENTICATION_PUBLIC_URL = authenticationPublicUrl;
    }

    @Value("${outlink.url}")
    public void setOutLinkUrl(String outLinkUrl) {
        OUT_LINK_URL = outLinkUrl;
    }

    @Value("${password.forget.uri}")
    public void setPasswordForgetUri(String passwordForgetUri) {
        PASSWORD_FORGET_URI = passwordForgetUri;
    }

    @Value("${password.confirmReset.uri}")
    public void setPasswordConfirmresetUri(String passwordConfirmresetUri) {
        PASSWORD_CONFIRMRESET_URI = passwordConfirmresetUri;
    }

    @Value("${password.reset.uri}")
    public void setPasswordResetUri(String passwordResetUri) {
        PASSWORD_RESET_URI = passwordResetUri;
    }

}
