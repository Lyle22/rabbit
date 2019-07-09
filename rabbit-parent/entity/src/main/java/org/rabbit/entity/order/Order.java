package org.rabbit.entity.order;

import java.math.BigDecimal;
import java.util.Date;

import org.rabbit.common.enums.OrderStatus;
import org.rabbit.common.enums.PayWay;
import org.rabbit.entity.base.BaseEntity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName(value = "rb_order")
public class Order extends BaseEntity{

	@TableField("order_number")
	private String orderNumber;
	
	private String remitCode;
	
	@TableField("cancel_date")
	private Date cancelDate;

	@TableField("order_status")
	private OrderStatus status;
	
	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 
	 */
	private String specifications;

	/**
	 * 购买数量
	 */
	private BigDecimal buyNum;

	/**
	 * 销售单价
	 */
	private BigDecimal unitPrice;

	/**
	 * 总金额
	 */
	private BigDecimal amount;
	
	/**
	 * 支付方式
	 */
	private PayWay payWay;

	/**
	 * 支付编号
	 */
	private String requestNum;
	
	/**
	 * 备注
	 */
	private String message;
	
}
