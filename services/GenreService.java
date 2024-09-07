package com.example.flim.services;

import com.example.flim.entities.Genre;
import com.example.flim.repositories.GenreRepository;
import com.example.flim.request.GenreRequest;
import com.example.flim.response.GenreResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    @Transactional
    public Genre saveGenre(GenreRequest genreRequest) {
        Genre genre = Genre.builder()
                .name(genreRequest.getName())
                .build();
        return genreRepository.save(genre);
    }

    @Transactional
    public Genre updateGenre(String name, GenreRequest genreRequest) {
        Genre genre = genreRepository.findGenreByName(name);
        if (genre == null) {
            return null; // Hoặc ném ngoại lệ tùy thuộc vào yêu cầu của bạn
        }
        genre.setName(genreRequest.getName());
        return genreRepository.save(genre);
    }


    @Transactional
    public boolean deleteGenre(String name) {
        // Check if the genre exists before deleting

        genreRepository.deleteByName(name);
        return true;
    }

    public Genre getGenreById(int id) {
        return genreRepository.findById(id).orElse(null);
    }


    public Page<GenreResponse> findAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable).map(GenreResponse::fromGenre);
    }



















}
