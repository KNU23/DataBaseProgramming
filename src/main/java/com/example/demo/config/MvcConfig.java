package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "file:./uploads/" 디렉토리에서 파일을 찾도록 설정
        // "file:" 접두사는 로컬 파일 시스템 경로를 의미
        Path uploadDir = Paths.get("./uploads/");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry
            .addResourceHandler("/uploads/**") // /uploads/ 로 시작하는 모든 URL 요청은
            .addResourceLocations("file:/" + uploadPath + "/"); // file:/절대경로/ 로 매핑
    }
}
