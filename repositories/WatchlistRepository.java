package com.example.flim.repositories;

import com.example.flim.entities.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist,Integer> {
    List<Watchlist> findByUserId(int userId);
}
