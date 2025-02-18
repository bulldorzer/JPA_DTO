package com.korea.shop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("A") // 구분자컬럼에 저장되는 값 - DTYPE에 A 저장됨
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Album extends Item {
    private String artist;
    private String etc;
    public Album(String name, int price, int stockQuantity, String artist, String etc) {
        super(name, price, stockQuantity);
        this.artist = artist;
        this.etc = etc;
    }
}
