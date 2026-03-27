package co.com.proptech.usecase.application;

import co.com.proptech.model.application.RiskLevel;
import co.com.proptech.model.user.User;

import java.math.BigDecimal;

/**
 * Evaluates financial risk of a tenant based on credit score and income ratio
 * 
 * Risk Matrix:
 * - Credit Score >= 700 + Income >= 2x rent = LOW
 * - Credit Score >= 700 + Income < 2x rent = MEDIUM
 * - Credit Score 500-699 + Income >= 3x rent = LOW
 * - Credit Score 500-699 + Income 2x-3x rent = MEDIUM
 * - Credit Score 500-699 + Income < 2x rent = HIGH
 * - Credit Score < 500 + Income >= 3x rent = MEDIUM
 * - Credit Score < 500 + Income < 3x rent = HIGH
 */
public class EvaluateFinancialRiskUseCase {
    
    /**
     * Calculate risk level for tenant based on financial profile
     * 
     * @param tenant User with role TENANT
     * @param rentPrice Monthly rent price
     * @return RiskLevel (LOW, MEDIUM, HIGH)
     */
    public RiskLevel evaluate(User tenant, BigDecimal rentPrice) {
        if (tenant.getCreditScore() == null || tenant.getMonthlyIncome() == null) {
            // If no financial data, return HIGH risk
            return RiskLevel.HIGH;
        }
        
        int creditScore = tenant.getCreditScore();
        BigDecimal monthlyIncome = tenant.getMonthlyIncome();
        
        // Calculate income to rent ratio
        BigDecimal incomeRatio = monthlyIncome.divide(rentPrice, 2, BigDecimal.ROUND_HALF_UP);
        
        // Apply risk matrix rules
        if (creditScore >= 700) {
            // High credit score (>=700)
            if (incomeRatio.compareTo(BigDecimal.valueOf(2)) >= 0) {
                return RiskLevel.LOW;  // Income >= 2x rent
            } else {
                return RiskLevel.MEDIUM;  // Income < 2x rent
            }
        } else if (creditScore >= 500) {
            // Medium credit score (500-699)
            if (incomeRatio.compareTo(BigDecimal.valueOf(3)) >= 0) {
                return RiskLevel.LOW;  // Income >= 3x rent
            } else if (incomeRatio.compareTo(BigDecimal.valueOf(2)) >= 0) {
                return RiskLevel.MEDIUM;  // Income 2x-3x rent
            } else {
                return RiskLevel.HIGH;  // Income < 2x rent
            }
        } else {
            // Low credit score (<500)
            if (incomeRatio.compareTo(BigDecimal.valueOf(3)) >= 0) {
                return RiskLevel.MEDIUM;  // Income >= 3x rent
            } else {
                return RiskLevel.HIGH;  // Income < 3x rent
            }
        }
    }
}
