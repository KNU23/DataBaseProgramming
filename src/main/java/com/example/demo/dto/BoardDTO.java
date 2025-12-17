package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

/** 도서 정보 DTO **/
@Getter
@Setter
@ToString
public class BoardDTO {
	
	private int bookid; // 도서 고유 ID (PK)
	
	@NotEmpty(message = "책 제목은 필수 항목입니다.")
	private String bookname; // 책 제목
	
	@NotEmpty(message = "출판사명은 필수 항목입니다.")
	private String publisher; // 출판사
	
	@NotNull(message = "가격을 입력해주세요.")
	@Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
	private Integer price; // 가격
 
	@NotEmpty(message = "저자명은 필수 항목입니다.")
	private String author; // 저자
	
	@NotNull(message = "재고를 입력해주세요.")
	@Min(value = 0, message = "재고는 0 이상이어야 합니다.")
	private Integer stock; // 재고 수량
	
	private String description; // 도서 상세 설명

	private String coverImagePath; // 업로드된 이미지의 저장 경로
	
	private MultipartFile coverImageFile; 

}