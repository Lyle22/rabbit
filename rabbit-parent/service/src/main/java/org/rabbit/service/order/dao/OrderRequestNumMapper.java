package org.rabbit.service.order.dao;

import org.rabbit.entity.order.OrderRequestNum;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface OrderRequestNumMapper extends BaseMapper<OrderRequestNum> {

	int updateByRequestNum(OrderRequestNum record);
	
}
