package org.rabbit.code.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 */
@Data
@Entity
@Table(name = "test_demo")
public class TestDemo {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_name", columnDefinition = "varchar(50) COMMENT '用户名'")
    private String userName;

    @Column(name = "phone_number", columnDefinition = "varchar(50) COMMENT '用户手机号'")
    private String phoneNumber;

    @Column(name = "user_password", columnDefinition = "varchar(50) COMMENT '用户密码'")
    private String userPassword;

    @Column(
            name = "account_status",
            columnDefinition = "tinyint(2) default '0' COMMENT '账号状态：0：正常，1：禁用，默认0'")
    private int accountStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}