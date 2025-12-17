package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.http.ResponseEntity; // 추가됨
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping;  
import org.springframework.web.bind.annotation.RequestBody; 
import org.springframework.web.bind.annotation.ResponseBody; 

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.BoardDTO;     
import com.example.demo.dto.CustomerDTO;   
import com.example.demo.dto.OrdersDTO;    
import com.example.demo.service.BoardService; 
import com.example.demo.service.CartService;	
import com.example.demo.service.OrdersService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;
    private final BoardService boardService;     
    private final CartService cartService;
    private final SqlSessionTemplate sql; 

	/** 내 주문 내역 조회 **/
    @GetMapping("/orderList")
    public String orderList(Model model, @AuthenticationPrincipal OAuth2User principal) {
        
        /** 로그인 체크 **/
        if (principal == null) {
            return "redirect:/login";
        }

        /** 현재 로그인한 사용자 정보 찾기 **/
        String email = "kakao_" + principal.getAttributes().get("id");
        CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);
        
        /** 내 주문 내역 가져오기 **/
        List<Map<String, Object>> orderList = ordersService.getMyOrders(customer.getCustid());
        
        model.addAttribute("orderList", orderList);
        return "orderList";
    }
    
    /** 단건 주문 페이지 이동 **/
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

    /** 단건 주문 처리 **/
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
    
    /** 단건 주문 결제 처리 **/
    @PostMapping("/order/pay")
    @ResponseBody
    public ResponseEntity<String> orderPay(@RequestBody Map<String, Object> paymentData, 
                                           @AuthenticationPrincipal OAuth2User principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            /** 사용자 정보 가져오기 **/
            String email = "kakao_" + principal.getAttributes().get("id");
            CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);

            /** 클라이언트에서 보낸 데이터 추출 **/
            String impUid = (String) paymentData.get("imp_uid");
            int bookid = Integer.parseInt(String.valueOf(paymentData.get("bookid")));
            int amount = Integer.parseInt(String.valueOf(paymentData.get("amount")));

            /** OrdersDTO 생성 **/
            OrdersDTO ordersDTO = new OrdersDTO();
            ordersDTO.setCustid(customer.getCustid());
            ordersDTO.setBookid(bookid);
            ordersDTO.setSaleprice(amount); 
            ordersDTO.setImpUid(impUid);    

            /** 주문 서비스 호출 (재고 감소 및 주문 생성) **/
            ordersService.orderOne(ordersDTO);

            return ResponseEntity.ok("success");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("주문 처리 실패: " + e.getMessage());
        }
    }

    /** 주문 화면에서 장바구니 담기 **/
    @PostMapping("/addOrder/cart")
    public String addCartFromOrder(OrdersDTO ordersDTO, @AuthenticationPrincipal OAuth2User principal) {
        
        /** 로그인한 사용자 자동 설정 **/
        if (principal == null) return "redirect:/login";
        String email = "kakao_" + principal.getAttributes().get("id");
        CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);
        
        CartDTO cart = new CartDTO();
        cart.setCustid(customer.getCustid()); 
        cart.setBookid(ordersDTO.getBookid());
        cart.setCount(1);
        
        cartService.addCart(cart);
        
        return "redirect:/cart";
    }
}