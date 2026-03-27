package co.com.proptech.usecase.application;

import co.com.proptech.model.application.Application;
import co.com.proptech.model.application.ApplicationStatus;
import co.com.proptech.model.application.gateways.ApplicationRepository;
import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.PropertyStatus;
import co.com.proptech.model.property.gateways.PropertyRepository;
import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.UserRole;
import co.com.proptech.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class ApplyForPropertyUseCase {
    
    private final ApplicationRepository applicationRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    
    /**
     * Apply for property rental
     * Business rules:
     * - Only TENANT can apply
     * - Property must exist and be AVAILABLE
     * - Tenant cannot have duplicate PENDING application for same property
     */
    public Application apply(UUID tenantId, UUID propertyId) {
        // Validate tenant exists and has TENANT role
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        if (tenant.getRole() != UserRole.TENANT) {
            throw new IllegalArgumentException("Only tenants can apply for properties");
        }
        
        // Validate property exists and is available
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
        
        if (property.getStatus() != PropertyStatus.AVAILABLE) {
            throw new IllegalArgumentException("Property is not available for rental");
        }
        
        // Check for duplicate pending application
        if (applicationRepository.existsByPropertyIdAndTenantIdAndStatus(
                propertyId, tenantId, ApplicationStatus.PENDING)) {
            throw new IllegalArgumentException("You already have a pending application for this property");
        }
        
        // Create application
        Application application = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(propertyId)
                .tenantId(tenantId)
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();
        
        application.validate();
        
        return applicationRepository.save(application);
    }
}
