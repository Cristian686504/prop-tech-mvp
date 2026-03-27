package co.com.proptech.api.controller;

import co.com.proptech.api.dto.ApplicationRequest;
import co.com.proptech.api.dto.ApplicationResponse;
import co.com.proptech.api.dto.ApplicationWithRiskResponse;
import co.com.proptech.model.application.Application;
import co.com.proptech.usecase.application.ApplyForPropertyUseCase;
import co.com.proptech.usecase.application.GetPropertyApplicationsUseCase;
import co.com.proptech.usecase.application.GetTenantApplicationsUseCase;
import co.com.proptech.usecase.application.dto.ApplicationWithRisk;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    
    private final ApplyForPropertyUseCase applyForPropertyUseCase;
    private final GetTenantApplicationsUseCase getTenantApplicationsUseCase;
    private final GetPropertyApplicationsUseCase getPropertyApplicationsUseCase;
    
    /**
     * Apply for property rental - TENANT only
     */
    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApplicationResponse> applyForProperty(
            @Valid @RequestBody ApplicationRequest request,
            @RequestAttribute("userId") UUID userId) {
        
        Application application = applyForPropertyUseCase.apply(userId, request.getPropertyId());
        return ResponseEntity.ok(mapToResponse(application));
    }
    
    /**
     * Get my applications - TENANT only
     */
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @RequestAttribute("userId") UUID userId) {
        
        List<Application> applications = getTenantApplicationsUseCase.execute(userId);
        List<ApplicationResponse> response = applications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get applications for a property - LANDLORD only (owner)
     * Includes financial risk evaluation
     */
    @GetMapping("/property/{propertyId}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<ApplicationWithRiskResponse>> getPropertyApplications(
            @PathVariable("propertyId") UUID propertyId,
            @RequestAttribute("userId") UUID userId) {
        
        List<ApplicationWithRisk> applicationsWithRisk = getPropertyApplicationsUseCase.execute(propertyId, userId);
        List<ApplicationWithRiskResponse> response = applicationsWithRisk.stream()
                .map(this::mapToResponseWithRisk)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    private ApplicationResponse mapToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .propertyId(application.getPropertyId())
                .tenantId(application.getTenantId())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .evaluatedAt(application.getEvaluatedAt())
                .build();
    }
    
    private ApplicationWithRiskResponse mapToResponseWithRisk(ApplicationWithRisk appWithRisk) {
        Application app = appWithRisk.getApplication();
        return ApplicationWithRiskResponse.builder()
                .id(app.getId())
                .propertyId(app.getPropertyId())
                .tenantId(app.getTenantId())
                .status(app.getStatus())
                .appliedAt(app.getAppliedAt())
                .evaluatedAt(app.getEvaluatedAt())
                .tenantName(appWithRisk.getTenant().getName())
                .tenantEmail(appWithRisk.getTenant().getEmail())
                .tenantPhone(appWithRisk.getTenant().getPhone())
                .monthlyIncome(appWithRisk.getTenant().getMonthlyIncome())
                .creditScore(appWithRisk.getTenant().getCreditScore())
                .riskLevel(appWithRisk.getRiskLevel())
                .incomeToRentRatio(appWithRisk.getIncomeToRentRatio())
                .securityDeposit(appWithRisk.getSecurityDeposit())
                .build();
    }
}
