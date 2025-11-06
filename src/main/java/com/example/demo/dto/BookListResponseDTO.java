package com.example.demo.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookListResponseDTO {

    private final List<BoardDTO> bookList;
    private final PagingInfoDTO pagingInfo;
    
}
