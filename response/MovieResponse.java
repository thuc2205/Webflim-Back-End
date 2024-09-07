package com.example.flim.response;

import com.example.flim.entities.Movie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private String thumbnailUrl;
    private int viewCount;
    private LocalDateTime createdAt;
    private Set<GenreResponse> genres;
    private Set<ActorResponse> actors;

    public static MovieResponse fromMovie(Movie movie) {
        MovieResponse movieResponse = new MovieResponse();
        movieResponse.setId(movie.getId());
        movieResponse.setTitle(movie.getTitle());
        movieResponse.setDescription(movie.getDescription());
        movieResponse.setReleaseDate(movie.getReleaseDate());
        movieResponse.setThumbnailUrl(movie.getThumbnailUrl());
        movieResponse.setViewCount(movie.getViewCount());
        movieResponse.setCreatedAt(movie.getCreatedAt());

        movieResponse.setGenres(movie.getGenres().stream()
                .map(genre -> GenreResponse.fromGenre(genre))
                .collect(Collectors.toSet()));


        movieResponse.setActors(movie.getActors().stream()
                .map(actor -> ActorResponse.fromActor(actor))
                .collect(Collectors.toSet()));

        return movieResponse;
    }
}
