package co.com.proptech.usecase.application;

import co.com.proptech.model.application.Application;
import co.com.proptech.model.application.ApplicationStatus;
import co.com.proptech.model.application.gateways.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get Tenant Applications Use Case Tests")
class GetTenantApplicationsUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;

    private GetTenantApplicationsUseCase getTenantApplicationsUseCase;

    @BeforeEach
    void setUp() {
        getTenantApplicationsUseCase = new GetTenantApplicationsUseCase(applicationRepository);
    }

    @Test
    @DisplayName("Should return all applications for tenant")
    void shouldReturnAllApplicationsForTenant() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        List<Application> expectedApplications = createSampleApplications(tenantId);

        when(applicationRepository.findByTenantId(tenantId)).thenReturn(expectedApplications);

        // Act
        List<Application> result = getTenantApplicationsUseCase.execute(tenantId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedApplications, result);

        verify(applicationRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should return empty list when tenant has no applications")
    void shouldReturnEmptyListWhenNoApplications() {
        // Arrange
        UUID tenantId = UUID.randomUUID();

        when(applicationRepository.findByTenantId(tenantId)).thenReturn(Collections.emptyList());

        // Act
        List<Application> result = getTenantApplicationsUseCase.execute(tenantId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(applicationRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should throw exception when tenantId is null")
    void shouldThrowExceptionWhenTenantIdNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getTenantApplicationsUseCase.execute(null)
        );
        assertEquals("Tenant ID cannot be null", exception.getMessage());

        verify(applicationRepository, never()).findByTenantId(any());
    }

    @Test
    @DisplayName("Should return applications with different statuses")
    void shouldReturnApplicationsWithDifferentStatuses() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        UUID propertyId1 = UUID.randomUUID();
        UUID propertyId2 = UUID.randomUUID();
        UUID propertyId3 = UUID.randomUUID();

        Application pending = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(propertyId1)
                .tenantId(tenantId)
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();

        Application approved = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(propertyId2)
                .tenantId(tenantId)
                .status(ApplicationStatus.APPROVED)
                .appliedAt(LocalDateTime.now().minusDays(2))
                .evaluatedAt(LocalDateTime.now().minusDays(1))
                .build();

        Application rejected = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(propertyId3)
                .tenantId(tenantId)
                .status(ApplicationStatus.REJECTED)
                .appliedAt(LocalDateTime.now().minusDays(3))
                .evaluatedAt(LocalDateTime.now().minusDays(2))
                .build();

        List<Application> applications = Arrays.asList(pending, approved, rejected);

        when(applicationRepository.findByTenantId(tenantId)).thenReturn(applications);

        // Act
        List<Application> result = getTenantApplicationsUseCase.execute(tenantId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(app -> app.getStatus() == ApplicationStatus.PENDING));
        assertTrue(result.stream().anyMatch(app -> app.getStatus() == ApplicationStatus.APPROVED));
        assertTrue(result.stream().anyMatch(app -> app.getStatus() == ApplicationStatus.REJECTED));

        verify(applicationRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should return applications for correct tenant only")
    void shouldReturnApplicationsForCorrectTenantOnly() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        List<Application> tenantApplications = createSampleApplications(tenantId);

        when(applicationRepository.findByTenantId(tenantId)).thenReturn(tenantApplications);

        // Act
        List<Application> result = getTenantApplicationsUseCase.execute(tenantId);

        // Assert
        assertNotNull(result);
        assertTrue(result.stream().allMatch(app -> app.getTenantId().equals(tenantId)));

        verify(applicationRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should call repository only once")
    void shouldCallRepositoryOnlyOnce() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        when(applicationRepository.findByTenantId(tenantId)).thenReturn(Collections.emptyList());

        // Act
        getTenantApplicationsUseCase.execute(tenantId);

        // Assert
        verify(applicationRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should handle repository returning null")
    void shouldHandleRepositoryReturningNull() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        when(applicationRepository.findByTenantId(tenantId)).thenReturn(null);

        // Act
        List<Application> result = getTenantApplicationsUseCase.execute(tenantId);

        // Assert
        assertNull(result);

        verify(applicationRepository, times(1)).findByTenantId(tenantId);
    }

    @Test
    @DisplayName("Should return applications in order returned by repository")
    void shouldReturnApplicationsInRepositoryOrder() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        List<Application> applications = createSampleApplications(tenantId);

        when(applicationRepository.findByTenantId(tenantId)).thenReturn(applications);

        // Act
        List<Application> result = getTenantApplicationsUseCase.execute(tenantId);

        // Assert
        assertNotNull(result);
        assertEquals(applications.size(), result.size());
        for (int i = 0; i < applications.size(); i++) {
            assertEquals(applications.get(i).getId(), result.get(i).getId());
        }

        verify(applicationRepository, times(1)).findByTenantId(tenantId);
    }

    /**
     * Helper method to create sample applications for testing
     */
    private List<Application> createSampleApplications(UUID tenantId) {
        Application app1 = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(UUID.randomUUID())
                .tenantId(tenantId)
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();

        Application app2 = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(UUID.randomUUID())
                .tenantId(tenantId)
                .status(ApplicationStatus.APPROVED)
                .appliedAt(LocalDateTime.now().minusDays(1))
                .evaluatedAt(LocalDateTime.now())
                .build();

        Application app3 = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(UUID.randomUUID())
                .tenantId(tenantId)
                .status(ApplicationStatus.REJECTED)
                .appliedAt(LocalDateTime.now().minusDays(2))
                .evaluatedAt(LocalDateTime.now().minusDays(1))
                .build();

        return Arrays.asList(app1, app2, app3);
    }
}
