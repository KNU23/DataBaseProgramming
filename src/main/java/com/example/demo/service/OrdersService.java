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

	/** 주문 목록 조회 **/
    public List<OrderDetailsDTO> getOrderList() {
        return ordersRepository.getAllWithDetails();
    }

	/** 신규 주문 생성 **/
    @Transactional 
    public void createOrder(OrdersDTO ordersDTO) throws Exception {
        
        BoardDTO book = boardRepository.detail(ordersDTO.getBookid());
        if (book == null || book.getStock() <= 0) {
            throw new Exception("재고가 부족하거나 존재하지 않는 도서입니다. (ID: " + ordersDTO.getBookid() + ")");
        }

        int updatedRows = boardRepository.decreaseStock(ordersDTO.getBookid());
        
        if (updatedRows == 0) {
             throw new Exception("재고 차감에 실패했습니다. (ID: " + ordersDTO.getBookid() + ")");
        }

        ordersDTO.setOrderdate(new java.sql.Date(System.currentTimeMillis()));
        ordersRepository.insert(ordersDTO);

    }
}
