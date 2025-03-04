package com.korea.shop.controller;

import com.korea.shop.domain.Category;
import com.korea.shop.dto.CategoryDTO;
import com.korea.shop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
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

    // 카테고리 이름변경
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @RequestParam String name){
        Category updatedCategory = categoryService.updateCategory(categoryId, name);
        CategoryDTO categoryDTO = new CategoryDTO(updatedCategory);
        return ResponseEntity.ok(categoryDTO);
    }

    // 부모 카테고리 수정
    @PutMapping("{parentId}")
    public ResponseEntity<CategoryDTO> updateParentCategory(@RequestParam Long id,@PathVariable Long parentId){
        Category updatedCategory = categoryService.updateParentCategory(id, parentId);
        CategoryDTO categoryDTO = new CategoryDTO(updatedCategory);
        return ResponseEntity.ok(categoryDTO);
    }
}
