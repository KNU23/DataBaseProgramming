package com.example.demo.dto;

import java.sql.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrdersDTO {
    
    private int orderid;     
    private int custid;      
    
    private int totalPrice;  
    private String status;   
    
    private Date orderdate;  
    
    private Integer bookid; 
    private Integer saleprice; 
}
