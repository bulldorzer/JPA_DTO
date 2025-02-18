package com.korea.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("M") // 구분자컬럼 - DTYPE에 M 저장됨
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Movie extends Item{
    private String director;
    private String actor;
    public Movie(String name, int price, int stockQuantity, String director, String actor) {
        super(name, price, stockQuantity);
        this.director = director;
        this.actor = actor;
    }
}
