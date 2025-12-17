package com.example.demo.controller;

import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.service.AdminService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /** 관리자 대쉬보드 **/
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> data = adminService.getDashboardData();
        model.addAllAttributes(data); 
        
        return "admin/dashboard";
    }
}