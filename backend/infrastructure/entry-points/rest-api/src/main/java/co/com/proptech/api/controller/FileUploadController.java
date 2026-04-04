package co.com.proptech.api.controller;

import co.com.proptech.model.property.gateways.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class FileUploadController {

    private static final long MAX_FILE_SIZE = 250L * 1024 * 1024; // 250MB per file
    private static final long MAX_TOTAL_SIZE = 250L * 1024 * 1024; // 250MB total

    private final FileStorage fileStorage;

    @PostMapping("/upload-images")
    public ResponseEntity<?> uploadImages(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "At least one file is required"));
        }

        // Validate total size
        long totalSize = Arrays.stream(files).mapToLong(MultipartFile::getSize).sum();
        if (totalSize > MAX_TOTAL_SIZE) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(Map.of("error", "Total file size must not exceed 250MB"));
        }

        // Validate each file individually
        for (MultipartFile file : files) {
            String error = validateFile(file);
            if (error != null) {
                return ResponseEntity.badRequest().body(Map.of("error", error));
            }
        }

        try {
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileUrl = fileStorage.store(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getInputStream()
                );
                urls.add(fileUrl);
            }

            log.info("{} images uploaded successfully", urls.size());
            return ResponseEntity.ok(Map.of("urls", urls));

        } catch (IOException e) {
            log.error("Error uploading images", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to upload files"));
        }
    }

    private String validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty";
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return "File must be an image";
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return "File size must not exceed 250MB";
        }
        return null;
    }
}
