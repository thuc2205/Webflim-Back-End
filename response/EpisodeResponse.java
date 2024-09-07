package com.example.flim.response;

import com.example.flim.entities.Episode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeResponse {

    private Long id;
    private String title;
    private String description;
    private String episodeNumber;
    private int duration;
    private String videoUrl;
    private MovieResponse movie;

    // Phương thức để chuyển đổi từ đối tượng Episode sang EpisodeResponse
    public static EpisodeResponse fromEpisode(Episode episode) {
        EpisodeResponse episodeResponse = new EpisodeResponse();
        episodeResponse.setId(episode.getId());
        episodeResponse.setTitle(episode.getTitle());
        episodeResponse.setDescription(episode.getDescription());
        episodeResponse.setEpisodeNumber(episode.getEpisodeNumber());
        episodeResponse.setDuration(episode.getDuration());
        episodeResponse.setVideoUrl(episode.getVideoUrl());

        // Chuyển đổi từ Movie sang MovieResponse
        episodeResponse.setMovie(MovieResponse.fromMovie(episode.getMovie()));

        return episodeResponse;
    }
}
