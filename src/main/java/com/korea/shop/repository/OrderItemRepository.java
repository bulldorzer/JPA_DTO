package com.korea.shop.repository;

import com.korea.shop.domain.Order;
import com.korea.shop.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 특정 주문서의 모든 주문 아이템 조회
    List<OrderItem> findByOrderId(Long orderId);

    // 특정 주문서의 모든 주문 아이템 삭제
    void deleteByOrder(Order order);
}



