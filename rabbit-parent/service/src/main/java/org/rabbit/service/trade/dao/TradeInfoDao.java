package org.rabbit.service.trade.dao;

import org.rabbit.entity.trade.TradeInfo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface TradeInfoDao extends BaseMapper<TradeInfo> {

	int insert(TradeInfo tradeInfo);

	int update(TradeInfo tradeInfo);
	
	int delete(TradeInfo tradeInfo);
	
	TradeInfo selectByOutTradeNo(String outTradeNo);
	
}
