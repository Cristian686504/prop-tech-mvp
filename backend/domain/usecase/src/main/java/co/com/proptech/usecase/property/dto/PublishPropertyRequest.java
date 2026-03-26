package co.com.proptech.usecase.property.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO for publishing a property
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishPropertyRequest {
    private String title;
    private String description;
    private String address;
    private BigDecimal price;
    private List<String> imageUrls;
    private UUID landlordId;
}
