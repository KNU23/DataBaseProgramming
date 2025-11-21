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
    		/** http 권한요청 **/
            .authorizeHttpRequests(authorize -> authorize
                // [수정 1] "/style.css"를 추가하여 비로그인 사용자도 CSS를 볼 수 있게 허용
                .requestMatchers("/css/**", "/style.css", "/js/**", "/error", "/uploads/**", "/images/**").permitAll()
                
                .requestMatchers(HttpMethod.GET, "/", "/list", "/bookid/**", "/refresh-books").permitAll()
                .requestMatchers("/login").permitAll()
                
                .requestMatchers("/cart/**", "/orderList", "/search", "/addApiBook", "/customers", "/addCustomer").authenticated()                
                .requestMatchers("/addBook", "/goUpdate/**", "/goDelete/**").authenticated()
                
                .anyRequest().authenticated()	
            )
            
        	/** 로그인 **/
            .oauth2Login(oauth2 -> oauth2
            		.loginPage("/login")
            		// [수정 2] 로그인 성공 후 이동할 주소를 "/list" -> "/" (홈)으로 변경
            		.defaultSuccessUrl("/", true) 
            		.userInfoEndpoint(userInfo -> userInfo
            				.userService(customOAuth2UserService)            		    
            		)
            )
            
        	/** 로그아웃 **/
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