package com.korea.shop.repository;

import com.korea.shop.domain.Order;
import com.korea.shop.domain.OrderStatus;
import com.korea.shop.dto.OrderDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ✅ Fetch Join을 사용한 전체 조회 (Lazy Loading 문제 해결)
    @Query("SELECT o FROM Order o JOIN FETCH o.member m JOIN FETCH o.delivery d")
    List<Order> findAllByFetch();

    // ✅ JPQL을 활용한 DTO 프로젝션 (개별 필드 반환)
    @Query("SELECT new com.korea.shop.dto.OrderDTO(o.id, m.name, o.orderDate, o.status, " +
            "d.address.city, d.address.street, d.address.zipcode) " +
            "FROM Order o JOIN o.member m JOIN o.delivery d")
    List<OrderDTO> findAllByJPQL();

    // ✅ 특정 주문 상태와 회원 이름으로 검색 (동적 쿼리 적용)
    @Query("SELECT o FROM Order o JOIN o.member m " +
            "WHERE (:status IS NULL OR o.status = :status) " +
            "AND (:name IS NULL OR m.name LIKE %:name%)")
    List<Order> findAllByString(@Param("status") OrderStatus status, @Param("name") String memberName);

    // ✅ 특정 주문 상태 및 특정 날짜 이전 주문 검색 (배치 작업용)
    List<Order> findByStatusAndOrderDateBefore(OrderStatus status, LocalDateTime beforeDate);

    // ✅ EntityGraph를 활용한 Lazy Loading 최적화 (N+1 문제 해결)
    @EntityGraph(attributePaths = {"member", "delivery"})
    List<Order> findAll();
}
