package com.korea.shop.service;

import com.korea.shop.domain.*;
import com.korea.shop.domain.item.Item;
import com.korea.shop.dto.CustomPage;
import com.korea.shop.dto.ItemDTO;
import com.korea.shop.dto.OrderDTO;
import com.korea.shop.repository.ItemRepository;
import com.korea.shop.repository.MemberRepository;
import com.korea.shop.repository.OrderItemRepository;
import com.korea.shop.repository.OrderRepository;
import com.korea.shop.util.NoDataFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional  // ✅ 클래스 레벨에서 전체 트랜잭션 적용
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    // 주문 생성 (아이템 추가 X)
    @Override
    public Long createOrder(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 없음"));

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        orderRepository.save(order);
        return order.getId();
    }

    // 주문서에 아이템 추가
    @Override
    public OrderItem addOrderItem(Long orderId, Long itemId, int qty) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문 없음"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품 없음"));

        if (qty <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");

        OrderItem orderItem = OrderItem.createOrderItem(order, item, item.getPrice(), qty);
        return orderItemRepository.save(orderItem);
    }

    // 전체 주문 조회
    @Override
    @Transactional(readOnly = true)  // ✅ 읽기 전용 트랜잭션 적용
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAllByFetch()
                .stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    // 전체 주문 상품 페이징처리
    @Override
    public CustomPage<OrderDTO> getAllItemsPaged(Pageable pageable) {

        // order엔티티를 페이지네이션된 형태로 조회
        Page<Order> orderPage = orderRepository.findAll(pageable);

        // 조회된 페이지가 없으면 예외처리
        if (orderPage.isEmpty()){
            throw new NoDataFoundException("조회된 데이터가 없습니다.");
        }

        // 엔티티 -> DTO 로 변환
        Page<OrderDTO> dtoPage = orderPage.map(order -> modelMapper.map(order, OrderDTO.class));
        log.info("======<OrderDtoPage>======");
        log.info(dtoPage.getContent());

        // DTO에 페이지 네이션 정보 추가 (별도의 DTO 만들기)
        int groupSize = 10;
        return CustomPage.of(dtoPage, groupSize);
    }

    // 특정 주문 아이템 삭제 (취소)
    @Override
    public void cancelOrderItem(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문 아이템 없음"));

        orderItem.cancel();         // 주문서 아이템을 삭제하면서 item 테이블의 수량을 하나 올려줌
        orderItem.setOrder(null);   // ✅ 관계 해제 주문서에 등록된 item 하나의 튜플의 관계를 없앰
        orderItemRepository.delete(orderItem); // 주문서에 등록된 주문 취소할 아이템을 삭제
    }

    // 전체 주문 취소
    @Override
    public void cancelAllOrderItems(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문 없음"));

        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId);
        if (orderItemList.isEmpty()) throw new IllegalArgumentException("취소할 주문 아이템 없음");

        order.cancel();

        for (OrderItem orderItem : orderItemList) {
            orderItem.cancel();
            orderItem.setOrder(null);  // ✅ 관계 해제
            orderItemRepository.delete(orderItem);
        }
    }
}
