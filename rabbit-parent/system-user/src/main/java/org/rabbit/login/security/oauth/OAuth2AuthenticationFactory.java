package org.rabbit.login.security.oauth;

import org.rabbit.common.OAuth2AuthenticationMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * the type of OAuth2AuthenticationService factory
 *
 * @author nine
 */
@Slf4j
@Service
public class OAuth2AuthenticationFactory {
    @Autowired
    List<OAuth2AuthenticationService> serviceList;

    public OAuth2AuthenticationService build(OAuth2AuthenticationMethod method) {
        if (serviceList == null || serviceList.size() == 0) {
            log.error("The class of OAuth2AuthenticationService missing implementation class");
        }
        for (OAuth2AuthenticationService service : serviceList) {
            if (OAuth2AuthenticationMethod.DEFAULT == method) {
                continue;
            }
            if (service.getStrategyKey() == method) {
                return service;
            }
        }
        throw new RuntimeException("509");
    }

    /**
     * The default service is oauth2 google service provider
     */
    public OAuth2AuthenticationService defaultBuild() {
        return serviceList.stream()
                .filter(e -> e.getStrategyKey() == OAuth2AuthenticationMethod.GOOGLE)
                .findFirst()
                .get();
    }

}
