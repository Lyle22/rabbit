package org.rabbit.mail.service;

import org.rabbit.exception.CustomException;
import org.rabbit.exception.ErrorCode;
import org.rabbit.mail.models.MailSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The class of mail send service
 * @author nine
 */
@Slf4j
@Service
public class MailSendService {
    private final OAuthMailSendServiceFactory oAuthMailSendServiceFactory;

    @Autowired
    public MailSendService(OAuthMailSendServiceFactory oAuthMailSendServiceFactory) {
        this.oAuthMailSendServiceFactory = oAuthMailSendServiceFactory;
    }

    /**
     * Pre-check Parameter
     *
     * @param mailSendRequest the object of mail
     * @return boolean Returns:true if the required parameters is exists
     */
    private boolean checkArgs(MailSendRequest mailSendRequest) {
        if (null == mailSendRequest.getTo() && mailSendRequest.getTos().size() == 0) {
            throw new CustomException(ErrorCode.GLOBAL, "Missing the parameter of receiver address");
        }
        if (StringUtils.isAllBlank(mailSendRequest.getText(), mailSendRequest.getTemplateId())) {
            throw new CustomException(ErrorCode.GLOBAL, "Missing body text of email");
        }
        if (StringUtils.isBlank(mailSendRequest.getSubject())) {
            throw new CustomException(ErrorCode.GLOBAL, "Missing subject of email");
        }
        return true;
    }

    public boolean sendText(MailSendRequest mailSendRequest) {
        checkArgs(mailSendRequest);
        return oAuthMailSendServiceFactory.build(mailSendRequest).sendText(mailSendRequest);
    }

    public boolean sendWithHtml(MailSendRequest mailSendRequest) {
        checkArgs(mailSendRequest);
        return oAuthMailSendServiceFactory.build(mailSendRequest).sendWithHtml(mailSendRequest);
    }

    public boolean sendWithAttachment(MailSendRequest mailSendRequest) {
        checkArgs(mailSendRequest);
        return oAuthMailSendServiceFactory.build(mailSendRequest).sendWithAttachment(mailSendRequest);
    }

}
