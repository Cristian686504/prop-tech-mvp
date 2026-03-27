package co.com.proptech.usecase.user.dto;

import co.com.proptech.model.user.User;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {
    String token;
    User user;
}
