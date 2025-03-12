package com.korea.shop.service;

import com.korea.shop.domain.Category;
import com.korea.shop.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    // 카테고리 등록
    //  상위 카테고리 등록
    @Override
    public Category addParentCatagory(String name){
        // 카테고리 이름을 받음 - 매개변수

        // 이름 중복검사
        checkDuplicateCategory(name);
        // 카테고리 객체를 생성
        Category category = new Category();
        // 카테고리 이름 설정

        System.out.println(name);
        category.setName(name);
        // 리포지토리에 저장
        categoryRepository.save(category);
        return category;
    };

    // isDuplicateCategory() -> 중복여부 ~ 반환값 : true/false
    // check ~~ -> 예외를 던질 경우 적절
    // exists ~ -> 카테고리 존재여부
    // findDuplicate~~ -> 중복된 객체 반환
    // 이름 중복검사

    public void checkDuplicateCategory(String name){
        if (categoryRepository.existsByName(name)){
            throw new IllegalArgumentException("이미 존재하는 카테고리");
        }

    }

    //  하위 카테고리 등록
    @Override
    public Category addChildCategory(Long parentId,String name){
        // 부모 카테고리 id, 자식 카테코리 이름
        // 자식카테고리 이름 - 중복검사
        checkDuplicateCategory(name);
        // 부모카테고리 id로 객체 조회
        Category parent = categoryRepository.findById(parentId).orElseThrow(
                ()->new IllegalArgumentException("존재하지 않는 부모 카테고리")
        );
        // 카테고리 객체를 생성
        Category category = new Category();
        // 카테고리 이름를 설정
        category.setName(name);
        // 부모 카테고리 객체설정
        category.setParent(parent);
        // 레포지토리에 저장
        categoryRepository.save(category);
        // 지정된 카테고리의 객체 id 반환
        return category;

    }

    // 카테고리 조회

    //  모든카테고리 조회
    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }



    //  상위카테고리만 조회
    @Override
    public List<Category> getParentCategories() {
        return categoryRepository.findByParentIdIsNull();
    }

    //  자식 카테고리만 조회
    @Override
    public List<Category> getChildCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }




    //  특정 카테고리에 속한 - 하위 카테고리 목록 조회
    //  1개 카테고리 조회
    
    // 카테고리 수정
    //  카테고리 이름 수정
    @Override
    public Category updateCategory(Long id, String name) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category not found"));
        existingCategory.setName(name);

        return categoryRepository.save(existingCategory);
    }

    //  부모 카테고리 수정
    @Override
    public Category updateParentCategory(Long id, Long parentId) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category not found"));

        Category parentCategory = categoryRepository.findById(parentId)
                .orElseThrow(()-> new RuntimeException("parentCategory not found"));

        existingCategory.setParent(parentCategory);
        return categoryRepository.save(existingCategory);
    }

    // 카테고리 삭제
    
}
