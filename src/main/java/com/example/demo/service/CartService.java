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

    public void addCart(CartDTO cartDTO) {
        sql.insert("Cart.addCart", cartDTO);
    }

    public List<CartDTO> getCartList(int custid) {
        return sql.selectList("Cart.getCartList", custid);
    }

    public void deleteCart(int cartId) {
        sql.delete("Cart.deleteCart", cartId);
    }
}