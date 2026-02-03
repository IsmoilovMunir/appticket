package com.surnekev.ticketing.web;

import com.surnekev.ticketing.dto.ConcertDto;
import com.surnekev.ticketing.dto.CreateConcertRequest;
import com.surnekev.ticketing.dto.UpdateConcertRequest;
import com.surnekev.ticketing.service.ConcertService;
import com.surnekev.ticketing.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/concerts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class AdminConcertController {

    private final ConcertService concertService;
    private final FileUploadService fileUploadService;

    @GetMapping
    public ResponseEntity<List<ConcertDto>> getAllConcerts() {
        return ResponseEntity.ok(concertService.getAllConcerts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConcertDto> getConcert(@PathVariable Long id) {
        return ResponseEntity.ok(concertService.getConcert(id));
    }

    @PostMapping
    public ResponseEntity<ConcertDto> createConcert(@RequestBody @Valid CreateConcertRequest request) {
        return ResponseEntity.ok(concertService.createConcert(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConcertDto> updateConcert(
            @PathVariable Long id,
            @RequestBody @Valid UpdateConcertRequest request) {
        return ResponseEntity.ok(concertService.updateConcert(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcert(@PathVariable Long id) {
        concertService.deleteConcert(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload/poster")
    public ResponseEntity<Map<String, String>> uploadPoster(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadFile(file, "posters");
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to upload poster: " + e.getMessage()));
        }
    }

    @PostMapping("/upload/scheme")
    public ResponseEntity<Map<String, String>> uploadScheme(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileUploadService.uploadFile(file, "schemes");
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to upload scheme: " + e.getMessage()));
        }
    }
}
