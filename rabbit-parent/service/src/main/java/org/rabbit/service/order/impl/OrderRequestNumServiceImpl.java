package org.rabbit.service.order.impl;

import org.rabbit.entity.order.OrderRequestNum;
import org.rabbit.service.order.OrderRequestNumService;
import org.rabbit.service.order.dao.OrderRequestNumMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class OrderRequestNumServiceImpl extends ServiceImpl<OrderRequestNumMapper, OrderRequestNum> implements OrderRequestNumService {

	public OrderRequestNum selectByOrderId(Integer firstOrderId) {
		// TODO Auto-generated method stub
		return null;
	}

}
