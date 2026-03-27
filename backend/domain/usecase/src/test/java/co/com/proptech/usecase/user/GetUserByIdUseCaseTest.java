package co.com.proptech.usecase.user;

import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.UserRole;
import co.com.proptech.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Get User By Id Use Case Tests")
class GetUserByIdUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private GetUserByIdUseCase getUserByIdUseCase;

    @BeforeEach
    void setUp() {
        getUserByIdUseCase = new GetUserByIdUseCase(userRepository);
    }

    @Test
    @DisplayName("Should return user when user exists")
    void shouldReturnUserWhenUserExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User expectedUser = User.builder()
                .id(userId)
                .name("Test User")
                .email("test@test.com")
                .role(UserRole.TENANT)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = getUserByIdUseCase.execute(userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedUser, result);
        assertEquals(userId, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@test.com", result.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getUserByIdUseCase.execute(userId)
        );
        assertEquals("User not found", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should return tenant user with all fields")
    void shouldReturnTenantUserWithAllFields() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User tenant = User.builder()
                .id(userId)
                .name("Tenant User")
                .email("tenant@test.com")
                .phone("+573001234567")
                .role(UserRole.TENANT)
                .monthlyIncome(BigDecimal.valueOf(5000000))
                .creditScore(750)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(tenant));

        // Act
        User result = getUserByIdUseCase.execute(userId);

        // Assert
        assertNotNull(result);
        assertEquals(UserRole.TENANT, result.getRole());
        assertEquals(BigDecimal.valueOf(5000000), result.getMonthlyIncome());
        assertEquals(750, result.getCreditScore());
        assertEquals("+573001234567", result.getPhone());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should return landlord user")
    void shouldReturnLandlordUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User landlord = User.builder()
                .id(userId)
                .name("Landlord User")
                .email("landlord@test.com")
                .role(UserRole.LANDLORD)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(landlord));

        // Act
        User result = getUserByIdUseCase.execute(userId);

        // Assert
        assertNotNull(result);
        assertEquals(UserRole.LANDLORD, result.getRole());
        assertNull(result.getMonthlyIncome());
        assertNull(result.getCreditScore());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should call repository with correct userId")
    void shouldCallRepositoryWithCorrectUserId() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .name("User")
                .email("user@test.com")
                .role(UserRole.TENANT)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        getUserByIdUseCase.execute(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should handle different user IDs")
    void shouldHandleDifferentUserIds() {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        User user1 = User.builder().id(userId1).name("User 1").email("user1@test.com").role(UserRole.TENANT).build();
        User user2 = User.builder().id(userId2).name("User 2").email("user2@test.com").role(UserRole.LANDLORD).build();

        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));

        // Act
        User result1 = getUserByIdUseCase.execute(userId1);
        User result2 = getUserByIdUseCase.execute(userId2);

        // Assert
        assertEquals(userId1, result1.getId());
        assertEquals("User 1", result1.getName());
        assertEquals(userId2, result2.getId());
        assertEquals("User 2", result2.getName());

        verify(userRepository, times(1)).findById(userId1);
        verify(userRepository, times(1)).findById(userId2);
    }

    @Test
    @DisplayName("Should preserve all user fields in response")
    void shouldPreserveAllUserFieldsInResponse() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .name("Complete User")
                .email("complete@test.com")
                .passwordHash("$2a$10$hash")
                .phone("+573009876543")
                .role(UserRole.TENANT)
                .monthlyIncome(BigDecimal.valueOf(8000000))
                .creditScore(800)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = getUserByIdUseCase.execute(userId);

        // Assert
        assertEquals(userId, result.getId());
        assertEquals("Complete User", result.getName());
        assertEquals("complete@test.com", result.getEmail());
        assertEquals("$2a$10$hash", result.getPasswordHash());
        assertEquals("+573009876543", result.getPhone());
        assertEquals(UserRole.TENANT, result.getRole());
        assertEquals(BigDecimal.valueOf(8000000), result.getMonthlyIncome());
        assertEquals(800, result.getCreditScore());
    }

    @Test
    @DisplayName("Should call repository only once per request")
    void shouldCallRepositoryOnlyOnce() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .name("User")
                .email("user@test.com")
                .role(UserRole.TENANT)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        getUserByIdUseCase.execute(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
    }
}
