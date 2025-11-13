package com.example.demo.dto;

import java.sql.Date;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrdersDTO {
    
    private int orderid;

    @NotNull(message = "고객을 선택해주세요.")
    private Integer custid; 

    @NotNull
    private Integer bookid; 

    @NotNull(message = "판매 가격을 입력해주세요.")
    private Integer saleprice; 

    private Date orderdate; 

}
