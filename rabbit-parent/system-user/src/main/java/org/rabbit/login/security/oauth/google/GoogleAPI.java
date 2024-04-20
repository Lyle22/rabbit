package org.rabbit.login.security.oauth.google;

public class GoogleAPI {

    public static final String MAIL_ROOT_URL = "https://mail.google.com/";

    public static final String GMAIL_SEND_URL = "https://www.googleapis.com/auth/gmail.send";

    public static final String GMAIL_USER_PROFILE_URL = "https://gmail.googleapis.com/gmail/v1/users/${email}/profile";

    /**
     * the url of Refreshing an access token (offline access)
     */
    public static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    /**
     * Get the information of accessToken
     */
    public static final String GOOGLE_TOKENINFO_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=$" +
            "{accessToken}";
}
