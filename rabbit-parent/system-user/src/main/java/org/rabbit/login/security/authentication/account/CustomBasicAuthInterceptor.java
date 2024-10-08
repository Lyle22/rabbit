package org.rabbit.login.security.authentication.account;

import org.rabbit.common.contains.ResponseEnum;
import org.rabbit.common.exception.ClientException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The type Custom basic auth interceptor.
 *
 * @author nine rabbit
 */
public class CustomBasicAuthInterceptor implements Interceptor {
    /**
     * The Token.
     */
    protected String token;

    /**
     * Instantiates a new Custom basic auth interceptor.
     *
     * @param username the username
     * @param password the password
     */
    public CustomBasicAuthInterceptor(String username, String password) {
        if (username != null && password != null) {
            String info = username + ":" + password;
            this.token = "Basic " + Base64.getEncoder().encodeToString(info.getBytes(StandardCharsets.UTF_8));
        } else {
            throw new ClientException(ResponseEnum.FAIL, "'username' and 'password' must be NOT NULL");
        }
    }

    /**
     * Instantiates a new Custom basic auth interceptor.
     *
     * @param token the token
     */
    public CustomBasicAuthInterceptor(String token) {
        assert token != null && !token.isEmpty();
        this.token = token;
    }

    public Response intercept(@NotNull Chain chain) throws IOException {
        Request original = chain.request();
        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", this.token)
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .method(original.method(), original.body())
                .build();
        return chain.proceed(request);
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }
}
