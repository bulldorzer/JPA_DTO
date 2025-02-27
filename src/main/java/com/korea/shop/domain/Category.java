package com.korea.shop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // 상위 카테고리 + 하위카테고리 : 양방향 관계 설정
    @JsonIgnore // 무한루프 끊어줌 => JSON 직렬화에서 parent 제외하고 무한루프 방지
    @ManyToOne
    @JoinColumn(name="parent_id")
    private Category parent;

    // 하위 카테고리
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    /*
    * 두개의 테이블이 양방향 관계이나
    * 한 엔티티에 합치기 위함
    * parent_id는 @id(PK)를 참조함
    * 
    * 
    * 
    */

}
