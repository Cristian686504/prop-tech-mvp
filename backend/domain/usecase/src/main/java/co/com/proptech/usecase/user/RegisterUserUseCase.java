package co.com.proptech.usecase.user;

import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.UserRole;
import co.com.proptech.model.user.gateways.JwtService;
import co.com.proptech.model.user.gateways.PasswordEncoder;
import co.com.proptech.model.user.gateways.UserRepository;
import co.com.proptech.usecase.user.dto.AuthResponse;
import co.com.proptech.usecase.user.dto.RegisterUserRequest;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse execute(RegisterUserRequest request) {
        // Validate email not already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Create user
        User user = User.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .documentType(request.getDocumentType())
                .documentId(request.getDocumentId())
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .build();
        
        // Generate simulated financial data for TENANT
        if (request.getRole() == UserRole.TENANT) {
            Random random = new Random();
            // Monthly income: 2M - 8M COP
            int income = 2_000_000 + random.nextInt(6_000_000);
            user.setMonthlyIncome(BigDecimal.valueOf(income));
            
            // Credit score: 400-800
            int score = 400 + random.nextInt(401);
            user.setCreditScore(score);
        }

        User savedUser = userRepository.save(user);

        // Generate JWT
        String token = jwtService.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .user(savedUser)
                .build();
    }
}
