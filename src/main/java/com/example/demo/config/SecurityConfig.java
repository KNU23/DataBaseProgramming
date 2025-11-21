package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.demo.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
    		/** HTTP 요청 권한 설정 **/
            .authorizeHttpRequests(authorize -> authorize
                // 정적 리소스 허용 (CSS, JS, 이미지 등)
                .requestMatchers("/css/**", "/style.css", "/js/**", "/error", "/uploads/**", "/images/**").permitAll()
                
                // 공개 페이지 (메인, 목록, 상세, 로그인)
                .requestMatchers(HttpMethod.GET, "/", "/list", "/bookid/**", "/refresh-books").permitAll()
                .requestMatchers("/login").permitAll()
                
                // 인증된 사용자만 접근 (장바구니, 주문, 검색, 마이페이지)
                .requestMatchers("/cart/**", "/orderList", "/search", "/addApiBook", "/customers", "/addCustomer").authenticated()                
                
                // 관리자 기능 (등록, 수정, 삭제)
                .requestMatchers("/addBook", "/goUpdate/**", "/goDelete/**").authenticated()
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()	
            )
            
        	/** OAuth2 로그인 설정 (카카오) **/
            .oauth2Login(oauth2 -> oauth2
            		.loginPage("/login")
            		.defaultSuccessUrl("/", true) 
            		.userInfoEndpoint(userInfo -> userInfo
            				.userService(customOAuth2UserService)            		    
            		)
            )
            
        	/** 로그아웃 설정 **/
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true) 
                .deleteCookies("JSESSIONID") 
                .permitAll()
            );
        
        return http.build();
    }
}