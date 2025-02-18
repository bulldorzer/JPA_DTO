package com.korea.shop.domain.item;

import com.korea.shop.domain.CategoryItem;
import com.korea.shop.exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

// 부모 클래스
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속 관리 전략 - 단일테이블로 관리
@DiscriminatorColumn(name = "DTYPE") // 싱글테이블 전략일 때만 사용함 - 구분자 컬럼
@Getter @Setter
@SuperBuilder
@NoArgsConstructor
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;

    private int stockQuantity;

    // 해당 아이템에 적용되는 카테고리들
    @OneToMany(mappedBy = "item")
    @Builder.Default
    private List<CategoryItem> categoryItems = new ArrayList<>();

    public Item(String name, int price, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // 재고 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    // 재고 감소
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
