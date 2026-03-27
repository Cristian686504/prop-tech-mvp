package co.com.proptech.usecase.application;

import co.com.proptech.model.application.RiskLevel;

import java.math.BigDecimal;

/**
 * Calculate security deposit based on risk level
 * 
 * Business Rules (HU008):
 * - LOW risk: 1 month of rent
 * - MEDIUM risk: 2 months of rent
 * - HIGH risk: 3 months of rent
 */
public class CalculateSecurityDepositUseCase {
    
    /**
     * Calculate security deposit amount based on risk level
     * 
     * @param riskLevel The evaluated risk level
     * @param rentPrice Monthly rent price
     * @return Security deposit amount
     */
    public BigDecimal calculate(RiskLevel riskLevel, BigDecimal rentPrice) {
        if (riskLevel == null) {
            throw new IllegalArgumentException("Risk level cannot be null");
        }
        if (rentPrice == null || rentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Rent price must be greater than zero");
        }
        
        int months = switch (riskLevel) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
        };
        
        return rentPrice.multiply(BigDecimal.valueOf(months));
    }
}
