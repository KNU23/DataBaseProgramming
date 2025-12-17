package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 페이징 정보 DTO **/
@Getter
@Setter
@ToString
public class PagingInfoDTO {
    
    private int page;           // 현재 페이지 번호
    private int totalPages;     // 전체 페이지 수
    private int startPage;      // 현재 블록의 시작 페이지 번호
    private int endPage;        // 현재 블록의 끝 페이지 번호
    private boolean hasPrev;    // '이전' 버튼 활성화 여부
    private boolean hasNext;    // '다음' 버튼 활성화 여부

    public PagingInfoDTO(int page, int totalCount, int pageSize, int pageBlockSize) {
        
        this.page = page;
        
        /** 전체 페이지 수 계산 **/
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);

        /** 현재 페이지 블록 계산 **/
        int currentBlock = (int) Math.ceil((double) page / pageBlockSize);
        
        /** 블록의 시작 페이지와 끝 페이지 계산 **/
        this.startPage = (currentBlock - 1) * pageBlockSize + 1;
        this.endPage = Math.min(startPage + pageBlockSize - 1, totalPages); 
        
        /** 이전/다음 버튼 유무 설정 **/
        this.hasPrev = startPage > 1;
        this.hasNext = endPage < totalPages;
        
        /** 요청한 페이지가 전체 페이지보다 클 경우 보정 **/
        if (this.page > this.totalPages && this.totalPages > 0) {
            this.page = this.totalPages;
        }
    }
}