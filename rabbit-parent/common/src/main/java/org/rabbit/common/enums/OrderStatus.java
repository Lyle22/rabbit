package org.rabbit.common.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 订单状态 
 */
public enum OrderStatus implements IEnum<Integer> {

	SUBMITTED(0,"已提交","已提交"),
	UNPAID(1,"未支付","未支付"),
	PAID(2,"已支付","已支付"),
	PENDING(3,"待审核","待审核"),
	VERIFIED(4,"审核通过","审核通过"),
	REJECTED(5,"已拒绝","已拒绝"),
	CANCELED(6,"已取消","已取消"),
	DISABLED(7,"已失效","已失效"),
	PENDING_PAY(8,"部分支付","部分支付");
	
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
	
	private OrderStatus(Integer value, String name, String desc) {
        this.value = value;
        this.name = name;
        this.desc = desc;
    }
}
