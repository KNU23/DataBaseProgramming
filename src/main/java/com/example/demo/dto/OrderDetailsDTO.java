package com.example.demo.dto;

import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

/** 주문 상세 내역 DTO **/
@Getter
@Setter
public class OrderDetailsDTO {
    
    private int orderid;   // 주문 ID
    private int saleprice; // 판매 금액
    private Date orderdate;// 주문 날짜

    // 조인을 통해 가져오는 부가 정보
    private String customerName; // 주문자 이름
    private String bookName;     // 책 제목
}