package com.example.demo.service;

import java.util.List;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import com.example.demo.dto.CartDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
    private final SqlSessionTemplate sql;

    /** 장바구니 담기 **/
    public void addCart(CartDTO cartDTO) {
        sql.insert("Cart.addCart", cartDTO);
    }
    
    /** 장바구니 목록 조회 **/
    public List<CartDTO> getCartList(int custid) {
        return sql.selectList("Cart.getCartList", custid);
    }
    
    /** 장바구니 항목 삭제 **/
    public void deleteCart(int cartId) {
        sql.delete("Cart.deleteCart", cartId);
    }
}