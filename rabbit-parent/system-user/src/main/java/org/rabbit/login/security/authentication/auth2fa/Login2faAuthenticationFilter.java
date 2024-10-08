package org.rabbit.login.security.authentication.auth2fa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rabbit.login.security.jwt.exception.JwtTokenMissingException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * The type Login2fa authentication filter.
 *
 * @author nine rabbit
 */
@Slf4j
public class Login2faAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;
    private final ObjectMapper objectMapper;
    private final boolean isRequired2fa;

    /**
     * Instantiates a new authentication filter.
     *
     * @param defaultProcessUrl the default process url
     * @param successHandler    the success handler
     * @param failureHandler    the failure handler
     * @param mapper            the mapper
     */
    public Login2faAuthenticationFilter(
            String defaultProcessUrl, AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler, ObjectMapper mapper, boolean isRequired2fa
    ) {
        super(defaultProcessUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.objectMapper = mapper;
        this.isRequired2fa = isRequired2fa;
    }

    @Override
    public Authentication attemptAuthentication(@NonNull HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("308");
        }
        if (!isRequired2fa) throw new BadCredentialsException("Internal Error: 2fa not supported");
        Login2faRequestDTO loginRequest = objectMapper.readValue(request.getReader(), Login2faRequestDTO.class);

        if (!StringUtils.hasLength(loginRequest.getPasscode())) {
            throw new AuthenticationServiceException("Passcode missing");
        }
        final String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new JwtTokenMissingException("No JWT token found in request headers");
        }
        String token = authorizationHeader.substring("Bearer ".length());
        final Login2faAuthenticationToken login2FaAuthenticationToken = new Login2faAuthenticationToken("no required", loginRequest.getPasscode(), token);
        return this.getAuthenticationManager().authenticate(login2FaAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws AuthenticationException, ServletException, IOException {
        successHandler.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException failed
    ) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
