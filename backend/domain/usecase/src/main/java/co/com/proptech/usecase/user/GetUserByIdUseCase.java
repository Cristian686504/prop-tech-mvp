package co.com.proptech.usecase.user;

import co.com.proptech.model.user.User;
import co.com.proptech.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class GetUserByIdUseCase {

    private final UserRepository userRepository;

    public User execute(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
