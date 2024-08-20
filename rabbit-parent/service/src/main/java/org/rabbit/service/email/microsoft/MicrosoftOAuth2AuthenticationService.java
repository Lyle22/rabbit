package org.rabbit.service.email.microsoft;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.rabbit.common.exception.EmailException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.common.utils.HttpRequestUtils;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.service.email.AbstractOAuthAuthenticationService;
import org.rabbit.service.email.models.OAuth2SettingRequestDTO;
import org.rabbit.service.email.models.OAuth2TokenResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Microsoft OAuth2 Authentication Service
 *
 * @see <a href="https://learn.microsoft.com/en-us/azure/active-directory/develop/web-app-quickstart?pivots=devlang-java">Microsoft OAuth2 Authentication Document</a>
 */
@Slf4j
@Service
public class MicrosoftOAuth2AuthenticationService extends AbstractOAuthAuthenticationService {

    private static final String MS_LOGIN_URL = "https://login.microsoftonline.com/";
    private static final String MS_OAUTH_TOKEN = "oauth2/v2.0/token";

    public String getRefreshToken(String senderAddress) {
        Map<String, String> tokenMap = super.query(senderAddress, OAUTH2_REFRESH_TOKEN, Map.class);
        return tokenMap.get(OAUTH2_REFRESH_TOKEN);
    }

    public String getAccessToken(String senderAddress) {
        OAuth2TokenResponse response = super.query(senderAddress, OAUTH2_ACCESS_TOKEN, OAuth2TokenResponse.class);
        if (null != response && response.getExpireTime().after(new Date())) {
            return response.getAccessToken();
        } else {
            OAuth2SettingRequestDTO setting = getClientSecret(senderAddress);
            return queryAccessToken(setting).getAccessToken();
        }
    }

    @Override
    protected OAuth2TokenResponse queryAccessToken(OAuth2SettingRequestDTO setting) {
        String refreshToken = Optional.ofNullable(getRefreshToken(setting.getSenderAddress()))
                .orElseThrow(() -> new EmailException(ErrorCode.GLOBAL,
                        "Missing refreshToken of agree authorization, Please confirm that the authorization credentials are valid"));
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("client_id", setting.getClientId());
        dataMap.put("client_secret", setting.getClientSecret());
        dataMap.put("refresh_token", refreshToken);
        dataMap.put("grant_type", "refresh_token");

        String httpResponse = null;
        try {
            String url = setting.getAuthority(MS_LOGIN_URL) + MS_OAUTH_TOKEN;
            httpResponse = HttpRequestUtils.postByForm(url, dataMap);
        } catch (IOException ioe) {
            log.error("Failed to obtain accessToken when use the refresh token to get a new access token",
                    ioe.getMessage());
        }

        if (StringUtils.isNotBlank(httpResponse)) {
            OAuth2TokenResponse tokenResponse = JsonHelper.read(httpResponse, OAuth2TokenResponse.class);
            tokenResponse.setExpireTime(DateUtils.addSeconds(new Date(), tokenResponse.getExpiresIn() - 3500));
            super.store(setting.getSenderAddress(), OAUTH2_ACCESS_TOKEN, JsonHelper.write(tokenResponse));
            // Why save it?
            // Because each request token will return a new accessToken and refreshToken, until the refreshToken exceeds the end-of-use period of 90 days
            saveRefreshToken(setting.getSenderAddress(), tokenResponse.getRefreshToken());
            return tokenResponse;
        } else {
            log.error("Failed to obtain accessToken when use the refresh token to get a new access token");
            log.error("This may be the reason why refresh Token expired or Credential become invalid of Microsoft");
            return null;
        }
    }

    protected boolean saveRefreshToken(String senderAddress, String refreshToken) {
        Map<String, String> tokenMap = new HashMap<>(2);
        tokenMap.put(OAUTH2_REFRESH_TOKEN, refreshToken);
        // RefreshToken Expiration time defaults to 90 days
        return super.store(senderAddress, OAUTH2_REFRESH_TOKEN, JsonHelper.write(tokenMap));
    }
}

