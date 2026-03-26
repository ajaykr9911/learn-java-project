package com.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final String UPLOAD_DIR = "uploads/";
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25 MB

    // Allowed MIME-type prefixes
    private static final Set<String> ALLOWED_PREFIXES = Set.of(
            "image/", "video/", "audio/"
    );

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file) throws IOException {

        // ── Validation ──────────────────────────────────────────────────────
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

//        if (file.getSize() > MAX_FILE_SIZE) {
//            return ResponseEntity.badRequest()
//                    .body(Map.of("error", "File too large. Maximum is 25 MB"));
//        }

        String contentType = file.getContentType() == null ? "" : file.getContentType();
        boolean allowed = ALLOWED_PREFIXES.stream().anyMatch(contentType::startsWith);
        if (!allowed) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Unsupported file type: " + contentType));
        }

        // ── Save ─────────────────────────────────────────────────────────────
        String originalName = file.getOriginalFilename() == null
                ? "file"
                : file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");

        String fileName = System.currentTimeMillis() + "_" + originalName;
        Path savePath = Paths.get(UPLOAD_DIR + fileName);
        Files.createDirectories(savePath.getParent());
        Files.write(savePath, file.getBytes());

        String fileUrl = "http://localhost:8080/uploads/" + fileName;
        return ResponseEntity.ok(Map.of("url", fileUrl));
    }
}