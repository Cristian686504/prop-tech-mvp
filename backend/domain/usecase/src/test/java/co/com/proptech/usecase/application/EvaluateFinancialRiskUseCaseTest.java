package co.com.proptech.usecase.application;

import co.com.proptech.model.application.RiskLevel;
import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Evaluate Financial Risk Use Case Tests")
class EvaluateFinancialRiskUseCaseTest {

    private EvaluateFinancialRiskUseCase evaluateFinancialRiskUseCase;

    @BeforeEach
    void setUp() {
        evaluateFinancialRiskUseCase = new EvaluateFinancialRiskUseCase();
    }

    // High Credit Score (>= 700) Tests

    @Test
    @DisplayName("Should return LOW risk when credit score >= 700 and income >= 2x rent")
    void shouldReturnLowRiskWithHighCreditAndSufficientIncome() {
        // Arrange
        User tenant = createTenant(750, BigDecimal.valueOf(4000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 2x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.LOW, result);
    }

    @Test
    @DisplayName("Should return LOW risk when credit score >= 700 and income > 2x rent")
    void shouldReturnLowRiskWithHighCreditAndHighIncome() {
        // Arrange
        User tenant = createTenant(800, BigDecimal.valueOf(6000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 3x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.LOW, result);
    }

    @Test
    @DisplayName("Should return MEDIUM risk when credit score >= 700 and income < 2x rent")
    void shouldReturnMediumRiskWithHighCreditButLowIncome() {
        // Arrange
        User tenant = createTenant(750, BigDecimal.valueOf(3000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 1.5x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.MEDIUM, result);
    }

    // Medium Credit Score (500-699) Tests

    @Test
    @DisplayName("Should return LOW risk when credit score 500-699 and income >= 3x rent")
    void shouldReturnLowRiskWithMediumCreditAndHighIncome() {
        // Arrange
        User tenant = createTenant(650, BigDecimal.valueOf(6000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 3x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.LOW, result);
    }

    @Test
    @DisplayName("Should return LOW risk when credit score 500-699 and income > 3x rent")
    void shouldReturnLowRiskWithMediumCreditAndVeryHighIncome() {
        // Arrange
        User tenant = createTenant(600, BigDecimal.valueOf(8000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 4x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.LOW, result);
    }

    @Test
    @DisplayName("Should return MEDIUM risk when credit score 500-699 and income 2x-3x rent")
    void shouldReturnMediumRiskWithMediumCreditAndMediumIncome() {
        // Arrange
        User tenant = createTenant(550, BigDecimal.valueOf(5000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 2.5x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.MEDIUM, result);
    }

    @Test
    @DisplayName("Should return MEDIUM risk when credit score 500-699 and income exactly 2x rent")
    void shouldReturnMediumRiskWithMediumCreditAndDoubleIncome() {
        // Arrange
        User tenant = createTenant(600, BigDecimal.valueOf(4000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is exactly 2x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.MEDIUM, result);
    }

    @Test
    @DisplayName("Should return HIGH risk when credit score 500-699 and income < 2x rent")
    void shouldReturnHighRiskWithMediumCreditAndLowIncome() {
        // Arrange
        User tenant = createTenant(650, BigDecimal.valueOf(3000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 1.5x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.HIGH, result);
    }

    // Low Credit Score (< 500) Tests

    @Test
    @DisplayName("Should return MEDIUM risk when credit score < 500 and income >= 3x rent")
    void shouldReturnMediumRiskWithLowCreditAndHighIncome() {
        // Arrange
        User tenant = createTenant(450, BigDecimal.valueOf(6000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 3x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.MEDIUM, result);
    }

    @Test
    @DisplayName("Should return HIGH risk when credit score < 500 and income < 3x rent")
    void shouldReturnHighRiskWithLowCreditAndLowIncome() {
        // Arrange
        User tenant = createTenant(400, BigDecimal.valueOf(5000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 2.5x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.HIGH, result);
    }

    @Test
    @DisplayName("Should return HIGH risk when credit score < 500 and income exactly 2x rent")
    void shouldReturnHighRiskWithLowCreditAndDoubleIncome() {
        // Arrange
        User tenant = createTenant(450, BigDecimal.valueOf(4000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is exactly 2x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.HIGH, result);
    }

    // Missing Financial Data Tests

    @Test
    @DisplayName("Should return HIGH risk when credit score is null")
    void shouldReturnHighRiskWhenCreditScoreNull() {
        // Arrange
        User tenant = createTenant(null, BigDecimal.valueOf(5000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000);

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.HIGH, result);
    }

    @Test
    @DisplayName("Should return HIGH risk when monthly income is null")
    void shouldReturnHighRiskWhenMonthlyIncomeNull() {
        // Arrange
        User tenant = createTenant(750, null);
        BigDecimal rentPrice = BigDecimal.valueOf(2000);

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.HIGH, result);
    }

    @Test
    @DisplayName("Should return HIGH risk when both credit score and income are null")
    void shouldReturnHighRiskWhenBothFieldsNull() {
        // Arrange
        User tenant = createTenant(null, null);
        BigDecimal rentPrice = BigDecimal.valueOf(2000);

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.HIGH, result);
    }

    // Boundary Tests

    @Test
    @DisplayName("Should return LOW risk with credit score exactly 700 and income 2x rent")
    void shouldReturnLowRiskAtCreditScoreBoundary700() {
        // Arrange
        User tenant = createTenant(700, BigDecimal.valueOf(4000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is exactly 2x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.LOW, result);
    }

    @Test
    @DisplayName("Should return MEDIUM risk with credit score exactly 500 and income 2.5x rent")
    void shouldReturnMediumRiskAtCreditScoreBoundary500() {
        // Arrange
        User tenant = createTenant(500, BigDecimal.valueOf(5000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 2.5x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.MEDIUM, result);
    }

    @Test
    @DisplayName("Should return HIGH risk with credit score 499 and income less than 2x rent")
    void shouldReturnHighRiskJustBelowCreditScoreBoundary500() {
        // Arrange
        User tenant = createTenant(499, BigDecimal.valueOf(3500));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 1.75x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.HIGH, result);
    }

    @Test
    @DisplayName("Should return MEDIUM risk with credit score 699 and income 3x rent")
    void shouldReturnMediumRiskJustBelowCreditScoreBoundary700() {
        // Arrange
        User tenant = createTenant(699, BigDecimal.valueOf(6000));
        BigDecimal rentPrice = BigDecimal.valueOf(2000); // Income is 3x rent

        // Act
        RiskLevel result = evaluateFinancialRiskUseCase.evaluate(tenant, rentPrice);

        // Assert
        assertEquals(RiskLevel.LOW, result);
    }

    /**
     * Helper method to create a tenant with specific financial data
     */
    private User createTenant(Integer creditScore, BigDecimal monthlyIncome) {
        return User.builder()
                .id(UUID.randomUUID())
                .email("tenant@test.com")
                .name("Test Tenant")
                .role(UserRole.TENANT)
                .creditScore(creditScore)
                .monthlyIncome(monthlyIncome)
                .build();
    }
}
