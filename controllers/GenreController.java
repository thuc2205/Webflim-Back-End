package com.example.flim.controllers;

import com.example.flim.entities.Genre;
import com.example.flim.request.GenreRequest;
import com.example.flim.response.GenreResponse;
import com.example.flim.response.GenreResponse2;
import com.example.flim.response.MovieResponse;
import com.example.flim.services.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<GenreResponse2> getAllGenres(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<GenreResponse> genrePage = genreService.findAllGenres(pageRequest);

        List<GenreResponse> genres = genrePage.getContent();

        GenreResponse2 genreResponse = new GenreResponse2();
        genreResponse.setGenres(genres);
        genreResponse.setTotalPages(genrePage.getTotalPages());
        genreResponse.setTotalElements(genrePage.getTotalElements());
        genreResponse.setCurrentPage(genrePage.getNumber());
        genreResponse.setPageSize(genrePage.getSize());

        return ResponseEntity.ok(genreResponse);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createGenre(@RequestBody @Valid GenreRequest genreRequest, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        Genre genre = genreService.saveGenre(genreRequest);
        return ResponseEntity.ok(genre);
    }

    @PutMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateGenre(@PathVariable String name, @RequestBody @Valid GenreRequest genreRequest, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        Genre updatedGenre = genreService.updateGenre(name, genreRequest);
        if (updatedGenre == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedGenre);
    }


    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteGenre(@PathVariable(name = "name") String name) {
        boolean isDeleted = genreService.deleteGenre(name);
        if (!isDeleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ApiResponse("Successfully deleted"));
    }

    // Tạo lớp ApiResponse để gửi phản hồi JSON
    public class ApiResponse {
        private String message;

        public ApiResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGenre(@PathVariable int id) {
        Genre genre = genreService.getGenreById(id);
        if (genre == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(genre);
    }
}
