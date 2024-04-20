package org.rabbit.login.security.oauth;

import org.rabbit.common.ResponseEnum;
import org.rabbit.exception.ClientException;
import org.rabbit.login.contants.Authority;
import org.rabbit.entity.system.SystemSetting;
import org.rabbit.mail.models.OAuth2SettingRequestDTO;
import org.rabbit.mail.models.OAuth2TokenResponse;
import org.rabbit.mail.repository.SystemSettingRepository;
import org.rabbit.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * The class of provide system-based OAuth2 services
 *
 * @author nine
 */
@Slf4j
@Service
public abstract class AbstractOAuth2AuthenticationService {
    public static final String OAUTH2_REFRESH_TOKEN = "oauth2_refresh_token";
    public static final String OAUTH2_ACCESS_TOKEN = "oauth2_access_token";
    private final SystemSettingRepository systemSettingRepository;

    @Autowired
    protected AbstractOAuth2AuthenticationService(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    /**
     * Save refreshToken of OAuth2.0
     *
     * @param senderAddress the sender mail of OAuth2.0
     * @param refreshToken  String of refreshToken
     * @return true or false
     */
    protected abstract boolean saveRefreshToken(String senderAddress, String refreshToken);

    /**
     * Query accessToken of OAuth2.0
     *
     * @param setting OAuth2.0 information
     * @return accessToken
     */
    protected abstract OAuth2TokenResponse queryAccessToken(OAuth2SettingRequestDTO setting);

    protected OAuth2SettingRequestDTO getClientSecret(String systemId) {
        SystemSetting systemSetting = getSettings(Authority.OAUTH2_CREDENTIAL, systemId);
        if (null == systemSetting) {
            throw new ClientException(ResponseEnum.FAIL, "The Email OAuth2 Settings of User does not exist. ");
        }
        return JsonHelper.read(systemSetting.getJsonValue(), OAuth2SettingRequestDTO.class);
    }

    protected SystemSetting getSettings(String systemIdType, String systemId) {
        if (StringUtils.isAllBlank(systemId, systemIdType)) {
            log.error("All parameter must be not null when store data of OAuth2.0");
            return null;
        }
        Optional<SystemSetting> optional =
                systemSettingRepository.findBySystemIdTypeAndSystemId(systemIdType, systemId)
                        .stream().sorted((a, b) -> b.getId().compareTo(a.getId())).findFirst();
        if (optional.isEmpty()) {
            return null;
        }
        return optional.get();
    }

    /**
     * storage data
     *
     * @param systemId     the systemId
     * @param systemIdType the type of systemId
     * @param storeData    the string of store
     * @return
     */
    protected boolean store(String systemId, String systemIdType, String storeData) {
        if (StringUtils.isAllBlank(systemId, systemIdType, storeData)) {
            log.error("All parameter must be not null when store data of OAuth2.0");
            return false;
        }
        SystemSetting record = getSettings(systemIdType, systemId);
        if (null == record) {
            SystemSetting systemSetting = new SystemSetting();
            systemSetting.setSystemId(systemId);
            systemSetting.setSystemIdType(systemIdType);
            systemSetting.setJsonValue(storeData);
            systemSetting.setName(systemIdType.trim() + "_" + systemId);
            systemSettingRepository.save(systemSetting);
        } else {
            record.setJsonValue(storeData);
            systemSettingRepository.save(record);
        }
        return true;
    }

    /**
     * query data from database
     *
     * @param systemId     the systemId
     * @param systemIdType the type of systemId
     * @return String of json format
     */
    protected <T> T query(String systemId, String systemIdType, Class<T> valueType) {
        if (StringUtils.isAllBlank(systemId, systemIdType)) {
            log.error("All parameter must be not null when query Email OAuth2.0 Setting [systemId, systemIdType]");
            return null;
        }
        SystemSetting record = getSettings(systemIdType, systemId);
        if (null != record) {
            return JsonHelper.read(record.getJsonValue(), valueType);
        }
        return null;
    }

}
