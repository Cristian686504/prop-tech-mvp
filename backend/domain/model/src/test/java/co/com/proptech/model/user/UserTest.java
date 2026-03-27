package co.com.proptech.model.user;

import co.com.proptech.model.user.enums.DocumentType;
import co.com.proptech.model.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Domain Model Tests")
class UserTest {

    @Test
    @DisplayName("Should create valid user with all fields")
    void shouldCreateValidUserWithAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        String email = "john.doe@test.com";
        String passwordHash = "$2a$10$hashedpassword";
        String phone = "+573001234567";
        DocumentType documentType = DocumentType.CC;
        String documentId = "1234567890";
        UserRole role = UserRole.TENANT;
        BigDecimal monthlyIncome = BigDecimal.valueOf(5000000);
        Integer creditScore = 750;
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        User user = User.builder()
                .id(id)
                .name(name)
                .email(email)
                .passwordHash(passwordHash)
                .phone(phone)
                .documentType(documentType)
                .documentId(documentId)
                .role(role)
                .monthlyIncome(monthlyIncome)
                .creditScore(creditScore)
                .createdAt(createdAt)
                .build();

        // Assert
        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(passwordHash, user.getPasswordHash());
        assertEquals(phone, user.getPhone());
        assertEquals(documentType, user.getDocumentType());
        assertEquals(documentId, user.getDocumentId());
        assertEquals(role, user.getRole());
        assertEquals(monthlyIncome, user.getMonthlyIncome());
        assertEquals(creditScore, user.getCreditScore());
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    @DisplayName("Should create tenant user with financial profile")
    void shouldCreateTenantWithFinancialProfile() {
        // Act
        User tenant = User.builder()
                .name("Tenant User")
                .email("tenant@test.com")
                .role(UserRole.TENANT)
                .monthlyIncome(BigDecimal.valueOf(5000000))
                .creditScore(750)
                .build();

        // Assert
        assertNotNull(tenant);
        assertEquals(UserRole.TENANT, tenant.getRole());
        assertEquals(BigDecimal.valueOf(5000000), tenant.getMonthlyIncome());
        assertEquals(750, tenant.getCreditScore());
    }

    @Test
    @DisplayName("Should create landlord user without financial profile")
    void shouldCreateLandlordWithoutFinancialProfile() {
        // Act
        User landlord = User.builder()
                .name("Landlord User")
                .email("landlord@test.com")
                .role(UserRole.LANDLORD)
                .build();

        // Assert
        assertNotNull(landlord);
        assertEquals(UserRole.LANDLORD, landlord.getRole());
        assertNull(landlord.getMonthlyIncome());
        assertNull(landlord.getCreditScore());
    }

    @Test
    @DisplayName("Should create user with CC document type")
    void shouldCreateUserWithCCDocumentType() {
        // Act
        User user = User.builder()
                .name("User with CC")
                .email("user@test.com")
                .documentType(DocumentType.CC)
                .documentId("1234567890")
                .role(UserRole.TENANT)
                .build();

        // Assert
        assertEquals(DocumentType.CC, user.getDocumentType());
        assertEquals("1234567890", user.getDocumentId());
    }

    @Test
    @DisplayName("Should create user with CE document type")
    void shouldCreateUserWithCEDocumentType() {
        // Act
        User user = User.builder()
                .name("User with CE")
                .email("user@test.com")
                .documentType(DocumentType.CE)
                .documentId("9876543210")
                .role(UserRole.TENANT)
                .build();

        // Assert
        assertEquals(DocumentType.CE, user.getDocumentType());
        assertEquals("9876543210", user.getDocumentId());
    }

    @Test
    @DisplayName("Should create user with PP document type")
    void shouldCreateUserWithPPDocumentType() {
        // Act
        User user = User.builder()
                .name("User with Passport")
                .email("user@test.com")
                .documentType(DocumentType.PP)
                .documentId("ABC123456")
                .role(UserRole.TENANT)
                .build();

        // Assert
        assertEquals(DocumentType.PP, user.getDocumentType());
        assertEquals("ABC123456", user.getDocumentId());
    }

