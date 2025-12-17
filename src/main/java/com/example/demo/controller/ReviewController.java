package com.example.demo.controller;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.dto.ReviewDTO;
import com.example.demo.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SqlSessionTemplate sql;

    @PostMapping("/review/add")
    public String addReview(ReviewDTO reviewDTO, 
                            @AuthenticationPrincipal OAuth2User principal,
                            RedirectAttributes redirectAttributes) {
        
        if (principal == null) {
            return "redirect:/login";
        }

        String email = "kakao_" + principal.getAttributes().get("id");
        CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);
        
        reviewDTO.setCustid(customer.getCustid());
        
        reviewService.addReview(reviewDTO);
        
        redirectAttributes.addFlashAttribute("msg", "리뷰가 등록되었습니다.");
        return "redirect:/bookid/" + reviewDTO.getBookid();
    }
}
