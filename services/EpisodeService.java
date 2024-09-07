package com.example.flim.services;

import com.example.flim.entities.Episode;
import com.example.flim.entities.Movie;
import com.example.flim.repositories.EpisodeRepository;
import com.example.flim.repositories.MovieRepository;
import com.example.flim.request.EpisodeRequest;
import com.example.flim.response.EpisodeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EpisodeService {

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private MovieRepository movieRepository;

    public Episode createEpisode(EpisodeRequest request) {
        Optional<Movie> movie = movieRepository.findById(request.getMovieId());
        if (movie.isEmpty()) {
            throw new RuntimeException("Movie not found with id: " + request.getMovieId());
        }
        if (episodeRepository.findEpisodeByEpisodeNumber(request.getEpisodeNumber()).isPresent()) {
            throw new RuntimeException("Episode with this episode number already exists");
        }
        Episode episode = new Episode();
        episode.setTitle(request.getTitle());
        episode.setDescription(request.getDescription());
        episode.setDuration(request.getDuration());
        episode.setEpisodeNumber(request.getEpisodeNumber());
        episode.setMovie(movie.get());

        return episodeRepository.save(episode);
    }

    public Episode getEpisodeById(int id) {
        return episodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Episode not found with id: " + id));
    }

    public List<Episode> getEpisodesByMovieTitle(String title) {
        return episodeRepository.findByMovieTitle(title);
    }

    public Episode updateEpisode(int id, EpisodeRequest request) {
        Episode episode = getEpisodeById(id);

        episode.setTitle(request.getTitle());
        episode.setDescription(request.getDescription());
        episode.setDuration(request.getDuration()); 
        episode.setEpisodeNumber(request.getEpisodeNumber());
        return episodeRepository.save(episode);
    }

    public void deleteEpisode(int id) {
        Episode episode = getEpisodeById(id);
        episodeRepository.delete(episode);
    }

    public Episode updateEpisodeVideoUrl(int episodeId, String videoUrl) {
        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id " + episodeId));
        episode.setVideoUrl(videoUrl);
        return episodeRepository.save(episode);
    }

    public List<EpisodeResponse> getEpisodes(String title, String episodeNumber) {
        List<Episode> episodes;

        if (episodeNumber == null || episodeNumber.isEmpty()) {
            // Nếu không có episodeNumber, lấy tất cả các tập phim của movieId
            episodes = episodeRepository.findByMovieTitle(title);
        } else {
            // Nếu có episodeNumber, tìm tập phim tương ứng
            episodes = episodeRepository.findByMovieIdAndOptionalEpisodeNumber(title, episodeNumber);
        }

        // Chuyển đổi danh sách Episode thành danh sách EpisodeResponse
        return episodes.stream()
                .map(EpisodeResponse::fromEpisode)
                .collect(Collectors.toList());
    }


}
