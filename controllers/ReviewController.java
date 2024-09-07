package com.example.flim.controllers;

import com.example.flim.entities.Review;
import com.example.flim.request.ReviewRequest;
import com.example.flim.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/review")
public class ReviewController {


    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewRequest request) {
        Review review = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable int id) {
        Review review = reviewService.getReviewById(id);
        return review != null ? ResponseEntity.ok(review) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable int id, @RequestBody ReviewRequest request) {
        Review updatedReview = reviewService.updateReview(id, request);
        return updatedReview != null ? ResponseEntity.ok(updatedReview) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable int id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/movie/{movieId}")
//    public ResponseEntity<List<Review>> getReviewsByMovieId(@PathVariable Long movieId) {
//        List<Review> reviews = reviewService.getReviewsByMovieId(movieId);
//        return ResponseEntity.ok(reviews);
//    }
}
