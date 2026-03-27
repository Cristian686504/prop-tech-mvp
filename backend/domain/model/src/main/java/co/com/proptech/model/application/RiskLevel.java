package co.com.proptech.model.application;

/**
 * Risk level for rental application evaluation
 * Based on credit score and income vs rent ratio
 */
public enum RiskLevel {
    LOW,      // Bajo riesgo - Good credit and sufficient income
    MEDIUM,   // Medio riesgo - Acceptable credit or income
    HIGH      // Alto riesgo - Poor credit or insufficient income
}
