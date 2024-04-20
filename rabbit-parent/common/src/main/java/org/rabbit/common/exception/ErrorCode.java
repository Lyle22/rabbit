package org.rabbit.common.exception;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The enum Error code.
 */
public enum ErrorCode {

    /**
     * Global error code.
     */
    GLOBAL(-1),

    /**
     * Authentication error code.
     */
    AUTHENTICATION(400),

    /**
     * Jwt token expired error code.
     */
    JWT_TOKEN_EXPIRED(401),

    /**
     * Jwt token invalid error code.
     */
    JWT_TOKEN_INVALID(402);

    private final int errorCode;

    ErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets error code.
     *
     * @return the error code
     */
    @JsonValue
    public int getErrorCode() {
        return errorCode;
    }
}
