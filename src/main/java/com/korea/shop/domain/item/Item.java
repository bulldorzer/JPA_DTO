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
@SuperBuilder // 상속관계에서 빌더패턴 만들때
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

    // 삭제 여부
    private boolean delFlag;

    // 이미지 리스트
    @ElementCollection
    @Builder.Default
    private List<ItemImage> imageList = new ArrayList<>();

    // 상품에 이미지 추가
    public void addImage(ItemImage image){
        image.setOrd(this.imageList.size());
        imageList.add(image);
    }

    // 상품에 이미지 파일이름 관리
    public void addImageString(String fileName){

        ItemImage itemImage = ItemImage.builder().fileName(fileName).build();
        addImage(itemImage);
    }

    // 이미지 리스트 초기화
    public void clearList(){
        this.imageList.clear();
    }

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
