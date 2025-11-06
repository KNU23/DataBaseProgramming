package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class BoardDTO {
	
	private int bookid;
	
	@NotEmpty(message = "책 제목은 필수 항목입니다.")
	private String bookname;
	
	@NotEmpty(message = "출판사명은 필수 항목입니다.")
	private String publisher;
	
	@NotNull(message = "가격을 입력해주세요.")
	@Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
	private Integer price; // int -> Integer

	@NotEmpty(message = "저자명은 필수 항목입니다.")
	private String author;
	
	@NotNull(message = "재고를 입력해주세요.")
	@Min(value = 0, message = "재고는 0 이상이어야 합니다.")
	private Integer stock;
	
	private String description;

	// 1. DB에 저장될 이미지 경로 (예: /uploads/UUID.jpg)
	private String coverImagePath;
	
	// 2. 폼에서 받을 이미지 파일 (DB 저장 X)
	private MultipartFile coverImageFile;

}

