package co.com.proptech.model.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Property domain model
 * Represents a property available for rent in the system
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Property {
    
    private UUID id;
    
    private String title;
    
    private String description;
    
    private String address;
    
    private BigDecimal price;
    
    private List<String> imageUrls;
    
    private UUID landlordId;
    
    private PropertyStatus status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    /**
     * Validates property business rules
     * @throws IllegalArgumentException if validation fails
     */
    public void validate() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (title.length() > 255) {
            throw new IllegalArgumentException("Title cannot exceed 255 characters");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (description.length() > 2000) {
            throw new IllegalArgumentException("Description cannot exceed 2000 characters");
        }
        
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }
        if (address.length() > 255) {
            throw new IllegalArgumentException("Address cannot exceed 255 characters");
        }
        
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        
        if (landlordId == null) {
            throw new IllegalArgumentException("Landlord ID is required");
        }
    }
}
