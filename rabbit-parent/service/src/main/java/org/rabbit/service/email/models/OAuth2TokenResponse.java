package org.rabbit.service.email.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

/**
 * The Response when get accessToken of google
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OAuth2TokenResponse {

    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("expires_in")
    int expiresIn;

    @JsonProperty("token_type")
    String tokenType;

    @JsonProperty("scope")
    String scope;

    @JsonProperty("refresh_token")
    String refreshToken;

    @JsonProperty("ext_expires_in")
    int extExpiresIn;

    @JsonProperty("id_token")
    String idToken;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonProperty("expire_time")
    Date expireTime;
}
