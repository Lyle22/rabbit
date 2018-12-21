package org.rabbit.service.order.dao;

import java.util.List;

import org.rabbit.entity.order.Order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface OrderMapper extends BaseMapper<Order>{

	int deleteByPrimaryKey(Order orderHeader);

	int insert(Order orderHeader);

	int updateByPrimaryKey(Order orderHeader);

	int updateOrderStatusById(Order orderHeader);

	List<Order> selectByOrderHeader(Order orderHeader);

	Order selectByPrimaryKey(Integer id);

	List<Order> selectByRequestNum(Order orderHeader);

	int updateByRequestNum(Order orderHeader, String requestNum);

}
