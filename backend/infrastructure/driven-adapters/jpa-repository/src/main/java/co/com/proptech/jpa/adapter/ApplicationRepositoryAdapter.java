package co.com.proptech.jpa.adapter;

import co.com.proptech.jpa.entity.ApplicationEntity;
import co.com.proptech.jpa.entity.PropertyEntity;
import co.com.proptech.jpa.entity.UserEntity;
import co.com.proptech.jpa.repository.ApplicationJpaRepository;
import co.com.proptech.model.application.Application;
import co.com.proptech.model.application.ApplicationStatus;
import co.com.proptech.model.application.gateways.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ApplicationRepositoryAdapter implements ApplicationRepository {
    
    private final ApplicationJpaRepository jpaRepository;
    
    @Override
    public Application save(Application application) {
        ApplicationEntity entity = toEntity(application);
        ApplicationEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Optional<Application> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }
    
    @Override
    public List<Application> findByTenantId(UUID tenantId) {
        return jpaRepository.findByTenantId(tenantId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Application> findByPropertyId(UUID propertyId) {
        return jpaRepository.findByPropertyId(propertyId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByPropertyIdAndTenantIdAndStatus(UUID propertyId, UUID tenantId, ApplicationStatus status) {
        return jpaRepository.existsByPropertyIdAndTenantIdAndStatus(propertyId, tenantId, status);
    }
    
    // Mappers: Entity <-> Domain
    private ApplicationEntity toEntity(Application application) {
        PropertyEntity property = PropertyEntity.builder()
                .id(application.getPropertyId())
                .build();
        
        UserEntity tenant = UserEntity.builder()
                .id(application.getTenantId())
                .build();
        
        return ApplicationEntity.builder()
                .id(application.getId())
                .property(property)
                .tenant(tenant)
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .evaluatedAt(application.getEvaluatedAt())
                .build();
    }
    
    private Application toDomain(ApplicationEntity entity) {
        return Application.builder()
                .id(entity.getId())
                .propertyId(entity.getProperty().getId())
                .tenantId(entity.getTenant().getId())
                .status(entity.getStatus())
                .appliedAt(entity.getAppliedAt())
                .evaluatedAt(entity.getEvaluatedAt())
                .build();
    }
}
