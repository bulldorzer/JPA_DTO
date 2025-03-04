package com.korea.shop.service;

import com.korea.shop.domain.Category;

import java.util.List;

public interface CategoryService {

    // 카테고리 등록
    //  상위 카테고리 등록
    Category addParentCatagory(String name);
    //  하위 카테고리 등록
    Category addChildCategory(Long parentId, String name);


    // 카테고리 조회
    //  상위카테고리만 조회
    //  특정 카테고리에 속한 - 하위 카테고리 목록 조회
    //  1개 카테고리 조회
    List<Category> getParentCategories();
    List<Category> getChildCategories(Long parentId);
    List<Category> getAllCategory();

    // 카테고리 수정
    //  카테고리 이름 수정
    //  부모 카테고리 수정
    Category updateCategory(Long id, String name);

    Category updateParentCategory(Long id, Long parentId);

    // 카테고리 삭제
}
