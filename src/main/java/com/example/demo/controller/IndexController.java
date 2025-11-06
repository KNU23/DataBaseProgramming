package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class IndexController {
	
	@GetMapping("/")
	public String index() {
		System.out.println("index 메서드 call");
		return "index";
	}
	
	/**
	 * (신규) 커스텀 로그인 페이지 매핑
	 */
	@GetMapping("/login")
	public String login() {
	    return "login"; // templates/login.html
	}
}
