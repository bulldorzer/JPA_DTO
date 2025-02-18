package com.korea.shop.repository;

import com.korea.shop.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // JPQL 필요없음, 메서드 이름 기반으로 실행함.
    // SELECT i FROM Item i WHERE i.name = :name
    public List<Item> findByName(String name);
}
