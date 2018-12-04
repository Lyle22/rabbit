package org.rabbit.entity.trade;

import java.math.BigDecimal;
import java.util.Date;

import org.rabbit.common.enums.PaySourceType;
import org.rabbit.common.enums.PayWay;

/**
 * 交易表
 * 
 * @author geestu
 *
 */
public class TradeInfo {

	private Integer id;

	private String outTradeNo;

	private PayWay tradeWay;

	private PaySourceType srcType;

	private String tradeWayName;

	private String tradeFlow;

	private BigDecimal amount;

	private String tradeDate;

	private String tradeStatus;

	private String msg;

	private String returnUrl;

	private String extraParams;

	private Date createDate;

	private String createBy;

	private Date updateDate;

	private String updateBy;

	// 第三方平台Id
	private String thirdAccountId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getTradeWayName() {
		return tradeWayName;
	}

	public void setTradeWayName(String tradeWayName) {
		this.tradeWayName = tradeWayName;
	}

	public String getTradeFlow() {
		return tradeFlow;
	}

	public void setTradeFlow(String tradeFlow) {
		this.tradeFlow = tradeFlow;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getExtraParams() {
		return extraParams;
	}

	public void setExtraParams(String extraParams) {
		this.extraParams = extraParams;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public String getThirdAccountId() {
		return thirdAccountId;
	}

	public void setThirdAccountId(String thirdAccountId) {
		this.thirdAccountId = thirdAccountId;
	}

	public PayWay getTradeWay() {
		return tradeWay;
	}

	public void setTradeWay(PayWay tradeWay) {
		this.tradeWay = tradeWay;
	}

	public PaySourceType getSrcType() {
		return srcType;
	}

	public void setSrcType(PaySourceType srcType) {
		this.srcType = srcType;
	}

}
