package co.com.proptech.api.controller;

import co.com.proptech.api.dto.ApplicationRequest;
import co.com.proptech.api.dto.ApplicationResponse;
import co.com.proptech.api.dto.ApplicationWithRiskResponse;
import co.com.proptech.model.application.Application;
import co.com.proptech.model.application.ApplicationStatus;
import co.com.proptech.model.application.RiskLevel;
import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.DocumentType;
import co.com.proptech.model.user.enums.UserRole;
import co.com.proptech.usecase.application.ApplyForPropertyUseCase;
import co.com.proptech.usecase.application.GetPropertyApplicationsUseCase;
import co.com.proptech.usecase.application.GetTenantApplicationsUseCase;
import co.com.proptech.usecase.application.dto.ApplicationWithRisk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationController - Unit Tests")
class ApplicationControllerTest {

    @Mock private ApplyForPropertyUseCase applyForPropertyUseCase;
    @Mock private GetTenantApplicationsUseCase getTenantApplicationsUseCase;
    @Mock private GetPropertyApplicationsUseCase getPropertyApplicationsUseCase;

    @InjectMocks
    private ApplicationController controller;

    private final UUID tenantId    = UUID.randomUUID();
    private final UUID propertyId  = UUID.randomUUID();
    private final UUID app1Id      = UUID.randomUUID();

    private Application sampleApplication() {
        return Application.builder()
                .id(app1Id)
                .propertyId(propertyId)
                .tenantId(tenantId)
                .status(ApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .build();
    }

    private User sampleTenant() {
        return User.builder()
                .id(tenantId)
                .name("Carlos Tenant")
                .email("carlos@tenant.com")
                .phone("3001234567")
                .documentType(DocumentType.CC)
                .documentId("1234567890")
                .role(UserRole.TENANT)
                .monthlyIncome(new BigDecimal("5000000"))
                .creditScore(720)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ------------------------------------------------------------------
    // applyForProperty
    // ------------------------------------------------------------------

    @Test
    @DisplayName("applyForProperty - should return 200 with application response")
    void shouldApplyForProperty() {
        ApplicationRequest request = ApplicationRequest.builder()
                .propertyId(propertyId)
                .build();

        when(applyForPropertyUseCase.apply(tenantId, propertyId)).thenReturn(sampleApplication());

        ResponseEntity<ApplicationResponse> response = controller.applyForProperty(request, tenantId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(app1Id);
        assertThat(response.getBody().getPropertyId()).isEqualTo(propertyId);
        assertThat(response.getBody().getTenantId()).isEqualTo(tenantId);
        assertThat(response.getBody().getStatus()).isEqualTo(ApplicationStatus.PENDING);
        verify(applyForPropertyUseCase).apply(tenantId, propertyId);
    }

    // ------------------------------------------------------------------
    // getMyApplications
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getMyApplications - should return 200 with list of applications")
    void shouldReturnMyApplications() {
        when(getTenantApplicationsUseCase.execute(tenantId)).thenReturn(List.of(sampleApplication()));

        ResponseEntity<List<ApplicationResponse>> response = controller.getMyApplications(tenantId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getTenantId()).isEqualTo(tenantId);
        verify(getTenantApplicationsUseCase).execute(tenantId);
    }

    @Test
    @DisplayName("getMyApplications - should return 200 with empty list when no applications")
    void shouldReturnEmptyListWhenNoApplications() {
        when(getTenantApplicationsUseCase.execute(tenantId)).thenReturn(List.of());

        ResponseEntity<List<ApplicationResponse>> response = controller.getMyApplications(tenantId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    // ------------------------------------------------------------------
    // getPropertyApplications (includes mapToResponseWithRisk coverage)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getPropertyApplications - should return 200 with enriched risk response")
    void shouldReturnPropertyApplicationsWithRisk() {
        UUID landlordId = UUID.randomUUID();
        ApplicationWithRisk appWithRisk = ApplicationWithRisk.builder()
                .application(sampleApplication())
                .tenant(sampleTenant())
                .riskLevel(RiskLevel.LOW)
                .incomeToRentRatio(new BigDecimal("2.78"))
                .securityDeposit(new BigDecimal("1500000"))
                .build();

        when(getPropertyApplicationsUseCase.execute(propertyId, landlordId))
                .thenReturn(List.of(appWithRisk));

        ResponseEntity<List<ApplicationWithRiskResponse>> response =
                controller.getPropertyApplications(propertyId, landlordId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);

        ApplicationWithRiskResponse body = response.getBody().get(0);
        assertThat(body.getId()).isEqualTo(app1Id);
        assertThat(body.getPropertyId()).isEqualTo(propertyId);
        assertThat(body.getTenantName()).isEqualTo("Carlos Tenant");
        assertThat(body.getTenantEmail()).isEqualTo("carlos@tenant.com");
        assertThat(body.getTenantPhone()).isEqualTo("3001234567");
        assertThat(body.getMonthlyIncome()).isEqualByComparingTo(new BigDecimal("5000000"));
        assertThat(body.getCreditScore()).isEqualTo(720);
        assertThat(body.getRiskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(body.getIncomeToRentRatio()).isEqualByComparingTo(new BigDecimal("2.78"));
        assertThat(body.getSecurityDeposit()).isEqualByComparingTo(new BigDecimal("1500000"));

        verify(getPropertyApplicationsUseCase).execute(propertyId, landlordId);
    }

    @Test
    @DisplayName("getPropertyApplications - should return 200 with empty list when no applications")
    void shouldReturnEmptyPropertyApplications() {
        UUID landlordId = UUID.randomUUID();
        when(getPropertyApplicationsUseCase.execute(propertyId, landlordId)).thenReturn(List.of());

        ResponseEntity<List<ApplicationWithRiskResponse>> response =
                controller.getPropertyApplications(propertyId, landlordId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}
