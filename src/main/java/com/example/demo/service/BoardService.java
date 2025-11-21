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

	/** 도서 정보 추가하기 (중복 방지 로직 적용) **/
	@Transactional
	public void save(BoardDTO boardDTO) {
		MultipartFile file = boardDTO.getCoverImageFile();
		
		if (file != null && !file.isEmpty()) {
			String imagepath = fileStorageService.storeFile(file);
			boardDTO.setCoverImagePath(imagepath);
		}
		
        // 중복 도서 확인 (제목 기준)
        BoardDTO existingBook = boardRepository.findByBookname(boardDTO.getBookname());

        if (existingBook != null) {
            // 이미 존재하면 저장하지 않고 기존 ID 사용
            boardDTO.setBookid(existingBook.getBookid());
        } else {
            // 없으면 신규 저장
            boardRepository.save(boardDTO);
        }
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
     * 인기 도서 목록 갱신 (데이터 보존 모드)
     * - 장바구니, 주문내역 삭제하지 않음
     * - 기존에 있는 책은 정보를 업데이트하고, 없는 책만 추가함
     */
	@Scheduled(cron = "0 0/30 * * * *") 
    @Transactional
    public void refreshToPopularBooks() {
        System.out.println("[스케줄러 실행] 알라딘 인기 도서 정보 갱신 (기존 데이터 유지)...");

        // [삭제 코드 제거됨] 
        // sql.delete("Cart.deleteAll"); ... 등등 삭제

        // 1. 알라딘 API 호출 (100권)
        List<BoardDTO> newBooks = bookApiService.fetchBooks("Bestseller", 100);
        
        int updatedCount = 0;
        int newCount = 0;

        // 2. 하나씩 확인하며 갱신
        for (BoardDTO newBook : newBooks) {
            // 이미 등록된 책인지 확인
            BoardDTO existingBook = boardRepository.findByBookname(newBook.getBookname());

            if (existingBook != null) {
                // (1) 이미 있으면 -> 기존 ID를 유지한 채로 내용만 최신으로 업데이트
                newBook.setBookid(existingBook.getBookid());
                boardRepository.goUpdate(newBook);
                updatedCount++;
            } else {
                // (2) 없으면 -> 새로 추가
                boardRepository.save(newBook);
                newCount++;
            }
        }
        
        System.out.println("[갱신 완료] 업데이트: " + updatedCount + "권, 신규 추가: " + newCount + "권");
    }
}