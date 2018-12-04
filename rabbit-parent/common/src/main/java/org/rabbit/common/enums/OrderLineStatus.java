package org.rabbit.common.enums;

public enum OrderLineStatus {

	UNPAID(0, "待支付", "待支付"),
	PENDING(1, "退款待审核", "退款待审核"), 
	VERIFIED(2, "退款审核通过", "退款审核通过"), 
	REJECTED(3, "退款已拒绝", "已拒绝"), 
	REFUNDING(4, "待退款", "待退款"), 
	REFUNDED(5, "已退款", "已退款"), 
	CANCELED(6, "已取消", "已取消"), 
	COMPLETED(7, "已完成", "已完成"), 
	FINISH(8, "已处理", "已处理");

	private Integer value;

	private String name;

	private String desc;

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

	private OrderLineStatus(Integer value, String name, String desc) {
		this.value = value;
		this.name = name;
		this.desc = desc;
	}

}
