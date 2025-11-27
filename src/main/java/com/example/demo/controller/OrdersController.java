package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping;  
import com.example.demo.dto.CartDTO;

import com.example.demo.dto.BoardDTO;     
import com.example.demo.dto.CustomerDTO;   
import com.example.demo.dto.OrdersDTO;    
import com.example.demo.service.BoardService; 
import com.example.demo.service.CartService;	

import com.example.demo.dto.CustomerDTO;
import com.example.demo.service.OrdersService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;
    private final BoardService boardService;     
    private final CartService cartService;
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
    
    /** 개별 상품 주문 페이지 이동 (GET) **/
    @GetMapping("/addOrder/{bookid}")
    public String addOrderForm(@PathVariable("bookid") Integer bookid, Model model) {
        BoardDTO book = boardService.detail(bookid);       
        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setBookid(bookid);
        ordersDTO.setSaleprice(book.getPrice());
        
        model.addAttribute("book", book);
        model.addAttribute("ordersDTO", ordersDTO);
        
        return "addOrder";
    }

    /** 개별 상품 주문 처리 (POST) **/
    @PostMapping("/addOrder")
    public String addOrder(OrdersDTO ordersDTO, @AuthenticationPrincipal OAuth2User principal) {
        try {          
            if (principal == null) return "redirect:/login";
            String email = "kakao_" + principal.getAttributes().get("id");
            CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);
            ordersDTO.setCustid(customer.getCustid());

            ordersService.orderOne(ordersDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/list?error=orderFailed";
        }
        return "redirect:/orderList?msg=success";
    }

    /** 주문 페이지에서 장바구니 담기 처리 (POST) **/
    @PostMapping("/addOrder/cart")
    public String addCartFromOrder(OrdersDTO ordersDTO, @AuthenticationPrincipal OAuth2User principal) {
        
        // 로그인한 사용자 자동 설정
        if (principal == null) return "redirect:/login";
        String email = "kakao_" + principal.getAttributes().get("id");
        CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);
        
        CartDTO cart = new CartDTO();
        cart.setCustid(customer.getCustid()); // 로그인한 고객 ID 사용
        cart.setBookid(ordersDTO.getBookid());
        cart.setCount(1);
        
        cartService.addCart(cart);
        
        return "redirect:/cart";
    }
}