package co.com.proptech.jpa.adapter;

import co.com.proptech.jpa.entity.UserEntity;
import co.com.proptech.jpa.repository.UserJpaRepository;
import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.DocumentType;
import co.com.proptech.model.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepositoryAdapter Unit Tests")
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository jpaRepository;

    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserRepositoryAdapter(jpaRepository);
    }

    private UserEntity sampleEntity() {
        return UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Juan Perez")
                .email("juan@email.com")
                .passwordHash("$2a$hash")
                .phone("3101234567")
                .documentType(DocumentType.CC)
                .documentId("1234567890")
                .role(UserRole.TENANT)
                .monthlyIncome(new BigDecimal("5000000"))
                .creditScore(700)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private User sampleDomain() {
        return User.builder()
                .name("Juan Perez")
                .email("juan@email.com")
                .passwordHash("$2a$hash")
                .phone("3101234567")
                .documentType(DocumentType.CC)
                .documentId("1234567890")
                .role(UserRole.TENANT)
                .monthlyIncome(new BigDecimal("5000000"))
                .creditScore(700)
                .build();
    }

    @Test
    @DisplayName("save - should persist entity and return mapped domain user")
    void shouldSaveUserAndReturnDomain() {
        UserEntity saved = sampleEntity();
        when(jpaRepository.save(any(UserEntity.class))).thenReturn(saved);

        User result = adapter.save(sampleDomain());

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Juan Perez", result.getName());
        assertEquals("juan@email.com", result.getEmail());
        assertEquals(UserRole.TENANT, result.getRole());
        verify(jpaRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("findById - should return mapped domain user when found")
    void shouldReturnDomainUserWhenFoundById() {
        UserEntity entity = sampleEntity();
        when(jpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findById(entity.getId());

        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
    }

    @Test
    @DisplayName("findById - should return empty when not found")
    void shouldReturnEmptyWhenNotFoundById() {
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<User> result = adapter.findById(id);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByEmail - should return mapped domain user when found")
    void shouldReturnDomainUserWhenFoundByEmail() {
        UserEntity entity = sampleEntity();
        when(jpaRepository.findByEmail("juan@email.com")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByEmail("juan@email.com");

        assertTrue(result.isPresent());
        assertEquals("juan@email.com", result.get().getEmail());
    }

    @Test
    @DisplayName("findByEmail - should return empty when not found")
    void shouldReturnEmptyWhenEmailNotFound() {
        when(jpaRepository.findByEmail("nobody@email.com")).thenReturn(Optional.empty());

        Optional<User> result = adapter.findByEmail("nobody@email.com");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("existsByEmail - should delegate to JPA repository")
    void shouldDelegateExistsByEmailToJpa() {
        when(jpaRepository.existsByEmail("juan@email.com")).thenReturn(true);
        when(jpaRepository.existsByEmail("nobody@email.com")).thenReturn(false);

        assertTrue(adapter.existsByEmail("juan@email.com"));
        assertFalse(adapter.existsByEmail("nobody@email.com"));
    }

    @Test
    @DisplayName("save - should map all user fields correctly to entity")
    void shouldMapAllFieldsToEntity() {
        UserEntity saved = sampleEntity();
        when(jpaRepository.save(any(UserEntity.class))).thenReturn(saved);

        User user = sampleDomain();
        User result = adapter.save(user);

        assertEquals(saved.getPhone(), result.getPhone());
        assertEquals(saved.getDocumentType(), result.getDocumentType());
        assertEquals(saved.getDocumentId(), result.getDocumentId());
        assertEquals(saved.getMonthlyIncome(), result.getMonthlyIncome());
        assertEquals(saved.getCreditScore(), result.getCreditScore());
        assertEquals(saved.getCreatedAt(), result.getCreatedAt());
    }
}
