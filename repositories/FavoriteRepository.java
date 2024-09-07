package com.example.flim.repositories;

import com.example.flim.entities.Favorite;
import com.example.flim.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite,Integer> {
    List<Favorite> findByUserId(int userId);

    @Query("SELECT f FROM Favorite f WHERE f.user.username = :username AND f.movie.title = :title")
    Favorite findFavoriteByUserAndMovie(@Param("username") String username, @Param("title") String title);




}
