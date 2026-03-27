package co.com.proptech.model.user;

import co.com.proptech.model.user.enums.DocumentType;
import co.com.proptech.model.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private UUID id;
    private String name;
    private String email;
    private String passwordHash;
    private String phone;
    private DocumentType documentType;
    private String documentId;
    private UserRole role;
    
    // Financial profile (only for TENANT) - Simulated data
    private BigDecimal monthlyIncome;  // Monthly income in COP
    private Integer creditScore;       // Credit score (300-850)
    
    private LocalDateTime createdAt;
}
