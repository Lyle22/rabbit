package org.rabbit.entity.order;

import java.math.BigDecimal;
import java.util.Date;

import org.rabbit.common.enums.OrderStatus;
import org.rabbit.entity.base.BaseEntity;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Order extends BaseEntity{

	@TableField("order_number")
	private String orderNumber;
	
	private String remitCode;
	
	@TableField("cancel_date")
	private Date cancelDate;

	private OrderStatus status;
	
	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 汇款识别码
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
	 * 优惠价
	 */
	private BigDecimal discountPrice;

	/**
	 * 总金额
	 */
	private BigDecimal amount;

	/**
	 * 备注
	 */
	private String message;

	@Tolerate
	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}
}
