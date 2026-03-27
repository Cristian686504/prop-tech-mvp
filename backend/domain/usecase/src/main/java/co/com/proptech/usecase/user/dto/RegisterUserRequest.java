package co.com.proptech.usecase.user.dto;

import co.com.proptech.model.user.enums.DocumentType;
import co.com.proptech.model.user.enums.UserRole;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RegisterUserRequest {
    String name;
    String email;
    String password;
    String phone;
    DocumentType documentType;
    String documentId;
    UserRole role;
}
