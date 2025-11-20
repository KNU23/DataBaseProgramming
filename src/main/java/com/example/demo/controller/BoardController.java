package com.example.demo.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
import com.example.demo.dto.CartDTO;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.service.BoardService;
import com.example.demo.service.BookApiService;
import com.example.demo.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	private final BookApiService bookApiService;
	private final CartService cartService; // <-- 1. 주입
    private final SqlSessionTemplate sql; // <-- 1. 주입
	
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
	
	/** API 검색 결과 DB 저장 (및 장바구니 추가) **/
	@PostMapping("/addApiBook")
	public String addApiBook(@ModelAttribute BoardDTO boardDTO, 
                             RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal OAuth2User principal) { // <-- 2. 매개변수 추가
		
		// 1. (기존 로직) API로 검색한 책을 우리 DB에 저장
        // (1단계 수정 덕분에 boardDTO 객체에 새로 생성된 bookid가 자동으로 채워집니다)
		boardService.save(boardDTO);
		
        // 2. (신규 로직) 방금 저장된 bookid로 장바구니에 추가
        if (principal != null) {
            try {
                // 3. 현재 로그인한 사용자(custid) 찾기
                String email = "kakao_" + principal.getAttributes().get("id");
                CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);

                if (customer != null && boardDTO.getBookid() > 0) {
                    // 4. 장바구니 DTO 생성 및 CartService 호출
                    CartDTO cartDTO = new CartDTO();
                    cartDTO.setCustid(customer.getCustid());
                    cartDTO.setBookid(boardDTO.getBookid());
                    cartDTO.setCount(1); // 기본 수량 1로 설정
                    
                    cartService.addCart(cartDTO);
                    
                    // 5. 장바구니 페이지로 리다이렉트
                    return "redirect:/cart";
                }
            } catch (Exception e) {
                // 오류 발생 시
                redirectAttributes.addFlashAttribute("error", "장바구니 추가 중 오류가 발생했습니다.");
                return "redirect:/list";
            }
        }
		
        // 5. (기존 로직 - 로그인 안됐거나 오류 시)
		redirectAttributes.addFlashAttribute("msg", "도서가 성공적으로 등록되었습니다.");
		return "redirect:/list";
	}
}

