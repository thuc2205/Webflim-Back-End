package com.example.flim.controllers;

import com.example.flim.entities.Movie;
import com.example.flim.entities.User;
import com.example.flim.repositories.MovieRepository;
import com.example.flim.repositories.UserRepository;
import com.example.flim.request.MovieRequest;
import com.example.flim.response.MovieResponse;
import com.example.flim.services.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
    @RequestMapping("${api.prefix}/movies")
public class MovieController {

    private final MovieService movieService;
    private final MovieRepository repository;
    private final UserRepository userRepository;

    @GetMapping("")
    public ResponseEntity<?> getMovies(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "genre_id") Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int limit
    ) {
        PageRequest pageRequest;
        if (genreId != 0) {
            pageRequest = PageRequest.of(page, limit);
        } else {
            pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
        }
        Page<MovieResponse> moviePage = movieService.getAllMovie(keyword, genreId, pageRequest);
        int totalPage = moviePage.getTotalPages();
        List<MovieResponse> movies = moviePage.getContent();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/newest")
    public ResponseEntity<List<MovieResponse>> getTop3NewestMovies() {
        List<MovieResponse> movies = movieService.getTop3NewestMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/user/{username}/favorites")
    public List<MovieResponse> getFavoriteMovies(@PathVariable(name = "username") String username) {
        List<Movie> movies = repository.findAllFavoriteMoviesByUser(username);

        return movies.stream()
                .map(MovieResponse::fromMovie)
                .collect(Collectors.toList());
    }
    @GetMapping("/{title}")
    public MovieResponse findBytitle(@PathVariable(name = "title")String title){
        return movieService.findMovieByTitle(title);
    }



        @PostMapping("")
        public ResponseEntity<?> createMovie(@RequestBody @Valid MovieRequest movieRequest, BindingResult result) {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Movie movie = movieService.createMovie(movieRequest);
            return ResponseEntity.ok(movie);
        }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable int id, @RequestBody @Valid MovieRequest movieRequest, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        Movie movie = movieService.updateMovie(id, movieRequest);
        return ResponseEntity.ok(movie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable int id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "upload-thumbnail/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadThumbnailImage(@RequestParam("file") MultipartFile file,
                                                  @PathVariable("id") int movieId) {
        try {
            if (file != null && file.getSize() > 0) {
                // Kiểm tra kích thước file
                if (file.getSize() > 10 * 1024 * 1024) { // 10 MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File size exceeds the limit of 10MB.");
                }

                // Kiểm tra định dạng file
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image.");
                }

                // Lưu file và lấy tên file
                String fileName = storefile(file);

                // Cập nhật thumbnailUrl của Movie

                movieService.updateMovieThumbnailUrl(movieId, fileName);

                return ResponseEntity.ok().body("Thumbnail uploaded and updated successfully.");
            } else {
                return ResponseEntity.badRequest().body("File is empty or not provided.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    private String storefile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid file format.");
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        Path uploadDir = Paths.get("uploads/images");

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }
    public boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @GetMapping("/images/{imageName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/images/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());
            if(resource.exists() ){
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
            }else{
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }

        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }




}



