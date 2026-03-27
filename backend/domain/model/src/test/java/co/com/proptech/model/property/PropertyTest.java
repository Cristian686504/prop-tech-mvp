package co.com.proptech.model.property;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Property Domain Model Tests")
class PropertyTest {

    @Test
    @DisplayName("Should create valid property with all required fields")
    void shouldCreateValidProperty() {
        // Given & When
        Property property = Property.builder()
                .id(UUID.randomUUID())
                .title("Apartamento en El Poblado")
                .description("Hermoso apartamento con vista a la ciudad")
                .address("Calle 10 #45-67, El Poblado, Medellín")
                .price(new BigDecimal("2500000"))
                .status(PropertyStatus.AVAILABLE)
                .landlordId(UUID.randomUUID())
                .imageUrls(List.of("http://example.com/image1.jpg"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Then
        assertDoesNotThrow(property::validate);
        assertNotNull(property.getId());
        assertNotNull(property.getTitle());
        assertNotNull(property.getDescription());
        assertNotNull(property.getAddress());
        assertNotNull(property.getPrice());
        assertNotNull(property.getStatus());
        assertNotNull(property.getLandlordId());
    }

    @Test
    @DisplayName("Should throw exception when title is null")
    void shouldThrowExceptionWhenTitleNull() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .title(null)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Title is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when title is empty")
    void shouldThrowExceptionWhenTitleEmpty() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .title("")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Title is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when title is only whitespace")
    void shouldThrowExceptionWhenTitleOnlyWhitespace() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .title("   ")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Title is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when title exceeds 255 characters")
    void shouldThrowExceptionWhenTitleTooLong() {
        // Given
        String longTitle = "A".repeat(256);
        Property property = createValidProperty()
                .toBuilder()
                .title(longTitle)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Title cannot exceed 255 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept title with exactly 255 characters")
    void shouldAcceptTitleWith255Characters() {
        // Given
        String maxTitle = "A".repeat(255);
        Property property = createValidProperty()
                .toBuilder()
                .title(maxTitle)
                .build();

        // When & Then
        assertDoesNotThrow(property::validate);
    }

    @Test
    @DisplayName("Should throw exception when description is null")
    void shouldThrowExceptionWhenDescriptionNull() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .description(null)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Description is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when description is empty")
    void shouldThrowExceptionWhenDescriptionEmpty() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .description("")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Description is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when description exceeds 2000 characters")
    void shouldThrowExceptionWhenDescriptionTooLong() {
        // Given
        String longDescription = "A".repeat(2001);
        Property property = createValidProperty()
                .toBuilder()
                .description(longDescription)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Description cannot exceed 2000 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept description with exactly 2000 characters")
    void shouldAcceptDescriptionWith2000Characters() {
        // Given
        String maxDescription = "A".repeat(2000);
        Property property = createValidProperty()
                .toBuilder()
                .description(maxDescription)
                .build();

        // When & Then
        assertDoesNotThrow(property::validate);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should throw exception when address is null or empty")
    void shouldThrowExceptionWhenAddressInvalid(String address) {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .address(address)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Address is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when address exceeds 255 characters")
    void shouldThrowExceptionWhenAddressTooLong() {
        // Given
        String longAddress = "A".repeat(256);
        Property property = createValidProperty()
                .toBuilder()
                .address(longAddress)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Address cannot exceed 255 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when price is null")
    void shouldThrowExceptionWhenPriceNull() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .price(null)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Price must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when price is zero")
    void shouldThrowExceptionWhenPriceZero() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .price(BigDecimal.ZERO)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Price must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when price is negative")
    void shouldThrowExceptionWhenPriceNegative() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .price(new BigDecimal("-1000"))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Price must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept positive price")
    void shouldAcceptPositivePrice() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .price(new BigDecimal("0.01"))
                .build();

        // When & Then
        assertDoesNotThrow(property::validate);
    }

    @Test
    @DisplayName("Should throw exception when landlordId is null")
    void shouldThrowExceptionWhenLandlordIdNull() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .landlordId(null)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                property::validate
        );
        assertEquals("Landlord ID is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept property without image URLs")
    void shouldAcceptPropertyWithoutImageUrls() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .imageUrls(null)
                .build();

        // When & Then
        assertDoesNotThrow(property::validate);
    }

    @Test
    @DisplayName("Should accept property with empty image URLs list")
    void shouldAcceptPropertyWithEmptyImageUrls() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .imageUrls(List.of())
                .build();

        // When & Then
        assertDoesNotThrow(property::validate);
    }

    @Test
    @DisplayName("Should accept property with multiple image URLs")
    void shouldAcceptPropertyWithMultipleImageUrls() {
        // Given
        Property property = createValidProperty()
                .toBuilder()
                .imageUrls(List.of(
                        "http://example.com/image1.jpg",
                        "http://example.com/image2.jpg",
                        "http://example.com/image3.jpg"
                ))
                .build();

        // When & Then
        assertDoesNotThrow(property::validate);
        assertEquals(3, property.getImageUrls().size());
    }

    @Test
    @DisplayName("Should use builder pattern correctly")
    void shouldUseBuilderPattern() {
        // Given & When
        UUID id = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Property property = Property.builder()
                .id(id)
                .title("Test Property")
                .description("Test Description")
                .address("Test Address")
                .price(new BigDecimal("1000000"))
                .status(PropertyStatus.AVAILABLE)
                .landlordId(landlordId)
                .imageUrls(List.of("test.jpg"))
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertEquals(id, property.getId());
        assertEquals("Test Property", property.getTitle());
        assertEquals("Test Description", property.getDescription());
        assertEquals("Test Address", property.getAddress());
        assertEquals(new BigDecimal("1000000"), property.getPrice());
        assertEquals(PropertyStatus.AVAILABLE, property.getStatus());
        assertEquals(landlordId, property.getLandlordId());
        assertEquals(1, property.getImageUrls().size());
        assertEquals(now, property.getCreatedAt());
        assertEquals(now, property.getUpdatedAt());
    }

    @Test
    @DisplayName("Should use toBuilder correctly for modifications")
    void shouldUseToBuilderForModifications() {
        // Given
        Property original = createValidProperty();

        // When
        Property modified = original.toBuilder()
                .title("Modified Title")
                .price(new BigDecimal("3000000"))
                .build();

        // Then
        assertEquals("Modified Title", modified.getTitle());
        assertEquals(new BigDecimal("3000000"), modified.getPrice());
        assertEquals(original.getDescription(), modified.getDescription());
        assertEquals(original.getAddress(), modified.getAddress());
    }

    @Test
    @DisplayName("Should have different status values")
    void shouldHandleDifferentStatusValues() {
        // Given & When
        Property availableProperty = createValidProperty()
                .toBuilder()
                .status(PropertyStatus.AVAILABLE)
                .build();

        Property rentedProperty = createValidProperty()
                .toBuilder()
                .status(PropertyStatus.RENTED)
                .build();

        // Then
        assertDoesNotThrow(availableProperty::validate);
        assertDoesNotThrow(rentedProperty::validate);
        assertEquals(PropertyStatus.AVAILABLE, availableProperty.getStatus());
        assertEquals(PropertyStatus.RENTED, rentedProperty.getStatus());
    }

    // Helper method
    private Property createValidProperty() {
        return Property.builder()
                .id(UUID.randomUUID())
                .title("Valid Property")
                .description("This is a valid property description")
                .address("Valid Address 123")
                .price(new BigDecimal("2000000"))
                .status(PropertyStatus.AVAILABLE)
                .landlordId(UUID.randomUUID())
                .imageUrls(List.of("http://example.com/image.jpg"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
