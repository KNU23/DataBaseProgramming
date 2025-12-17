package com.example.demo.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 도서 목록 응답 DTO **/
@Getter
@RequiredArgsConstructor
public class BookListResponseDTO {

    private final List<BoardDTO> bookList; // 조회된 도서 목록
    private final PagingInfoDTO pagingInfo;  // 계산된 페이징 정보
}