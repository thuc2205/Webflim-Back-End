package com.example.flim.services;

import com.example.flim.entities.Review;
import com.example.flim.entities.User;
import com.example.flim.entities.Movie;
import com.example.flim.repositories.ReviewRepository;
import com.example.flim.repositories.MovieRepository;
import com.example.flim.repositories.UserRepository;
import com.example.flim.request.ReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Transactional
    public Review createReview(ReviewRequest request) {
        Optional<User> user = userRepository.findById(request.getUserId());
        Optional<Movie> movie = movieRepository.findById(request.getMovieId());

        if (user.isPresent() && movie.isPresent()) {
            Review review = Review.builder()
                    .user(user.get())
                    .movie(movie.get())
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .build();
            return reviewRepository.save(review);
        } else {
            throw new IllegalArgumentException("Invalid user ID or movie ID");
        }
    }

    @Transactional(readOnly = true)
    public Review getReviewById(int id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Transactional
    public Review updateReview(int id, ReviewRequest request) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            Optional<User> user = userRepository.findById(request.getUserId());
            Optional<Movie> movie = movieRepository.findById(request.getMovieId());

            if (user.isPresent() && movie.isPresent()) {
                review.setUser(user.get());
                review.setMovie(movie.get());
                review.setRating(request.getRating());
                review.setComment(request.getComment());
                return reviewRepository.save(review);
            } else {
                throw new IllegalArgumentException("Invalid user ID or movie ID");
            }
        }
        return null;
    }

    @Transactional
    public void deleteReview(int id) {
        reviewRepository.deleteById(id);
    }


}
