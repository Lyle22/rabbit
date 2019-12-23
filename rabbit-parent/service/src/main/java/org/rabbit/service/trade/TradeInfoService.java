package org.rabbit.service.trade;

import org.rabbit.entity.trade.TradeInfo;
import org.springframework.stereotype.Service;

@Service
public interface TradeInfoService {

	TradeInfo selectByOrderRequestNum(String requestNum);

	int updateTradeInfo(TradeInfo tradeInfo);
	
}
