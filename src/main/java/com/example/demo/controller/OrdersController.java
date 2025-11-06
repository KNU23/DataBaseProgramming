package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.OrderDetailsDTO;
import com.example.demo.dto.OrdersDTO;
import com.example.demo.service.BoardService;
import com.example.demo.service.CustomerService;
import com.example.demo.service.OrdersService;

import jakarta.validation.Valid; // (1. import 추가)
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;
    private final BoardService boardService;
    private final CustomerService customerService;

    /**
     * 주문 목록 페이지 (GET)
     */
    @GetMapping("/orderList")
    public String orderList(Model model) {
        List<OrderDetailsDTO> orderList = ordersService.getOrderList();
        model.addAttribute("orderList", orderList);
        return "orderList";
    }

    /**
     * 신규 주문 페이지 (GET)
     */
    @GetMapping("/addOrder/{bookid}")
    public String addOrderForm(@PathVariable("bookid") int bookid, Model model) {
        
        // 1. 주문할 도서 정보
        BoardDTO book = boardService.detail(bookid);
        model.addAttribute("book", book);

        // 2. 고객 선택 목록
        List<CustomerDTO> customers = customerService.getAll();
        model.addAttribute("customers", customers);

        // 3. 폼과 바인딩할 빈 OrdersDTO 객체
        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setBookid(book.getBookid());
        ordersDTO.setSaleprice(book.getPrice());
        model.addAttribute("ordersDTO", ordersDTO);

        return "addOrder";
    }

    /**
     * 신규 주문 처리 (POST)
     */
    @PostMapping("/addOrder")
    public String submitOrder(
            // (2. @Valid 및 BindingResult 추가)
            @Valid @ModelAttribute("ordersDTO") OrdersDTO ordersDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) { // (3. Model 추가)

        // (4. 유효성 검사 실패 시)
        if (bindingResult.hasErrors()) {
            // 폼을 다시 로드하기 위해 도서 정보와 고객 목록을 다시 조회해야 함
            BoardDTO book = boardService.detail(ordersDTO.getBookid());
            List<CustomerDTO> customers = customerService.getAll();
            model.addAttribute("book", book);
            model.addAttribute("customers", customers);
            return "addOrder"; // 에러 메시지와 함께 폼 뷰로 다시 이동
        }

        try {
            // (5. 유효성 검사 통과 시)
            ordersService.createOrder(ordersDTO);
            redirectAttributes.addFlashAttribute("msg", "신규 주문이 성공적으로 처리되었습니다.");
            return "redirect:/list"; // 성공 시 목록으로

        } catch (Exception e) { // (6. IllegalStateException -> Exception으로 확장)
            String errorMessage = "주문 처리 중 오류가 발생했습니다.";
            if (e instanceof IllegalStateException) {
                errorMessage = e.getMessage(); // "재고가 부족합니다"
            }
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/list"; // 실패 시 목록으로
        }
    }
}

