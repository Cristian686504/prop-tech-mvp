package co.com.proptech.usecase.application;

import co.com.proptech.model.application.Application;
import co.com.proptech.model.application.RiskLevel;
import co.com.proptech.model.application.gateways.ApplicationRepository;
import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.gateways.PropertyRepository;
import co.com.proptech.model.user.User;
import co.com.proptech.model.user.gateways.UserRepository;
import co.com.proptech.usecase.application.dto.ApplicationWithRisk;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GetPropertyApplicationsUseCase {
    
    private final ApplicationRepository applicationRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final EvaluateFinancialRiskUseCase evaluateFinancialRiskUseCase;
    private final CalculateSecurityDepositUseCase calculateSecurityDepositUseCase;
    
    /**
     * Get all applications for a property with financial risk evaluation
     * Validates property exists and belongs to landlord
     */
    public List<ApplicationWithRisk> execute(UUID propertyId, UUID landlordId) {
        if (propertyId == null) {
            throw new IllegalArgumentException("Property ID cannot be null");
        }
        
        // Validate property exists and belongs to landlord
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
        
        if (!property.getLandlordId().equals(landlordId)) {
            throw new IllegalArgumentException("Not authorized to view applications for this property");
        }
        
        // Get all applications and evaluate risk
        List<Application> applications = applicationRepository.findByPropertyId(propertyId);
        
        return applications.stream()
                .map(application -> evaluateApplication(application, property.getPrice()))
                .collect(Collectors.toList());
    }
    
    private ApplicationWithRisk evaluateApplication(Application application, BigDecimal rentPrice) {
        // Get tenant details
        User tenant = userRepository.findById(application.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));
        
        // Calculate risk level
        RiskLevel riskLevel = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);
        
        // Calculate income to rent ratio
        BigDecimal incomeRatio = BigDecimal.ZERO;
        if (tenant.getMonthlyIncome() != null && rentPrice.compareTo(BigDecimal.ZERO) > 0) {
            incomeRatio = tenant.getMonthlyIncome().divide(rentPrice, 2, BigDecimal.ROUND_HALF_UP);
        }
        
        // Calculate security deposit based on risk level (HU008)
        BigDecimal securityDeposit = calculateSecurityDepositUseCase.calculate(riskLevel, rentPrice);
        
        return ApplicationWithRisk.builder()
                .application(application)
                .tenant(tenant)
                .riskLevel(riskLevel)
                .incomeToRentRatio(incomeRatio)
                .securityDeposit(securityDeposit)
                .build();
    }
}
