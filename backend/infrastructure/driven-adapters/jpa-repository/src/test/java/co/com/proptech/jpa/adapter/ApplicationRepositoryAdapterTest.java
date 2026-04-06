package co.com.proptech.jpa.adapter;

import co.com.proptech.jpa.entity.ApplicationEntity;
import co.com.proptech.jpa.entity.PropertyEntity;
import co.com.proptech.jpa.entity.UserEntity;
import co.com.proptech.jpa.repository.ApplicationJpaRepository;
import co.com.proptech.model.application.Application;
import co.com.proptech.model.application.ApplicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationRepositoryAdapter Unit Tests")
class ApplicationRepositoryAdapterTest {

    @Mock
    private ApplicationJpaRepository jpaRepository;

    private ApplicationRepositoryAdapter adapter;

    private final UUID propertyId = UUID.randomUUID();
    private final UUID tenantId   = UUID.randomUUID();
    private final UUID appId      = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new ApplicationRepositoryAdapter(jpaRepository);
    }

    private ApplicationEntity sampleEntity() {
        return ApplicationEntity.builder()
                .id(appId)
                .property(PropertyEntity.builder().id(propertyId).build())
                .tenant(UserEntity.builder().id(tenantId).build())
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();
    }

    private Application sampleDomain() {
        return Application.builder()
                .id(appId)
                .propertyId(propertyId)
                .tenantId(tenantId)
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("save - should persist and return mapped domain application")
    void shouldSaveAndReturnDomain() {
        ApplicationEntity saved = sampleEntity();
        when(jpaRepository.save(any(ApplicationEntity.class))).thenReturn(saved);

        Application result = adapter.save(sampleDomain());

        assertNotNull(result);
        assertEquals(appId, result.getId());
        assertEquals(propertyId, result.getPropertyId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals(ApplicationStatus.PENDING, result.getStatus());
        verify(jpaRepository).save(any(ApplicationEntity.class));
    }

    @Test
    @DisplayName("findById - should return mapped domain when found")
    void shouldReturnDomainWhenFoundById() {
        when(jpaRepository.findById(appId)).thenReturn(Optional.of(sampleEntity()));

        Optional<Application> result = adapter.findById(appId);

        assertTrue(result.isPresent());
        assertEquals(appId, result.get().getId());
    }

    @Test
    @DisplayName("findById - should return empty when not found")
    void shouldReturnEmptyWhenNotFoundById() {
        when(jpaRepository.findById(appId)).thenReturn(Optional.empty());

        assertFalse(adapter.findById(appId).isPresent());
    }

    @Test
    @DisplayName("findByTenantId - should return list of mapped domain applications")
    void shouldReturnApplicationsByTenantId() {
        when(jpaRepository.findByTenantId(tenantId)).thenReturn(List.of(sampleEntity()));

        List<Application> results = adapter.findByTenantId(tenantId);

        assertEquals(1, results.size());
        assertEquals(tenantId, results.get(0).getTenantId());
    }

    @Test
    @DisplayName("findByTenantId - should return empty list when no applications")
    void shouldReturnEmptyListWhenNoApplicationsByTenantId() {
        when(jpaRepository.findByTenantId(tenantId)).thenReturn(List.of());

        assertTrue(adapter.findByTenantId(tenantId).isEmpty());
    }

    @Test
    @DisplayName("findByPropertyId - should return list of mapped domain applications")
    void shouldReturnApplicationsByPropertyId() {
        when(jpaRepository.findByPropertyId(propertyId)).thenReturn(List.of(sampleEntity()));

        List<Application> results = adapter.findByPropertyId(propertyId);

        assertEquals(1, results.size());
        assertEquals(propertyId, results.get(0).getPropertyId());
    }

    @Test
    @DisplayName("existsByPropertyIdAndTenantIdAndStatus - should delegate to JPA repository")
    void shouldDelegateExistsCheck() {
        when(jpaRepository.existsByPropertyIdAndTenantIdAndStatus(propertyId, tenantId, ApplicationStatus.PENDING))
                .thenReturn(true);
        when(jpaRepository.existsByPropertyIdAndTenantIdAndStatus(propertyId, tenantId, ApplicationStatus.APPROVED))
                .thenReturn(false);

        assertTrue(adapter.existsByPropertyIdAndTenantIdAndStatus(propertyId, tenantId, ApplicationStatus.PENDING));
        assertFalse(adapter.existsByPropertyIdAndTenantIdAndStatus(propertyId, tenantId, ApplicationStatus.APPROVED));
    }
}
