package co.com.proptech.api.dto;

import co.com.proptech.model.property.PropertyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponse {

    private UUID id;
    private String title;
    private String description;
    private String address;
    private BigDecimal price;
    private PropertyStatus status;
    private UUID landlordId;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
