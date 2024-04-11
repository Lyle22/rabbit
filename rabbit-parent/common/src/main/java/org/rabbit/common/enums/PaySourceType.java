package org.rabbit.common.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

public enum PaySourceType implements IEnum<Integer> {

	PC(0, "PC", "PC"), WECHAT(1, "微信", "微信"), PHONE(2, "手机网页", "手机网页");

	private Integer value;

	private String name;

	private String desc;

	@Override
	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	private PaySourceType(Integer value, String name, String desc) {
		this.value = value;
		this.name = name;
		this.desc = desc;
	}

}
