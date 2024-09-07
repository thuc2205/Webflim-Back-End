package com.example.flim.controllers;

import com.example.flim.entities.Favorite;
import com.example.flim.repositories.FavoriteRepository;
import com.example.flim.request.FavoriteRequest;
import com.example.flim.response.FavoriteResponse;
import com.example.flim.services.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final FavoriteRepository repository;


    @GetMapping("/username/{username}")
    public ResponseEntity<List<FavoriteResponse>> getFavoritesByUsername(@PathVariable String username) {
        List<FavoriteResponse> favoriteResponses = favoriteService.getFavoritesByUsername(username);
        if (favoriteResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(favoriteResponses);
    }

    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteRequest request) {
        try {
            Favorite favorite = favoriteService.addFavorite(request);
            return ResponseEntity.ok(FavoriteResponse.fromFavorite(favorite));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Favorite> getFavoriteById(@PathVariable int id) {
        Favorite favorite = favoriteService.getFavoriteById(id);
        if (favorite != null) {
            return ResponseEntity.ok(favorite);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFavorite(@PathVariable int id) {
        favoriteService.removeFavorite(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getFavoritesByUserId(@PathVariable int userId) {
        List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
        return ResponseEntity.ok(favorites);
    }

    // FavoriteController.java

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFavorite(@RequestBody FavoriteRequest requestDTO) {
        try {
            favoriteService.removeFavorite(requestDTO.getUsername(), requestDTO.getTitleMovie());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/find")
    public ResponseEntity<FavoriteResponse> findFavorite(@RequestBody FavoriteRequest request) {
        Favorite favorite = repository.findFavoriteByUserAndMovie(request.getUsername(), request.getTitleMovie());
        if (favorite != null) {
            return ResponseEntity.ok(FavoriteResponse.fromFavorite(favorite));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



}
