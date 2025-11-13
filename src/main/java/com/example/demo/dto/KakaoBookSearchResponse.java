package com.example.demo.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoBookSearchResponse {
    private List<Document> documents;

    @Getter
    @Setter
    public static class Document {
        private String title;
        private String contents; 
        private String url;
        private String isbn;
        private List<String> authors;
        private String publisher;
        private Integer price;
        private String thumbnail; 
    }
}
