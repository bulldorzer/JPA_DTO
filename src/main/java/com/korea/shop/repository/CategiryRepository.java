package com.korea.shop.repository;

import com.korea.shop.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// JpaRepository<엔티티, PK>
@Repository
public interface CategiryRepository extends JpaRepository<Category, Long> {

    // select c from category c where c.name = :name;
    // 데이터가 1개만 나온다 = Optional
    // 데이터가 여러개 - List or 중복 가능성 있음
    // 존재여부를 검사해주는 메서드 exists 포함
    public Optional<Category> findByname(String name); // 일치하는 결과물을 찾아줌
    public Boolean existsByName(String name); // 일치하는것 있으면 true/ 없으면 false
    public List<Category> findByParentId(Long parentId);

    public List<Category> findByParentIdIsNull();
}
