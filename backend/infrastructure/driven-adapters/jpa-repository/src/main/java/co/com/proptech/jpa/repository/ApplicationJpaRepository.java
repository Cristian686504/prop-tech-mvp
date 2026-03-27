package co.com.proptech.jpa.repository;

import co.com.proptech.jpa.entity.ApplicationEntity;
import co.com.proptech.model.application.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationJpaRepository extends JpaRepository<ApplicationEntity, UUID> {
    
    List<ApplicationEntity> findByTenantId(UUID tenantId);
    
    List<ApplicationEntity> findByPropertyId(UUID propertyId);
    
    boolean existsByPropertyIdAndTenantIdAndStatus(UUID propertyId, UUID tenantId, ApplicationStatus status);
}
