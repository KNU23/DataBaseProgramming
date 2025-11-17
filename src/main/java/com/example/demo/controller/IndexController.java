package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.dto.BookListResponseDTO;
import com.example.demo.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class IndexController {
	
	private final BoardService boardService;
	
	@GetMapping("/")
	public String index(Model model) {
		log.info("메인 페이지 접속");
		BookListResponseDTO response = boardService.getList(null, 1, "bookid","desc");
		model.addAttribute("latestBooks", response.getBookList());
		return "index";
	}
	
	/** 커스텀 로그인 매핑 **/
	@GetMapping("/login")
	public String login() {
	    return "login"; 
	}
}
