package com.korea.shop.service;

import com.korea.shop.domain.*;
import com.korea.shop.domain.item.Book;
import com.korea.shop.repository.OrderRepository;
import com.korea.shop.repository.OrderItemRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {  // ✅ 클래스명 앞에 `public` 제거 (Junit 5에서는 public 필요 없음)

    @Autowired
    private EntityManager em;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    @Test  // ✅ `@Test` 확인
    void 상품주문() throws Exception {
        // given
        Member member = createMember();
        Book item = createBook("JAVA Spring", 10000, 10);
        int orderQty = 3;

        // when
        Long orderId = orderService.createOrder(member.getId());
        OrderItem orderItem = orderService.addOrderItem(orderId, item.getId(), orderQty);
        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId);
        Order getOrder = orderRepository.findById(orderId).orElseThrow();

        // then
        int totalPrice = orderItemList.stream().mapToInt(OrderItem::getTotalPrice).sum();
        assertEquals(OrderStatus.ORDER, getOrder.getStatus());
        assertEquals(7, item.getStockQuantity());
        assertEquals(1, orderItemList.size());
        assertEquals(item.getPrice() * orderQty, totalPrice);
    }

    // ✅ 테스트를 위한 회원 생성 메서드
    private Member createMember() {
        Member member = new Member();
        member.changeName("회원1");
        member.changeEmail("user100@aaa.com");
        member.changePw(passwordEncoder.encode("1111"));
        em.persist(member);
        return member;
    }

    // ✅ 테스트를 위한 책 생성 메서드
    private Book createBook(String name, int price, int stockQty) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQty);
        em.persist(book);
        return book;
    }
}
