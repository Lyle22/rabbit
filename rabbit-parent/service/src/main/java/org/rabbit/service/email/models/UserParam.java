package org.rabbit.service.email.models;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author nine rabbit
 */
@Data
@Builder
public class UserParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String email;

    private String cardNo;

    private String nickName;

    private int sex;

    private int age;

}