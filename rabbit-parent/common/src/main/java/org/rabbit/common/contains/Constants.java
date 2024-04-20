package org.rabbit.common.contains;

/**
 * All constants.
 *
 * @author nine
 */
public class Constants {

    public static class Auth {

        public static final String AUTH_ENABLED = "authentication.enabled";
        public static final String AUTH_2FA_ENABLED = "authentication.2fa.enable";
        public static final String AUTH_2FA_EXPIRATION_MINUTES = "authentication.2fa.expiration.minutes";
        public static final String AUTH_ROOT_URL = "authentication.root.url";
        public static final String AUTH_LOGIN_URI = "authentication.login.uri";
        public static final String AUTH_PUBLIC_URL = "authentication.public.url";
        public static final String AUTH_REFRESH_TOKEN_URI = "authentication.refresh.token.uri";
    }

    public static class Jwt {

        public static final String SECRET = "jwt.secret";
        public static final String EXPIRATION_MINUTES = "jwt.expiration.minutes";
        public static final String REFRESH_EXPIRATION_MINUTES = "jwt.refresh.expiration.minutes";
    }

}
