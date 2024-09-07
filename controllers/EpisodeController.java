package com.example.flim.controllers;


import com.example.flim.entities.Episode;
import com.example.flim.entities.Movie;
import com.example.flim.request.EpisodeRequest;
import com.example.flim.response.EpisodeResponse;
import com.example.flim.services.EpisodeService;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/episode")
public class EpisodeController {
    @Autowired
    private EpisodeService episodeService;

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping
    public ResponseEntity<List<EpisodeResponse>> getEpisodes(
            @RequestParam("title") String title,
            @RequestParam(value = "episodeNumber", required = false) String episodeNumber) {

        List<EpisodeResponse> episodes = episodeService.getEpisodes(title, episodeNumber);

        return ResponseEntity.ok(episodes);
    }


    @PostMapping
    public ResponseEntity<Episode> createEpisode(@RequestBody EpisodeRequest request) {
        Episode episode = episodeService.createEpisode(request);
        return ResponseEntity.ok(episode);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Episode> getEpisodeById(@PathVariable int id) {
        Episode episode = episodeService.getEpisodeById(id);
        return ResponseEntity.ok(episode);
    }

    @GetMapping("/movie/{title}")
    public ResponseEntity<List<EpisodeResponse>> getEpisodesByMovieId(@PathVariable String title) {
        List<Episode> episodes = episodeService.getEpisodesByMovieTitle(title);
        return ResponseEntity.ok(
                episodes.stream()
                        .map(EpisodeResponse::fromEpisode) // Apply the method on each Episode object
                        .collect(Collectors.toList())
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<Episode> updateEpisode(@PathVariable int id, @RequestBody EpisodeRequest request) {
        Episode updatedEpisode = episodeService.updateEpisode(id, request);
        return ResponseEntity.ok(updatedEpisode);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEpisode(@PathVariable int id) {
        episodeService.deleteEpisode(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping(value = "/upload/{episodeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file,
                                         @PathVariable("episodeId") int episodeId) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded");
            }

            // Kiểm tra kích thước tệp
            if (file.getSize() > 200 * 1024 * 1024) { // 200 MB
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File size exceeds 200 MB limit");
            }

            // Kiểm tra loại tệp
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Must be a video file");
            }

            // Lấy episode hiện tại để kiểm tra xem đã có video chưa
            Episode existingEpisode = episodeService.getEpisodeById(episodeId);
            if (existingEpisode == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Episode not found");
            }

            // Xóa video cũ nếu có
            String currentVideoUrl = existingEpisode.getVideoUrl();
            if (currentVideoUrl != null && !currentVideoUrl.isEmpty()) {
                Path videoPath = Paths.get("uploads/videos", currentVideoUrl);
                Files.deleteIfExists(videoPath);
            }

            // Lưu tệp và nhận URL video mới
            String videoUrl = storeFile(file);

            // Cập nhật thông tin video cho episode
            existingEpisode.setVideoUrl(videoUrl);
            Episode updatedEpisode = episodeService.updateEpisodeVideoUrl(episodeId, videoUrl);

            return ResponseEntity.ok(updatedEpisode);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store video file: " + e.getMessage());
        }
    }


//    @PostMapping(value = "/upload/{episodeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file,
//                                         @PathVariable("episodeId") int episodeId) {
//        try {
//            if (file == null || file.isEmpty()) {
//                return ResponseEntity.badRequest().body("No file uploaded");
//            }
//
//            // Kiểm tra kích thước tệp
//            if (file.getSize() > 200 * 1024 * 1024) { // 100 MB
//                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File size exceeds 100 MB limit");
//            }
//
//            // Kiểm tra loại tệp
//            String contentType = file.getContentType();
//            if (contentType == null || !contentType.startsWith("video/")) {
//                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Must be a video file");
//            }
//
//            // Lưu tệp và nhận URL video
//            String videoUrl = storeFile(file);
//
//            // Cập nhật thông tin video cho phim
//            Episode updatedMovie = episodeService.updateEpisodeVideoUrl(episodeId, videoUrl);
//                return ResponseEntity.ok(updatedMovie);
//
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to store video file: " + e.getMessage());
//        }
//    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isVideoFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Not a video file or file name is null");
        }

        // Loại bỏ ký tự đặc biệt và khoảng trắng
        String fileName = file.getOriginalFilename()
                .replaceAll("[^a-zA-Z0-9.]", "_"); // Thay thế ký tự không hợp lệ bằng dấu gạch dưới
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;

        Path uploadDir = Paths.get("uploads/videos");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName; // Trả về đường dẫn đã mã hóa
    }


    private boolean isVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("video/");
    }

    @GetMapping("/video/{filename:.+}")
    public ResponseEntity<StreamingResponseBody> getVideo(@PathVariable(name = "filename") String filename,
                                                          @RequestHeader HttpHeaders headers) {
        try {
            System.out.println("đang gọi");
            String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8.toString());
            System.out.println("Decoded Filename: " + decodedFilename);

            if (!decodedFilename.endsWith(".mp4")) {
                decodedFilename += ".mp4";
            }

            Path videoPath = Paths.get("uploads", "videos", decodedFilename);
            System.out.println("Video Path: " + videoPath.toAbsolutePath());

            if (!Files.exists(videoPath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            long fileSize = Files.size(videoPath);
            String rangeHeader = headers.getFirst(HttpHeaders.RANGE);

            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                long start = Long.parseLong(ranges[0]);
                long end = fileSize - 1;

                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }

                if (start > end || start >= fileSize) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
                }

                final long rangeStart = start;
                final long rangeEnd = end;

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileSize));
                responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
                responseHeaders.setContentType(MediaType.valueOf("video/mp4"));
                responseHeaders.setContentLength(rangeEnd - rangeStart + 1);

                StreamingResponseBody stream = out -> {
                    try (InputStream inputStream = Files.newInputStream(videoPath)) {
                        inputStream.skip(rangeStart);
                        byte[] buffer = new byte[8192];
                        long bytesRead = 0;
                        int length;
                        while (bytesRead <= (rangeEnd - rangeStart) && (length = inputStream.read(buffer)) != -1) {
                            long remaining = (rangeEnd - rangeStart + 1) - bytesRead;
                            if (length > remaining) {
                                length = (int) remaining;
                            }
                            out.write(buffer, 0, length);
                            bytesRead += length;
                        }
                        out.flush();
                    } catch (IOException e) {
                        System.out.println("IOException during streaming: " + e.getMessage());
                    }
                };

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(responseHeaders)
                        .body(stream);

            } else {
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));
                responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
                responseHeaders.setContentType(MediaType.valueOf("video/mp4"));

                StreamingResponseBody stream = outputStream -> {
                    try (InputStream inputStream = Files.newInputStream(videoPath)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            outputStream.flush();
                            // Optional: adjust sleep if needed
                            Thread.sleep(10);
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                };

                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .body(stream);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



}
