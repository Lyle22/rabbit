package org.rabbit.login.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

/**
 * @author nine
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Proxy(lazy = false)
@Entity
@Table(name = "user")
public class LoginUser {
    @Id
    private String id;

    @Column(name = "user_id", length = 64)
    @Schema(description = "user id")
    private String userId;

    @Schema(description = "user name")
    @Column(name = "user_name", length = 64)
    private String username;

    @Schema(description = "user first name")
    @Column(name = "first_name", length = 64)
    private String firstName;

    @Schema(description = "user last name")
    @Column(name = "last_name")
    private String lastName;

    @Schema(description = "email")
    @Column(name = "email", length = 64)
    private String email;

    @Schema(description = "password")
    @Column(name = "password", length = 64)
    private String password;

    @Schema(description = "tenant id")
    @Column(name = "tenant_id", length = 64)
    private String tenantId;

    @Schema(description = "source")
    @Column(name = "source", length = 32)
    private String source;

    /**
     * A: active
     * D: un-active
     */
    @Schema(description = "status")
    @Column(name = "status", length = 16, columnDefinition = "varchar(16) default 'D'")
    private String status;

    @Transient
    private Map<String, Object> properties;

    @Schema(description = "created user id", required = true)
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Schema(description = "modified user id", required = true)
    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;

    @Schema(description = "Created date", required = true)
    @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @Schema(description = "modified date", required = true)
    @Column(name = "modified_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedDate;

}
