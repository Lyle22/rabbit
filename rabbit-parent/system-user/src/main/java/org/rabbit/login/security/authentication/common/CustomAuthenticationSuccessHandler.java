package org.rabbit.login.security.authentication.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rabbit.common.Result;
import org.rabbit.login.config.AuthConfigs;
import org.rabbit.login.contants.Authority;
import org.rabbit.login.entity.User;
import org.rabbit.login.models.LoginAuthenticationStore;
import org.rabbit.login.security.authentication.account.LoginAuthenticationDetails;
import org.rabbit.login.security.authentication.auth2fa.Login2faAuthenticationDetails;
import org.rabbit.login.security.jwtrefresh.JwtRefreshAuthenticationDetails;
import org.rabbit.login.service.LoginAuthenticationStoreService;
import org.rabbit.login.service.UserService;
import lombok.NonNull;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

/**
 * The type Custom authentication success handler.
 *
 * @author nine
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper mapper;
    private final AuthConfigs authConfigs;
    private final UserService userService;
    private final LoginAuthenticationStoreService loginAuthenticationStoreService;


    /**
     * Instantiates a new Custom authentication success handler.
     *
     * @param mapper                          the mapper
     * @param authConfigs
     * @param loginAuthenticationStoreService
     * @param userService
     */
    @Autowired
    public CustomAuthenticationSuccessHandler(
            final ObjectMapper mapper, AuthConfigs authConfigs,
            LoginAuthenticationStoreService loginAuthenticationStoreService,
            UserService userService) {
        this.mapper = mapper;
        this.authConfigs = authConfigs;
        this.loginAuthenticationStoreService = loginAuthenticationStoreService;
        this.userService = userService;
    }

    private @NotNull Calendar getJwtExpiresAt() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, authConfigs.getJwtExpirationMinutes());
        return calendar;
    }

    private @NotNull Calendar getJwtRefreshExpiresAt() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, authConfigs.getJwtRefreshExpirationMinutes());
        return calendar;
    }

    private @NonNull Calendar get2FAJwtExpiresAt() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, authConfigs.getAuth2faExpirationMinutes());
        return calendar;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Object authenticationDetails = authentication.getDetails();
        String username = (String) authentication.getPrincipal();
        if (authConfigs.isRequired2FA()) {
            if (authenticationDetails instanceof Login2faAuthenticationDetails) {
                this.handleSuccessAndGenerateJWT(request, response, authentication);
            } else if (authenticationDetails instanceof LoginAuthenticationDetails) {
                this.handleRequestFor2fa(request, response, authentication);
            } else if (authenticationDetails instanceof JwtRefreshAuthenticationDetails) {
                this.handleRefreshJWT(request, response, authentication);
            }
        } else {
            if (authenticationDetails instanceof LoginAuthenticationDetails) {
                this.handleSuccessAndGenerateJWT(request, response, authentication);
            }
            if (authenticationDetails instanceof JwtRefreshAuthenticationDetails) {
                this.handleRefreshJWT(request, response, authentication);
            }
        }
    }

    private void handleRefreshJWT(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        final JwtRefreshAuthenticationDetails authenticationDetails = (JwtRefreshAuthenticationDetails) authentication.getDetails();
        final String username = (String) authentication.getPrincipal();
        final String loginSessionId = authenticationDetails.getLoginSessionId();
        final Collection<? extends GrantedAuthority> authorityList = authentication.getAuthorities();
        final String docPalAuthenticationSessionId = UUID.randomUUID().toString();
        Algorithm algorithm = Algorithm.HMAC256(authConfigs.getJwtSecret().getBytes());

        Date jwtExpiredAt = getJwtExpiresAt().getTime();
        String access_token = JWT.create()
                .withSubject(username)
                .withExpiresAt(jwtExpiredAt)
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", authorityList.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withClaim(Authority.SESSION_ID_KEY, docPalAuthenticationSessionId)
                .sign(algorithm);
        Date jwtRefreshExpiresAt = getJwtRefreshExpiresAt().getTime();
        String refresh_token = JWT.create()
                .withSubject(username)
                .withExpiresAt(jwtRefreshExpiresAt)
                .withIssuer(request.getRequestURL().toString())
                .withClaim(Authority.SESSION_ID_KEY, docPalAuthenticationSessionId)
                .sign(algorithm);

        // Write corresponding information to redis for token refresh purpose
        User user = userService.get(username);
        loginAuthenticationStoreService.storeSession
                (LoginAuthenticationStore.builder()
                        .loginSessionId(docPalAuthenticationSessionId)
                        .authSessionId(loginSessionId)
                        .enabled(true)
                        .expiredDate(jwtRefreshExpiresAt)
                        .userId(user.getId())
                        .build());
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        tokens.put("accessTokenExpiry", String.valueOf(jwtExpiredAt.getTime()));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), Result.ok(tokens));

        clearAuthenticationAttributes(request);
    }

    // Handle Login and ask for 2FA
    private void handleRequestFor2fa(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        final Object authenticationDetails = authentication.getDetails();
        if (!(authenticationDetails instanceof LoginAuthenticationDetails)) {
            throw new IOException("Internal Error");
        }
        final String username = (String) authentication.getPrincipal();
        final LoginAuthenticationDetails loginAuthenticationDetails = (LoginAuthenticationDetails) authenticationDetails;
        final String docPalAuthenticationSessionId = UUID.randomUUID().toString();
        final Date jwtExpiredAt = this.get2FAJwtExpiresAt().getTime();

        Algorithm algorithm = Algorithm.HMAC256(authConfigs.getJwtSecret().getBytes());
        final String access_token = JWT.create()
                .withSubject(username)
                .withExpiresAt(jwtExpiredAt)
                .withIssuer(request.getRequestURL().toString())
                .withClaim(Authority.SESSION_ID_KEY, docPalAuthenticationSessionId)
                .sign(algorithm);
        final String email = loginAuthenticationDetails.getUserEmail();
        if (email == null || email.isBlank() || email.isEmpty() || !email.contains("@")) {
            throw new IOException("User without email, username=[" + username + "]");
        }

        // Generate Passcode
        RandomStringGenerator passcode = new RandomStringGenerator.Builder().withinRange('0', '9').build();
        String passcodeString = passcode.generate(6);

        // Write corresponding information to redis for token refresh purpose
        User user = userService.get(username);
        loginAuthenticationStoreService.storeSession
                (LoginAuthenticationStore.builder()
                        .loginSessionId(docPalAuthenticationSessionId)
                        .authSessionId(((LoginAuthenticationDetails) authentication.getDetails()).getLoginSessionId())
                        .enabled(true)
                        .expiredDate(jwtExpiredAt)
                        .passcode(passcodeString)
                        .userId(user.getId())
                        .build());
        // Send Email
//        mailSendService.sendText(MailSendRequest.builder()
//                .to(email)
//                .subject("Passcode from DocPal")
//                .text(passcodeString)
//                .build());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("isRequired2FA", "true");
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), Result.ok(tokens));

        clearAuthenticationAttributes(request);
    }

    // Handle Login Success without 2FA
    private void handleSuccessAndGenerateJWT(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String username = (String) authentication.getPrincipal();
        final String docPalAuthenticationSessionId = UUID.randomUUID().toString();

        Algorithm algorithm = Algorithm.HMAC256(authConfigs.getJwtSecret().getBytes());
        String access_token;
        String loginSessionId;
        if (authentication.getDetails() instanceof LoginAuthenticationDetails) {
            loginSessionId = ((LoginAuthenticationDetails) authentication.getDetails()).getLoginSessionId();
        } else if (authentication.getDetails() instanceof Login2faAuthenticationDetails) {
            loginSessionId = ((Login2faAuthenticationDetails) authentication.getDetails()).getLoginSessionId();
        } else {
            throw new IOException("Internal Error");
        }

        Date jwtExpiredAt = getJwtExpiresAt().getTime();
        access_token = JWT.create()
                .withSubject(username)
                .withExpiresAt(jwtExpiredAt)
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withClaim(Authority.SESSION_ID_KEY, docPalAuthenticationSessionId)
                .sign(algorithm);

        Date jwtRefreshExpiresAt = getJwtRefreshExpiresAt().getTime();
        String refresh_token = JWT.create()
                .withSubject(username)
                .withExpiresAt(jwtRefreshExpiresAt)
                .withIssuer(request.getRequestURL().toString())
                .withClaim(Authority.SESSION_ID_KEY, docPalAuthenticationSessionId)
                .sign(algorithm);

        User user = userService.get(username);
        loginAuthenticationStoreService.storeSession
                (LoginAuthenticationStore.builder()
                        .loginSessionId(docPalAuthenticationSessionId)
                        .authSessionId(loginSessionId)
                        .enabled(true)
                        .expiredDate(jwtExpiredAt)
                        .userId(user.getId())
                        .build());
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        tokens.put("accessTokenExpiry", String.valueOf(jwtExpiredAt.getTime()));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), Result.ok(tokens));

        clearAuthenticationAttributes(request);
    }

    /**
     * Removes temporary authentication-related data which may have been stored
     * in the session during the authentication process
     *
     * @param request the request
     */
    protected final void clearAuthenticationAttributes(@NotNull HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
