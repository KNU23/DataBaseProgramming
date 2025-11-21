package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.service.OrdersService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;
    private final SqlSessionTemplate sql; 

	/** 내 주문 목록 페이지 **/
    @GetMapping("/orderList")
    public String orderList(Model model, @AuthenticationPrincipal OAuth2User principal) {
        
        // 로그인 체크
        if (principal == null) {
            return "redirect:/login";
        }

        // 현재 로그인한 사용자 정보(custid) 찾기
        String email = "kakao_" + principal.getAttributes().get("id");
        CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);
        
        // 내 주문 내역 가져오기
        List<Map<String, Object>> orderList = ordersService.getMyOrders(customer.getCustid());
        
        model.addAttribute("orderList", orderList);
        return "orderList";
    }
}