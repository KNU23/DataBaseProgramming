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
	private final CartService cartService; 
    private final SqlSessionTemplate sql; 
	
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
	
	/** 도서정보 추가하기 **/
	@GetMapping("/addBook")
	public String addBook(Model model) {
		model.addAttribute("boardDTO", new BoardDTO());
		return "addBook";
	}
	
	/** 도서정보 저장하기 **/
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
	
	/** API 검색 결과 DB 저장 및 장바구니 추가 **/
	@PostMapping("/addApiBook")
	public String addApiBook(@ModelAttribute BoardDTO boardDTO, 
                             RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal OAuth2User principal) { 
		
		// API로 검색한 책을 우리 DB에 저장
		boardService.save(boardDTO);
		
        // 방금 저장된 bookid로 장바구니에 추가
        if (principal != null) {
            try {
                // 현재 로그인한 사용자(custid) 찾기
                String email = "kakao_" + principal.getAttributes().get("id");
                CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);

                if (customer != null && boardDTO.getBookid() > 0) {
                    CartDTO cartDTO = new CartDTO();
                    cartDTO.setCustid(customer.getCustid());
                    cartDTO.setBookid(boardDTO.getBookid());
                    cartDTO.setCount(1); 
                    
                    cartService.addCart(cartDTO);
                    
                    return "redirect:/cart";
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "장바구니 추가 중 오류가 발생했습니다.");
                return "redirect:/list";
            }
        }
		
		redirectAttributes.addFlashAttribute("msg", "도서가 성공적으로 등록되었습니다.");
		return "redirect:/list";
	}
	
	/** 도서 목록 100권 리셋 **/
    @GetMapping("/refresh-books")
    public String refreshBooks(RedirectAttributes redirectAttributes) {
        try {
            boardService.refreshToPopularBooks();
            redirectAttributes.addFlashAttribute("msg", "도서 목록이 인기 도서 100권으로 재설정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "도서 목록 갱신 중 오류가 발생했습니다. (주문 내역 등이 원인일 수 있습니다)");
        }
        return "redirect:/list";
    }
}

