package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.example.demo.dto.DashboardDTO;
import com.example.demo.repository.AdminRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    
    /** 관리자 대시보드 데이터 조회 **/
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();
        
        /** 카드 지표 **/
        data.put("totalUsers", adminRepository.countUsers());
        data.put("totalBooks", adminRepository.countBooks());
        data.put("totalOrders", adminRepository.countOrders());
        data.put("totalRevenue", adminRepository.totalRevenue());
        
        /** 차트 데이터 **/
        data.put("dailySales", adminRepository.getDailySales());
        data.put("topBooks", adminRepository.getTopSellingBooks());
        
        return data;
    }
}
