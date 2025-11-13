package com.example.demo.repository;

import java.util.List;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import com.example.demo.dto.OrderDetailsDTO;
import com.example.demo.dto.OrdersDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrdersRepository {

    private final SqlSessionTemplate sql;

	/** 주문 생성 **/
    public void insert(OrdersDTO ordersDTO) {
        sql.insert("Orders.insert", ordersDTO);
    }

	/** 주문 목록 상세 조회 **/
    public List<OrderDetailsDTO> getAllWithDetails() {
        return sql.selectList("Orders.getAllWithDetails");
    }
}
