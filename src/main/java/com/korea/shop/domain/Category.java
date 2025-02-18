package com.korea.shop.domain;

import com.korea.shop.domain.item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    private String name; // 카테고리 이름

    // 해당 카테고리에 소속될 상품들
    @OneToMany(mappedBy = "category")
    private List<CategoryItem> categoryItems = new ArrayList<>();

    // 상위 카테고리
    @ManyToOne
    @JoinColumn(name="parent_id")
    private Category parent;

    // 하위 카테고리
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    public void addChild(Category newChild){
        this.child.add(newChild); // 하위 카테고리 리스트에 새로운 자식 카테고리 추가
        newChild.setParent(this); // 변경된 this(=category) 로 다시 설정
    }
}
