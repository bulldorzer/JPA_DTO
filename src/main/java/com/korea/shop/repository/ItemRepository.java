package com.korea.shop.repository;

import com.korea.shop.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// JpaRepository<엔티티, PK자료형>
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // JPQL 필요없음, 메서드 이름 기반으로 실행함.
    // SELECT i FROM Item i WHERE i.name = :name
    public List<Item> findByName(String name);
}
