package org.rabbit.service.email.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rabbit.common.exception.EmailException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.configuration.EmailConfiguration;
import org.rabbit.entity.system.SystemConfig;
import org.rabbit.service.email.ISendEmailService;
import org.rabbit.service.email.OAuthWay;
import org.rabbit.service.email.models.MailSendRequest;
import org.rabbit.service.email.models.OAuth2Constant;
import org.rabbit.service.email.models.OAuth2SettingRequestDTO;
import org.rabbit.service.system.ISystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The class for send email service factory
 *
 * @author nine rabbit
 */
@Slf4j
@Service
@ConditionalOnBean(value = EmailConfiguration.class)
public class SendEmailServiceFactory {

    @Autowired
    List<ISendEmailService> sendEmailServices;

    @Autowired
    ISystemConfigService systemSettingService;

    /**
     * To build send email service
     */
    public ISendEmailService build(MailSendRequest mailSendRequest) {
        OAuth2SettingRequestDTO setting = this.getSetting(mailSendRequest.getFromEmail());
        if (setting == null) {
            throw new EmailException(ErrorCode.GLOBAL, "Invalid FromEmail Address");
        }
        // reassign value
        mailSendRequest.setFromEmail(setting.getSenderAddress());
        for (ISendEmailService sendEmailService : sendEmailServices) {
            if (sendEmailService.match(setting)) {
                log.debug("Using [{}] MailSendService, Send server address is [{}]",
                        setting.getAuthenticationMethod(), setting.getSenderAddress());
                return sendEmailService;
            }
        }
        log.error("Missing send mail service implement class, maybe doesn't set up Email OAuth2.0 Setting. [{}]", JsonHelper.write(mailSendRequest));
        throw new EmailException(ErrorCode.GLOBAL, "Failed to find Oauth2.0 mail service");
    }

    /**
     * Query the email configuration based on the sender's email address. If it is empty, use the default value.
     * 根据发件人邮箱地址查询邮箱配置，如果为空则取默认值
     *
     * @param senderAddress the specified sender email address
     * @return OAuth2SettingRequestDTO the Email OAuth2.0 Setting
     */
    public OAuth2SettingRequestDTO getSetting(String senderAddress) {
        // 如已指定发件人地址
        if (StringUtils.isNotBlank(senderAddress)) {
            Optional<OAuth2SettingRequestDTO> optional =
                    systemSettingService.findBySystemTypeAndSystemId(OAuth2Constant.OAUTH2_CREDENTIAL, senderAddress)
                            .stream()
                            .map(setting -> JsonHelper.read(setting.getJsonConfig(), OAuth2SettingRequestDTO.class))
                            .filter(Objects::nonNull)
                            .findFirst();
            return optional.orElse(null);
        }
        // 如果获取不到最新有效的OAuth2.0 Email Setting 则使用默认邮件配置
        OAuth2SettingRequestDTO setting = getFirstOAuth2Setting();
        return Objects.requireNonNullElseGet(setting, () ->
                getOAuth2Setting(OAuthWay.DEFAULT, EmailConfiguration.DEFAULT_SENDER_ADDRESS));
    }

    /**
     * Get First Setting
     * 当前默认逻辑：查询最新的OAUTH2_ACCESS_TOKEN配置记录
     *
     * @return OAuth2SettingRequestDTO the first setting of email oauth2.0
     */
    private OAuth2SettingRequestDTO getFirstOAuth2Setting() {
        String refreshToken = OAuth2Constant.OAUTH2_REFRESH_TOKEN;
        Optional<OAuth2SettingRequestDTO> optional = systemSettingService.findBySystemType(refreshToken)
                .stream()
                .map(firstSetting -> {
                    // Filter the effective setting list of oauth2 access token
                    Map<String, String> map = JsonHelper.read(firstSetting.getJsonConfig(), Map.class);
                    if (map.isEmpty() || StringUtils.isBlank(map.get(refreshToken))) {
                        return null;
                    }
                    String senderAddress = firstSetting.getSystemId();
                    Optional<SystemConfig> op =
                            systemSettingService.findBySystemTypeAndSystemId(OAuth2Constant.OAUTH2_CREDENTIAL, senderAddress)
                                    .stream()
                                    .findFirst();
                    return op.map(baseSystemSetting -> JsonHelper.read(baseSystemSetting.getJsonConfig(), OAuth2SettingRequestDTO.class)).orElse(null);
                })
                .filter(Objects::nonNull)
                .findFirst();
        return optional.orElse(null);
    }

    private OAuth2SettingRequestDTO getOAuth2Setting(OAuthWay method, String senderAddress) {
        List<SystemConfig> list = systemSettingService.findBySystemTypeAndSystemId(OAuth2Constant.OAUTH2_CREDENTIAL, senderAddress);
        if (CollectionUtils.isEmpty(list)) {
            throw new EmailException(ErrorCode.GLOBAL, String.format("The email oauth2.0 configuration of User(%s) " +
                    "does not exist. ", senderAddress));
        }
        List<OAuth2SettingRequestDTO> settingList = list.stream().map(
                        os -> JsonHelper.read(os.getJsonConfig(), OAuth2SettingRequestDTO.class)
                ).filter(e -> e.getAuthenticationMethod() == method)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(settingList)) {
            throw new EmailException(ErrorCode.GLOBAL, String.format("The email oauth2.0 configuration of User(%s) " +
                    "does not exist. ", senderAddress));
        }
        return settingList.stream().findFirst().get();
    }

}