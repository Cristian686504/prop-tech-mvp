package co.com.proptech.api.dto;

import co.com.proptech.model.user.enums.DocumentType;
import co.com.proptech.model.user.enums.UserRole;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class UserResponse {
    UUID id;
    String name;
    String email;
    String phone;
    DocumentType documentType;
    String documentId;
    UserRole role;
    LocalDateTime createdAt;
}
