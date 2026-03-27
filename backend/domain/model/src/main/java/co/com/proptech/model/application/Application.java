package co.com.proptech.model.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    
    private UUID id;
    private UUID propertyId;
    private UUID tenantId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime evaluatedAt;
    
    /**
     * Domain validation - Business rules
     */
    public void validate() {
        if (propertyId == null) {
            throw new IllegalArgumentException("Property ID cannot be null");
        }
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }
}
