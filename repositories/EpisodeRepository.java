package com.example.flim.repositories;

import com.example.flim.entities.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode,Integer> {
    List<Episode> findByMovieTitle(String title);

    Optional<Episode> findByMovieIdAndEpisodeNumber(Long movieId, String episodeNumber);

    Optional<Episode> findEpisodeByEpisodeNumber(String episodeNumber);

    @Query("SELECT e FROM Episode e WHERE e.movie.title = :title AND (:episodeNumber IS NULL OR e.episodeNumber = :episodeNumber)")
    List<Episode> findByMovieIdAndOptionalEpisodeNumber(@Param("title") String title, @Param("episodeNumber") String episodeNumber);

}
