package com.example.demo.dto;

import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailsDTO {
    
    // orders 테이블
    private int orderid;
    private int saleprice;
    private Date orderdate;

    // JOIN된 테이블
    private String customerName; 
    private String bookName;     
}
