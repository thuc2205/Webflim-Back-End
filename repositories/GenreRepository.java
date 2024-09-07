package com.example.flim.repositories;

import com.example.flim.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre,Integer> {

    @Modifying
    @Query("DELETE FROM Genre g WHERE g.name = :name")
    void deleteByName(@Param("name") String name);

    @Query("SELECT g FROM Genre g WHERE g.name = :name")
    Genre findGenreByName(@Param("name") String name);
}
