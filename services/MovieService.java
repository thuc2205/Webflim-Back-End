package com.example.flim.services;

import com.example.flim.entities.Actor;
import com.example.flim.entities.Genre;
import com.example.flim.entities.Movie;
import com.example.flim.repositories.ActorRepository;
import com.example.flim.repositories.GenreRepository;
import com.example.flim.repositories.MovieRepository;
import com.example.flim.request.MovieRequest;
import com.example.flim.response.MovieResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;

    @Transactional
    public Movie createMovie(MovieRequest movieRequest) {
        Set<Genre> genres = new HashSet<>();
        Set<Actor> actors = new HashSet<>();

        if (movieRequest.getGenreIds() != null) {
            for (Integer genreId : movieRequest.getGenreIds()) {
                Optional<Genre> genre = genreRepository.findById(genreId);
                genre.ifPresent(genres::add);
            }
        }

        if (movieRequest.getActorIds() != null) {
            for (Integer actorId : movieRequest.getActorIds()) {
                Optional<Actor> actor = actorRepository.findById(actorId);
                actor.ifPresent(actors::add);
            }
        }

        Movie movie = Movie.builder()
                .title(movieRequest.getTitle())
                .description(movieRequest.getDescription())
                .releaseDate(movieRequest.getReleaseDate())
                .createdAt(LocalDateTime.now())
                .genres(genres)
                .actors(actors)
                .build();

        return movieRepository.save(movie);
    }

    @Transactional
    public Movie updateMovie(Integer id, MovieRequest movieRequest) {
        Optional<Movie> existingMovieOpt = movieRepository.findById(id);
        if (existingMovieOpt.isEmpty()) {
            throw new RuntimeException("Movie not found");
        }

        Movie existingMovie = existingMovieOpt.get();
        Set<Genre> genres = new HashSet<>();
        Set<Actor> actors = new HashSet<>();

        if (movieRequest.getGenreIds() != null) {
            for (Integer genreId : movieRequest.getGenreIds()) {
                Optional<Genre> genre = genreRepository.findById(genreId);
                genre.ifPresent(genres::add);
            }
        }

        if (movieRequest.getActorIds() != null) {
            for (Integer actorId : movieRequest.getActorIds()) {
                Optional<Actor> actor = actorRepository.findById(actorId);
                actor.ifPresent(actors::add);
            }
        }

        existingMovie.setTitle(movieRequest.getTitle());
        existingMovie.setDescription(movieRequest.getDescription());
        existingMovie.setReleaseDate(movieRequest.getReleaseDate());
        existingMovie.setGenres(genres);
        existingMovie.setActors(actors);

        return movieRepository.save(existingMovie);
    }

    @Transactional
    public void deleteMovie(Integer id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found");
        }
        movieRepository.deleteById(id);
    }

    @Transactional
    public Movie updateMovieThumbnailUrl(int movieId, String videoUrl) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id " + movieId));
        movie.setThumbnailUrl(videoUrl);
        return movieRepository.save(movie);
    }

    public Movie getMovieById(int id) {
        return movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found"));
    }
    public Page<MovieResponse> getAllMovie(String keyword, Long categoryId , PageRequest request) {
        //lấy danh sách sản phẩm page và limit
        if (categoryId != null && categoryId != 0) {
            return movieRepository.searchMovies(categoryId, keyword, request).map(MovieResponse::fromMovie);
        } else {
            // Nếu không, sử dụng phương thức findAll để lấy tất cả sản phẩm
            return movieRepository.findAll(request).map(MovieResponse::fromMovie);
        }
    }

    public List<MovieResponse> getTop3NewestMovies() {
        List<Movie> movies = movieRepository.findTop3NewestMovies(PageRequest.of(0, 3));
        return movies.stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());
    }

    public MovieResponse findMovieByTitle(String title){
        return movieRepository.findMovieByTitle(title)
                .map(MovieResponse::fromMovie)
                .orElseThrow(() -> new NullPointerException("Movie not found with title: " + title));

    }
}
