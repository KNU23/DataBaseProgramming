package com.example.demo.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 리뷰 DTO **/
@Getter @Setter @ToString
public class ReviewDTO {
    private int reviewId; // 리뷰 고유 ID (PK)
    private int bookid;   // 도서 ID (FK)
    private int custid;   // 작성자 ID (FK)
    private int rating;   // 별점 (1~5)
    private String content;   // 리뷰 내용
    private Timestamp regdate;// 작성 일시
    
    private String writerName; 
}