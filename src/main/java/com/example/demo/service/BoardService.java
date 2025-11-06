package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.example.demo.dto.BoardDTO;
import com.example.demo.repository.BoardRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
	
	private final BoardRepository boardRepository;
	
	/** 도서 목록 불러오기 **/
	public List<BoardDTO> getList() {
		return boardRepository.getList();
	}

	/** 도서 정보 추가하기 **/
	public void save(BoardDTO boardDTO) {
		boardRepository.save(boardDTO);
	}
	
	/** 도서정보 상세보기 **/
	public BoardDTO detail(Integer id) {
		return boardRepository.detail(id);
	}
	
	/** 도서정보 삭제하기 **/
	public void goDelete(Integer id) {
		boardRepository.goDelete(id);
		
	}
	
	/** 도서정보 수정하기 **/
	public void goUpdate(BoardDTO boardDTO) {
		boardRepository.goUpdate(boardDTO);
		
	}

}
