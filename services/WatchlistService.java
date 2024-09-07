package com.example.flim.services;
import com.example.flim.entities.Movie;
import com.example.flim.entities.User;
import com.example.flim.entities.Watchlist;
import com.example.flim.repositories.MovieRepository;
import com.example.flim.repositories.UserRepository;
import com.example.flim.repositories.WatchlistRepository;
import com.example.flim.request.WatchlistRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    @Transactional
    public Watchlist addToWatchlist(WatchlistRequest request) {
        Optional<User> user = userRepository.findById(request.getUserId());
        Optional<Movie> movie = movieRepository.findById(request.getMovieId());

        if (user.isPresent() && movie.isPresent()) {
            Watchlist watchlist = Watchlist.builder()
                    .user(user.get())
                    .movie(movie.get())
                    .build();
            return watchlistRepository.save(watchlist);
        } else {
            throw new IllegalArgumentException("Invalid user ID or movie ID");
        }
    }

    @Transactional(readOnly = true)
    public Watchlist getWatchlistById(int id) {
        return watchlistRepository.findById(id).orElse(null);
    }

    @Transactional
    public void removeFromWatchlist(int id) {
        watchlistRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Watchlist> getWatchlistByUserId(int userId) {
        return watchlistRepository.findByUserId(userId);
    }
}
