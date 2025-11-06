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
    private Integer custid; // 고객 ID

    @NotNull
    private Integer bookid; // 도서 ID

    @NotNull(message = "판매 가격을 입력해주세요.")
    private Integer saleprice; // 판매 가격

    private Date orderdate; // 주문 날짜

}
