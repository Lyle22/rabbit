package org.rabbit.login.service;

import org.rabbit.common.contains.RedisConstants;
import org.rabbit.login.models.LoginAuthenticationStore;
import org.rabbit.common.utils.JsonHelper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

/**
 * the type of cache authentication store service
 *
 * @author nine rabbit
 */
@Service
@Slf4j
public class LoginAuthenticationStoreService {

    private final RedisTemplate<String, String> redisTemplate;

    public LoginAuthenticationStoreService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 登录成功之后存储Login数据
     *
     * @param session the authentication store information
     */
    public void storeSession(@NonNull LoginAuthenticationStore session) {
        String userSessionKey = RedisConstants.combinationSessionKey(session.getUserId(), session.getLoginSessionId());
        redisTemplate.opsForValue().set(userSessionKey, JsonHelper.write(session));
        redisTemplate.expireAt(userSessionKey, session.getExpiredDate().toInstant());
    }

    public LoginAuthenticationStore getSession(@NonNull String loginSessionId, @NonNull String userId) {
        String userSessionKey = RedisConstants.combinationSessionKey(userId, loginSessionId);
        String sessionJson = redisTemplate.opsForValue().get(userSessionKey);
        if (StringUtils.isBlank(sessionJson)) {
            throw new AuthenticationCredentialsNotFoundException("Invalid session id!");
        }
        return JsonHelper.read(sessionJson, LoginAuthenticationStore.class);
    }

    public void deleteSession(@NonNull String loginSessionId, @NonNull String userId) {
        String userSessionKey = RedisConstants.combinationSessionKey(userId, loginSessionId);
        redisTemplate.delete(userSessionKey);
    }

}
