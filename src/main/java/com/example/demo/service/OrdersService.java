package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.OrdersDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final SqlSessionTemplate sql;
    private final CartService cartService;

    /** 장바구니 모든 상품 주문 **/
    @Transactional
    public void orderCart(int custid) throws Exception {
        
        // 장바구니 목록 가져오기
        List<CartDTO> cartList = cartService.getCartList(custid);
        if (cartList.isEmpty()) {
            throw new Exception("장바구니가 비어있습니다.");
        }

        // 총 가격 계산 및 재고 체크
        int totalPrice = 0;
        for (CartDTO cart : cartList) {
            totalPrice += cart.getTotalPrice();
            int result = sql.update("Board.decreaseStock", cart.getBookid()); 
            if(result == 0) throw new Exception("재고 부족: 도서 ID " + cart.getBookid());
        }

        // 주문정보 생성 (Orders)
        OrdersDTO order = new OrdersDTO();
        order.setCustid(custid);
        order.setTotalPrice(totalPrice);
        sql.insert("Orders.insertOrder", order); 

        // 주문 상세정보 저장 (OrderDetails)
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", order.getOrderid());
        params.put("details", cartList);
        sql.insert("Orders.insertOrderDetails", params);

        // 장바구니 비우기
        sql.delete("Cart.clearCart", custid);
    }
    
    // 내 주문 내역 조회
    public List<Map<String, Object>> getMyOrders(int custid) {
        return sql.selectList("Orders.getMyOrders", custid);
    }
}