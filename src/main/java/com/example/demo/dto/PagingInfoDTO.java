package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PagingInfoDTO {
    
    private int page;           // 현재 페이지
    private int totalPages;     // 전체 페이지 수
    private int startPage;      // 페이지 블록 시작 번호
    private int endPage;        // 페이지 블록 끝 번호
    private boolean hasPrev;    // 이전 블록
    private boolean hasNext;    // 다음 블록

    /**
     * 페이징 UI에 필요한 값들을 계산
     * @param page 현재 페이지
     * @param totalCount 전체 도서 수
     * @param pageSize 페이지당 도서 수 (예: 10)
     * @param pageBlockSize 페이지 블록 크기 (예: 5)
     */
    public PagingInfoDTO(int page, int totalCount, int pageSize, int pageBlockSize) {
        
        this.page = page;
        
        // 1. 전체 페이지 수 계산
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // 2. 현재 페이지 블록의 시작/끝 페이지 계산
        int currentBlock = (int) Math.ceil((double) page / pageBlockSize);
        this.startPage = (currentBlock - 1) * pageBlockSize + 1;
        this.endPage = Math.min(startPage + pageBlockSize - 1, totalPages);
        
        // 3. 이전/다음 블록 존재 여부
        this.hasPrev = startPage > 1;
        this.hasNext = endPage < totalPages;
        
        // 4. 예외 처리: 현재 페이지가 전체 페이지 수보다 클 경우
        if (this.page > this.totalPages && this.totalPages > 0) {
            this.page = this.totalPages;
        }
    }
}