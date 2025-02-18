package com.korea.shop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 엔티티 안에 들어가는 값 타입으로 사용될 클래스임
// 값타입으로 포함시킬 수 있다.
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address {

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String street;

    @Column(nullable = true)
    private String zipcode;
}
