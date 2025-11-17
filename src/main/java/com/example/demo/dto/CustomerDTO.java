package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CustomerDTO {

    private int custid;

    @NotEmpty(message = "고객 이름은 필수 항목입니다.")
    private String name;
    
    private String address;
    private String phone;
    private String email;
}
