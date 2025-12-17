package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 고객 정보 DTO **/
@Getter
@Setter
@ToString
public class CustomerDTO {

    private int custid; // 고객 고유 ID (PK)

    @NotEmpty(message = "고객 이름은 필수 항목입니다.")
    private String name; // 고객 이름
    
    private String address; // 주소
    private String phone;   // 전화번호
    private String email;   // 이메일
}