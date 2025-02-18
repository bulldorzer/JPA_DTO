package com.korea.shop.repository;

import com.korea.shop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

// 주문 조회하는 검색조건을 저장
@Getter @Setter
public class OrderSearch {
    private String memberName; // 회원이름
    private OrderStatus orderStatus; // 주문상태 [ORDER, CANCEL]
}
