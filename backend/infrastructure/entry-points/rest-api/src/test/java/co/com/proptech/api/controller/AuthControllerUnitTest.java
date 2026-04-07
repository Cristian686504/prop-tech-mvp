package co.com.proptech.api.controller;

import co.com.proptech.api.dto.LoginRequestDto;
import co.com.proptech.api.dto.RegisterRequest;
import co.com.proptech.api.dto.UserResponse;
import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.DocumentType;
import co.com.proptech.model.user.enums.UserRole;
import co.com.proptech.usecase.user.GetUserByIdUseCase;
import co.com.proptech.usecase.user.LoginUserUseCase;
import co.com.proptech.usecase.user.RegisterUserUseCase;
import co.com.proptech.usecase.user.dto.AuthResponse;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - Unit Tests (getCurrentUser & logout)")
class AuthControllerUnitTest {

    @Mock private RegisterUserUseCase registerUserUseCase;
    @Mock private LoginUserUseCase loginUserUseCase;
    @Mock private GetUserByIdUseCase getUserByIdUseCase;

    @InjectMocks
    private AuthController controller;

    private final UUID userId = UUID.randomUUID();

    private User sampleUser() {
        return User.builder()
                .id(userId)
                .name("Ana Landlord")
                .email("ana@landlord.com")
                .phone("3009876543")
                .documentType(DocumentType.CC)
                .documentId("9876543210")
                .role(UserRole.LANDLORD)
                .monthlyIncome(new BigDecimal("8000000"))
                .creditScore(780)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ------------------------------------------------------------------
    // getCurrentUser
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getCurrentUser - should return 200 with UserResponse mapped from domain user")
    void shouldReturnCurrentUser() {
        when(getUserByIdUseCase.execute(userId)).thenReturn(sampleUser());

        ResponseEntity<UserResponse> response = controller.getCurrentUser(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        UserResponse body = response.getBody();
        assertThat(body.getId()).isEqualTo(userId);
        assertThat(body.getName()).isEqualTo("Ana Landlord");
        assertThat(body.getEmail()).isEqualTo("ana@landlord.com");
        assertThat(body.getPhone()).isEqualTo("3009876543");
        assertThat(body.getDocumentType()).isEqualTo(DocumentType.CC);
        assertThat(body.getDocumentId()).isEqualTo("9876543210");
        assertThat(body.getRole()).isEqualTo(UserRole.LANDLORD);
        assertThat(body.getMonthlyIncome()).isEqualByComparingTo(new BigDecimal("8000000"));
        assertThat(body.getCreditScore()).isEqualTo(780);
        assertThat(body.getCreatedAt()).isNotNull();

        verify(getUserByIdUseCase).execute(userId);
    }

    // ------------------------------------------------------------------
    // logout
    // ------------------------------------------------------------------

    @Test
    @DisplayName("logout - should return 200 and clear jwt cookie")
    void shouldClearJwtCookieOnLogout() {
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();

        ResponseEntity<Void> response = controller.logout(httpResponse);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Cookie jwtCookie = Arrays.stream(httpResponse.getCookies())
                .filter(c -> "jwt".equals(c.getName()))
                .findFirst()
                .orElse(null);

        assertThat(jwtCookie).isNotNull();
        assertThat(jwtCookie.getMaxAge()).isEqualTo(0);
        assertThat(jwtCookie.isHttpOnly()).isTrue();
        assertThat(jwtCookie.getPath()).isEqualTo("/");
    }

    // ------------------------------------------------------------------
    // register
    // ------------------------------------------------------------------

    @Test
    @DisplayName("register - should return 201 CREATED and set jwt cookie")
    void shouldRegisterAndReturn201() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Ana Landlord");
        request.setEmail("ana@landlord.com");
        request.setPassword("password123");
        request.setPhone("3009876543");
        request.setDocumentType(DocumentType.CC);
        request.setDocumentId("9876543210");
        request.setRole(UserRole.LANDLORD);

        AuthResponse authResponse = AuthResponse.builder()
                .token("test-jwt-token")
                .user(sampleUser())
                .build();

        when(registerUserUseCase.execute(any())).thenReturn(authResponse);

        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        ResponseEntity<UserResponse> response = controller.register(request, httpResponse);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(userId);
        assertThat(response.getBody().getName()).isEqualTo("Ana Landlord");

        Cookie jwtCookie = Arrays.stream(httpResponse.getCookies())
                .filter(c -> "jwt".equals(c.getName()))
                .findFirst()
                .orElse(null);
        assertThat(jwtCookie).isNotNull();
        assertThat(jwtCookie.getValue()).isEqualTo("test-jwt-token");
        assertThat(jwtCookie.isHttpOnly()).isTrue();

        verify(registerUserUseCase).execute(any());
    }

    // ------------------------------------------------------------------
    // login
    // ------------------------------------------------------------------

    @Test
    @DisplayName("login - should return 200 OK and set jwt cookie")
    void shouldLoginAndReturn200() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("ana@landlord.com");
        request.setPassword("password123");

        AuthResponse authResponse = AuthResponse.builder()
                .token("login-jwt-token")
                .user(sampleUser())
                .build();

        when(loginUserUseCase.execute(any())).thenReturn(authResponse);

        MockHttpServletResponse httpResponse = new MockHttpServletResponse();
        ResponseEntity<UserResponse> response = controller.login(request, httpResponse);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("ana@landlord.com");

        Cookie jwtCookie = Arrays.stream(httpResponse.getCookies())
                .filter(c -> "jwt".equals(c.getName()))
                .findFirst()
                .orElse(null);
        assertThat(jwtCookie).isNotNull();
        assertThat(jwtCookie.getValue()).isEqualTo("login-jwt-token");

        verify(loginUserUseCase).execute(any());
    }
}
