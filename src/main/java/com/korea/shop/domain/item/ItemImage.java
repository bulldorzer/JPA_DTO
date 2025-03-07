package com.korea.shop.domain.item;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemImage {

    private String fileName;

    private int ord;

    public void setOrd(int ord){
        this.ord = ord;
    }

}
