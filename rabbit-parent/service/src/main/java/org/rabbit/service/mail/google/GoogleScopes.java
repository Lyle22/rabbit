package org.rabbit.service.mail.google;

import java.util.Arrays;
import java.util.List;

/**
 * The type of google api scope
 */
public class GoogleScopes {

    public static List<String> getDefault() {
        return Arrays.asList(GoogleAPI.MAIL_ROOT_URL);
    }

    public static List<String> getGmailScope() {
        return Arrays.asList(GoogleAPI.MAIL_ROOT_URL);
    }

    public static boolean isContainGmailScope(String accessScope) {
        return accessScope.contains(GoogleAPI.MAIL_ROOT_URL);
    }

}
