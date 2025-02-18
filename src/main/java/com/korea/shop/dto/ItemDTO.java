package com.korea.shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

    private String name;
    private int price;

    @JsonProperty("stockQty")
    private int stockQuantity;

    private String dtype; // 구분 문자

    // 책
    private String author;
    private String isbn;

    // 앨범
    private String artist;
    private String etc;

    // 무비
    private String director;
    private String actor;



}
