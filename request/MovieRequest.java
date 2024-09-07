package com.example.flim.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MovieRequest {

    @NotBlank(message = "Title không được để trống")
    @Size(max = 255, message = "Title không được vượt quá 255 ký tự")
    private String title;

    private String description;

    @JsonProperty("release_date")
    @NotNull(message = "Release date không được để trống")
    private LocalDate releaseDate;


    @JsonProperty("genre_ids")
    private Set<Integer> genreIds;

    @JsonProperty("actor_ids")
    private Set<Integer> actorIds;
}
