package org.rabbit.service.mail.google;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.rabbit.common.exception.EmailException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.common.utils.HttpRequestUtils;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.service.mail.AbstractOAuth2AuthenticationService;
import org.rabbit.service.mail.models.OAuth2SettingRequestDTO;
import org.rabbit.service.mail.models.OAuth2TokenResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Google OAuth2.0 Authentication Service
 *
 * <p> Use Google AuthorizationCode Flow to achieve OAuth2.0 Authentication process </p>
 *
 * @author Lyle
 * @see <a href="https://developers.google.com/identity/protocols/oauth2/web-server#offline">Google oauth2 identity</a>
 */
@Slf4j
@Service
public class GoogleOAuth2AuthenticationService extends AbstractOAuth2AuthenticationService {
    /**
     * the url of Refreshing an access token (offline access)
     */
    public static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";

    public String getRefreshToken(String senderAddress) {
        Map<String, String> tokenMap = super.query(senderAddress, OAUTH2_REFRESH_TOKEN, Map.class);
        return tokenMap.get(OAUTH2_REFRESH_TOKEN);
    }

    public String getAccessToken(String senderAddress) {
        OAuth2TokenResponse response = super.query(senderAddress, OAUTH2_ACCESS_TOKEN, OAuth2TokenResponse.class);
        if (null != response && response.getExpireTime().after(new Date())) {
            return response.getAccessToken();
        } else {
            OAuth2SettingRequestDTO setting = super.getClientSecret(senderAddress);
            return queryAccessToken(setting).getAccessToken();
        }
    }

    @Override
    protected OAuth2TokenResponse queryAccessToken(OAuth2SettingRequestDTO setting) {
        try {
            String refreshToken = Optional.ofNullable(getRefreshToken(setting.getSenderAddress()))
                    .orElseThrow(() -> new EmailException(ErrorCode.GLOBAL,
                            "Missing refreshToken of agree authorization, Please confirm that the authorization credentials are valid"));
            Map<String, String> dataMap = new HashMap<String, String>(4);
            dataMap.put("client_id", setting.getClientId());
            dataMap.put("client_secret", setting.getClientSecret());
            dataMap.put("refresh_token", refreshToken);
            dataMap.put("grant_type", "refresh_token");

            String jsonStr = HttpRequestUtils.postByForm(GOOGLE_TOKEN_URL, dataMap);
            if (StringUtils.isBlank(jsonStr)) {
                throw new EmailException(ErrorCode.GLOBAL,
                        "The accessToken does not have authorize access to mail services, Please recertification authorization");
            } else {
                OAuth2TokenResponse response = JsonHelper.read(jsonStr, OAuth2TokenResponse.class);
                response.setExpireTime(DateUtils.addSeconds(new Date(), response.getExpiresIn() - 3500));
                super.store(setting.getSenderAddress(), OAUTH2_ACCESS_TOKEN, JsonHelper.write(response));
                return response;
            }
        } catch (IOException ioe) {
            throw new EmailException(ErrorCode.GLOBAL, "Failed to obtain accessToken, Please check authorization information");
        }
    }
}
