package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DashboardDTO {
  
    private int totalUsers;      // 총 회원 수
    private int totalBooks;      // 총 도서 수
    private int totalOrders;     // 총 주문 건수 (취소 제외)
    private long totalRevenue;   // 총 매출액

    // 차트용 데이터 (SQL 결과 매핑용)
    private String label;        // x축 (날짜, 책 제목 등)
    private long value;          // y축 (매출, 판매량)
}
