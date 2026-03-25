package co.com.proptech.usecase.user;

import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.DocumentType;
import co.com.proptech.model.user.enums.UserRole;
import co.com.proptech.model.user.gateways.JwtService;
import co.com.proptech.model.user.gateways.PasswordEncoder;
import co.com.proptech.model.user.gateways.UserRepository;
import co.com.proptech.usecase.user.dto.AuthResponse;
import co.com.proptech.usecase.user.dto.RegisterUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserUseCase Tests")
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private RegisterUserUseCase registerUserUseCase;

    @BeforeEach
    void setUp() {
        registerUserUseCase = new RegisterUserUseCase(userRepository, passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("Should register user successfully with valid data")
    void shouldRegisterUserSuccessfully() {
        // Given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Juan Perez")
                .email("juan.perez@example.com")
                .password("Password123!")
                .phone("+573001234567")
                .documentType(DocumentType.CC)
                .documentId("1234567890")
                .role(UserRole.LANDLORD)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$hashedPassword");
        when(jwtService.generateToken(any(UUID.class), anyString(), anyString()))
                .thenReturn("jwt.token.here");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AuthResponse response = registerUserUseCase.execute(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("jwt.token.here", response.getToken());
        assertNotNull(response.getUser());
        assertEquals(request.getName(), response.getUser().getName());
        assertEquals(request.getEmail(), response.getUser().getEmail());
        assertEquals(request.getPhone(), response.getUser().getPhone());
        assertEquals(request.getDocumentType(), response.getUser().getDocumentType());
        assertEquals(request.getDocumentId(), response.getUser().getDocumentId());
        assertEquals(request.getRole(), response.getUser().getRole());
        assertNotNull(response.getUser().getId());
        assertNotNull(response.getUser().getCreatedAt());

        // Verify interactions
        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(UUID.class), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Juan Perez")
                .email("existing@example.com")
                .password("Password123!")
                .phone("+573001234567")
                .documentType(DocumentType.CC)
                .documentId("1234567890")
                .role(UserRole.LANDLORD)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> registerUserUseCase.execute(request));

        assertEquals("Email already registered", exception.getMessage());

        // Verify that no user was saved
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(jwtService, never()).generateToken(any(UUID.class), anyString(), anyString());
    }

    @Test
    @DisplayName("Should hash password before saving user")
    void shouldHashPasswordBeforeSaving() {
        // Given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Juan Perez")
                .email("juan.perez@example.com")
                .password("PlainTextPassword")
                .phone("+573001234567")
                .documentType(DocumentType.CC)
                .documentId("1234567890")
                .role(UserRole.LANDLORD)
                .build();

        String hashedPassword = "$2a$10$verySecureHashedPassword";

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("PlainTextPassword")).thenReturn(hashedPassword);
        when(jwtService.generateToken(any(UUID.class), anyString(), anyString()))
                .thenReturn("jwt.token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        registerUserUseCase.execute(request);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(hashedPassword, savedUser.getPasswordHash());
        verify(passwordEncoder).encode("PlainTextPassword");
    }

    @Test
    @DisplayName("Should generate JWT token with user details")
    void shouldGenerateJwtTokenWithUserDetails() {
        // Given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Juan Perez")
                .email("juan.perez@example.com")
                .password("Password123!")
                .phone("+573001234567")
                .documentType(DocumentType.CC)
                .documentId("1234567890")
                .role(UserRole.TENANT)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(jwtService.generateToken(any(UUID.class), anyString(), anyString()))
                .thenReturn("generated.jwt.token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AuthResponse response = registerUserUseCase.execute(request);

        // Then
        assertEquals("generated.jwt.token", response.getToken());
        verify(jwtService).generateToken(any(UUID.class), anyString(), anyString());
    }

    @Test
    @DisplayName("Should generate unique UUID for new user")
    void shouldGenerateUniqueUuidForNewUser() {
        // Given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Juan Perez")
                .email("juan.perez@example.com")
                .password("Password123!")
                .phone("+573001234567")
                .documentType(DocumentType.CC)
                .documentId("1234567890")
                .role(UserRole.LANDLORD)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(jwtService.generateToken(any(UUID.class), anyString(), anyString()))
                .thenReturn("jwt.token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AuthResponse response = registerUserUseCase.execute(request);

        // Then
        assertNotNull(response.getUser().getId());
        assertInstanceOf(UUID.class, response.getUser().getId());
        
        // Verify the saved user has an ID
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getId());
    }

    @Test
    @DisplayName("Should register TENANT user successfully")
    void shouldRegisterTenantUserSuccessfully() {
        // Given
        RegisterUserRequest request = RegisterUserRequest.builder()
                .name("Maria Garcia")
                .email("maria.garcia@example.com")
                .password("SecurePass456!")
                .phone("+573009876543")
                .documentType(DocumentType.CE)
                .documentId("CE9876543")
                .role(UserRole.TENANT)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("$2a$10$hashedPassword");
        when(jwtService.generateToken(any(UUID.class), anyString(), anyString()))
                .thenReturn("tenant.jwt.token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AuthResponse response = registerUserUseCase.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(UserRole.TENANT, response.getUser().getRole());
        assertEquals("Maria Garcia", response.getUser().getName());
        assertEquals("maria.garcia@example.com", response.getUser().getEmail());
        assertEquals(DocumentType.CE, response.getUser().getDocumentType());
    }
}
