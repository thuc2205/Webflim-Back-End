package com.example.flim.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    @NotNull
    private int userId;

    @NotNull
    private int movieId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(max = 1000)
    private String comment;
}
