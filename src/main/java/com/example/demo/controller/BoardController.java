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

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	
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
	    // 폼 바인딩을 위해 빈 DTO를 모델에 추가
		model.addAttribute("boardDTO", new BoardDTO());
		return "addBook";
	}
	
	
	@PostMapping("/addBook")
	// 파일 업로드를 위해 @ModelAttribute 명시
	public String save(@Valid @ModelAttribute BoardDTO boardDTO, 
	                   BindingResult bindingResult, 
	                   RedirectAttributes redirectAttributes) {
		
		// 유효성 검사 실패 시, 폼 페이지로 다시 이동
		if (bindingResult.hasErrors()) {
			return "addBook"; // 입력했던 내용을 유지한 채 addBook.html 뷰를 반환
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
		// 폼 바인딩을 위해 "boardDTO" 대신 "bookDetail" 이름으로 모델에 추가
		model.addAttribute("bookDetail", boardDTO);
		return "updateBook";
	}
	
	/** 도서정보 수정 및 저장 */
	@PostMapping("/goUpdate/{id}")
	// 파일 업로드를 위해 @ModelAttribute 명시 (객체 이름을 "bookDetail"로 지정)
	public String goUpdate(@PathVariable("id") Integer id,
	                       @Valid @ModelAttribute("bookDetail") BoardDTO boardDTO, 
	                       BindingResult bindingResult, 
	                       RedirectAttributes redirectAttributes,
	                       Model model) {
		
		boardDTO.setBookid(id); // ID 설정

        // 유효성 검사 실패 시
		if (bindingResult.hasErrors()) {
			// model.addAttribute("bookDetail", boardDTO); // @ModelAttribute("bookDetail")이 이 역할을 대신함
			return "updateBook"; // 수정 폼 뷰로 다시 이동
		}
		
		boardService.goUpdate(boardDTO);
		redirectAttributes.addFlashAttribute("msg", "도서(ID: " + boardDTO.getBookid() + ") 정보가 수정되었습니다.");
		return "redirect:/list";
	}
	
}

