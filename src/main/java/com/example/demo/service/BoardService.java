package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

// DTO 임포트
import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.BookListResponseDTO;
import com.example.demo.dto.PagingInfoDTO;
import com.example.demo.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	
	private final BoardRepository boardRepository;
	private final FileStorageService fileStorageService; 

	private static final int PAGE_SIZE = 10; 
	private static final int PAGE_BLOCK_SIZE = 5; 
	
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
}

