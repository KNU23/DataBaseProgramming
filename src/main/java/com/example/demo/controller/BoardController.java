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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.BookListResponseDTO;
import com.example.demo.dto.CartDTO;
import com.example.demo.dto.CustomerDTO;
import com.example.demo.service.BoardService;
import com.example.demo.service.BookApiService;
import com.example.demo.service.CartService;
import com.example.demo.dto.ReviewDTO;
import com.example.demo.service.ReviewService;


import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	private final BookApiService bookApiService;
	private final CartService cartService; 
    private final SqlSessionTemplate sql; 
    private final ReviewService reviewService;
	
    // ==========================================
    //              도서 조회 기능
    // ==========================================

    /** 도서 목록 페이지 (페이징, 검색, 정렬) **/
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
	
	/** 도서 상세 정보 **/
	@GetMapping("/bookid/{id}")
	public String detail(@PathVariable("id") Integer id, Model model, 
	                     @AuthenticationPrincipal OAuth2User principal) { // principal 파라미터 추가
	    
	    // 도서 상세 정보
	    BoardDTO boardDTO = boardService.detail(id);
	    model.addAttribute("bookDetail", boardDTO);
	    
	    // 리뷰 목록 가져오기
	    List<ReviewDTO> reviews = reviewService.getReviewsByBookId(id);
	    model.addAttribute("reviews", reviews);
	    
	 // ================= [수정된 부분 시작] =================
	    
	    // 3. 리뷰 작성 권한 확인 (관리자 OR 구매자)
	    boolean canReview = false;
	    
	    // (1) 현재 로그인한 사용자의 인증 정보 가져오기 (권한 확인용)
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    boolean isAdmin = false;
	    
	    if (auth != null && auth.isAuthenticated()) {
	        // "ROLE_ADMIN" 권한을 가지고 있는지 확인 (스트림 API 사용)
	        isAdmin = auth.getAuthorities().stream()
	                      .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
	    }

	    // (2) 관리자라면 무조건 true, 아니라면 구매 내역 확인
	    if (isAdmin) {
	        canReview = true;
	    } else if (principal != null) {
	        // 로그인한 일반 사용자 -> 구매 내역 체크
	        String email = "kakao_" + principal.getAttributes().get("id");
	        CustomerDTO customer = sql.selectOne("Customer.findByEmail", email);
	        if (customer != null) {
	            canReview = reviewService.canReview(id, customer.getCustid());
	        }
	    }
	    
	    // ================= [수정된 부분 끝] =================
	    
	    model.addAttribute("canReview", canReview);

	    return "detailBook";
	}

    // ==========================================
    //            관리자 기능 (등록/수정/삭제)
    // ==========================================
	
	/** 도서 등록 페이지 이동 **/
	@GetMapping("/addBook")
	public String addBookForm(Model model) {
		model.addAttribute("boardDTO", new BoardDTO());
		return "addBook";
	}
	
	/** 도서 등록 처리 **/
	@PostMapping("/addBook")
	public String saveBook(@Valid @ModelAttribute BoardDTO boardDTO, 
	                       BindingResult bindingResult, 
	                       RedirectAttributes redirectAttributes) {
		
		if (bindingResult.hasErrors()) {
			return "addBook"; 
		}
		
		boardService.save(boardDTO);
		redirectAttributes.addFlashAttribute("msg", "새 도서가 성공적으로 등록되었습니다.");
		return "redirect:/list";
	}
	
	/** 도서 수정 페이지 이동 **/
	@GetMapping("/goUpdate/{id}")
	public String updateBookForm(@PathVariable("id") Integer id, Model model) {
		BoardDTO boardDTO = boardService.detail(id);
		model.addAttribute("bookDetail", boardDTO);
		return "updateBook";
	}
	
	/** 도서 수정 처리 **/
	@PostMapping("/goUpdate/{id}")
	public String updateBook(@PathVariable("id") Integer id,
	                         @Valid @ModelAttribute("bookDetail") BoardDTO boardDTO, 
	                         BindingResult bindingResult, 
	                         RedirectAttributes redirectAttributes) {
		
		boardDTO.setBookid(id);

		if (bindingResult.hasErrors()) {
			return "updateBook"; 
		}
		
		boardService.goUpdate(boardDTO);
		redirectAttributes.addFlashAttribute("msg", "도서(ID: " + boardDTO.getBookid() + ") 정보가 수정되었습니다.");
		return "redirect:/list";
	}

	/** 도서 삭제 처리 **/
	@GetMapping("/goDelete/{id}")
	public String deleteBook(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		boardService.goDelete(id);
		redirectAttributes.addFlashAttribute("msg", "도서(ID: " + id + ") 정보가 삭제되었습니다.");
		return "redirect:/list";
	}
	
    // ==========================================
    //            API 검색 및 추가 기능
    // ==========================================

	/** API 도서 검색 페이지 이동 **/
	@GetMapping("/search")
	public String searchPage() {
		return "searchBook";
	}
	
	/** API 도서 검색 수행 (알라딘) **/
	@PostMapping("/search")
	public String searchApi(@RequestParam("keyword") String keyword, Model model) {
		List<BoardDTO> books = bookApiService.searchBooks(keyword);
		model.addAttribute("books", books);
		model.addAttribute("keyword", keyword);
		return "searchBook";
	}
	
	/** API 검색 결과 DB 저장 및 장바구니 담기 **/
	@PostMapping("/addApiBook")
	public String addApiBook(@ModelAttribute BoardDTO boardDTO, 
                             RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal OAuth2User principal) { 
		
		// 도서 정보 DB 저장
		boardService.save(boardDTO);
		
        if (principal != null) {
            try {
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
	
    // ==========================================
    //               기타 편의 기능
    // ==========================================

	/** 도서 목록 리셋 (인기 도서 100권 갱신) **/
    @GetMapping("/refresh-books")
    public String refreshBooks(RedirectAttributes redirectAttributes) {
        try {
            boardService.refreshToPopularBooks();
            redirectAttributes.addFlashAttribute("msg", "도서 목록이 인기 도서 100권으로 재설정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "도서 목록 갱신 중 오류가 발생했습니다.");
        }
        return "redirect:/list";
    }
}