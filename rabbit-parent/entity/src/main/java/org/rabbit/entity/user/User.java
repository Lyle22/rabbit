package org.rabbit.entity.user;

import org.rabbit.entity.base.BaseEntity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity{
	
	private String username;
	
	private String password;
	
	private String email;
	
	private String mobile;
	
	private String fax;
	
	private String telPhone;
	
	private Integer gender;
	
	// QQ登录ID
	private String unionid;
	
	// 微信openid
	private String openid;
	
	// 支付宝id
	private String alipayId;

	@Tolerate
	public User() {
		super();
	}
}
