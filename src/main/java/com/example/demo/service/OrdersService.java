package com.example.demo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.OrderDetailsDTO;
import com.example.demo.dto.OrdersDTO;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final BoardRepository boardRepository; 

    /**
     * 주문 목록 조회
     */
    public List<OrderDetailsDTO> getOrderList() {
        return ordersRepository.getAllWithDetails();
    }

    /**
     * (핵심) 신규 주문 생성 (트랜잭션)
     */
    @Transactional 
    public void createOrder(OrdersDTO ordersDTO) throws Exception {
        
        // 1. 현재 도서 재고 확인
        BoardDTO book = boardRepository.detail(ordersDTO.getBookid());
        if (book == null || book.getStock() <= 0) {
            // (중요) 재고가 없으면 예외를 발생시켜 트랜잭션 롤백
            throw new Exception("재고가 부족하거나 존재하지 않는 도서입니다. (ID: " + ordersDTO.getBookid() + ")");
        }

        // 2. 재고 차감 (UPDATE)
        // (중요) WHERE stock > 0 조건으로 인해, 
        // 동시에 여러 주문이 들어와도 재고 이상으로 차감되지 않음.
        int updatedRows = boardRepository.decreaseStock(ordersDTO.getBookid());
        
        if (updatedRows == 0) {
            // 재고 차감에 실패 (동시성 문제 등)
             throw new Exception("재고 차감에 실패했습니다. (ID: " + ordersDTO.getBookid() + ")");
        }

        // 3. 주문 날짜 설정 (오늘)
        ordersDTO.setOrderdate(new java.sql.Date(System.currentTimeMillis()));

        // 4. 주문 생성 (INSERT)
        ordersRepository.insert(ordersDTO);

    }
}
