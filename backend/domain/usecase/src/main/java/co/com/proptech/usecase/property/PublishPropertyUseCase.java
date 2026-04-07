package co.com.proptech.usecase.property;

import co.com.proptech.model.exceptions.UnauthorizedOperationException;
import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.PropertyStatus;
import co.com.proptech.model.property.gateways.PropertyRepository;
import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.UserRole;
import co.com.proptech.model.user.gateways.UserRepository;
import co.com.proptech.usecase.property.dto.PublishPropertyRequest;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Use case for publishing a new property
 * Following Single Responsibility Principle (SOLID)
 * Only LANDLORD users can publish properties
 */
@RequiredArgsConstructor
public class PublishPropertyUseCase {
    
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    
    /**
     * Execute the use case to publish a property
     * @param request Property details
     * @return Published property
     * @throws IllegalArgumentException if validation fails
     * @throws UnauthorizedOperationException if user is not a landlord
     */
    public Property execute(PublishPropertyRequest request) {
        // Verify landlord exists and has LANDLORD role
        User landlord = userRepository.findById(request.getLandlordId())
                .orElseThrow(() -> new IllegalArgumentException("Landlord not found"));
        
        if (landlord.getRole() != UserRole.LANDLORD) {
            throw new UnauthorizedOperationException("Only landlords can publish properties");
        }
        
        // Create property
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .price(request.getPrice())
                .imageUrls(request.getImageUrls())
                .landlordId(request.getLandlordId())
                .status(PropertyStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Validate business rules
        property.validate();
        
        // Save property
        return propertyRepository.save(property);
    }
}
