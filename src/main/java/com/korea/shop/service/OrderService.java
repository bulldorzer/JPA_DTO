package com.korea.shop.service;

import com.korea.shop.domain.OrderItem;
import com.korea.shop.dto.CustomPage;
import com.korea.shop.dto.OrderDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface OrderService {

    public Long createOrder(Long memberId);
    public OrderItem addOrderItem(Long orderId, Long itemId, int qty);
    public List<OrderDTO> getAllOrders();
    public CustomPage<OrderDTO> getAllItemsPaged(Pageable pageable);
    public void cancelOrderItem(Long orderItemId);
    public void cancelAllOrderItems(Long orderId);

}
