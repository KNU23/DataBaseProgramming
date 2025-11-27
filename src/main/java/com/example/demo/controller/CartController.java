package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable; // 추가 필요

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.service.CartService;
import com.example.demo.service.OrdersService;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final OrdersService ordersService;
    private final SqlSessionTemplate sql; 

    /** 장바구니 담기 **/
    @PostMapping("/cart/add")
    public String addCart(@ModelAttribute CartDTO cartDTO, 
                          @AuthenticationPrincipal OAuth2User principal) {
        
        String email = "kakao_" + principal.getAttributes().get("id");
        CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);
        
        cartDTO.setCustid(customer.getCustid());
        cartService.addCart(cartDTO);
        
        return "redirect:/cart";
    }

    /** 장바구니 목록 페이지 **/
    @GetMapping("/cart")
    public String cartList(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return "redirect:/login";

        String email = "kakao_" + principal.getAttributes().get("id");
        CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);

        model.addAttribute("cartList", cartService.getCartList(customer.getCustid()));
        return "cartList";
    }

    /** 장바구니 주문하기 **/
    @PostMapping("/cart/order")
    @ResponseBody
    public ResponseEntity<String> orderCart(@AuthenticationPrincipal OAuth2User principal, 
                                          @RequestBody Map<String, String> paymentData) {
        try {
            String email = "kakao_" + principal.getAttributes().get("id");
            CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);
            
            String impUid = paymentData.get("imp_uid");
            
            ordersService.orderCart(customer.getCustid());
            
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /** 장바구니 항목 삭제 **/
    @GetMapping("/cart/delete/{cartId}")
    public String deleteCart(@PathVariable("cartId") int cartId) {
        
        cartService.deleteCart(cartId);
        
        return "redirect:/cart";
    }
}