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
        // given - 회원 생성 및 아이템 생성
        Member member = createMember();
        Book item = createBook("JAVA Spring", 10000, 10);
        int orderQty = 3;

        // when - 주문서 생성 및 주문서에 상품 추가
        Long orderId = orderService.createOrder(member.getId());
        OrderItem orderItem = orderService.addOrderItem(orderId, item.getId(), orderQty);
        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId);
        Order getOrder = orderRepository.findById(orderId).orElseThrow();

        // then -  주문수 아이템수량 변화 검증
        int totalPrice = orderItemList.stream().mapToInt(OrderItem::getTotalPrice).sum();
        assertEquals(OrderStatus.ORDER, getOrder.getStatus(),"주문상태가 주문이 아닙니다!");
        assertEquals(7, item.getStockQuantity(),"주문 연산이 잘못 되었습니다!");
        System.out.println("아이템수량 테스트 10-3: "+item.getStockQuantity());
        assertEquals(1, orderItemList.size(),"주문서 갯수가 잘못 되었습니다!");
        System.out.println("주문서 확인 테스트 :"+orderItemList.size());
        assertEquals(item.getPrice() * orderQty, totalPrice,"총가격의 값이 일치하지 않습니다!");
        System.out.println("총 가격비교 = "+item.getPrice() * orderQty+" 주문한 총가격 = "+totalPrice);
    }

    @Test
    void 주문취소() throws Exception{
        // given - 회원 생성 및 아이템 생성
        Member member = createMember();
        Book item = createBook("JAVA Spring", 10000, 10);
        int orderQty = 3;

        // when - 주문서 생성 및 주문서에 상품 추가 후 특정 상품 취소
        Long orderId = orderService.createOrder(member.getId());
        OrderItem orderItem = orderService.addOrderItem(orderId, item.getId(), orderQty);
        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId);
        Order getOrder = orderRepository.findById(orderId).orElseThrow();

        Long orderItemId = orderItem.getId();
        orderService.cancelOrderItem(orderItemId);

        // then - 아이템 주문 취소후 아이템 수량 및 주문서확인ㅖ
        assertEquals(10, item.getStockQuantity(), "취소 수량이 복구되지 않았습니다!");
        System.out.println("아이템수량 테스트 "+item.getStockQuantity());
        assertEquals(1, orderItemList.size(),"주문서까지 삭제 되었습니다!");
        System.out.println("주문서 확인 테스트 :"+orderItemList.size());
    }

    // ✅ 테스트를 위한 회원 생성 메서드
    private Member createMember() {
        Member member = new Member();
        member.changeName("회원1");
        member.changeEmail("user100@aaa.com");
        member.changePw(passwordEncoder.encode("1111"));
        // 영속성 컨텍스트에 저장 (EntityManager)
        em.persist(member);
        return member;
    }

    // ✅ 테스트를 위한 책 생성 메서드
    private Book createBook(String name, int price, int stockQty) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQty);
        // 영속성 컨텍스트에 저장 (EntityManager)
        em.persist(book);
        return book;
    }
}
