package co.com.proptech.usecase.application.dto;

import co.com.proptech.model.application.Application;
import co.com.proptech.model.application.RiskLevel;
import co.com.proptech.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for application with tenant details and risk evaluation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationWithRisk {
    private Application application;
    private User tenant;
    private RiskLevel riskLevel;
    private BigDecimal incomeToRentRatio;
    private BigDecimal securityDeposit;  // Calculated based on risk level (1-3 months of rent)
}
