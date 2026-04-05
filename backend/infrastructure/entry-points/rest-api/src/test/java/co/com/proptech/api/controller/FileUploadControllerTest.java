package co.com.proptech.api.controller;

import co.com.proptech.model.property.gateways.FileStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for FileUploadController size-limit validation.
 *
 * Uses a lightweight {@link SizedMultipartFile} stub whose {@code getSize()} returns
 * a configurable logical value without allocating the full byte array — making
 * 250 MB boundary tests feasible in CI without heap pressure.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FileUploadController - Size limit tests")
class FileUploadControllerTest {

    private static final long MB = 1024L * 1024L;

    @Mock
    private FileStorage fileStorage;

    @InjectMocks
    private FileUploadController controller;

    // -----------------------------------------------------------------------
    // Happy path — files at or below the 250 MB limit
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Should return 200 when a single image is exactly 250 MB")
    void shouldReturn200WhenFileSizeIsExactly250MB() throws Exception {
        when(fileStorage.store(anyString(), anyString(), any(InputStream.class)))
                .thenReturn("http://localhost:8080/uploads/properties/photo.jpg");

        MultipartFile[] files = {new SizedMultipartFile("photo.jpg", "image/jpeg", 250 * MB)};

        ResponseEntity<?> response = controller.uploadImages(files);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Map<?, ?>) response.getBody()).get("urls")).isNotNull();
    }

    @Test
    @DisplayName("Should return 200 when total size of two images is exactly 250 MB")
    void shouldReturn200WhenTotalSizeIsExactly250MB() throws Exception {
        when(fileStorage.store(anyString(), anyString(), any(InputStream.class)))
                .thenReturn("http://localhost:8080/uploads/properties/photo.jpg");

        // 125 MB + 125 MB = 250 MB total
        MultipartFile[] files = {
            new SizedMultipartFile("a.jpg", "image/jpeg", 125 * MB),
            new SizedMultipartFile("b.jpg", "image/jpeg", 125 * MB)
        };

        ResponseEntity<?> response = controller.uploadImages(files);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // -----------------------------------------------------------------------
    // Boundary violations — files above 250 MB
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Should return 413 when a single image is 251 MB")
    void shouldReturn413WhenSingleFileExceeds250MB() {
        MultipartFile[] files = {new SizedMultipartFile("big.jpg", "image/jpeg", 251 * MB)};

        ResponseEntity<?> response = controller.uploadImages(files);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(((Map<?, ?>) response.getBody()).get("error"))
                .isEqualTo("Total file size must not exceed 250MB");
    }

    @Test
    @DisplayName("Should return 413 when combined size of two images exceeds 250 MB")
    void shouldReturn413WhenTotalSizeExceeds250MB() {
        MultipartFile[] files = {
            new SizedMultipartFile("a.jpg", "image/jpeg", 200 * MB),
            new SizedMultipartFile("b.jpg", "image/jpeg", 51 * MB)
        };

        ResponseEntity<?> response = controller.uploadImages(files);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
    }

    // -----------------------------------------------------------------------
    // Content-type validation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Should return 400 when file is not an image")
    void shouldReturn400ForNonImageFile() {
        MultipartFile[] files = {new SizedMultipartFile("doc.pdf", "application/pdf", 1 * MB)};

        ResponseEntity<?> response = controller.uploadImages(files);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((Map<?, ?>) response.getBody()).get("error"))
                .isEqualTo("File must be an image");
    }

    @Test
    @DisplayName("Should return 400 when no files are provided")
    void shouldReturn400WhenNoFilesProvided() {
        ResponseEntity<?> response = controller.uploadImages(new MultipartFile[0]);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // -----------------------------------------------------------------------
    // Lightweight stub: logical size without allocating megabytes
    // -----------------------------------------------------------------------

    /**
     * A {@link MultipartFile} whose {@link #getSize()} returns a configurable
     * {@code logicalSize} but whose actual content is a minimal JPEG header (4 bytes).
     * This lets the controller's size-validation logic be tested without allocating
     * hundreds of megabytes in heap.
     */
    static class SizedMultipartFile implements MultipartFile {
        private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        private final String originalFilename;
        private final String contentType;
        private final long logicalSize;

        SizedMultipartFile(String originalFilename, String contentType, long logicalSize) {
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.logicalSize = logicalSize;
        }

        @Override public String getName()             { return "files"; }
        @Override public String getOriginalFilename() { return originalFilename; }
        @Override public String getContentType()      { return contentType; }
        @Override public boolean isEmpty()            { return false; }
        @Override public long getSize()               { return logicalSize; }
        @Override public byte[] getBytes()            { return JPEG_MAGIC; }
        @Override public InputStream getInputStream() { return new ByteArrayInputStream(JPEG_MAGIC); }
        @Override public void transferTo(File dest) throws IOException {}
    }
}
