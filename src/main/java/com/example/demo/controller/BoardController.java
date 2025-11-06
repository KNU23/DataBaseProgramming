package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.dto.BoardDTO;
import com.example.demo.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor

public class BoardController {
	
	private final BoardService boardService;
	
	@GetMapping("/list")
	public String getList(Model model) {
		List<BoardDTO> boardDTOList = boardService.getList();
		model.addAttribute("bookList", boardDTOList);
		
		return "bookList";
	}
	
	@GetMapping("/addBook")
	public String addBook() {
		return "addBook";
		
	}
	
	
	@PostMapping("/addBook")
	public String save(BoardDTO boardDTO) {
		boardService.save(boardDTO);
		return "redirect:/list";
	}
	
	/** 도서정보 상세보기 **/
	@GetMapping("/bookid/{id}")
	public String detail(@PathVariable("id") Integer id, Model model) {
		BoardDTO boardDTO = boardService.detail(id);
		model.addAttribute("bookDetail", boardDTO);
		return "detailBook";
	}
	
	/** 도서정보 삭제하기 **/
	@GetMapping("/goDelete/{id}")
	public String goDelete(@PathVariable("id") Integer id) {
		boardService.goDelete(id);
		// 게시글 삭제 후 목록 페이지 이동
		return "redirect:/list";
	}
	
	/** 도서정보 수정화면 호출 **/
	@GetMapping("/goUpdate/{id}")
	public String goUpdate(@PathVariable("id") Integer id, Model model) {
		BoardDTO boardDTO = boardService.detail(id);
		model.addAttribute("bookDetail", boardDTO);
		return "updateBook";
	}
	
	/** 도서정보 수정 및 저장 */
	@PostMapping("/goUpdate/{id}")
	public String goUpdate(BoardDTO boardDTO, Model model) {
		
		// (1) 도서정보 수정
		boardService.goUpdate(boardDTO);
		
		// (2) 도서정보 수정 후, 수정된 내용을 다시 조회
		BoardDTO dto = boardService.detail(boardDTO.getBookid());
		model.addAttribute("bookDetail", dto);
		
		
		
		return "detailBook";
	}
	

}
