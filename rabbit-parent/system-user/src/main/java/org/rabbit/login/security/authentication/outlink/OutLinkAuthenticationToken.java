package org.rabbit.login.security.authentication.outlink;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class OutLinkAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public OutLinkAuthenticationToken(String username, String password, List<GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
}
