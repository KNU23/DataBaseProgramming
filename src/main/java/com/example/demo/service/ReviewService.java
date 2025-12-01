package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public void addReview(ReviewDTO review) {
        reviewRepository.save(review);
    }

    public List<ReviewDTO> getReviewsByBookId(int bookid) {
        return reviewRepository.findByBookId(bookid);
    }

    // 리뷰 작성 가능 여부 (구매했는지)
    public boolean canReview(int bookid, int custid) {
        return reviewRepository.countPurchase(bookid, custid) > 0;
    }
}
