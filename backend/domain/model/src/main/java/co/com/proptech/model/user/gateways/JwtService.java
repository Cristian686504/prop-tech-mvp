package co.com.proptech.model.user.gateways;

import java.util.UUID;

public interface JwtService {
    
    String generateToken(UUID userId, String email, String role);
    
    boolean validateToken(String token);
    
    UUID extractUserId(String token);
    
    String extractEmail(String token);
}
