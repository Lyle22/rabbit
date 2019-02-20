package org.rabbit.service.trade.dao;

import org.rabbit.entity.trade.TradeInfo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface TradeInfoDao extends BaseMapper<TradeInfo> {

	TradeInfo selectByOutTradeNo(String outTradeNo);
	
}
