package org.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rabbit.login.security.ErrorAuthenticationEntryPoint;
import org.rabbit.login.security.authentication.account.LoginAuthenticationFilter;
import org.rabbit.login.security.authentication.account.LoginAuthenticationProvider;
import org.rabbit.login.security.authentication.common.CustomAuthenticationFailureHandler;
import org.rabbit.login.security.authentication.outlink.OutLinkAuthenticationFilter;
import org.rabbit.login.security.authentication.outlink.OutLinkAuthenticationProvider;
import org.rabbit.login.security.authentication.publics.PublicAuthenticationFilter;
import org.rabbit.login.security.authentication.publics.PublicAuthenticationProvider;
import org.rabbit.login.security.jwt.JwtAuthenticationProvider;
import org.rabbit.login.security.jwt.JwtAuthorizationFailureHandler;
import org.rabbit.login.security.jwt.JwtAuthorizationFilter;
import org.rabbit.login.security.jwt.SkipPathRequestMatcher;
import org.rabbit.login.security.jwtrefresh.JwtRefreshAuthenticationFailureHandler;
import org.rabbit.login.security.jwtrefresh.JwtRefreshAuthenticationProvider;
import org.rabbit.login.security.jwtrefresh.JwtRefreshAuthorizationFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

import static org.rabbit.SystemAuthConfiguration.API_ROOT_URL;

/**
 * 自定义 Spring Security 配置类
 * <br/>
 * 1. 设定正确的提供程序配置身份验证管理器
 * 2. 配置网络安全（公共 URL、私有 URL、授权等）
 *
 * @author nine
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SystemAuthConfiguration authConfig;
    private final ObjectMapper objectMapper;
    private final @NonNull LoginAuthenticationProvider loginAuthenticationProvider;
    private final @NonNull JwtAuthenticationProvider jwtAuthenticationProvider;
    private final @NonNull JwtRefreshAuthenticationProvider jwtRefreshAuthenticationProvider;
    private final @NonNull PublicAuthenticationProvider publicAuthenticationProvider;
    private final @NonNull OutLinkAuthenticationProvider outgoingLinkAuthenticationProvider;

    private final @NonNull ErrorAuthenticationEntryPoint errorAuthenticationEntryPoint;
    private final @NonNull AuthenticationSuccessHandler authenticationSuccessHandler;
    private final @NonNull CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final @NonNull JwtAuthorizationFailureHandler jwtAuthenticationFailureHandler;
    private final @NonNull JwtRefreshAuthenticationFailureHandler jwtRefreshAuthenticationFailureHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Enable CORS and disable CSRF
        http = http.cors().and().csrf().disable();
        // Set session management to stateless
        http = http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
        // Set unauthorized requests exception handler
        http = http.exceptionHandling().authenticationEntryPoint(errorAuthenticationEntryPoint).and();
        // Set permissions on endpoints
        http.authorizeRequests()
                // Our public endpoints
                .antMatchers(HttpMethod.POST, SystemAuthConfiguration.AUTHENTICATION_LOGIN_URL).permitAll()
                // Our private endpoints
                .anyRequest().authenticated();

        http.addFilterBefore(this.loginAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(this.jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(this.jwtRefreshAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(this.publicAuthenticationFilter(SystemAuthConfiguration.AUTHENTICATION_PUBLIC_URL), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(this.outLinkAuthenticationFilter(SystemAuthConfiguration.OUT_LINK_URL), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Used by Spring Security if CORS is enabled.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 加入自定义的安全认证
        auth.authenticationProvider(loginAuthenticationProvider);
        auth.authenticationProvider(jwtAuthenticationProvider);
        auth.authenticationProvider(jwtRefreshAuthenticationProvider);
        auth.authenticationProvider(publicAuthenticationProvider);
        auth.authenticationProvider(outgoingLinkAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    protected LoginAuthenticationFilter loginAuthenticationFilter() throws Exception {
        LoginAuthenticationFilter filter = new LoginAuthenticationFilter(
                SystemAuthConfiguration.AUTHENTICATION_LOGIN_URL,
                authenticationSuccessHandler,
                customAuthenticationFailureHandler,
                objectMapper
        );
        filter.setAuthenticationManager(super.authenticationManager());
        return filter;
    }

    /**
     * Jwt authorization filter jwt authorization filter.
     *
     * @return the jwt authorization filter
     * @throws Exception the exception
     */
    protected JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        final List<String> permitAllEndpointList = Arrays.asList(
                SystemAuthConfiguration.AUTHENTICATION_LOGIN_URL,
                SystemAuthConfiguration.REFRESH_TOKEN_URL,
                SystemAuthConfiguration.AUTHENTICATION_PUBLIC_URL,
                SystemAuthConfiguration.SWAGGER_WHITELIST_URL,
                SystemAuthConfiguration.PASSWORD_CONFIRMRESET_URI,
                SystemAuthConfiguration.PASSWORD_FORGET_URI,
                SystemAuthConfiguration.PASSWORD_RESET_URI
        );
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(permitAllEndpointList, API_ROOT_URL);
        JwtAuthorizationFilter filter = new JwtAuthorizationFilter(jwtAuthenticationFailureHandler, matcher);
        filter.setAuthenticationManager(super.authenticationManager());
        return filter;
    }

    protected JwtRefreshAuthorizationFilter jwtRefreshAuthorizationFilter() throws Exception {
        JwtRefreshAuthorizationFilter filter = new JwtRefreshAuthorizationFilter(
                SystemAuthConfiguration.REFRESH_TOKEN_URL, authenticationSuccessHandler, jwtRefreshAuthenticationFailureHandler
        );
        filter.setAuthenticationManager(super.authenticationManager());
        return filter;
    }

    protected PublicAuthenticationFilter publicAuthenticationFilter(String pattern) throws Exception {
        final List<String> permitJWTEndpointList = Arrays.asList(
                SystemAuthConfiguration.AUTHENTICATION_LOGIN_URL,
                SystemAuthConfiguration.REFRESH_TOKEN_URL,
                SystemAuthConfiguration.SWAGGER_WHITELIST_URL
        );
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(permitJWTEndpointList, pattern);
        PublicAuthenticationFilter filter = new PublicAuthenticationFilter(
                customAuthenticationFailureHandler, matcher);
        filter.setAuthenticationManager(super.authenticationManager());
        return filter;
    }

    protected OutLinkAuthenticationFilter outLinkAuthenticationFilter(String pattern) throws Exception {
        final List<String> pathsToSkip = Arrays.asList(
                SystemAuthConfiguration.AUTHENTICATION_LOGIN_URL,
                SystemAuthConfiguration.REFRESH_TOKEN_URL,
                SystemAuthConfiguration.AUTHENTICATION_PUBLIC_URL,
                SystemAuthConfiguration.SWAGGER_WHITELIST_URL,
                SystemAuthConfiguration.PASSWORD_CONFIRMRESET_URI,
                SystemAuthConfiguration.PASSWORD_FORGET_URI,
                SystemAuthConfiguration.PASSWORD_RESET_URI
        );
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, pattern);
        OutLinkAuthenticationFilter filter = new OutLinkAuthenticationFilter(
                customAuthenticationFailureHandler, matcher);
        filter.setAuthenticationManager(super.authenticationManager());
        return filter;
    }

    /**
     * 使异步任务可以继承安全上下文(SecurityContext);
     * 注意，该方法只适用于spring 框架本身创建线程时使用(例如，在使用@Async方法时)，这种方法才有效;
     *
     * @return InitializingBean
     */
    @Bean
    public InitializingBean initializingBean() {
        return () -> SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

}
