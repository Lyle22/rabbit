package org.rabbit.login.security.oauth.microsoft;


import com.microsoft.aad.msal4j.AuthorizationRequestUrlParameters;
import com.microsoft.aad.msal4j.Prompt;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.ResponseMode;
import org.rabbit.common.OAuth2AuthenticationMethod;
import org.rabbit.exception.CustomException;
import org.rabbit.exception.ErrorCode;
import org.rabbit.login.security.oauth.AbstractOAuth2AuthenticationService;
import org.rabbit.login.security.oauth.OAuth2AuthenticationService;
import org.rabbit.mail.models.OAuth2SettingRequestDTO;
import org.rabbit.mail.models.OAuth2TokenResponse;
import org.rabbit.mail.repository.SystemSettingRepository;
import org.rabbit.utils.HttpRequestUtils;
import org.rabbit.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Microsoft OAuth2 Authentication Service
 *
 * @see <a href="https://learn.microsoft.com/en-us/azure/active-directory/develop/web-app-quickstart?pivots=devlang-java">Microsoft OAuth2 Authentication Document</a>
 */
@Slf4j
@Service
public class MsOAuth2AuthenticationServiceImpl extends AbstractOAuth2AuthenticationService
        implements OAuth2AuthenticationService<OAuth2TokenResponse> {
    private static final String OAUTH2_REFRESH_TOKEN = "oauth2_refresh_token";
    private static final String OAUTH2_ACCESS_TOKEN = "oauth2_access_token";
    private static final String MS_LOGIN_URL = "https://login.microsoftonline.com/";
    private static final String MS_LOGIN_ME_URL = "https://graph.microsoft.com/v1.0/me/";
    private static final String MS_OAUTH_TOKEN = "oauth2/v2.0/token";

    protected MsOAuth2AuthenticationServiceImpl(SystemSettingRepository systemSettingRepository) {
        super(systemSettingRepository);
    }

    @Override
    public String applyAuthorize(OAuth2SettingRequestDTO setting) {
        // state parameter to validate response from Authorization server and nonce parameter to validate idToken
        String state = "senderAddress=" + setting.getSenderAddress();
        String nonce = UUID.randomUUID().toString();
        try {
            PublicClientApplication pca = PublicClientApplication
                    .builder(setting.getClientId())
                    .authority(setting.getAuthority(MS_LOGIN_URL))
                    .build();
            AuthorizationRequestUrlParameters parameters =
                    AuthorizationRequestUrlParameters.builder(setting.getRedirectUri(), MsScopes.getScope())
                            .responseMode(ResponseMode.QUERY)
                            .prompt(Prompt.SELECT_ACCOUNT)
                            .state(state)
                            .nonce(nonce)
                            .build();
            return pca.getAuthorizationRequestUrl(parameters).toString();
        } catch (MalformedURLException e) {
            log.error("Failed to MalformedURLException, the message is {}", e.getMessage());
        }
        return null;
    }

    @Override
    public OAuth2TokenResponse createCredential(String authorizationCode, String senderAddress) {
        OAuth2SettingRequestDTO setting = getClientSecret(senderAddress);
        String scope = "Mail.Send User.Read";
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("client_id", setting.getClientId());
        dataMap.put("client_secret", setting.getClientSecret());
        dataMap.put("redirect_uri", setting.getRedirectUri());
        dataMap.put("code", authorizationCode);
        dataMap.put("grant_type", "authorization_code");
        dataMap.put("scope", scope);

        String httpResponse = null;
        try {
            String url = setting.getAuthority(MS_LOGIN_URL) + MS_OAUTH_TOKEN;
            httpResponse = HttpRequestUtils.postByForm(url, dataMap);
        } catch (IOException ioe) {
            log.error("An exception occurred while sending request to microsoft graph api, {} ", ioe.getMessage());
        }
        if (StringUtils.isNotBlank(httpResponse)) {
            OAuth2TokenResponse tokenResponse = JsonHelper.read(httpResponse, OAuth2TokenResponse.class);
            saveRefreshToken(senderAddress, tokenResponse.getRefreshToken());
            return tokenResponse;
        }
        log.error("Failed to obtain Credential when using microsoft graph api");
        return null;
    }

    private boolean checkAccessToken(String senderAddress, String accessToken) {
        String httpResponse = HttpRequestUtils.getByJson(MS_LOGIN_ME_URL, accessToken);
        if (StringUtils.isBlank(httpResponse)) {
            log.error("AccessToken is not available");
            return false;
        } else {
            log.info("AccessToken is available information is {}", httpResponse);
            return true;
        }
    }

    @Override
    public String getRefreshToken(String senderAddress) {
        Map<String, String> tokenMap = super.query(senderAddress, OAUTH2_REFRESH_TOKEN, Map.class);
        return tokenMap.get(OAUTH2_REFRESH_TOKEN);
    }

    @Override
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
                .orElseThrow(() -> new CustomException(ErrorCode.GLOBAL,
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

    @Override
    protected boolean saveRefreshToken(String senderAddress, String refreshToken) {
        if (log.isInfoEnabled()) {
            log.info("The refreshToken of Office 365 oauth2.0 is: {}", refreshToken);
        }
        Map<String, String> tokenMap = new HashMap<>(2);
        tokenMap.put(OAUTH2_REFRESH_TOKEN, refreshToken);
        // RefreshToken Expiration time defaults to 90 days
        return super.store(senderAddress, OAUTH2_REFRESH_TOKEN, JsonHelper.write(tokenMap));
    }

    @Override
    public OAuth2AuthenticationMethod getStrategyKey() {
        return OAuth2AuthenticationMethod.MICROSOFT_OFFICE_365;
    }
}

