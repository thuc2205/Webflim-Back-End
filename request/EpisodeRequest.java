package com.example.flim.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EpisodeRequest {
    private int movieId;
    private String title;
    private String description;
    private int duration;
    private String episodeNumber;
    private Date releaseDate;
}