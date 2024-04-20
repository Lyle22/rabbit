package org.rabbit.login.security.authentication.auth2fa;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The type Login request.
 *
 * @author nine
 */
@Data
public class Login2faRequestDTO {

    private final String passcode;

    @JsonCreator
    public Login2faRequestDTO(@JsonProperty("passcode") String passcode) {
        this.passcode = passcode;
    }
}
