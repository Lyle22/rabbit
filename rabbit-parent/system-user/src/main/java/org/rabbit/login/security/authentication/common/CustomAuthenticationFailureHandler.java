package org.rabbit.login.security.authentication.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rabbit.common.contains.Result;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The type Custom authentication failure handler.
 *
 * @author nine rabbit
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;

    /**
     * Instantiates a new Custom authentication failure handler.
     *
     * @param mapper the mapper
     */
    public CustomAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        String language = request.getHeader("Accept-Language");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Result<Object> result = Result.errorCode(300);
        if (NumberUtils.isParsable(e.getMessage())) {
            result.setCode(Integer.parseInt(e.getMessage()));
        }
        mapper.writeValue(response.getWriter(), result);
    }

}
