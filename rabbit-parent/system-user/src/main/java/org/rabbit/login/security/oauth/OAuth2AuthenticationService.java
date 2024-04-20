package org.rabbit.login.security.oauth;

import org.rabbit.common.OAuth2AuthenticationMethod;
import org.rabbit.mail.models.OAuth2SettingRequestDTO;

/**
 * The interface of OAuth2 Authentication Service
 *
 * @author nine
 */
public interface OAuth2AuthenticationService<T> {

    /**
     * Get the OAuth2 identity authentication method
     */
    OAuth2AuthenticationMethod getStrategyKey();

    /**
     * Apply oAuth2 Authorize
     *
     * @param oAuth2SettingRequestDTO the information of OAuth2 identity
     * @return the url of OAuth2 identity authentication server for login
     */
    String applyAuthorize(OAuth2SettingRequestDTO oAuth2SettingRequestDTO);

    /**
     * Get credential
     * <p>When the user successfully logs in using OAuth2 and agrees to authorize access to the API, an authorization code will be returned</p>
     *
     * @param authorizationCode the authorization code
     * @param senderAddress     id of loginUser
     * @return the object of credential, return null if an exception occurs
     */
    T createCredential(String authorizationCode, String senderAddress);

    /**
     * Get a accessToken
     *
     * @param senderAddress id of loginUser
     * @return accessToken
     */
    String getAccessToken(String senderAddress);

    /**
     * Get a refreshToken
     *
     * @param senderAddress id of loginUser
     * @return RefreshToken
     */
    String getRefreshToken(String senderAddress);

}
