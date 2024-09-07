package com.example.flim.response;
import com.example.flim.entities.Movie;
import com.example.flim.entities.Favorite; // Đảm bảo import đúng lớp Favorite
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class FavoriteResponse {
    private MovieResponse movieResponse;
    private Long id;
    private String username;
    private LocalDateTime addedAt;

    public static FavoriteResponse fromFavorite(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .username(favorite.getUser().getUsername())
                .movieResponse(MovieResponse.fromMovie(favorite.getMovie()))
                .addedAt(favorite.getAddedAt())
                .build();
    }
}
