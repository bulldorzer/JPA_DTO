package com.korea.shop.repository;

import com.korea.shop.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository<엔티티, PK자료형>
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
