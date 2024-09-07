package com.example.flim.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WatchlistRequest {

    private int userId;
    private int movieId;
}
