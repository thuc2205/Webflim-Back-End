package com.example.flim.controllers;
import com.example.flim.entities.Watchlist;
import com.example.flim.request.WatchlistRequest;
import com.example.flim.services.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/watchlist")
@RequiredArgsConstructor
public class WatchlistController {


    private final WatchlistService watchlistService;

    @PostMapping
    public ResponseEntity<Watchlist> addToWatchlist(@RequestBody WatchlistRequest request) {
        try {
            Watchlist watchlist = watchlistService.addToWatchlist(request);
            return ResponseEntity.ok(watchlist);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Watchlist> getWatchlistById(@PathVariable int id) {
        Watchlist watchlist = watchlistService.getWatchlistById(id);
        if (watchlist != null) {
            return ResponseEntity.ok(watchlist);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromWatchlist(@PathVariable int id) {
        watchlistService.removeFromWatchlist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Watchlist>> getWatchlistByUserId(@PathVariable int userId) {
        List<Watchlist> watchlists = watchlistService.getWatchlistByUserId(userId);
        return ResponseEntity.ok(watchlists);
    }
}
