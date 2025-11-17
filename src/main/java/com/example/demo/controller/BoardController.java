package com.example.demo.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.BookListResponseDTO;
import com.example.demo.service.BoardService;
import com.example.demo.service.BookApiService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	private final BookApiService bookApiService;
	
	@GetMapping("/list")
	public String getList(Model model, 
	                      @RequestParam(value = "keyword", required = false) String keyword,
	                      @RequestParam(value = "page", defaultValue = "1") int page,
	                      @RequestParam(value = "sort", defaultValue = "bookid") String sort,
	                      @RequestParam(value = "order", defaultValue = "asc") String order) {
		
		BookListResponseDTO response = boardService.getList(keyword, page, sort, order);
		
		model.addAttribute("bookList", response.getBookList());
		model.addAttribute("pagingInfo", response.getPagingInfo());
		model.addAttribute("keyword", keyword);
		model.addAttribute("sort", sort);
		model.addAttribute("order", order);
		
		return "bookList";
	}
	
	@GetMapping("/addBook")
	public String addBook(Model model) {
		model.addAttribute("boardDTO", new BoardDTO());
		return "addBook";
	}
	
	
	@PostMapping("/addBook")
	public String save(@Valid @ModelAttribute BoardDTO boardDTO, 
	                   BindingResult bindingResult, 
	                   RedirectAttributes redirectAttributes) {
		
		if (bindingResult.hasErrors()) {
			return "addBook"; 
		}
		
		boardService.save(boardDTO);
		redirectAttributes.addFlashAttribute("msg", "새 도서가 성공적으로 등록되었습니다.");
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
	public String goDelete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		boardService.goDelete(id);
		redirectAttributes.addFlashAttribute("msg", "도서(ID: " + id + ") 정보가 삭제되었습니다.");
		return "redirect:/list";
	}
	
	/** 도서정보 수정화면 호출 **/
	@GetMapping("/goUpdate/{id}")
	public String goUpdate(@PathVariable("id") Integer id, Model model) {
		BoardDTO boardDTO = boardService.detail(id);
		model.addAttribute("bookDetail", boardDTO);
		return "updateBook";
	}
	
	/** 도서정보 수정 및 저장 **/
	@PostMapping("/goUpdate/{id}")
	public String goUpdate(@PathVariable("id") Integer id,
	                       @Valid @ModelAttribute("bookDetail") BoardDTO boardDTO, 
	                       BindingResult bindingResult, 
	                       RedirectAttributes redirectAttributes,
	                       Model model) {
		
		boardDTO.setBookid(id);

		if (bindingResult.hasErrors()) {
			return "updateBook"; 
		}
		
		boardService.goUpdate(boardDTO);
		redirectAttributes.addFlashAttribute("msg", "도서(ID: " + boardDTO.getBookid() + ") 정보가 수정되었습니다.");
		return "redirect:/list";
	}
	
	/** 도서 검색 페이지 이동 **/
	@GetMapping("/search")
	public String searchPage() {
		return "searchBook";
	}
	
	/** 도서 검색 수행 **/
	@PostMapping("/search")
	public String search(@RequestParam("keyword") String keyword, Model model) {
		List<BoardDTO> books = bookApiService.searchBooks(keyword);
	
		model.addAttribute("books", books);
		model.addAttribute("keyword", keyword);
		
		return "searchBook";
	}
	
	/** API 검색 결과 DB 저장 **/
	@PostMapping("/addApiBook")
	public String addApiBook(@ModelAttribute BoardDTO boardDTO, RedirectAttributes redirectAttributes) {
		
		boardService.save(boardDTO);
		
		redirectAttributes.addFlashAttribute("msg", "도서가 성공적으로 등록되었습니다.");
		return "redirect:/list";
	}
}

