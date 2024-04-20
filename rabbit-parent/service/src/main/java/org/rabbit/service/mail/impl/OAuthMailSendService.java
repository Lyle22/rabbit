package org.rabbit.service.mail.impl;

import org.rabbit.service.mail.models.MailSendRequest;
import org.rabbit.service.mail.models.OAuth2SettingRequestDTO;

/**
 * The interface of mail sender service
 *
 * @author nine rabbit
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
