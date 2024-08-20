package org.rabbit.core.core;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * NUXEO properties
 *
 * @author ninerabbit
 */
@Data
@Configuration
public class NuxeoProperties {

    @Value("${nuxeo.jwt.secret:123456}")
    private String jwtSecret;

    @Value("${jwt.expiration.minutes:120}")
    private int jwtExpiresInMinutes;

    @Value("${jwt.refresh.expiration.minutes:120}")
    private int jwtRefreshExpiresInMinutes;

    @Value("${spring.ldap.enabled:false}")
    private Boolean isLdap;

    @Value("${support.user:Administrators}")
    private String supportUser;

    @Value("${nuxeo.app.url:http://localhost:8080}")
    private String nuxeoServerURL;

    @Value("${nuxeo.public.username:Administrators}")
    public String nuxeoUserName;

    @Value("${nuxeo.public.password:password}")
    private String nuxeoPassword;

    @Value("${nuxeo.upload.writeTimeout:12000}")
    private Integer writeTimeout;

    @Value("${nuxeo.upload.chunkSize:1024}")
    private int uploadChunkSize;

}
