package co.com.proptech.model.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Application Domain Model Tests")
class ApplicationTest {

    @Test
    @DisplayName("Should create valid application with all fields")
    void shouldCreateValidApplication() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        LocalDateTime appliedAt = LocalDateTime.now();
        LocalDateTime evaluatedAt = LocalDateTime.now().plusDays(1);

        // Act
        Application application = Application.builder()
                .id(id)
                .propertyId(propertyId)
                .tenantId(tenantId)
                .status(ApplicationStatus.PENDING)
                .appliedAt(appliedAt)
                .evaluatedAt(evaluatedAt)
                .build();

        // Assert
        assertNotNull(application);
        assertEquals(id, application.getId());
        assertEquals(propertyId, application.getPropertyId());
        assertEquals(tenantId, application.getTenantId());
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertEquals(appliedAt, application.getAppliedAt());
        assertEquals(evaluatedAt, application.getEvaluatedAt());
    }

    @Test
    @DisplayName("Should validate successfully when all required fields are present")
    void shouldValidateSuccessfully() {
        // Arrange
        Application application = createValidApplication();

        // Act & Assert
        assertDoesNotThrow(() -> application.validate());
    }

    @Test
    @DisplayName("Should throw exception when propertyId is null")
    void shouldThrowExceptionWhenPropertyIdNull() {
        // Arrange
        Application application = createValidApplication();
        application.setPropertyId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> application.validate()
        );
        assertEquals("Property ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when tenantId is null")
    void shouldThrowExceptionWhenTenantIdNull() {
        // Arrange
        Application application = createValidApplication();
        application.setTenantId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> application.validate()
        );
        assertEquals("Tenant ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when status is null")
    void shouldThrowExceptionWhenStatusNull() {
        // Arrange
        Application application = createValidApplication();
        application.setStatus(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> application.validate()
        );
        assertEquals("Status cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should create application with PENDING status")
    void shouldCreateApplicationWithPendingStatus() {
        // Arrange & Act
        Application application = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(UUID.randomUUID())
                .tenantId(UUID.randomUUID())
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();

        // Assert
        assertEquals(ApplicationStatus.PENDING, application.getStatus());
        assertDoesNotThrow(() -> application.validate());
    }

    @Test
    @DisplayName("Should create application with APPROVED status")
    void shouldCreateApplicationWithApprovedStatus() {
        // Arrange & Act
        Application application = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(UUID.randomUUID())
                .tenantId(UUID.randomUUID())
                .status(ApplicationStatus.APPROVED)
                .appliedAt(LocalDateTime.now())
                .evaluatedAt(LocalDateTime.now())
                .build();

        // Assert
        assertEquals(ApplicationStatus.APPROVED, application.getStatus());
        assertDoesNotThrow(() -> application.validate());
    }

    @Test
    @DisplayName("Should create application with REJECTED status")
    void shouldCreateApplicationWithRejectedStatus() {
        // Arrange & Act
        Application application = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(UUID.randomUUID())
                .tenantId(UUID.randomUUID())
                .status(ApplicationStatus.REJECTED)
                .appliedAt(LocalDateTime.now())
                .evaluatedAt(LocalDateTime.now())
                .build();

        // Assert
        assertEquals(ApplicationStatus.REJECTED, application.getStatus());
        assertDoesNotThrow(() -> application.validate());
    }

    @Test
    @DisplayName("Should allow null appliedAt and evaluatedAt")
    void shouldAllowNullTimestamps() {
        // Arrange & Act
        Application application = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(UUID.randomUUID())
                .tenantId(UUID.randomUUID())
                .status(ApplicationStatus.PENDING)
                .appliedAt(null)
                .evaluatedAt(null)
                .build();

        // Assert
        assertNull(application.getAppliedAt());
        assertNull(application.getEvaluatedAt());
        assertDoesNotThrow(() -> application.validate());
    }

    @Test
    @DisplayName("Should allow null id for new applications")
    void shouldAllowNullId() {
        // Arrange & Act
        Application application = Application.builder()
                .id(null)
                .propertyId(UUID.randomUUID())
                .tenantId(UUID.randomUUID())
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();

        // Assert
        assertNull(application.getId());
        assertDoesNotThrow(() -> application.validate());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void shouldUseBuilderPattern() {
        // Arrange & Act
        Application application = Application.builder()
                .propertyId(UUID.randomUUID())
                .tenantId(UUID.randomUUID())
                .status(ApplicationStatus.PENDING)
                .build();

        // Assert
        assertNotNull(application);
        assertNotNull(application.getPropertyId());
        assertNotNull(application.getTenantId());
        assertNotNull(application.getStatus());
    }

    @Test
    @DisplayName("Should support all ApplicationStatus enum values")
    void shouldSupportAllStatusValues() {
        // Arrange
        UUID propertyId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        // Act & Assert - PENDING
        Application pending = Application.builder()
                .propertyId(propertyId)
                .tenantId(tenantId)
                .status(ApplicationStatus.PENDING)
                .build();
        assertEquals(ApplicationStatus.PENDING, pending.getStatus());

        // Act & Assert - APPROVED
        Application approved = Application.builder()
                .propertyId(propertyId)
                .tenantId(tenantId)
                .status(ApplicationStatus.APPROVED)
                .build();
        assertEquals(ApplicationStatus.APPROVED, approved.getStatus());

        // Act & Assert - REJECTED
        Application rejected = Application.builder()
                .propertyId(propertyId)
                .tenantId(tenantId)
                .status(ApplicationStatus.REJECTED)
                .build();
        assertEquals(ApplicationStatus.REJECTED, rejected.getStatus());
    }

    /**
     * Helper method to create a valid application for testing
     */
    private Application createValidApplication() {
        return Application.builder()
                .id(UUID.randomUUID())
                .propertyId(UUID.randomUUID())
                .tenantId(UUID.randomUUID())
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();
    }
}
