package com.example.flim.repositories;

import com.example.flim.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie,Integer> {


    @Query("SELECT p FROM Movie p WHERE "
            + "(:genreId IS NULL OR :genreId = 0 OR :genreId IN (SELECT g.id FROM p.genres g WHERE g.id = :genreId)) "
            + "AND (:keyword IS NULL OR :keyword = '' OR p.title LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Movie> searchMovies(@Param("genreId") Long genreId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Movie p ORDER BY p.releaseDate DESC")
    List<Movie> findTop3NewestMovies(Pageable pageable);

    @Query("SELECT f.movie FROM Favorite f WHERE f.user.username = :user")
    List<Movie> findAllFavoriteMoviesByUser(String user);

    Optional<Movie> findMovieByTitle(String title);
}
