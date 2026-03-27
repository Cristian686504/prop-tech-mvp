package co.com.proptech.model.application.gateways;

import co.com.proptech.model.application.Application;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository {
    
    /**
     * Save or update an application
     */
    Application save(Application application);
    
    /**
     * Find application by ID
     */
    Optional<Application> findById(UUID id);
    
    /**
     * Find all applications submitted by a tenant
     */
    List<Application> findByTenantId(UUID tenantId);
    
    /**
     * Find all applications for a specific property
     */
    List<Application> findByPropertyId(UUID propertyId);
    
    /**
     * Check if tenant already has a pending application for a property
     */
    boolean existsByPropertyIdAndTenantIdAndStatus(UUID propertyId, UUID tenantId, co.com.proptech.model.application.ApplicationStatus status);
}
