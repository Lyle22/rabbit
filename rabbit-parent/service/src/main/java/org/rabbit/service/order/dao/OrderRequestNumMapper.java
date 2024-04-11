package org.rabbit.service.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.rabbit.entity.order.OrderRequestNum;

public interface OrderRequestNumMapper extends BaseMapper<OrderRequestNum> {

    int updateByRequestNum(OrderRequestNum record);

}
