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
	private final FileStorageService fileStorageService; // (신규) 파일 서비스 주입

	// 페이징 설정 값
	private static final int PAGE_SIZE = 10; // 페이지당 도서 수
	private static final int PAGE_BLOCK_SIZE = 5; // 페이지 블록 크기
	
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
		// 1. (신규) 이미지 파일 저장
		MultipartFile file = boardDTO.getCoverImageFile();
		String imagePath = fileStorageService.storeFile(file); // 저장 후 웹 경로 반환
		
		// 2. (신규) DTO에 이미지 경로 설정
		boardDTO.setCoverImagePath(imagePath);
		
		// 3. DB에 도서 정보 저장
		boardRepository.save(boardDTO);
	}
	
	/** 도서정보 상세보기 **/
	public BoardDTO detail(Integer id) {
		return boardRepository.detail(id);
	}
	
	/** 도서정보 삭제하기 **/
	@Transactional
	public void goDelete(Integer id) {
		// (참고) S3 등에 업로드했다면 DB 삭제 전에 파일을 먼저 삭제해야 함
		boardRepository.goDelete(id);
	}
	
	/** 도서정보 수정하기 (파일 업로드 추가) **/
	@Transactional
	public void goUpdate(BoardDTO boardDTO) {
	    
	    // 1. (신규) 새 이미지 파일이 업로드되었는지 확인
	    MultipartFile file = boardDTO.getCoverImageFile();
	    if (file != null && !file.isEmpty()) {
	        // (참고) 기존 파일이 있다면 삭제하는 로직이 필요할 수 있음
	        
	        // 2. 새 파일 저장
	        String imagePath = fileStorageService.storeFile(file);
	        
	        // 3. DTO에 새 이미지 경로 설정
	        boardDTO.setCoverImagePath(imagePath);
	    }
	    // 4. (참고) 새 파일이 없으면 DTO의 coverImagePath는
	    //    updateBook.html의 hidden input에 있던 '기존 경로'를 유지함.
	    
		boardRepository.goUpdate(boardDTO);
	}

}

