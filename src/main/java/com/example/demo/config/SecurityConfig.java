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
                // 전체 허용 (CSS, JS, 이미지 등)
                .requestMatchers("/css/**", "/style.css", "/js/**", "/error", "/uploads/**", "/images/**").permitAll()                
                // 전체 허용 (메인, 목록, 상세)
                .requestMatchers(HttpMethod.GET, "/", "/list", "/bookid/**", "/refresh-books").permitAll()
                // 전체 허용 (로그인)
                .requestMatchers("/login").permitAll()
                
                // 로그인 사용자만 허용 (장바구니, 주문, 검색, 리뷰)
                .requestMatchers("/cart/**", "/orderList", "/search", "/addApiBook", "/customers", "/addCustomer", "/review/**").authenticated()                             
                // 로그인 사용자만 허용 (관리자 모드 토글 URL 허용)
                .requestMatchers("/toggle-admin").authenticated() 
                
                // 관리자 기능 (등록, 수정, 삭제)
                .requestMatchers("/addBook", "/goUpdate/**", "/goDelete/**").hasRole("ADMIN")
                // 관리자 기능 (대쉬보드)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // 그 외 모든 요청은 로그인 사용자만 허용
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
            )
            
            /** 권한 없는 페이지 접근 시 처리 (예외 핸들링) **/
            .exceptionHandling(handling -> handling
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    // 권한 문제 발생 시 홈('/')으로 이동하며 error 파라미터 전달
                    response.sendRedirect("/?error=access_denied");
                })
            );
        
        
        
        return http.build();
    }
}