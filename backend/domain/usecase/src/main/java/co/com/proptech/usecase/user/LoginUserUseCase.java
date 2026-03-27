package co.com.proptech.usecase.user;

import co.com.proptech.model.user.User;
import co.com.proptech.model.user.gateways.JwtService;
import co.com.proptech.model.user.gateways.PasswordEncoder;
import co.com.proptech.model.user.gateways.UserRepository;
import co.com.proptech.usecase.user.dto.AuthResponse;
import co.com.proptech.usecase.user.dto.LoginRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse execute(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Generate JWT
        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .user(user)
                .build();
    }
}
