package org.rabbit.service.mail.google;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.rabbit.common.exception.CustomException;
import org.rabbit.common.exception.ErrorCode;
import org.rabbit.common.utils.HttpRequestUtils;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.service.mail.AbstractOAuth2AuthenticationService;
import org.rabbit.service.mail.OAuth2AuthenticationMethod;
import org.rabbit.service.mail.OAuth2AuthenticationService;
import org.rabbit.service.mail.models.OAuth2SettingRequestDTO;
import org.rabbit.service.mail.models.OAuth2TokenResponse;
import org.rabbit.service.system.ISystemSettingService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Google OAuth2.0 Authentication Service
 *
 * <p> Use Google AuthorizationCode Flow to achieve OAuth2.0 Authentication process </p>
 *
 * @see <a href="https://developers.google.com/identity/protocols/oauth2/web-server#offline">Google oauth2 identity</a>
 */
@Slf4j
@Service
public class GoogleOAuth2AuthenticationServiceImpl extends AbstractOAuth2AuthenticationService
        implements OAuth2AuthenticationService<OAuth2TokenResponse> {

    public static final String OAUTH2_REFRESH_TOKEN = "oauth2_refresh_token";
    public static final String OAUTH2_ACCESS_TOKEN = "oauth2_access_token";

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "oauth_tokens/";

    protected GoogleOAuth2AuthenticationServiceImpl(ISystemSettingService systemSettingRepository) {
        super(systemSettingRepository);
    }

    @Override
    public String applyAuthorize(OAuth2SettingRequestDTO oAuth2Setting) {
        // build AuthorizationCode Flow
        GoogleAuthorizationCodeFlow flow = buildAuthorizationCodeFlow(oAuth2Setting);

        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
                // Parameters required when Google redirects the callback method
                .setState("senderAddress=" + oAuth2Setting.getSenderAddress())
                .setRedirectUri(oAuth2Setting.getRedirectUri());
        String url = authorizationUrl.build();
        Preconditions.checkNotNull(url);
        return url;
    }

    private GoogleAuthorizationCodeFlow buildAuthorizationCodeFlow(OAuth2SettingRequestDTO oAuth2Setting) {
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            File tokenFile = new File(TOKENS_DIRECTORY_PATH + oAuth2Setting.getSenderAddress());
            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
                    oAuth2Setting.getClientId(), oAuth2Setting.getClientSecret(), GoogleScopes.getGmailScope())
                    // .setDataStoreFactory(new FileDataStoreFactory(tokenFile))
                    .setAccessType("offline")
                    .build();
            return flow;
        } catch (GeneralSecurityException gex) {
            String errorMsg = " Apply credential of google oauth2.0 fail";
            log.error(errorMsg + " , the message is : {}", gex.getMessage());
            throw new CustomException(ErrorCode.GLOBAL, errorMsg, gex.getStackTrace());
        } catch (IOException ioe) {
            String errorMsg = " Apply credential of google oauth2.0 occur IO error";
            log.error(errorMsg + " , the message is : {}", ioe.getMessage());
            throw new CustomException(ErrorCode.GLOBAL, errorMsg, ioe.getStackTrace());
        }
    }

    @Override
    public OAuth2TokenResponse createCredential(String authorizationCode, String senderAddress) {
        try {
            OAuth2SettingRequestDTO oAuth2Setting = getClientSecret(senderAddress);
            // Exchange an authorization code for a refresh token and an access token
            GoogleAuthorizationCodeFlow flow = buildAuthorizationCodeFlow(oAuth2Setting);

            TokenResponse response = flow.newTokenRequest(authorizationCode)
                    .setRedirectUri(oAuth2Setting.getRedirectUri())
                    .execute();
            Credential credential = flow.createAndStoreCredential(response,
                    senderAddress+ DateFormatUtils.format(new Date(), "yyyyMMdd"));
            // We should be Pre-check whether the authorization is correct ?
            checkAccessToken(senderAddress, credential.getAccessToken());
            checkGmailScope(credential.getAccessToken());
            // store refreshToken
            saveRefreshToken(senderAddress, credential.getRefreshToken());

            OAuth2TokenResponse tokenResponse = new OAuth2TokenResponse();
            tokenResponse.setAccessToken(credential.getAccessToken());
            tokenResponse.setRefreshToken(credential.getRefreshToken());
            tokenResponse.setExpiresIn(Math.toIntExact(credential.getExpiresInSeconds()));
            return tokenResponse;
        } catch (IOException ioe) {
            log.error(" Apply credential of google oauth2.0 occur IO error, the message is : {} ", ioe.getMessage());
            throw new CustomException(ErrorCode.GLOBAL, "Apply credential of google oauth2.0 fail");
        }
    }

    private boolean checkAccessToken(String senderAddress, String accessToken) {
        OAuth2SettingRequestDTO setting = super.getClientSecret(senderAddress);
        // send an request url of query user profile
        String url = GoogleAPI.GMAIL_USER_PROFILE_URL.replace("${email}", setting.getSenderAddress());
        String response = HttpRequestUtils.getByJson(url, accessToken);
        if (StringUtils.isBlank(response)) {
            throw new CustomException(ErrorCode.GLOBAL,
                    "Google OAuth2 Authentication fail, Please check Google OAuth2 Settings");
        }
        if (log.isInfoEnabled()) {
            log.info("Validation Google OAuth2 Authentication was success, the response is:\n{}", response);
        }
        return true;
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
        try {
            String refreshToken = Optional.ofNullable(getRefreshToken(setting.getSenderAddress()))
                    .orElseThrow(() -> new CustomException(ErrorCode.GLOBAL,
                            "Missing refreshToken of agree authorization, Please confirm that the authorization credentials are valid"));
            Map<String, String> dataMap = new HashMap<String, String>(4);
            dataMap.put("client_id", setting.getClientId());
            dataMap.put("client_secret", setting.getClientSecret());
            dataMap.put("refresh_token", refreshToken);
            dataMap.put("grant_type", "refresh_token");

            String jsonStr = HttpRequestUtils.postByForm(GoogleAPI.GOOGLE_TOKEN_URL, dataMap);
            if (StringUtils.isBlank(jsonStr)) {
                throw new CustomException(ErrorCode.GLOBAL,
                        "The accessToken does not have authorize access to mail services, Please recertification " +
                                "authorization");
            } else {
                OAuth2TokenResponse response = JsonHelper.read(jsonStr, OAuth2TokenResponse.class);
                response.setExpireTime(DateUtils.addSeconds(new Date(), response.getExpiresIn() - 3500));
                super.store(setting.getSenderAddress(), OAUTH2_ACCESS_TOKEN, JsonHelper.write(response));
                return response;
            }
        } catch (IOException ioe) {
            throw new CustomException(ErrorCode.GLOBAL,
                    "Failed to obtain accessToken, Please check authorization information");
        }
    }

    /**
     * Check if access scope covers Gmail API ?
     * @param accessToken the information of accessToken
     */
    private boolean checkGmailScope(String accessToken) {
        String url = GoogleAPI.GOOGLE_TOKENINFO_URL.replace("${accessToken}", accessToken);
        String jsonStr = HttpRequestUtils.getByJson(url);
        if (StringUtils.isBlank(jsonStr)) {
            return false;
        }
        Map<String, String> dataMap = JsonHelper.read(jsonStr, Map.class);
        return GoogleScopes.isContainGmailScope(dataMap.get("scope"));
    }

    private Credential loadCredential(OAuth2SettingRequestDTO oAuth2Setting) {
        try {
            GoogleAuthorizationCodeFlow flow = buildAuthorizationCodeFlow(oAuth2Setting);
            // Check if the user's credentials are known
            return flow.loadCredential(oAuth2Setting.getSenderAddress());
        } catch (IOException ioe) {
            log.error("Load credential of google oauth2.0 occur IO error, the message is : {} ", ioe.getMessage());
            throw new CustomException(ErrorCode.GLOBAL, "Load credential of google oauth2.0 fail");
        }
    }

    @Override
    protected boolean saveRefreshToken(String senderAddress, String refreshToken) {
        if (log.isInfoEnabled()) {
            log.info("The refreshToken of google oauth2.0 is: \n {}", refreshToken);
        }
        Map<String, String> tokenMap = new HashMap<>(2);
        tokenMap.put(OAUTH2_REFRESH_TOKEN, refreshToken);
        return super.store(senderAddress, OAUTH2_REFRESH_TOKEN, JsonHelper.write(tokenMap));
    }

    @Override
    public OAuth2AuthenticationMethod getStrategyKey() {
        return OAuth2AuthenticationMethod.GOOGLE;
    }
}
