package org.rabbit.login.security.oauth.microsoft;

import java.util.HashSet;
import java.util.Set;

public class MsScopes {

    public static Set<String> getScope() {
        Set<String> scopes = new HashSet<>(8);
        scopes.add("offline_access");
        scopes.add("openid");
        scopes.add("profile");
        // scopes.add("SMTP.Send");
        // scopes.add("IMAP.AccessAsUser.All");
        scopes.add("Mail.Send");
        scopes.add("User.Read");
        scopes.add("email");
        return scopes;
    }

    public static Set<String> getExchangeOnlineScope() {
        Set<String> scopes = new HashSet<>(8);
        scopes.add("offline_access");
        scopes.add("openid");
        scopes.add("profile");
        scopes.add("SMTP.Send");
        scopes.add("User.Read");
        return scopes;
    }

}
