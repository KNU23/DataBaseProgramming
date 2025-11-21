package com.example.demo.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AladinBookResponse {
    private List<Item> item;

    @Getter
    @Setter
    public static class Item {
        private String title;       // 책 제목
        private String author;      // 저자
        private String publisher;   // 출판사
        private Integer priceSales; // 판매가
        private String description; // 설명
        private String cover;       // 표지 이미지 URL (기본)
        
        // ★ 추가된 필드: 알라딘 평점 (10점 만점)
        private int customerReviewRank; 
    }
}