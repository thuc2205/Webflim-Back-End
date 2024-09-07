package com.example.flim.response;

import com.example.flim.entities.Genre;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreResponse {
    private Long id;
    private String name;

    public static GenreResponse fromGenre(Genre genre) {
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }
}
