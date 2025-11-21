package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.AladinBookResponse; // 카카오 대신 알라딘 DTO import
import com.example.demo.dto.BoardDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookApiService {
    
    // application.yml에 aladin.api.key가 설정되어 있어야 합니다.
    @Value("${aladin.api.key}")
    private String aladinApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /** * 도서 검색 (알라딘 API - ItemSearch.aspx) */
    public List<BoardDTO> searchBooks(String query) {
        List<BoardDTO> resultList = new ArrayList<>();
        
        try {
            // 인코딩 제거한 URL (이전 단계에서 수정한 상태 유지)
            String url = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx?ttbkey=" + aladinApiKey
                       + "&Query=" + query  
                       + "&QueryType=Title" 
                       + "&MaxResults=50" // 필터링하다 보면 개수가 줄어들 수 있으니 20개 -> 50개로 넉넉하게 요청
                       + "&start=1"
                       + "&SearchTarget=Book"
                       + "&output=js"
                       + "&Version=20131101";

            System.out.println(">>> 알라딘 검색 요청: " + query); 

            ResponseEntity<AladinBookResponse> response = restTemplate.getForEntity(url, AladinBookResponse.class);

            if (response.getBody() != null && response.getBody().getItem() != null) {
                for (AladinBookResponse.Item item : response.getBody().getItem()) {
                    
                    // [★핵심 수정] API가 출판사로 검색해온 녀석들은 여기서 걸러냅니다.
                    // "책 제목"에 "검색어"가 없으면 리스트에 넣지 않고 건너뜀 (continue)
                    if (item.getTitle() == null || !item.getTitle().contains(query)) {
                        continue; 
                    }

                    BoardDTO book = new BoardDTO();
                    
                    // 1. 제목 길이 처리
                    String title = item.getTitle();
                    if (title != null && title.length() > 50) { 
                        title = title.substring(0, 50) + "...";
                    }
                    book.setBookname(title);
                    
                    book.setPublisher(item.getPublisher());
                    book.setAuthor(item.getAuthor());
                    book.setPrice(item.getPriceSales());
                    book.setStock(10); 
                    
                    // 2. 고화질 이미지
                    String coverUrl = item.getCover();
                    if (coverUrl != null) {
                        book.setCoverImagePath(coverUrl.replace("coversum", "cover500"));
                    }

                    // 3. 평점 정보
                    String originalDesc = item.getDescription();
                    if (originalDesc == null) originalDesc = "";
                    
                    String reviewInfo = "";
                    if (item.getCustomerReviewRank() > 0) {
                        reviewInfo = "★ 알라딘 평점: " + item.getCustomerReviewRank() + "점\n\n";
                    }
                    book.setDescription(reviewInfo + originalDesc);
                    
                    resultList.add(book);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultList;
    }
    
    /** * 인기 도서 / 베스트셀러 가져오기 (알라딘 API - ItemList.aspx) 
     */
    public List<BoardDTO> fetchBooks(String queryType, int limit) {
        List<BoardDTO> allBooks = new ArrayList<>();
        
        // 알라딘은 한 번에 최대 50개까지 조회 가능
        int size = 50; 
        int pages = (int) Math.ceil((double) limit / size);

        for (int i = 1; i <= pages; i++) {
            try {
                // 알라딘 리스트 조회 API URL
                String url = "http://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey=" + aladinApiKey
                           + "&QueryType=" + queryType // 예: "Bestseller"
                           + "&MaxResults=" + size 
                           + "&start=" + i
                           + "&SearchTarget=Book"
                           + "&output=js"
                           + "&Version=20131101";
    
                ResponseEntity<AladinBookResponse> response = restTemplate.getForEntity(url, AladinBookResponse.class);
    
                if (response.getBody() != null && response.getBody().getItem() != null) {
                    for (AladinBookResponse.Item item : response.getBody().getItem()) {
                        if (allBooks.size() >= limit) break; 
    
                        BoardDTO book = new BoardDTO();
                        
                        // 1. 제목 설정 (기존 동일)
                        String title = item.getTitle();
                        if (title != null && title.length() > 50) { 
                            title = title.substring(0, 50) + "...";
                        }
                        book.setBookname(title);
                        
                        book.setPublisher(item.getPublisher());
                        book.setAuthor(item.getAuthor());
                        book.setPrice(item.getPriceSales());
                        book.setStock(10);
                        
                        // 2. ★ 고화질 이미지 처리 로직
                        String coverUrl = item.getCover();
                        if (coverUrl != null) {
                            // 알라딘 이미지 URL 규칙: "coversum" -> "cover500"으로 바꾸면 고화질(500px)이 됨
                            // 예: .../coversum/isbn.jpg -> .../cover500/isbn.jpg
                            book.setCoverImagePath(coverUrl.replace("coversum", "cover500"));
                        }

                        // 3. ★ 평점(리뷰) 정보 처리 로직
                        // DB에 별도 평점 컬럼이 없으므로, 도서 설명(Description) 앞부분에 추가
                        String originalDesc = item.getDescription();
                        if (originalDesc == null) originalDesc = "";
                        
                        // 평점 정보 문자열 생성 (예: "★9.5 ")
                        String reviewInfo = "";
                        if (item.getCustomerReviewRank() > 0) {
                            reviewInfo = "★ 알라딘 평점: " + item.getCustomerReviewRank() + "점\n\n";
                        }
                        
                        book.setDescription(reviewInfo + originalDesc);
                        
                        allBooks.add(book);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return allBooks;
    }
}