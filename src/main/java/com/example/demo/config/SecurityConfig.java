package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // 1. 정적 리소스, /error, /uploads/** 는 누구나 접근 허용
                .requestMatchers("/css/**", "/js/**", "/error", "/uploads/**").permitAll()
                // 2. / (홈), /list (목록), /bookid/** (상세)는 누구나 접근 허용
                .requestMatchers(HttpMethod.GET, "/", "/list", "/bookid/**").permitAll()
                // 3. /login (로그인 페이지)는 누구나 접근 허용
                .requestMatchers("/login").permitAll()
                // 4. /customers, /addCustomer 는 "ADMIN" 또는 "USER" 역할 허용
                .requestMatchers("/customers", "/addCustomer").hasAnyRole("ADMIN", "USER")
                // 5. 그 외 /addBook, /goDelete 등 CUD 작업은 "ADMIN" 역할만 허용
                .requestMatchers("/**").hasRole("ADMIN")
                // 6. 나머지 모든 요청은 인증을 받아야 함
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                // 7. 커스텀 로그인 페이지 경로
                .loginPage("/login")
                // 8. 로그인 성공 시 이동할 기본 URL
                .defaultSuccessUrl("/list", true)
                .permitAll()
            )
            .logout(logout -> logout
                // 9. 로그아웃 URL
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                // 10. 로그아웃 성공 시 이동할 URL
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true) // 세션 무효화
                .deleteCookies("JSESSIONID") // 쿠키 삭제
                .permitAll()
            )
            // (참고) CSRF를 비활성화하면 POST, PUT, DELETE 요청이 간단해지지만, 보안에 취약해집니다.
            // .csrf(csrf -> csrf.disable()) 
            ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 암호화를 위한 Bcrypt 인코더
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // (데모용) 메모리 기반 사용자 저장소
        // 실제 운영 시에는 DB에서 사용자를 조회해야 합니다. (CustomUserDetailsService)

        // 1. 'user' (USER 역할)
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("password")) // "password"
            .roles("USER")
            .build();

        // 2. 'admin' (ADMIN, USER 역할)
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin")) // "admin"
            .roles("ADMIN", "USER")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
