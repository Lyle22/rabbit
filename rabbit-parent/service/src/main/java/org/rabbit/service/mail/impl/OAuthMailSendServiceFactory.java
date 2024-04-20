package org.rabbit.service.mail.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rabbit.common.exception.CustomException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.entity.system.SystemSetting;
import org.rabbit.service.mail.AbstractOAuth2AuthenticationService;
import org.rabbit.service.mail.OAuth2AuthenticationMethod;
import org.rabbit.service.mail.models.MailSendRequest;
import org.rabbit.service.mail.models.OAuth2SettingRequestDTO;
import org.rabbit.service.system.ISystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * the class of mail service factory
 */
@Slf4j
@Service
public class OAuthMailSendServiceFactory {

    public static final String OAUTH2_CREDENTIAL = "oauth2_credential";

    @Value("${mail.default.from.address}")
    private String mailFrom;

    @Autowired
    List<OAuthMailSendService> oAuthMailSenderServices;

    @Autowired
    ISystemSettingService systemSettingRepository;

    /**
     * Build MailSenderService by email OAuth2 setting
     *
     * <br/>
     * <li>1. If parameters of the {@link MailSendRequest#getFromEmail()} is not null then use this
     * {@link MailSendRequest#getFromEmail()} MailSenderService implement class
     * </li>
     * <li>2. If not match first condition, then query the first setting of email OAuth2 to get MailSenderService implement class
     * </li>
     * <li>3. If above conditions does not match, finally to use the default MailSenderService implement class
     * {@link DefaultMailSendService}
     * </li>
     */
    public OAuthMailSendService build(MailSendRequest mailSendRequest) {
        OAuth2SettingRequestDTO setting = this.getSetting(mailSendRequest.getFromEmail());
        for (OAuthMailSendService oAuthMailSenderService : oAuthMailSenderServices) {
            if (oAuthMailSenderService.match(setting)) {
                log.debug("Using {} MailSendService, SendAddress is {}",
                        setting.getAuthenticationMethod(), setting.getSenderAddress());
                return oAuthMailSenderService;
            }
        }
        log.error("Missing OAuthMailSendService implement class, {}", JsonHelper.write(mailSendRequest));
        throw new CustomException(ErrorCode.GLOBAL, "Failed to find Oauth2.0 mail service");
    }

    /**
     * obtain default mail send service or specified service
     */
    public OAuth2SettingRequestDTO getSetting(String senderAddress) {
        if (StringUtils.isNotBlank(senderAddress)) {
            // 如已指定发件人地址
            Optional<OAuth2SettingRequestDTO> optional =
                    systemSettingRepository.findBySystemIdTypeAndSystemId(OAUTH2_CREDENTIAL, senderAddress)
                            .stream().sorted((a, b) -> b.getId().compareTo(a.getId()))
                            .map(setting -> {
                                return JsonHelper.read(setting.getJsonValue(), OAuth2SettingRequestDTO.class);
                            })
                            .filter(Objects::nonNull)
                            .findFirst();
            if (optional.isEmpty()) {
                log.error("Fail to query Email OAuth2.0 Setting when send email, SenderAddress is [{}]", senderAddress);
                return null;
            } else {
                return optional.get();
            }
        }

        OAuth2SettingRequestDTO setting = getFirstOAuth2Setting();
        if (null != setting) {
            return setting;
        } else {
            // 使用默认邮件配置
            return getOAuth2Setting(OAuth2AuthenticationMethod.DEFAULT, mailFrom);
        }
    }

    /**
     * TODO 当前默认逻辑：查询最新的OAUTH2_ACCESS_TOKEN配置记录，get first setting
     *
     * @return OAuth2SettingRequestDTO the first setting of email oauth2.0
     */
    private OAuth2SettingRequestDTO getFirstOAuth2Setting() {
        String refreshToken = AbstractOAuth2AuthenticationService.OAUTH2_REFRESH_TOKEN;
        Optional<OAuth2SettingRequestDTO> optional =
                systemSettingRepository.findBySystemIdType(refreshToken)
                        .stream().sorted((a, b) -> b.getId().compareTo(a.getId()))
                        .map(firstSetting -> {
                            // Filter the effective setting list of oauth2 access token
                            Map<String, String> map = JsonHelper.read(firstSetting.getJsonValue(), Map.class);
                            if (map.isEmpty() || null == map.get(refreshToken)) {
                                return null;
                            }
                            String senderAddress = firstSetting.getSystemId();
                            Optional<SystemSetting> op =
                                    systemSettingRepository.findBySystemIdTypeAndSystemId(OAUTH2_CREDENTIAL, senderAddress)
                                            .stream().sorted((a, b) -> b.getId().compareTo(a.getId())).findFirst();
                            if (op.isPresent()) {
                                return JsonHelper.read(op.get().getJsonValue(), OAuth2SettingRequestDTO.class);
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .findFirst();
        if (optional.isEmpty()) {
            log.warn("The system has enabled Email OAuth2.0 Setting, But does not set configuration. " +
                    "Please navigate setting page to setup value");
            return null;
        } else {
            return optional.get();
        }
    }

    private OAuth2SettingRequestDTO getOAuth2Setting(OAuth2AuthenticationMethod method, String senderAddress) {
        List<SystemSetting> list = systemSettingRepository.
                findBySystemIdTypeAndSystemId(OAUTH2_CREDENTIAL, senderAddress);
        if (CollectionUtils.isEmpty(list)) {
            throw new CustomException(ErrorCode.GLOBAL, String.format("The Email OAuth2 Settings of User(%s) " +
                    "does not exist. ", senderAddress));
        }
        List<OAuth2SettingRequestDTO> settingList = list.stream().map(record -> {
            return JsonHelper.read(record.getJsonValue(), OAuth2SettingRequestDTO.class);
        }).filter(e-> e.getAuthenticationMethod() == method).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(settingList)) {
            throw new CustomException(ErrorCode.GLOBAL, String.format("The Email OAuth2 Settings of User(%s) " +
                    "does not exist. ", senderAddress));
        }
        return settingList.stream().findFirst().get();
    }

}