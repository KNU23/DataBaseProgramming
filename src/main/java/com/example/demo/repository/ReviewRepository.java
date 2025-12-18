package com.example.demo.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {
    
    private final SqlSessionTemplate sql;
    
    public void save(ReviewDTO review) {
        sql.insert("Review.insert", review);
    }
    
    public List<ReviewDTO> findByBookId(int bookid) {
        return sql.selectList("Review.findByBookId", bookid);
    }
    
    /** 구매 횟수 조회 **/
    public int countPurchase(int bookid, int custid) {
        Map<String, Object> params = new HashMap<>();
        params.put("bookid", bookid);
        params.put("custid", custid);
        return sql.selectOne("Review.countPurchase", params);
    }

    /** 리뷰 수정 **/
    public void update(ReviewDTO review) {
        sql.update("Review.update", review);
    }

    /** 리뷰 삭제 **/
    public void delete(int reviewId, int custid) {
        Map<String, Object> params = new HashMap<>();
        params.put("reviewId", reviewId);
        params.put("custid", custid);
        sql.delete("Review.delete", params);
    }

    /** 리뷰 단건 조회 **/
    public ReviewDTO findById(int reviewId) {
        return sql.selectOne("Review.findById", reviewId);
    }
}