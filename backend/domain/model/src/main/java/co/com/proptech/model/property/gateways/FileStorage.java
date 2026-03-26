package co.com.proptech.model.property.gateways;

import java.io.InputStream;

public interface FileStorage {
    
    /**
     * Stores a file and returns its accessible URL
     * 
     * @param fileName Original file name
     * @param contentType MIME type (e.g., "image/jpeg")
     * @param inputStream File content
     * @return URL where the file can be accessed
     */
    String store(String fileName, String contentType, InputStream inputStream);
    
    /**
     * Deletes a file by its URL
     * 
     * @param fileUrl The URL of the file to delete
     */
    void delete(String fileUrl);
}
