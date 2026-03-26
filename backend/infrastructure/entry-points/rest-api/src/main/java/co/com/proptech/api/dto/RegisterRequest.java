package co.com.proptech.api.dto;

import co.com.proptech.model.user.enums.DocumentType;
import co.com.proptech.model.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @Size(max = 20)
    private String phone;
    
    @NotNull(message = "Document type is required")
    private DocumentType documentType;
    
    @NotBlank(message = "Document ID is required")
    @Size(max = 50)
    private String documentId;
    
    @NotNull(message = "Role is required")
    private UserRole role;
}
