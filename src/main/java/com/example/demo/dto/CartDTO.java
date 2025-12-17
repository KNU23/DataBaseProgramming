package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 장바구니 DTO **/
@Getter @Setter @ToString
public class CartDTO {
    private int cartId; // 장바구니 항목 고유 ID (PK)
    private int custid; // 고객 ID (FK)
    private int bookid; // 도서 ID (FK)
    private int count;  // 담은 수량
    
    // 도서 테이블(Book)과 조인하여 가져오는 정보
    private String bookname;       // 책 제목
    private int price;             // 책 가격
    private String coverImagePath; // 책 표지 이미지 경로
    private int totalPrice;        // 총 가격
}