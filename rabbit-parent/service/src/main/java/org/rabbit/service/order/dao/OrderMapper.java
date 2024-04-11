package org.rabbit.service.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.rabbit.entity.order.Order;

import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {

    int deleteByPrimaryKey(Order orderHeader);

    int insert(Order orderHeader);

    int updateByPrimaryKey(Order orderHeader);

    int updateOrderStatusById(Order orderHeader);

    List<Order> selectByOrderHeader(Order orderHeader);

    Order selectByPrimaryKey(Integer id);

    List<Order> selectByRequestNum(Order orderHeader);

    int updateByRequestNum(Order orderHeader, String requestNum);

}
