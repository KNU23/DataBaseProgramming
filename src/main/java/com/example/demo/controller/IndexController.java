package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.ArrayList;
import java.util.List;

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
	 
	/** 메인 페이지 **/
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
	
	/** 관리자 권한 토글(ON/OFF) 기능 **/
    @GetMapping("/toggle-admin")
    public String toggleAdmin(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthAuth = (OAuth2AuthenticationToken) auth;
            OAuth2User user = oauthAuth.getPrincipal();
            
            List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
            
            boolean isAdmin = updatedAuthorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                
            if (isAdmin) {
                updatedAuthorities.removeIf(a -> a.getAuthority().equals("ROLE_ADMIN"));
            } else {
                updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }

            Authentication newAuth = new OAuth2AuthenticationToken(
                user,
                updatedAuthorities,
                oauthAuth.getAuthorizedClientRegistrationId()
            );
            
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
        
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}
