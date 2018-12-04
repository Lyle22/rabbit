package org.rabbit.service.trade.dao;

import org.rabbit.entity.trade.TradeInfo;

public interface TradeInfoDao {

	int insert(TradeInfo tradeInfo);

	int update(TradeInfo tradeInfo);
	
	int delete(TradeInfo tradeInfo);
	
	TradeInfo selectByOutTradeNo(String outTradeNo);
	
}
