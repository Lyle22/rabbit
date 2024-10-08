package org.rabbit.common.contains;

public class RedisConstants {

    public static final String CACHE_SESSION_KEY = "cache:Session:";

    /**
     * Combination Session Key
     *
     * @param userId the user id
     * @param authenticationSessionId the session id of authentication
     * @return String the session key of this user
     */
    public static String combinationSessionKey(String userId, String authenticationSessionId) {
        return String.format("%s%s:%s", CACHE_SESSION_KEY, userId, authenticationSessionId);
    }

}
