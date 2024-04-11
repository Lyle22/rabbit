package org.rabbit.entity.trade;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;
import org.rabbit.common.enums.PaySourceType;
import org.rabbit.common.enums.PayWay;
import org.rabbit.entity.base.BaseEntity;

import java.math.BigDecimal;

/**
 * 交易表
 *
 * @author geestu
 */
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class TradeInfo extends BaseEntity {

    private String outTradeNo;

    private PayWay tradeWay;

    private PaySourceType type;

    private PaySourceType srcType;

    private String tradeWayName;

    private String tradeFlow;

    private BigDecimal amount;

    private String tradeDate;

    private String tradeStatus;

    private String msg;

    private String returnUrl;

    private String extraParams;

    @Tolerate
    public TradeInfo() {
        super();
    }
}
