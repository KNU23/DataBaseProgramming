package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.BookListResponseDTO;
import com.example.demo.dto.PagingInfoDTO;
import com.example.demo.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	
	private final BoardRepository boardRepository;
	private final BookApiService bookApiService;
	private final FileStorageService fileStorageService; 

	private static final int PAGE_SIZE = 10; 
	private static final int PAGE_BLOCK_SIZE = 5; 
	private final SqlSessionTemplate sql;
	
	/** 도서 목록 불러오기 (페이징 + 검색 + 정렬) **/
	public BookListResponseDTO getList(String keyword, int page, String sort, String order) {
		
		if (page <= 0) {
		    page = 1;
		}
		int offset = (page - 1) * PAGE_SIZE;
		int totalCount = boardRepository.countList(keyword);
		List<BoardDTO> bookList = boardRepository.getList(keyword, offset, PAGE_SIZE, sort, order);
		PagingInfoDTO pagingInfo = new PagingInfoDTO(page, totalCount, PAGE_SIZE, PAGE_BLOCK_SIZE);

		return new BookListResponseDTO(bookList, pagingInfo);
	}

	/** 도서 정보 추가하기 (파일 업로드 추가) **/
	@Transactional
	public void save(BoardDTO boardDTO) {
		MultipartFile file = boardDTO.getCoverImageFile();
		
		if (file != null && !file.isEmpty()) {
			String imagepath = fileStorageService.storeFile(file);
			boardDTO.setCoverImagePath(imagepath);
		}
		
		boardRepository.save(boardDTO);
	}
	
	/** 도서정보 상세보기 **/
	public BoardDTO detail(Integer id) {
		return boardRepository.detail(id);
	}
	
	/** 도서정보 삭제하기 **/
	@Transactional
	public void goDelete(Integer id) {
		boardRepository.goDelete(id);
	}
	
	/** 도서정보 수정하기 **/
	@Transactional
	public void goUpdate(BoardDTO boardDTO) {
	    
	    MultipartFile file = boardDTO.getCoverImageFile();
	    if (file != null && !file.isEmpty()) {
	    	
	        String imagePath = fileStorageService.storeFile(file);
	        
	        boardDTO.setCoverImagePath(imagePath);
	    }
	    
		boardRepository.goUpdate(boardDTO);
	}
	
	/**
     * 인기 도서 100권으로 목록 초기화
     * @Scheduled
     * (사용자가 없더라도 서버가 켜져 있으면 실행됨)
     */
	@Scheduled(cron = "0 0/30 * * * *") 
    @Transactional
    public void refreshToPopularBooks() {
        System.out.println("[스케줄러 실행] 도서 목록을 베스트셀러 100권으로 재설정합니다...");

        // 1. 장바구니 비우기
        sql.delete("Cart.deleteAll"); 
        
        // 2. 주문 상세 내역 비우기
        sql.delete("Orders.deleteAllDetails"); 
        
        // 3. 주문 내역 비우기
        sql.delete("Orders.deleteAllOrders");

        // 4. 그 다음, 책 전체 삭제
        boardRepository.deleteAll();

        // 카카오 API 호출 및 저장
        List<BoardDTO> newBooks = bookApiService.fetchBooks("베스트셀러", 100);
        for (BoardDTO book : newBooks) {
            boardRepository.save(book);
        }
        
        System.out.println("[스케줄러 완료] 갱신 완료.");
    }
}

