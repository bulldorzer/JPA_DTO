package com.korea.shop.batch;

import com.korea.shop.domain.Order;
import com.korea.shop.domain.OrderStatus;
import com.korea.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j // 콘솔창에 로그(기록) 출력하는 도구
@Component // 컨테이너가 관리하는 클래스
@RequiredArgsConstructor // 의존성 주입
public class OrderStatusScheduler {

    private final OrderRepository orderRepository;


    // 스프링 스케줄링 어노테이션 - 특정 주기마다 메서드를 실행하도록 함
    // 초, 분, 시, 일, 월, 요일
    // 0초 0분 0시, 매일, 매월, 특정요일 무관 == 매일 자정에 실행된다

    @Scheduled (cron = "0 0 0 * * ?")
    @Transactional
    public void cancelUnpaidOrders(){ // 미결제 주문 취소
        
        log.info("배치 작업 시작 : 미결제 주문 취소 실행");

        LocalDateTime now = LocalDateTime.now(); // 현재 시간 구함
        // 주문한지 1일이 지난 PENDING 상태 주문서
        List<Order> pendingOrders = orderRepository
                .findByStatusAndOrderDateBefore(OrderStatus.PENDING, now.minusDays(1) );

        for(Order order : pendingOrders){
            order.cancel();
            log.info("주문 id : {} 취소 완료", order.getId());
        }
        log.info("배치 작업 완료 : 미결제 주문 취소 - 작업 종료");
    }

}














