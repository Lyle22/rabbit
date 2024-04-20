package org.rabbit.login.security.jwtrefresh;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rabbit.common.contains.Result;
import lombok.NonNull;
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
 * @author nine
 */
@Component
public class JwtRefreshAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper mapper;

    /**
     * Instantiates a new Custom authentication failure handler.
     *
     * @param mapper the mapper
     */
    public JwtRefreshAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, @NonNull HttpServletResponse response, AuthenticationException e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        Result result = Result.errorCode(306);
        if (NumberUtils.isParsable(e.getMessage())) {
            result.setCode(Integer.valueOf(e.getMessage()));
        }
        mapper.writeValue(response.getWriter(), result);
    }
}
