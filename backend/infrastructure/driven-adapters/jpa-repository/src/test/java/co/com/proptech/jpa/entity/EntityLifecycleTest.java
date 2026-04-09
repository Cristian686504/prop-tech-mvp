package co.com.proptech.jpa.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Entity Lifecycle Callbacks")
class EntityLifecycleTest {

    // ------------------------------------------------------------------
    // UserEntity
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("UserEntity @PrePersist")
    class UserEntityPrePersist {

        @Test
        @DisplayName("should generate id when null")
        void shouldGenerateIdWhenNull() {
            UserEntity entity = new UserEntity();
            assertNull(entity.getId());

            entity.onCreate();

            assertNotNull(entity.getId());
        }

        @Test
        @DisplayName("should set createdAt when null")
        void shouldSetCreatedAtWhenNull() {
            UserEntity entity = new UserEntity();
            assertNull(entity.getCreatedAt());

            entity.onCreate();

            assertNotNull(entity.getCreatedAt());
        }

        @Test
        @DisplayName("should NOT overwrite id when already set")
        void shouldNotOverwriteExistingId() {
            UUID existingId = UUID.randomUUID();
            UserEntity entity = UserEntity.builder().id(existingId).build();

            entity.onCreate();

            assertEquals(existingId, entity.getId());
        }

        @Test
        @DisplayName("should NOT overwrite createdAt when already set")
        void shouldNotOverwriteExistingCreatedAt() {
            LocalDateTime existingTime = LocalDateTime.of(2024, 1, 15, 10, 0);
            UserEntity entity = UserEntity.builder().createdAt(existingTime).build();

            entity.onCreate();

            assertEquals(existingTime, entity.getCreatedAt());
        }
    }

    // ------------------------------------------------------------------
    // PropertyEntity
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("PropertyEntity @PrePersist and @PreUpdate")
    class PropertyEntityLifecycle {

        @Test
        @DisplayName("@PrePersist should generate id when null")
        void shouldGenerateIdWhenNull() {
            PropertyEntity entity = new PropertyEntity();
            assertNull(entity.getId());

            entity.onCreate();

            assertNotNull(entity.getId());
        }

        @Test
        @DisplayName("@PrePersist should set createdAt when null")
        void shouldSetCreatedAtWhenNull() {
            PropertyEntity entity = new PropertyEntity();

            entity.onCreate();

            assertNotNull(entity.getCreatedAt());
        }

        @Test
        @DisplayName("@PrePersist should set updatedAt when null")
        void shouldSetUpdatedAtWhenNull() {
            PropertyEntity entity = new PropertyEntity();

            entity.onCreate();

            assertNotNull(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("@PrePersist should NOT overwrite existing id")
        void shouldNotOverwriteExistingId() {
            UUID existingId = UUID.randomUUID();
            PropertyEntity entity = PropertyEntity.builder().id(existingId).build();

            entity.onCreate();

            assertEquals(existingId, entity.getId());
        }

        @Test
        @DisplayName("@PrePersist should NOT overwrite existing createdAt")
        void shouldNotOverwriteExistingCreatedAt() {
            LocalDateTime existingTime = LocalDateTime.of(2024, 3, 20, 8, 0);
            PropertyEntity entity = PropertyEntity.builder().createdAt(existingTime).build();

            entity.onCreate();

            assertEquals(existingTime, entity.getCreatedAt());
        }

        @Test
        @DisplayName("@PreUpdate should always overwrite updatedAt")
        void shouldAlwaysOverwriteUpdatedAt() {
            LocalDateTime oldTime = LocalDateTime.of(2020, 1, 1, 0, 0);
            PropertyEntity entity = PropertyEntity.builder().updatedAt(oldTime).build();

            entity.onUpdate();

            assertNotNull(entity.getUpdatedAt());
            assertTrue(entity.getUpdatedAt().isAfter(oldTime));
        }
    }

    // ------------------------------------------------------------------
    // ApplicationEntity
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("ApplicationEntity @PrePersist")
    class ApplicationEntityPrePersist {

        @Test
        @DisplayName("should set appliedAt when null")
        void shouldSetAppliedAtWhenNull() {
            ApplicationEntity entity = new ApplicationEntity();
            assertNull(entity.getAppliedAt());

            entity.onCreate();

            assertNotNull(entity.getAppliedAt());
        }

        @Test
        @DisplayName("should NOT overwrite appliedAt when already set")
        void shouldNotOverwriteExistingAppliedAt() {
            LocalDateTime existingTime = LocalDateTime.of(2024, 6, 1, 12, 0);
            ApplicationEntity entity = ApplicationEntity.builder().appliedAt(existingTime).build();

            entity.onCreate();

            assertEquals(existingTime, entity.getAppliedAt());
        }
    }
}
