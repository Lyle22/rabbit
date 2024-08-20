package org.rabbit.service.email;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rabbit.common.exception.EmailException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.entity.system.SystemConfig;
import org.rabbit.service.email.models.OAuth2Constant;
import org.rabbit.service.email.models.OAuth2SettingRequestDTO;
import org.rabbit.service.email.models.OAuth2TokenResponse;
import org.rabbit.service.system.ISystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The class of provide system-based OAuth2 authentication service
 *
 * @author nine rabbit
 */
@Slf4j
@Service
public abstract class AbstractOAuthAuthenticationService {

    public static final String OAUTH2_REFRESH_TOKEN = "oauth2_refresh_token";
    public static final String OAUTH2_ACCESS_TOKEN = "oauth2_access_token";

    @Autowired
    private ISystemConfigService systemSettingService;

    /**
     * Query accessToken of OAuth2.0
     *
     * @param setting OAuth2.0 information
     * @return accessToken
     */
    protected abstract OAuth2TokenResponse queryAccessToken(OAuth2SettingRequestDTO setting);

    protected OAuth2SettingRequestDTO getClientSecret(String systemId) {
        SystemConfig systemConfig = query(OAuth2Constant.OAUTH2_CREDENTIAL, systemId);
        if (null == systemConfig) {
            throw new EmailException(ErrorCode.GLOBAL, "The Email OAuth2 Settings of User does not exist.");
        }
        return JsonHelper.read(systemConfig.getJsonConfig(), OAuth2SettingRequestDTO.class);
    }

    protected SystemConfig query(String systemType, String systemId) {
        if (StringUtils.isAllBlank(systemId, systemType)) {
            log.error("All parameter must be not null when store data of OAuth2.0");
            return null;
        }
        return systemSettingService.findBySystemTypeAndSystemId(systemType, systemId)
                .stream().min((a, b) -> b.getId().compareTo(a.getId()))
                .orElse(null);
    }

    /**
     * storage data
     *
     * @param systemId   the systemId
     * @param systemType the type of system
     * @param storeData  the string of store
     * @return boolean
     */
    protected boolean store(String systemId, String systemType, String storeData) {
        if (StringUtils.isAllBlank(systemId, systemType, storeData)) {
            log.error("All parameter must be not null when store data of OAuth2.0");
            return false;
        }
        SystemConfig record = query(systemType, systemId);
        if (null == record) {
            SystemConfig systemConfig = new SystemConfig();
            systemConfig.setSystemId(systemId);
            systemConfig.setSystemType(systemType);
            systemConfig.setJsonConfig(storeData);
            systemConfig.setName(systemType.trim() + "_" + systemId);
            systemSettingService.save(systemConfig);
        } else {
            record.setJsonConfig(storeData);
            systemSettingService.save(record);
        }
        return true;
    }

    /**
     * query data from database
     *
     * @param systemId   the systemId
     * @param systemType the system type
     * @return String of json format
     */
    protected <T> T query(String systemId, String systemType, Class<T> valueType) {
        if (StringUtils.isAllBlank(systemId, systemType)) {
            log.error("All parameter must be not null when query Email OAuth2.0 Setting [systemId, systemType]");
            return null;
        }
        SystemConfig systemConfig = query(systemType, systemId);
        if (null != systemConfig) {
            return JsonHelper.read(systemConfig.getJsonConfig(), valueType);
        }
        return null;
    }

}
