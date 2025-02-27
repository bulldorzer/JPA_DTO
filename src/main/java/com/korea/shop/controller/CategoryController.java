package com.korea.shop.controller;

import com.korea.shop.domain.Category;
import com.korea.shop.dto.CategoryDTO;
import com.korea.shop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 전체 상위 카테고리조회
    @GetMapping("/parents")
    public ResponseEntity<List<CategoryDTO>> getParentCategories(){
        List<CategoryDTO> categories = categoryService.getParentCategories()
                .stream().map(CategoryDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }


    // 상위 카테고리에 해당하는 하위 카테고리 목록조회
    @GetMapping("/parents/{parentId}")
    public ResponseEntity<List<CategoryDTO>> getChildCategories(@PathVariable Long parentId){
        List<CategoryDTO> categories = categoryService.getChildCategories(parentId)
                .stream().map(CategoryDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    // 상위 카테고리 등록
    @PostMapping("/addParent")
    public Category createParentCategory(@RequestParam String name){
        return categoryService.addParentCatagory(name);

    }

    // 하위 카테고리 등록
    @PostMapping("/{parentId}/addChild")
    public Category createChildCategory(@PathVariable Long parentId,@RequestParam String name){
        return categoryService.addChildCategory(parentId,name);
    }
}