    @Test
    @DisplayName("Should allow null optional fields")
    void shouldAllowNullOptionalFields() {
        // Act
        User user = User.builder()
                .name("Minimal User")
                .email("minimal@test.com")
                .role(UserRole.TENANT)
                .build();

        // Assert
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getPasswordHash());
        assertNull(user.getPhone());
        assertNull(user.getDocumentType());
        assertNull(user.getDocumentId());
        assertNull(user.getMonthlyIncome());
        assertNull(user.getCreditScore());
        assertNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void shouldUseBuilderPattern() {
        // Act
        User user = User.builder()
                .name("Test User")
                .email("test@test.com")
                .role(UserRole.TENANT)
                .build();

        // Assert
        assertNotNull(user);
        assertEquals("Test User", user.getName());
        assertEquals("test@test.com", user.getEmail());
        assertEquals(UserRole.TENANT, user.getRole());
    }

    @Test
    @DisplayName("Should use toBuilder for modifications")
    void shouldUseToBuilderForModifications() {
        // Arrange
        User originalUser = User.builder()
                .name("Original Name")
                .email("original@test.com")
                .role(UserRole.TENANT)
                .creditScore(650)
                .build();

        // Act
        User modifiedUser = originalUser.toBuilder()
                .creditScore(750)
                .monthlyIncome(BigDecimal.valueOf(6000000))
                .build();

        // Assert
        assertEquals("Original Name", modifiedUser.getName());
        assertEquals("original@test.com", modifiedUser.getEmail());
        assertEquals(750, modifiedUser.getCreditScore());
        assertEquals(BigDecimal.valueOf(6000000), modifiedUser.getMonthlyIncome());
    }

    @Test
    @DisplayName("Should support both UserRole enum values")
    void shouldSupportBothUserRoleValues() {
        // Act
        User tenant = User.builder()
                .email("tenant@test.com")
                .role(UserRole.TENANT)
                .build();

        User landlord = User.builder()
                .email("landlord@test.com")
                .role(UserRole.LANDLORD)
                .build();

        // Assert
        assertEquals(UserRole.TENANT, tenant.getRole());
        assertEquals(UserRole.LANDLORD, landlord.getRole());
    }

    @Test
    @DisplayName("Should allow setters to modify fields")
    void shouldAllowSettersToModifyFields() {
        // Arrange
        User user = User.builder()
                .name("Initial Name")
                .email("initial@test.com")
                .role(UserRole.TENANT)
                .build();

        // Act
        user.setName("Updated Name");
        user.setEmail("updated@test.com");
        user.setCreditScore(800);

        // Assert
        assertEquals("Updated Name", user.getName());
        assertEquals("updated@test.com", user.getEmail());
        assertEquals(800, user.getCreditScore());
    }

    @Test
    @DisplayName("Should handle high credit score")
    void shouldHandleHighCreditScore() {
        // Act
        User user = User.builder()
                .email("user@test.com")
                .role(UserRole.TENANT)
                .creditScore(850)
                .build();

        // Assert
        assertEquals(850, user.getCreditScore());
    }

    @Test
    @DisplayName("Should handle low credit score")
    void shouldHandleLowCreditScore() {
        // Act
        User user = User.builder()
                .email("user@test.com")
                .role(UserRole.TENANT)
                .creditScore(300)
                .build();

        // Assert
        assertEquals(300, user.getCreditScore());
    }

    @Test
    @DisplayName("Should handle large monthly income")
    void shouldHandleLargeMonthlyIncome() {
        // Act
        User user = User.builder()
                .email("user@test.com")
                .role(UserRole.TENANT)
                .monthlyIncome(BigDecimal.valueOf(50000000))
                .build();

        // Assert
        assertEquals(BigDecimal.valueOf(50000000), user.getMonthlyIncome());
    }

    @Test
    @DisplayName("Should handle small monthly income")
    void shouldHandleSmallMonthlyIncome() {
        // Act
        User user = User.builder()
                .email("user@test.com")
                .role(UserRole.TENANT)
                .monthlyIncome(BigDecimal.valueOf(1000000))
                .build();

        // Assert
        assertEquals(BigDecimal.valueOf(1000000), user.getMonthlyIncome());
    }

    @Test
    @DisplayName("Should create no-args constructor user")
    void shouldCreateNoArgsConstructorUser() {
        // Act
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setRole(UserRole.TENANT);

        // Assert
        assertNotNull(user);
        assertEquals("Test User", user.getName());
        assertEquals("test@test.com", user.getEmail());
        assertEquals(UserRole.TENANT, user.getRole());
    }
}
