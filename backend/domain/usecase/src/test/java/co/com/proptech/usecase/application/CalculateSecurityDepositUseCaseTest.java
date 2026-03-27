package co.com.proptech.usecase.application;

import co.com.proptech.model.application.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CalculateSecurityDepositUseCase
 * Tests the business rules for HU008
 */
class CalculateSecurityDepositUseCaseTest {

    private CalculateSecurityDepositUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CalculateSecurityDepositUseCase();
    }

    @Test
    void shouldCalculateOneMonthForLowRisk() {
        // Given
        RiskLevel riskLevel = RiskLevel.LOW;
        BigDecimal rentPrice = new BigDecimal("1000000"); // 1M COP

        // When
        BigDecimal securityDeposit = useCase.calculate(riskLevel, rentPrice);

        // Then
        assertEquals(new BigDecimal("1000000"), securityDeposit);
    }

    @Test
    void shouldCalculateTwoMonthsForMediumRisk() {
        // Given
        RiskLevel riskLevel = RiskLevel.MEDIUM;
        BigDecimal rentPrice = new BigDecimal("1000000"); // 1M COP

        // When
        BigDecimal securityDeposit = useCase.calculate(riskLevel, rentPrice);

        // Then
        assertEquals(new BigDecimal("2000000"), securityDeposit);
    }

    @Test
    void shouldCalculateThreeMonthsForHighRisk() {
        // Given
        RiskLevel riskLevel = RiskLevel.HIGH;
        BigDecimal rentPrice = new BigDecimal("1000000"); // 1M COP

        // When
        BigDecimal securityDeposit = useCase.calculate(riskLevel, rentPrice);

        // Then
        assertEquals(new BigDecimal("3000000"), securityDeposit);
    }

    @Test
    void shouldHandleDecimalRentPrices() {
        // Given
        RiskLevel riskLevel = RiskLevel.MEDIUM;
        BigDecimal rentPrice = new BigDecimal("1500000.50"); // 1.5M COP

        // When
        BigDecimal securityDeposit = useCase.calculate(riskLevel, rentPrice);

        // Then
        assertEquals(new BigDecimal("3000001.00"), securityDeposit);
    }

    @Test
    void shouldThrowExceptionWhenRiskLevelIsNull() {
        // Given
        BigDecimal rentPrice = new BigDecimal("1000000");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.calculate(null, rentPrice)
        );
        
        assertEquals("Risk level cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRentPriceIsNull() {
        // Given
        RiskLevel riskLevel = RiskLevel.LOW;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.calculate(riskLevel, null)
        );
        
        assertEquals("Rent price must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRentPriceIsZero() {
        // Given
        RiskLevel riskLevel = RiskLevel.LOW;
        BigDecimal rentPrice = BigDecimal.ZERO;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.calculate(riskLevel, rentPrice)
        );
        
        assertEquals("Rent price must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRentPriceIsNegative() {
        // Given
        RiskLevel riskLevel = RiskLevel.LOW;
        BigDecimal rentPrice = new BigDecimal("-1000");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> useCase.calculate(riskLevel, rentPrice)
        );
        
        assertEquals("Rent price must be greater than zero", exception.getMessage());
    }
}
