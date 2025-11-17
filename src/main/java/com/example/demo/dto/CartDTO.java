package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CartDTO {
    private int cartId;
    private int custid;
    private int bookid;
    private int count;
    
    private String bookname;
    private int price;
    private String coverImagePath;
    private int totalPrice; 
}