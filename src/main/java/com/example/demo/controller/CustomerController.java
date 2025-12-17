package com.example.demo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") 
public class CustomerController {

    private final CustomerService customerService;

    /** 고객 목록 조회 **/
    @GetMapping("/customers")
    public String customerList(Model model) {
        List<CustomerDTO> customerList = customerService.getAll();
        model.addAttribute("customerList", customerList);
        return "customerList"; 
    }

    /** 고객 등록 페이지 이동 **/
    @GetMapping("/addCustomer")
    public String addCustomerForm(Model model) {
        model.addAttribute("customerDTO", new CustomerDTO());
        return "addCustomer"; 
    }

    /** 고객 등록 처리 **/
    @PostMapping("/addCustomer")
    public String addCustomer(@Valid CustomerDTO customerDTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "addCustomer";
        }

        customerService.insert(customerDTO);
        redirectAttributes.addFlashAttribute("msg", "새 고객이 등록되었습니다.");
        return "redirect:/customers";
    }

}

