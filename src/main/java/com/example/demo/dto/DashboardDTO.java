package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

/** 관리자 대시보드 DTO **/

@Getter @Setter
public class DashboardDTO {
  
    // 상단 카드형 통계 데이터
    private int totalUsers;      // 총 회원 수
    private int totalBooks;      // 총 도서 수
    private int totalOrders;     // 총 주문 건수
    private long totalRevenue;   // 총 매출액

    // 차트용 데이터 (SQL 결과 매핑용)
    private String label;        // x축 라벨 (날짜, 책 제목 등)
    private long value;          // y축 값 (매출액, 판매량 등)
}