package com.example.demo.dto;

import java.sql.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 주문 정보 DTO **/
@Getter
@Setter
@ToString
public class OrdersDTO {
    
    private int orderid;     // 주문 고유 ID (PK)
    private int custid;      // 주문한 고객 ID (FK)
    
    private int totalPrice;  // 총 주문 금액
    private String status;   // 주문 상태
    
    private Date orderdate;  // 주문 일자
    
    // 단건 주문(바로 구매) 처리를 위한 임시 필드
    private Integer bookid;    // 주문할 책 ID
    private Integer saleprice; // 판매 가격
    
    private String impUid; // 결제 고유 번호
}