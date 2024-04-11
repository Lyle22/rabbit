package org.rabbit.service.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.rabbit.common.enums.OrderStatus;
import org.rabbit.entity.order.Order;
import org.rabbit.service.order.OrderService;
import org.rabbit.service.order.dao.OrderMapper;
import org.rabbit.service.trade.TradeInfoService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderDao;
    private final OrderRequestNumServiceImpl orderRequestNumService;
    private final TradeInfoService tradeInfoService;

    public OrderServiceImpl(OrderMapper orderDao, OrderRequestNumServiceImpl orderRequestNumService, TradeInfoService tradeInfoService) {
        this.orderDao = orderDao;
        this.orderRequestNumService = orderRequestNumService;
        this.tradeInfoService = tradeInfoService;
    }

    public List<Order> payOrder(List<Order> orderHeaders) {
        return Lists.newArrayList();
    }

    /**
     * 保存订单头
     *
     * @param orderHeader 订单头
     * @return orderHeader 订单头（包含订单行数据）
     */
    public Order create(Order orderHeader) {
        orderHeader.setStatus(OrderStatus.UNPAID);
        orderHeader.setUpdateDate(new Date());
        orderHeader.setCreateDate(new Date());
        orderHeader.setDelFlag(0);
        orderDao.insert(orderHeader);
        orderHeader.setOrderNumber(DateFormatUtils.format(new Date(), "yyyyMMdd") + orderHeader.getId());
        orderDao.updateByPrimaryKey(orderHeader);
        return orderHeader;
    }

    /**
     * 保存订单集合
     *
     * @param orderHeaders 订单集合
     * @return
     */
    public List<Order> createOrders(List<Order> orderHeaders) {
        List<Order> list = Lists.newArrayList();
        for (Order entity : orderHeaders) {
            list.add(this.create(entity));
        }
        return orderHeaders;
    }

}
