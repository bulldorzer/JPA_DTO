package com.korea.shop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.korea.shop.domain.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime orderDate;

    private OrderStatus status;
    private String city;
    private String street;
    private String zipcode;

    public OrderDTO(
            Long orderId, String name, LocalDateTime orderDate, OrderStatus status,
                    String city, String street, String zipcode) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.status = status;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
