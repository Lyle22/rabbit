package org.rabbit.login.security.authentication.outlink;

import org.rabbit.login.config.OutLinkProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * the type of out link server authentication provider
 *
 * @author nine
 */
@Component
@Slf4j
public class OutLinkAuthenticationProvider implements AuthenticationProvider {

    private OutLinkProperties outLinkProperties;

    public OutLinkAuthenticationProvider(OutLinkProperties outLinkProperties) {
        this.outLinkProperties = outLinkProperties;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String serverName = authentication.getName();
        String serverKey = (String) authentication.getCredentials();
        String requestedServerKey = outLinkProperties.getKey().get(serverName);
        if (!serverKey.equals(requestedServerKey)) {
            throw new BadCredentialsException("The server key is not registered");
        }
        List<GrantedAuthority> authorities = Stream.of("ROLE_USER").map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new OutLinkAuthenticationToken(serverName, serverKey, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (OutLinkAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
