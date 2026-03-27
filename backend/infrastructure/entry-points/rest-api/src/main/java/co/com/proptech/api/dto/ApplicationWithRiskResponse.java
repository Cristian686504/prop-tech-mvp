package co.com.proptech.api.dto;

import co.com.proptech.model.application.ApplicationStatus;
import co.com.proptech.model.application.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for application with financial risk evaluation
 * Used by LANDLORD to view applications with risk assessment
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationWithRiskResponse {
    // Application data
    private UUID id;
    private UUID propertyId;
    private UUID tenantId;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime evaluatedAt;
    
    // Tenant information
    private String tenantName;
    private String tenantEmail;
    private String tenantPhone;
    
    // Financial evaluation
    private BigDecimal monthlyIncome;
    private Integer creditScore;
    private RiskLevel riskLevel;
    private BigDecimal incomeToRentRatio;  // How many times income covers rent
    private BigDecimal securityDeposit;     // Security deposit amount (1-3 months based on risk)
}
