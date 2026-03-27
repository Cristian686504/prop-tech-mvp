package co.com.proptech.usecase.application;

import co.com.proptech.model.application.Application;
import co.com.proptech.model.application.ApplicationStatus;
import co.com.proptech.model.application.RiskLevel;
import co.com.proptech.model.application.gateways.ApplicationRepository;
import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.PropertyStatus;
import co.com.proptech.model.property.gateways.PropertyRepository;
import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.UserRole;
import co.com.proptech.model.user.gateways.UserRepository;
import co.com.proptech.usecase.application.dto.ApplicationWithRisk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get Property Applications Use Case Tests")
class GetPropertyApplicationsUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EvaluateFinancialRiskUseCase evaluateFinancialRiskUseCase;

    @Mock
    private CalculateSecurityDepositUseCase calculateSecurityDepositUseCase;

    private GetPropertyApplicationsUseCase getPropertyApplicationsUseCase;

    @BeforeEach
    void setUp() {
        getPropertyApplicationsUseCase = new GetPropertyApplicationsUseCase(
                applicationRepository,
                propertyRepository,
                userRepository,
                evaluateFinancialRiskUseCase,
                calculateSecurityDepositUseCase
        );
    }

    @Test
    @DisplayName("Should return applications with risk evaluation for property")
    void shouldReturnApplicationsWithRiskEvaluation() {
        // Arrange
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        BigDecimal rentPrice = BigDecimal.valueOf(2000);

        Property property = createProperty(propertyId, landlordId, rentPrice);
        Application application = createApplication(propertyId, tenantId);
        User tenant = createTenant(tenantId, 750, BigDecimal.valueOf(5000));

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(applicationRepository.findByPropertyId(propertyId)).thenReturn(Collections.singletonList(application));
        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice)).thenReturn(RiskLevel.LOW);
        when(calculateSecurityDepositUseCase.calculate(RiskLevel.LOW, rentPrice)).thenReturn(rentPrice);

        // Act
        List<ApplicationWithRisk> result = getPropertyApplicationsUseCase.execute(propertyId, landlordId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ApplicationWithRisk applicationWithRisk = result.get(0);
        assertEquals(application, applicationWithRisk.getApplication());
        assertEquals(tenant, applicationWithRisk.getTenant());
        assertEquals(RiskLevel.LOW, applicationWithRisk.getRiskLevel());
        assertEquals(rentPrice, applicationWithRisk.getSecurityDeposit());

        verify(propertyRepository, times(1)).findById(propertyId);
        verify(applicationRepository, times(1)).findByPropertyId(propertyId);
        verify(userRepository, times(1)).findById(tenantId);
        verify(evaluateFinancialRiskUseCase, times(1)).evaluate(tenant, rentPrice);
        verify(calculateSecurityDepositUseCase, times(1)).calculate(RiskLevel.LOW, rentPrice);
    }

    @Test
    @DisplayName("Should throw exception when propertyId is null")
    void shouldThrowExceptionWhenPropertyIdNull() {
        // Arrange
        UUID landlordId = UUID.randomUUID();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getPropertyApplicationsUseCase.execute(null, landlordId)
        );
        assertEquals("Property ID cannot be null", exception.getMessage());

        verify(propertyRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw exception when property not found")
    void shouldThrowExceptionWhenPropertyNotFound() {
        // Arrange
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getPropertyApplicationsUseCase.execute(propertyId, landlordId)
        );
        assertEquals("Property not found", exception.getMessage());

        verify(propertyRepository, times(1)).findById(propertyId);
        verify(applicationRepository, never()).findByPropertyId(any());
    }

    @Test
    @DisplayName("Should throw exception when landlord not authorized")
    void shouldThrowExceptionWhenLandlordNotAuthorized() {
        // Arrange
        UUID propertyId = UUID.randomUUID();
        UUID correctLandlordId = UUID.randomUUID();
        UUID wrongLandlordId = UUID.randomUUID();

        Property property = createProperty(propertyId, correctLandlordId, BigDecimal.valueOf(2000));

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getPropertyApplicationsUseCase.execute(propertyId, wrongLandlordId)
        );
        assertEquals("Not authorized to view applications for this property", exception.getMessage());

        verify(propertyRepository, times(1)).findById(propertyId);
        verify(applicationRepository, never()).findByPropertyId(any());
    }

    @Test
    @DisplayName("Should return empty list when property has no applications")
    void shouldReturnEmptyListWhenNoApplications() {
        // Arrange
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        BigDecimal rentPrice = BigDecimal.valueOf(2000);

        Property property = createProperty(propertyId, landlordId, rentPrice);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(applicationRepository.findByPropertyId(propertyId)).thenReturn(Collections.emptyList());

        // Act
        List<ApplicationWithRisk> result = getPropertyApplicationsUseCase.execute(propertyId, landlordId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(propertyRepository, times(1)).findById(propertyId);
        verify(applicationRepository, times(1)).findByPropertyId(propertyId);
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should process multiple applications correctly")
    void shouldProcessMultipleApplications() {
        // Arrange
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        UUID tenant1Id = UUID.randomUUID();
        UUID tenant2Id = UUID.randomUUID();
        BigDecimal rentPrice = BigDecimal.valueOf(2000);

        Property property = createProperty(propertyId, landlordId, rentPrice);
        Application app1 = createApplication(propertyId, tenant1Id);
        Application app2 = createApplication(propertyId, tenant2Id);
        User tenant1 = createTenant(tenant1Id, 750, BigDecimal.valueOf(5000));
        User tenant2 = createTenant(tenant2Id, 600, BigDecimal.valueOf(4000));

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(applicationRepository.findByPropertyId(propertyId)).thenReturn(Arrays.asList(app1, app2));
        when(userRepository.findById(tenant1Id)).thenReturn(Optional.of(tenant1));
        when(userRepository.findById(tenant2Id)).thenReturn(Optional.of(tenant2));
        when(evaluateFinancialRiskUseCase.evaluate(tenant1, rentPrice)).thenReturn(RiskLevel.LOW);
        when(evaluateFinancialRiskUseCase.evaluate(tenant2, rentPrice)).thenReturn(RiskLevel.MEDIUM);
        when(calculateSecurityDepositUseCase.calculate(RiskLevel.LOW, rentPrice)).thenReturn(rentPrice);
        when(calculateSecurityDepositUseCase.calculate(RiskLevel.MEDIUM, rentPrice)).thenReturn(rentPrice.multiply(BigDecimal.valueOf(2)));

        // Act
        List<ApplicationWithRisk> result = getPropertyApplicationsUseCase.execute(propertyId, landlordId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(RiskLevel.LOW, result.get(0).getRiskLevel());
        assertEquals(RiskLevel.MEDIUM, result.get(1).getRiskLevel());

        verify(propertyRepository, times(1)).findById(propertyId);
        verify(applicationRepository, times(1)).findByPropertyId(propertyId);
        verify(userRepository, times(1)).findById(tenant1Id);
        verify(userRepository, times(1)).findById(tenant2Id);
    }

    @Test
    @DisplayName("Should calculate income to rent ratio correctly")
    void shouldCalculateIncomeToRentRatioCorrectly() {
        // Arrange
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        BigDecimal rentPrice = BigDecimal.valueOf(2000);
        BigDecimal monthlyIncome = BigDecimal.valueOf(6000);

        Property property = createProperty(propertyId, landlordId, rentPrice);
        Application application = createApplication(propertyId, tenantId);
        User tenant = createTenant(tenantId, 750, monthlyIncome);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(applicationRepository.findByPropertyId(propertyId)).thenReturn(Collections.singletonList(application));
        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice)).thenReturn(RiskLevel.LOW);
        when(calculateSecurityDepositUseCase.calculate(RiskLevel.LOW, rentPrice)).thenReturn(rentPrice);

        // Act
        List<ApplicationWithRisk> result = getPropertyApplicationsUseCase.execute(propertyId, landlordId);

        // Assert
        ApplicationWithRisk applicationWithRisk = result.get(0);
        // 6000 / 2000 = 3.00
        assertEquals(BigDecimal.valueOf(3.00).setScale(2), applicationWithRisk.getIncomeToRentRatio().setScale(2));
    }

    @Test
    @DisplayName("Should set income ratio to zero when tenant has no monthly income")
    void shouldSetIncomeRatioToZeroWhenNoMonthlyIncome() {
        // Arrange
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        BigDecimal rentPrice = BigDecimal.valueOf(2000);

        Property property = createProperty(propertyId, landlordId, rentPrice);
        Application application = createApplication(propertyId, tenantId);
        User tenant = createTenant(tenantId, 750, null);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(applicationRepository.findByPropertyId(propertyId)).thenReturn(Collections.singletonList(application));
        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice)).thenReturn(RiskLevel.HIGH);
        when(calculateSecurityDepositUseCase.calculate(RiskLevel.HIGH, rentPrice)).thenReturn(rentPrice.multiply(BigDecimal.valueOf(3)));

        // Act
        List<ApplicationWithRisk> result = getPropertyApplicationsUseCase.execute(propertyId, landlordId);

        // Assert
        ApplicationWithRisk applicationWithRisk = result.get(0);
        assertEquals(BigDecimal.ZERO, applicationWithRisk.getIncomeToRentRatio());
        assertEquals(RiskLevel.HIGH, applicationWithRisk.getRiskLevel());
    }

    @Test
    @DisplayName("Should throw exception when tenant not found")
    void shouldThrowExceptionWhenTenantNotFound() {
        // Arrange
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        BigDecimal rentPrice = BigDecimal.valueOf(2000);

        Property property = createProperty(propertyId, landlordId, rentPrice);
        Application application = createApplication(propertyId, tenantId);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(applicationRepository.findByPropertyId(propertyId)).thenReturn(Collections.singletonList(application));
        when(userRepository.findById(tenantId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getPropertyApplicationsUseCase.execute(propertyId, landlordId)
        );
        assertEquals("Tenant not found", exception.getMessage());

        verify(propertyRepository, times(1)).findById(propertyId);
        verify(applicationRepository, times(1)).findByPropertyId(propertyId);
        verify(userRepository, times(1)).findById(tenantId);
    }

    @Test
    @DisplayName("Should handle different risk levels correctly")
    void shouldHandleDifferentRiskLevels() {
        // Arrange
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        BigDecimal rentPrice = BigDecimal.valueOf(2000);

        Property property = createProperty(propertyId, landlordId, rentPrice);
        Application application = createApplication(propertyId, tenantId);
        User tenant = createTenant(tenantId, 450, BigDecimal.valueOf(3000));

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(applicationRepository.findByPropertyId(propertyId)).thenReturn(Collections.singletonList(application));
        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice)).thenReturn(RiskLevel.HIGH);
        when(calculateSecurityDepositUseCase.calculate(RiskLevel.HIGH, rentPrice)).thenReturn(rentPrice.multiply(BigDecimal.valueOf(3)));

        // Act
        List<ApplicationWithRisk> result = getPropertyApplicationsUseCase.execute(propertyId, landlordId);

        // Assert
        ApplicationWithRisk applicationWithRisk = result.get(0);
        assertEquals(RiskLevel.HIGH, applicationWithRisk.getRiskLevel());
        assertEquals(rentPrice.multiply(BigDecimal.valueOf(3)), applicationWithRisk.getSecurityDeposit());
    }

    /**
     * Helper method to create a test property
     */
    private Property createProperty(UUID propertyId, UUID landlordId, BigDecimal price) {
        return Property.builder()
                .id(propertyId)
                .landlordId(landlordId)
                .title("Test Property")
                .description("Test Description")
                .address("Test Address")
                .price(price)
                .status(PropertyStatus.AVAILABLE)
                .build();
    }

    /**
     * Helper method to create a test application
     */
    private Application createApplication(UUID propertyId, UUID tenantId) {
        return Application.builder()
                .id(UUID.randomUUID())
                .propertyId(propertyId)
                .tenantId(tenantId)
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Helper method to create a test tenant
     */
    private User createTenant(UUID tenantId, Integer creditScore, BigDecimal monthlyIncome) {
        return User.builder()
                .id(tenantId)
                .email("tenant@test.com")
                .name("Test Tenant")
                .role(UserRole.TENANT)
                .creditScore(creditScore)
                .monthlyIncome(monthlyIncome)
                .build();
    }
}
