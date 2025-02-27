package com.korea.shop.dto;

import com.korea.shop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter@Setter
public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private List<CategoryDTO> children;

    public CategoryDTO(Category category){
        this.id = category.getId();
        this.name = category.getName();
        this.parentId = category.getParent() != null
                ? category.getParent().getId()
                : null ;
        this.children = category.getChild().stream()
                .map(CategoryDTO::new) // 생성사
                .collect(Collectors.toList());
    }
}
