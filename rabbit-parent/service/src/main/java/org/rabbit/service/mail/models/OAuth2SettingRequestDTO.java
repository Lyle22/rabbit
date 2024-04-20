package org.rabbit.service.mail.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.rabbit.service.mail.OAuth2AuthenticationMethod;

import java.util.Date;

/**
 * The OAuth2.0 Setting
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OAuth2SettingRequestDTO {

    /**
     * Application (Client) ID
     */
    @NotNull
    String clientId;

    /**
     * The Client Secret
     */
    @NotNull
    String clientSecret;

    /**
     * Directory (Tenant) ID
     * <p>This property exists only for Microsoft OAuth authentication</p>
     */
    String tenantId;

    /**
     * The callback path provided to the OAuth2.0 authorization server after obtaining the authorization
     */
    @NotNull
    String redirectUri;

    /**
     * the OAuth2.0 authorization server.
     * Office 365 OAuth2 or Google OAuth2
     */
    @NotNull
    OAuth2AuthenticationMethod authenticationMethod;

    /**
     * 发件人
     */
    String senderAddress;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    Date expireTime;

    public String getAuthority(String url) {
        return  url + getTenantId() + "/";
    }

    String userId;

    String code;

    String state;

    String scope;

}
