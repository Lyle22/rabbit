package org.rabbit.service.logs.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.rabbit.common.contains.StatusValue;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.entity.logs.EmailLog;
import org.rabbit.service.email.models.MailSendRequest;
import org.rabbit.service.email.models.SendEmailParam;
import org.rabbit.service.logs.dao.EmailLogMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * @author nine rabbit
 */
@Slf4j
@Service
public class EmailLogService extends ServiceImpl<EmailLogMapper, EmailLog> {

    private final EmailLogMapper emailLogMapper;

    public EmailLogService(EmailLogMapper emailLogMapper) {
        this.emailLogMapper = emailLogMapper;
    }

    public EmailLog create(SendEmailParam param, MailSendRequest mailSendRequest) {
        EmailLog log = new EmailLog();
        log.setEmailServer(param.getFromEmail());
        log.setTos(String.join(",", param.getTos()));
        log.setCcs(String.join(",", param.getCcs()));
        log.setBcc(String.join(",", param.getBcc()));
        log.setSubject(param.getSubject());
        log.setBody(param.getMainBodyText());
        log.setStatus(StatusValue.PENDING_ACTIVE);
        log.setCreatedDate(Instant.now());
        log.setModifiedDate(Instant.now());
        try {
            // 注意无法转换的问题
            String content = JsonHelper.write(mailSendRequest);
            if (content != null && content.length() < 65535/2) {
                log.setErrorMessage(content);
            }
        } catch (Exception e) {
            System.out.println("transform request parameters was error");
        }
        emailLogMapper.insert(log);
        return log;
    }

    public void update(String id, boolean sendResult) {
        EmailLog log = emailLogMapper.selectById(id);
        if (null != log && sendResult) {
            log.setStatus(StatusValue.ACTIVE);
            log.setModifiedDate(Instant.now());
            emailLogMapper.updateById(log);
        }
    }

    public void doFail(String id, String errorMessage) {
        EmailLog log = emailLogMapper.selectById(id);
        if (null != log) {
            log.setErrorMessage(errorMessage);
            log.setStatus(StatusValue.DELETE);
            log.setModifiedDate(Instant.now());
            emailLogMapper.insert(log);
        }
    }

}
