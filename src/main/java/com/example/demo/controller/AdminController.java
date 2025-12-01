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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        // 서비스에서 가져온 Map(지표 + 리스트)을 그대로 모델에 담습니다.
        Map<String, Object> data = adminService.getDashboardData();
        model.addAllAttributes(data); 
        
        // 모델에는 "dailySales" (List<DashboardDTO>) 와 
        // "topBooks" (List<DashboardDTO>)가 들어있습니다.
        
        return "admin/dashboard";
    }
}