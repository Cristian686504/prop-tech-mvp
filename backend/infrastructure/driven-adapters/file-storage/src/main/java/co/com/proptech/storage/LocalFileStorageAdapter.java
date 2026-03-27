package co.com.proptech.storage;

import co.com.proptech.model.property.gateways.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Component
public class LocalFileStorageAdapter implements FileStorage {

    private final Path uploadPath;
    private final String baseUrl;

    public LocalFileStorageAdapter(
            @Value("${file.upload-dir:uploads/properties}") String uploadDir,
            @Value("${file.base-url:http://localhost:8080}") String baseUrl) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;
        
        try {
            Files.createDirectories(this.uploadPath);
            log.info("Upload directory created/verified at: {}", this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Override
    public String store(String fileName, String contentType, InputStream inputStream) {
        try {
            // Generate unique filename
            String extension = getFileExtension(fileName);
            String uniqueFileName = UUID.randomUUID() + extension;
            
            // Store file
            Path targetLocation = this.uploadPath.resolve(uniqueFileName);
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // Return accessible URL
            String fileUrl = baseUrl + "/uploads/properties/" + uniqueFileName;
            log.info("File stored successfully: {}", fileUrl);
            
            return fileUrl;
        } catch (IOException e) {
            log.error("Failed to store file: {}", fileName, e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String fileUrl) {
        try {
            // Extract filename from URL
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            Path filePath = uploadPath.resolve(fileName);
            
            Files.deleteIfExists(filePath);
            log.info("File deleted successfully: {}", fileName);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileUrl, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}
