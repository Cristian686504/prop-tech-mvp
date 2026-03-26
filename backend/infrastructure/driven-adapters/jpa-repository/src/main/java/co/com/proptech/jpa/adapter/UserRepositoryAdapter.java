package co.com.proptech.jpa.adapter;

import co.com.proptech.jpa.entity.UserEntity;
import co.com.proptech.jpa.repository.UserJpaRepository;
import co.com.proptech.model.user.User;
import co.com.proptech.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    // Mappers: Entity <-> Domain
    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .phone(user.getPhone())
                .documentType(user.getDocumentType())
                .documentId(user.getDocumentId())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .phone(entity.getPhone())
                .documentType(entity.getDocumentType())
                .documentId(entity.getDocumentId())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
