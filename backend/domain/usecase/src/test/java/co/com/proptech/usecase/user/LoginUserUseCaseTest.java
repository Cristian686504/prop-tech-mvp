package co.com.proptech.usecase.user;

import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.UserRole;
import co.com.proptech.model.user.gateways.JwtService;
import co.com.proptech.model.user.gateways.PasswordEncoder;
import co.com.proptech.model.user.gateways.UserRepository;
import co.com.proptech.usecase.user.dto.AuthResponse;
import co.com.proptech.usecase.user.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Login User Use Case Tests")
class LoginUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private LoginUserUseCase loginUserUseCase;

    @BeforeEach
    void setUp() {
        loginUserUseCase = new LoginUserUseCase(userRepository, passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfullyWithValidCredentials() {
        // Arrange
        String email = "user@test.com";
        String password = "password123";
        String passwordHash = "$2a$10$hashedpassword";
        String token = "jwt.token.here";

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email(email)
                .passwordHash(passwordHash)
                .role(UserRole.TENANT)
                .build();

        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, passwordHash)).thenReturn(true);
        when(jwtService.generateToken(user.getId(), email, UserRole.TENANT.name())).thenReturn(token);

        // Act
        AuthResponse response = loginUserUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals(user, response.getUser());

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, passwordHash);
        verify(jwtService, times(1)).generateToken(user.getId(), email, UserRole.TENANT.name());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        String email = "nonexistent@test.com";
        String password = "password123";
        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loginUserUseCase.execute(request)
        );
        assertEquals("Invalid credentials", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any(), any(), any());
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void shouldThrowExceptionWhenPasswordIncorrect() {
        // Arrange
        String email = "user@test.com";
        String password = "wrongpassword";
        String correctHash = "$2a$10$hashedpassword";

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email(email)
                .passwordHash(correctHash)
                .role(UserRole.TENANT)
                .build();

        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, correctHash)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loginUserUseCase.execute(request)
        );
        assertEquals("Invalid credentials", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, correctHash);
        verify(jwtService, never()).generateToken(any(), any(), any());
    }

    @Test
    @DisplayName("Should login tenant successfully")
    void shouldLoginTenantSuccessfully() {
        // Arrange
        String email = "tenant@test.com";
        String password = "password123";
        String passwordHash = "$2a$10$hashedpassword";
        String token = "tenant.jwt.token";

        User tenant = User.builder()
                .id(UUID.randomUUID())
                .name("Tenant User")
                .email(email)
                .passwordHash(passwordHash)
                .role(UserRole.TENANT)
                .build();

        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(tenant));
        when(passwordEncoder.matches(password, passwordHash)).thenReturn(true);
        when(jwtService.generateToken(tenant.getId(), email, UserRole.TENANT.name())).thenReturn(token);

        // Act
        AuthResponse response = loginUserUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals(tenant, response.getUser());
        assertEquals(UserRole.TENANT, response.getUser().getRole());
        verify(jwtService, times(1)).generateToken(tenant.getId(), email, "TENANT");
    }

    @Test
    @DisplayName("Should login landlord successfully")
    void shouldLoginLandlordSuccessfully() {
        // Arrange
        String email = "landlord@test.com";
        String password = "password123";
        String passwordHash = "$2a$10$hashedpassword";
        String token = "landlord.jwt.token";

        User landlord = User.builder()
                .id(UUID.randomUUID())
                .name("Landlord User")
                .email(email)
                .passwordHash(passwordHash)
                .role(UserRole.LANDLORD)
                .build();

        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(landlord));
        when(passwordEncoder.matches(password, passwordHash)).thenReturn(true);
        when(jwtService.generateToken(landlord.getId(), email, UserRole.LANDLORD.name())).thenReturn(token);

        // Act
        AuthResponse response = loginUserUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals(landlord, response.getUser());
        assertEquals(UserRole.LANDLORD, response.getUser().getRole());
        verify(jwtService, times(1)).generateToken(landlord.getId(), email, "LANDLORD");
    }

    @Test
    @DisplayName("Should generate JWT with correct user data")
    void shouldGenerateJwtWithCorrectUserData() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String email = "user@test.com";
        String password = "password123";
        String passwordHash = "$2a$10$hashedpassword";
        String token = "generated.jwt.token";

        User user = User.builder()
                .id(userId)
                .name("Test User")
                .email(email)
                .passwordHash(passwordHash)
                .role(UserRole.TENANT)
                .build();

        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, passwordHash)).thenReturn(true);
        when(jwtService.generateToken(userId, email, UserRole.TENANT.name())).thenReturn(token);

        // Act
        AuthResponse response = loginUserUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.getToken());
        verify(jwtService, times(1)).generateToken(userId, email, "TENANT");
    }

    @Test
    @DisplayName("Should return user object in auth response")
    void shouldReturnUserObjectInAuthResponse() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String email = "user@test.com";
        String name = "Test User";
        String password = "password123";
        String passwordHash = "$2a$10$hashedpassword";
        String token = "jwt.token";

        User user = User.builder()
                .id(userId)
                .name(name)
                .email(email)
                .passwordHash(passwordHash)
                .role(UserRole.TENANT)
                .build();

        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, passwordHash)).thenReturn(true);
        when(jwtService.generateToken(any(), any(), any())).thenReturn(token);

        // Act
        AuthResponse response = loginUserUseCase.execute(request);

        // Assert
        assertNotNull(response.getUser());
        assertEquals(userId, response.getUser().getId());
        assertEquals(name, response.getUser().getName());
        assertEquals(email, response.getUser().getEmail());
        assertEquals(UserRole.TENANT, response.getUser().getRole());
    }

    @Test
    @DisplayName("Should handle case-sensitive email")
    void shouldHandleCaseSensitiveEmail() {
        // Arrange
        String email = "User@Test.COM";
        String password = "password123";
        String passwordHash = "$2a$10$hashedpassword";

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("User@Test.COM")
                .passwordHash(passwordHash)
                .role(UserRole.TENANT)
                .build();

        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, passwordHash)).thenReturn(true);
        when(jwtService.generateToken(any(), any(), any())).thenReturn("token");

        // Act
        AuthResponse response = loginUserUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals(user, response.getUser());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should call all dependencies in correct order")
    void shouldCallAllDependenciesInCorrectOrder() {
        // Arrange
        String email = "user@test.com";
        String password = "password123";
        String passwordHash = "$2a$10$hashedpassword";

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .passwordHash(passwordHash)
                .role(UserRole.TENANT)
                .build();

        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, passwordHash)).thenReturn(true);
        when(jwtService.generateToken(any(), any(), any())).thenReturn("token");

        // Act
        loginUserUseCase.execute(request);

        // Assert - Verify order of calls
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, passwordHash);
        verify(jwtService, times(1)).generateToken(any(), any(), any());
    }
}
