package com.example.flim.services;
import com.example.flim.entities.Favorite;
import com.example.flim.entities.Movie;
import com.example.flim.entities.User;
import com.example.flim.repositories.FavoriteRepository;
import com.example.flim.repositories.MovieRepository;
import com.example.flim.repositories.UserRepository;
import com.example.flim.request.FavoriteRequest;
import com.example.flim.response.FavoriteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;


    @Transactional
    public Favorite addFavorite(FavoriteRequest request) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(request.getUsername()));
        Optional<Movie> movie = movieRepository.findMovieByTitle(request.getTitleMovie());

        if (user.isPresent() && movie.isPresent()) {
            // Check if the favorite already exists
            Optional<Favorite> existingFavorite = Optional.ofNullable(favoriteRepository.findFavoriteByUserAndMovie(request.getUsername(), request.getTitleMovie()));
            if (existingFavorite.isPresent()) {
                // Throw an exception or return an error message
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Movie is already in favorites");
            }

            Favorite favorite = Favorite.builder()
                    .user(user.get())
                    .movie(movie.get())
                    .build();
            return favoriteRepository.save(favorite);
        } else {
            throw new IllegalArgumentException("Invalid user or movie");
        }
    }

    @Transactional(readOnly = true)
    public Favorite getFavoriteById(int id) {
        return favoriteRepository.findById(id).orElse(null);
    }

    @Transactional
    public void removeFavorite(int id) {
        favoriteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Favorite> getFavoritesByUserId(int userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Transactional
    public void removeFavorite(String username, String title) {
        Favorite favorite = favoriteRepository.findFavoriteByUserAndMovie(username, title);
        if (favorite != null) {
            favoriteRepository.delete(favorite);
        } else {
            throw new IllegalArgumentException("Favorite not found");
        }
    }


}
