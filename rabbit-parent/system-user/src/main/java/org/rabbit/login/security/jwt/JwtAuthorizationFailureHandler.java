package org.rabbit.login.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rabbit.common.Result;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The type Jwt authorization failure handler.
 *
 * @author nine
 */
@Component
public class JwtAuthorizationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper mapper;

    /**
     * Instantiates a new Jwt authorization failure handler.
     *
     * @param mapper the mapper
     */
    @Autowired
    public JwtAuthorizationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, @NotNull HttpServletResponse response, AuthenticationException e) throws IOException {
        String language = request.getHeader("Accept-Language");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Result result = Result.errorCode(301);
        if (NumberUtils.isParsable(e.getMessage())) {
            result.setCode(Integer.valueOf(e.getMessage()));
        }
//        cacheInitUtils.setErrorMessAge(result, language);
        mapper.writeValue(response.getWriter(), result);
    }
}
