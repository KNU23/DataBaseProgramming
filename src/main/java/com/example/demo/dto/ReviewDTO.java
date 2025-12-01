package com.example.demo.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ReviewDTO {
    private int reviewId;
    private int bookid;
    private int custid;
    private int rating;
    private String content;
    private Timestamp regdate;
    
    // 조인을 통해 가져올 작성자 이름
    private String writerName; 
}
