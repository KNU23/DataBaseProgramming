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
                /** 전체 허용 **/
                .requestMatchers("/css/**", "/style.css", "/js/**", "/error", "/uploads/**", "/images/**").permitAll()                
                .requestMatchers(HttpMethod.GET, "/", "/list", "/bookid/**", "/refresh-books").permitAll()
                .requestMatchers("/login").permitAll()
                
                /** 로그인 사용자만 허용 **/
                .requestMatchers("/cart/**", "/orderList", "/search", "/addApiBook", "/customers", "/addCustomer", "/review/**").authenticated()                             
                .requestMatchers("/toggle-admin").authenticated() 
                
                /** 관리자 기능 **/
                .requestMatchers("/addBook", "/goUpdate/**", "/goDelete/**").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                /** 그 외 모든 요청은 로그인 사용자만 허용 **/
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
            
            /** 권한 없는 페이지 접근 시 처리 핸들러 **/
            .exceptionHandling(handling -> handling
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/?error=access_denied");
                })
            );
                
        return http.build();
    }
}