package com.example.flim.response;

import com.example.flim.entities.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenreResponse2 {
    private List<GenreResponse> genres;
    private int totalPages;
    private long totalElements;
    private int currentPage;
    private int pageSize;

}
