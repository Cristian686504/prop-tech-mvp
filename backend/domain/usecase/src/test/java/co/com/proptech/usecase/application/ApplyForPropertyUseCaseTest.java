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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Apply For Property Use Case Tests")
class ApplyForPropertyUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    private ApplyForPropertyUseCase applyForPropertyUseCase;

    @BeforeEach
    void setUp() {
        applyForPropertyUseCase = new ApplyForPropertyUseCase(
                applicationRepository,
                propertyRepository,
                userRepository
        );
    }

    @Test
    @DisplayName("Should apply for property successfully when all conditions are met")
    void shouldApplyForPropertySuccessfully() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();

        User tenant = User.builder()
                .id(tenantId)
                .email("tenant@test.com")
                .role(UserRole.TENANT)
                .build();

        Property property = Property.builder()
                .id(propertyId)
                .landlordId(landlordId)
                .title("Test Property")
                .description("Test Description")
                .address("Test Address")
                .price(BigDecimal.valueOf(1000))
                .status(PropertyStatus.AVAILABLE)
                .build();

        Application savedApplication = Application.builder()
                .id(UUID.randomUUID())
                .propertyId(propertyId)
                .tenantId(tenantId)
                .status(ApplicationStatus.PENDING)
                .build();

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(applicationRepository.existsByPropertyIdAndTenantIdAndStatus(
                propertyId, tenantId, ApplicationStatus.PENDING)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(savedApplication);

        // Act
        Application result = applyForPropertyUseCase.apply(tenantId, propertyId);

        // Assert
        assertNotNull(result);
        assertEquals(propertyId, result.getPropertyId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals(ApplicationStatus.PENDING, result.getStatus());

        verify(userRepository, times(1)).findById(tenantId);
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(applicationRepository, times(1)).existsByPropertyIdAndTenantIdAndStatus(
                propertyId, tenantId, ApplicationStatus.PENDING);
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    @DisplayName("Should throw exception when tenant not found")
    void shouldThrowExceptionWhenTenantNotFound() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        when(userRepository.findById(tenantId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applyForPropertyUseCase.apply(tenantId, propertyId)
        );
        assertEquals("Tenant not found", exception.getMessage());

        verify(userRepository, times(1)).findById(tenantId);
        verify(propertyRepository, never()).findById(any());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user is not a tenant")
    void shouldThrowExceptionWhenUserNotTenant() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        User landlord = User.builder()
                .id(userId)
                .email("landlord@test.com")
                .role(UserRole.LANDLORD)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(landlord));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applyForPropertyUseCase.apply(userId, propertyId)
        );
        assertEquals("Only tenants can apply for properties", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(propertyRepository, never()).findById(any());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when property not found")
    void shouldThrowExceptionWhenPropertyNotFound() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        User tenant = User.builder()
                .id(tenantId)
                .email("tenant@test.com")
                .role(UserRole.TENANT)
                .build();

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applyForPropertyUseCase.apply(tenantId, propertyId)
        );
        assertEquals("Property not found", exception.getMessage());

        verify(userRepository, times(1)).findById(tenantId);
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when property is not available")
    void shouldThrowExceptionWhenPropertyNotAvailable() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();

        User tenant = User.builder()
                .id(tenantId)
                .email("tenant@test.com")
                .role(UserRole.TENANT)
                .build();

        Property rentedProperty = Property.builder()
                .id(propertyId)
                .landlordId(landlordId)
                .title("Rented Property")
                .description("Already rented")
                .address("Test Address")
                .price(BigDecimal.valueOf(1000))
                .status(PropertyStatus.RENTED)
                .build();

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(rentedProperty));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applyForPropertyUseCase.apply(tenantId, propertyId)
        );
        assertEquals("Property is not available for rental", exception.getMessage());

        verify(userRepository, times(1)).findById(tenantId);
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when duplicate pending application exists")
    void shouldThrowExceptionWhenDuplicatePendingApplication() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();

        User tenant = User.builder()
                .id(tenantId)
                .email("tenant@test.com")
                .role(UserRole.TENANT)
                .build();

        Property property = Property.builder()
                .id(propertyId)
                .landlordId(landlordId)
                .title("Test Property")
                .description("Test Description")
                .address("Test Address")
                .price(BigDecimal.valueOf(1000))
                .status(PropertyStatus.AVAILABLE)
                .build();

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(applicationRepository.existsByPropertyIdAndTenantIdAndStatus(
                propertyId, tenantId, ApplicationStatus.PENDING)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> applyForPropertyUseCase.apply(tenantId, propertyId)
        );
        assertEquals("You already have a pending application for this property", exception.getMessage());

        verify(userRepository, times(1)).findById(tenantId);
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(applicationRepository, times(1)).existsByPropertyIdAndTenantIdAndStatus(
                propertyId, tenantId, ApplicationStatus.PENDING);
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create application with correct fields")
    void shouldCreateApplicationWithCorrectFields() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();

        User tenant = User.builder()
                .id(tenantId)
                .email("tenant@test.com")
                .role(UserRole.TENANT)
                .build();

        Property property = Property.builder()
                .id(propertyId)
                .landlordId(landlordId)
                .title("Test Property")
                .description("Test Description")
                .address("Test Address")
                .price(BigDecimal.valueOf(1000))
                .status(PropertyStatus.AVAILABLE)
                .build();

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(applicationRepository.existsByPropertyIdAndTenantIdAndStatus(
                propertyId, tenantId, ApplicationStatus.PENDING)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        applyForPropertyUseCase.apply(tenantId, propertyId);

        // Assert
        ArgumentCaptor<Application> applicationCaptor = ArgumentCaptor.forClass(Application.class);
        verify(applicationRepository).save(applicationCaptor.capture());

        Application capturedApplication = applicationCaptor.getValue();
        assertNotNull(capturedApplication.getId());
        assertEquals(propertyId, capturedApplication.getPropertyId());
        assertEquals(tenantId, capturedApplication.getTenantId());
        assertEquals(ApplicationStatus.PENDING, capturedApplication.getStatus());
        assertNotNull(capturedApplication.getAppliedAt());
        assertNull(capturedApplication.getEvaluatedAt());
    }

    @Test
    @DisplayName("Should allow same tenant to apply to different properties")
    void shouldAllowSameTenantToApplyToDifferentProperties() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        UUID propertyId1 = UUID.randomUUID();
        UUID propertyId2 = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();

        User tenant = User.builder()
                .id(tenantId)
                .email("tenant@test.com")
                .role(UserRole.TENANT)
                .build();

        Property property2 = Property.builder()
                .id(propertyId2)
                .landlordId(landlordId)
                .title("Second Property")
                .description("Another property")
                .address("Another Address")
                .price(BigDecimal.valueOf(1500))
                .status(PropertyStatus.AVAILABLE)
                .build();

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(propertyId2)).thenReturn(Optional.of(property2));
        when(applicationRepository.existsByPropertyIdAndTenantIdAndStatus(
                propertyId2, tenantId, ApplicationStatus.PENDING)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act & Assert
        assertDoesNotThrow(() -> applyForPropertyUseCase.apply(tenantId, propertyId2));

        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    @DisplayName("Should allow tenant to reapply after previous rejection")
    void shouldAllowReapplyAfterRejection() {
        // Arrange
        UUID tenantId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();

        User tenant = User.builder()
                .id(tenantId)
                .email("tenant@test.com")
                .role(UserRole.TENANT)
                .build();

        Property property = Property.builder()
                .id(propertyId)
                .landlordId(landlordId)
                .title("Test Property")
                .description("Test Description")
                .address("Test Address")
                .price(BigDecimal.valueOf(1000))
                .status(PropertyStatus.AVAILABLE)
                .build();

        // No pending application exists (previous was rejected)
        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(applicationRepository.existsByPropertyIdAndTenantIdAndStatus(
                propertyId, tenantId, ApplicationStatus.PENDING)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act & Assert
        assertDoesNotThrow(() -> applyForPropertyUseCase.apply(tenantId, propertyId));

        verify(applicationRepository, times(1)).save(any(Application.class));
    }
}
