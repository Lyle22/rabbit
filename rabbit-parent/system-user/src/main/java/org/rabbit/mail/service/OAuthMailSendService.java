package org.rabbit.mail.service;

import org.rabbit.mail.models.MailSendRequest;
import org.rabbit.mail.models.OAuth2SettingRequestDTO;

/**
 * The interface of mail sender service
 */
public interface OAuthMailSendService {

    /**
     * match invoke service
     */
    boolean match(OAuth2SettingRequestDTO setting);

    boolean sendText(MailSendRequest mailSendRequest);

    boolean sendWithHtml(MailSendRequest mailSendRequest);

    boolean sendWithAttachment(MailSendRequest mailSendRequest);

}
