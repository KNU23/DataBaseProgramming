package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.KakaoBookSearchResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookApiService {
    
	@Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<BoardDTO> searchBooks(String query) {
    	
    	/** 헤더 설정 **/
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

    	/** API 호출 **/
        String url = "https://dapi.kakao.com/v3/search/book?target=title&query=" + query;
        ResponseEntity<KakaoBookSearchResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, KakaoBookSearchResponse.class);

        /**결과 변환 (API DTO -> 우리 프로젝트 BoardDTO) **/
        List<BoardDTO> resultList = new ArrayList<>();
        if (response.getBody() != null && response.getBody().getDocuments() != null) {
            for (KakaoBookSearchResponse.Document doc : response.getBody().getDocuments()) {
                BoardDTO book = new BoardDTO();
                book.setBookname(doc.getTitle());
                book.setPublisher(doc.getPublisher());
                book.setAuthor(String.join(", ", doc.getAuthors())); 
                book.setPrice(doc.getPrice());
                book.setDescription(doc.getContents());
                book.setCoverImagePath(doc.getThumbnail()); 
                book.setStock(10); 
                
                resultList.add(book);
            }
        }
        return resultList;
    }
}
