package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.AladinBookResponse; 
import com.example.demo.dto.BoardDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookApiService {
 
    @Value("${aladin.api.key}")
    private String aladinApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /** API로 도서 검색 **/
    public List<BoardDTO> searchBooks(String query) {
        List<BoardDTO> resultList = new ArrayList<>();
        
        try {
            String url = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx?ttbkey=" + aladinApiKey
                       + "&Query=" + query  
                       + "&QueryType=Title" 
                       + "&MaxResults=50" 
                       + "&start=1"
                       + "&SearchTarget=Book"
                       + "&output=js"
                       + "&Version=20131101";

            System.out.println(">>> 알라딘 검색 요청: " + query); 

            ResponseEntity<AladinBookResponse> response = restTemplate.getForEntity(url, AladinBookResponse.class);

            if (response.getBody() != null && response.getBody().getItem() != null) {
                for (AladinBookResponse.Item item : response.getBody().getItem()) {
                    
                    if (item.getTitle() == null || !item.getTitle().contains(query)) {
                        continue; 
                    }

                    BoardDTO book = new BoardDTO();
                    
                    /** 제목 길이 처리 **/
                    String title = item.getTitle();
                    if (title != null && title.length() > 50) { 
                        title = title.substring(0, 50) + "...";
                    }
                    book.setBookname(title);                    
                    book.setPublisher(item.getPublisher());
                    book.setAuthor(item.getAuthor());
                    book.setPrice(item.getPriceSales());
                    book.setStock(10); 
                    
                    /** 고화질 이미지 **/
                    String coverUrl = item.getCover();
                    if (coverUrl != null) {
                        book.setCoverImagePath(coverUrl.replace("coversum", "cover500"));
                    }

                    /**. 평점 정보 **/
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
    
    /** 인기 도서 100권 가져오기 **/
    public List<BoardDTO> fetchBooks(String queryType, int limit) {
        List<BoardDTO> allBooks = new ArrayList<>();
        
        int size = 50; 
        int pages = (int) Math.ceil((double) limit / size);

        for (int i = 1; i <= pages; i++) {
            try {
                String url = "http://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey=" + aladinApiKey
                           + "&QueryType=" + queryType 
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
                        
                        /** 제목 길이 처리 **/
                        String title = item.getTitle();
                        if (title != null && title.length() > 50) { 
                            title = title.substring(0, 50) + "...";
                        }
                        book.setBookname(title);
                        
                        book.setPublisher(item.getPublisher());
                        book.setAuthor(item.getAuthor());
                        book.setPrice(item.getPriceSales());
                        book.setStock(10);
                        
                        /** 고화질 이미지 **/
                        String coverUrl = item.getCover();
                        if (coverUrl != null) {
                            book.setCoverImagePath(coverUrl.replace("coversum", "cover500"));
                        }

                        /** 평점 정보 **/
                        String originalDesc = item.getDescription();
                        if (originalDesc == null) originalDesc = "";
                        
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