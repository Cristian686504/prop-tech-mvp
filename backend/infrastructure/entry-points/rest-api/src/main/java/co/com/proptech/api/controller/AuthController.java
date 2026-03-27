package co.com.proptech.api.controller;

import co.com.proptech.api.dto.LoginRequestDto;
import co.com.proptech.api.dto.RegisterRequest;
import co.com.proptech.api.dto.UserResponse;
import co.com.proptech.model.user.User;
import co.com.proptech.usecase.user.GetUserByIdUseCase;
import co.com.proptech.usecase.user.LoginUserUseCase;
import co.com.proptech.usecase.user.RegisterUserUseCase;
import co.com.proptech.usecase.user.dto.AuthResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        
        AuthResponse authResponse = registerUserUseCase.execute(
                co.com.proptech.usecase.user.dto.RegisterUserRequest.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .phone(request.getPhone())
                        .documentType(request.getDocumentType())
                        .documentId(request.getDocumentId())
                        .role(request.getRole())
                        .build()
        );

        // Set JWT in httpOnly cookie
        setJwtCookie(response, authResponse.getToken());

        return ResponseEntity.ok(mapToUserResponse(authResponse.getUser()));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletResponse response) {
        
        AuthResponse authResponse = loginUserUseCase.execute(
                co.com.proptech.usecase.user.dto.LoginRequest.builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .build()
        );

        // Set JWT in httpOnly cookie
        setJwtCookie(response, authResponse.getToken());

        return ResponseEntity.ok(mapToUserResponse(authResponse.getUser()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestAttribute("userId") UUID userId) {
        User user = getUserByIdUseCase.execute(userId);
        return ResponseEntity.ok(mapToUserResponse(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Clear JWT cookie
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        
        return ResponseEntity.ok().build();
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
        response.addCookie(cookie);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .documentType(user.getDocumentType())
                .documentId(user.getDocumentId())
                .role(user.getRole())
                .monthlyIncome(user.getMonthlyIncome())
                .creditScore(user.getCreditScore())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
