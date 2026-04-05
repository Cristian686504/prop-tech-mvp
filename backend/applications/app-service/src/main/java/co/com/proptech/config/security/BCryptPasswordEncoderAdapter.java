package co.com.proptech.config.security;

import co.com.proptech.model.user.gateways.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt;

    public BCryptPasswordEncoderAdapter(@Value("${security.bcrypt.strength:8}") int strength) {
        this.bcrypt = new BCryptPasswordEncoder(strength);
    }

    @Override
    public String encode(String rawPassword) {
        return bcrypt.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return bcrypt.matches(rawPassword, encodedPassword);
    }
}
