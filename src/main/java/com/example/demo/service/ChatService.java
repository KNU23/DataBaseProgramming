package com.example.demo.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.BoardDTO;
import com.example.demo.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final BoardRepository boardRepository;

    // AI와 대화하는 메인 메서드
    public String getChatResponse(String userMessage) {
        try {
            // 1. 사용자의 질문에서 '검색 키워드' 추출 (AI 사용)
            String searchKeyword = extractSearchKeyword(userMessage);
            
            // 2. DB에서 책 검색 (키워드가 없으면 최신순 5권 조회)
            List<BoardDTO> foundBooks;
            if (searchKeyword == null || searchKeyword.isEmpty() || searchKeyword.equals("NO_SEARCH")) {
                // 검색어가 없으면(예: "안녕", "배송 언제 와?") -> 최신 도서 5권만 참고용으로 가져옴
                foundBooks = boardRepository.getList(null, 0, 5, "bookid", "desc");
            } else {
                // 검색어가 있으면(예: "해리포터") -> 해당 키워드로 DB 검색 (최대 10개)
                foundBooks = boardRepository.getList(searchKeyword, 0, 10, "bookid", "desc");
            }

            // 3. 검색된 책 정보를 문자열로 변환
            String bookContext = convertBooksToString(foundBooks);

            // 4. 최종 프롬프트 생성 (가게 정보 + 찾은 책 정보 + 사용자 질문)
            String finalPrompt = createFinalPrompt(userMessage, bookContext, searchKeyword);

            // 5. AI에게 최종 답변 요청
            return callGeminiApi(finalPrompt);

        } catch (Exception e) {
            e.printStackTrace();
            return "오류가 발생했습니다: " + e.getMessage();
        }
    }

    // [1단계] 사용자 질문에서 검색어만 뽑아내는 메서드
    private String extractSearchKeyword(String userMessage) {
        // AI에게 역할을 부여해서 검색어를 뽑게 시킵니다.
        String prompt = "다음 문장에서 사용자가 찾고 있는 '책 제목'이나 '저자명'을 단어 하나로 추출해줘.\n"
                      + "인사말이나 일반적인 질문이라면 'NO_SEARCH'라고만 답변해.\n"
                      + "문장: \"" + userMessage + "\"\n"
                      + "답변:";
        
        String response = callGeminiApi(prompt);
        return response.trim().replace("\"", "").replace("'", ""); // 따옴표 제거
    }

    // [2단계] 검색된 책 리스트를 예쁜 문자열로 정리
    private String convertBooksToString(List<BoardDTO> books) {
        if (books.isEmpty()) {
            return "검색 결과가 없습니다. (없는 책입니다)";
        }
        return books.stream()
                .map(book -> String.format("[%d] 제목: %s | 저자: %s | 출판사: %s | 가격: %d원 | 재고: %d권", 
                        book.getBookid(), book.getBookname(), book.getAuthor(), book.getPublisher(), book.getPrice(), book.getStock()))
                .collect(Collectors.joining("\n"));
    }

    // [3단계] 최종 프롬프트 조합
    private String createFinalPrompt(String userMessage, String bookContext, String keyword) {
        String storeInfo = 
                  "- 상호명: KNU BookStore\n"
                + "- 배송: 오후 2시 전 주문 당일 발송 (전 상품 무료배송)\n"
                + "- 결제: 카카오페이, 카드 가능\n";

        return "당신은 서점 AI 직원입니다. 한국어로 친절하게 답변하세요.\n"
             + "사용자 질문: \"" + userMessage + "\"\n\n"
             + "[상황 판단]\n"
             + "사용자가 책을 찾으려 해서 DB에서 '" + keyword + "'(으)로 검색했습니다.\n"
             + "[검색된 도서 목록]\n" + bookContext + "\n\n"
             + "[가게 정보]\n" + storeInfo + "\n\n"
             + "[지침]\n"
             + "1. 검색된 도서 목록에 있는 책 내용을 바탕으로 재고, 가격 등을 정확히 안내하세요.\n"
             + "2. 목록에 없는 책을 찾으면 '죄송하지만 현재 재고가 없습니다'라고 안내하세요.\n"
             + "3. 책과 관련 없는 질문에는 가게 정보를 바탕으로 답변하세요.";
    }

    // Gemini API 호출 공통 메서드
    private String callGeminiApi(String text) {
        try {
            String url = apiUrl + "?key=" + apiKey;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> part = new HashMap<>();
            part.put("text", text);
            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(part));
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", Collections.singletonList(content));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> candidateContent = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) candidateContent.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}