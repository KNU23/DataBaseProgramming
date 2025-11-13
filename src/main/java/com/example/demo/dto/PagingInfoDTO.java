package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PagingInfoDTO {
    
    private int page;           
    private int totalPages;     
    private int startPage;      
    private int endPage;        
    private boolean hasPrev;   
    private boolean hasNext;    

    public PagingInfoDTO(int page, int totalCount, int pageSize, int pageBlockSize) {
        
        this.page = page;
        
        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);

        int currentBlock = (int) Math.ceil((double) page / pageBlockSize);
        this.startPage = (currentBlock - 1) * pageBlockSize + 1;
        this.endPage = Math.min(startPage + pageBlockSize - 1, totalPages);
        
        this.hasPrev = startPage > 1;
        this.hasNext = endPage < totalPages;
        
        if (this.page > this.totalPages && this.totalPages > 0) {
            this.page = this.totalPages;
        }
    }
}