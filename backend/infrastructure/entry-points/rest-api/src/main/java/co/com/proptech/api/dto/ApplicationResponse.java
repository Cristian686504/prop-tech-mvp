package co.com.proptech.api.dto;

import co.com.proptech.model.application.ApplicationStatus;
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
public class ApplicationResponse {
    private UUID id;
    private UUID propertyId;
    private UUID tenantId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime evaluatedAt;
}
