package org.rabbit.common.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum PayWay implements IEnum<Integer> {

	ALIPAY(0, "支付宝", "支付宝"), WECHAT(1, "微信", "微信"), ONLINE(2, "在线", "在线");

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

	private PayWay(Integer value, String name, String desc) {
		this.value = value;
		this.name = name;
		this.desc = desc;
	}

}
