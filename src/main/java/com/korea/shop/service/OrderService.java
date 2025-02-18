package com.korea.shop.service;

import com.korea.shop.domain.OrderItem;
import com.korea.shop.dto.OrderDTO;

import java.util.List;


public interface OrderService {

    public Long createOrder(Long memberId);
    public OrderItem addOrderItem(Long orderId, Long itemId, int qty);
    public List<OrderDTO> getAllOrders();
    public void cancelOrderItem(Long orderItemId);
    public void cancelAllOrderItems(Long orderId);

}
