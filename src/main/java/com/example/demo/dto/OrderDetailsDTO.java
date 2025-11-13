package com.example.demo.dto;

import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailsDTO {
    
    private int orderid;
    private int saleprice;
    private Date orderdate;

    private String customerName; 
    private String bookName;     
}
