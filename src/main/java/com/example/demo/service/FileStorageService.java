package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path rootLocation; // ./uploads

    public FileStorageService() {
        // 1. ./uploads 경로 설정
        this.rootLocation = Paths.get("./uploads");
        try {
            // 2. (중요) ./uploads 폴더가 없으면 생성
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    /**
     * 파일을 저장하고, 웹 접근 경로를 반환
     */
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 1. 파일명 정리 (예: "My Image.jpg")
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        // 2. 파일 확장자 추출 (예: "jpg")
        String extension = StringUtils.getFilenameExtension(originalFilename);
        
        // 3. 중복되지 않는 고유한 파일명 생성 (예: "UUID.jpg")
        String storedFilename = UUID.randomUUID().toString() + "." + extension;

        try {
            // 4. ./uploads/UUID.jpg 경로 생성
            Path destinationFile = this.rootLocation.resolve(storedFilename);

            // 5. 파일 저장
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // 6. 웹에서 접근 가능한 경로 반환 (예: "/uploads/UUID.jpg")
            return "/uploads/" + storedFilename;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}
