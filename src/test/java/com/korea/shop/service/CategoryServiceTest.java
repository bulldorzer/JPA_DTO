package com.korea.shop.service;

import com.korea.shop.domain.Category;
import com.korea.shop.repository.CategiryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategiryRepository categiryRepository;

    @Test
    public void  상위_카테고리등록(){

        // Given
        String name = "컴퓨터공학";
        // when
        Long savedId = categoryService.addParentCatagory(name).getId();

        // then
        Category category = categiryRepository.findById(savedId).orElseThrow();
        assertEquals(name, category.getName(), "이름이 일치합니다.");
    }

    @Test
    public void 하위_카테고리등록(){
        // Given
        String name = "JPA-22";
        Long parentId = 4L;
        // when
        Long savedId = categoryService.addChildCategory(parentId,name).getId();

        // then
        Category category = categiryRepository.findById(savedId).orElseThrow();
        assertEquals(name, category.getName(), "이름이 일치합니다.");
    }
}
