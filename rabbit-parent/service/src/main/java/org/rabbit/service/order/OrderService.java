package org.rabbit.service.order;

import java.util.List;

import org.rabbit.entity.order.Order;

import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<Order> {

	List<Order> createOrders(List<Order> orders);
}
