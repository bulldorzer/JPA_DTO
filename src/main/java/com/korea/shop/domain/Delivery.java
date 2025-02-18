package com.korea.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Delivery {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="delivery_id")
    private Long id;

    @Embedded // 값 타입
    private Address address; // 배송지

    @Enumerated(EnumType.STRING) // enum의 자료형 지정
    private DeliveryStatus status; // 배송상태
}
