package org.rabbit.service.trade.impl;

import org.rabbit.entity.trade.TradeInfo;
import org.rabbit.service.trade.TradeInfoService;
import org.rabbit.service.trade.dao.TradeInfoDao;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class TradeInfoServiceImpl extends ServiceImpl<TradeInfoDao, TradeInfo> implements TradeInfoService{

	@Override
	public TradeInfo selectByOrderRequestNum(String requestNum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateTradeInfo(TradeInfo tradeInfo) {
		// TODO Auto-generated method stub
		return 0;
	}

}
