package org.rabbit.service.email;

import org.apache.commons.lang3.StringUtils;
import org.rabbit.service.email.models.OAuth2SettingRequestDTO;
import org.rabbit.service.email.models.SendEmailParam;

/**
 * The interface of send email
 *
 * @author nine rabbit
 */
public interface ISendEmailService {

    /**
     * match invoke service
     */
    boolean match(OAuth2SettingRequestDTO setting);

    boolean doSend(SendEmailParam param);

    String getAccessToken(String senderAddress);

    /**
     * Email format rule
     */
    String EMAIL_PATTERN = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    static boolean validateEmail(final String email) {
        if (StringUtils.isEmpty(email)) {
            return Boolean.FALSE;
        }
        return email.matches(EMAIL_PATTERN);
    }

    static String getSmtpServerHost(OAuthWay method) {
        switch (method) {
            case GOOGLE:
                return "smtp.gmail.com";
            case MICROSOFT_OFFICE_365:
                return "outlook.office365.com";
            default:
                return null;
        }
    }
}
