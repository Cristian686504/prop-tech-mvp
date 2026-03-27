package co.com.proptech.usecase.property;

import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.PropertyStatus;
import co.com.proptech.model.property.gateways.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPropertyByIdUseCase Tests")
class GetPropertyByIdUseCaseTest {

    @Mock
    private PropertyRepository propertyRepository;

    private GetPropertyByIdUseCase getPropertyByIdUseCase;

    @BeforeEach
    void setUp() {
        getPropertyByIdUseCase = new GetPropertyByIdUseCase(propertyRepository);
    }

    @Test
    @DisplayName("Should return property when found by ID")
    void shouldReturnPropertyWhenFound() {
        // Given
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        Property property = Property.builder()
                .id(propertyId)
                .title("Apartamento en El Poblado")
                .description("Hermoso apartamento con vista a la ciudad")
                .address("Calle 10 #45-67, El Poblado, Medellín")
                .price(new BigDecimal("2500000"))
                .status(PropertyStatus.AVAILABLE)
                .landlordId(landlordId)
                .imageUrls(List.of("http://example.com/image1.jpg"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // When
        Property result = getPropertyByIdUseCase.execute(propertyId);

        // Then
        assertNotNull(result);
        assertEquals(propertyId, result.getId());
        assertEquals("Apartamento en El Poblado", result.getTitle());
        assertEquals("Hermoso apartamento con vista a la ciudad", result.getDescription());
        assertEquals("Calle 10 #45-67, El Poblado, Medellín", result.getAddress());
        assertEquals(new BigDecimal("2500000"), result.getPrice());
        assertEquals(PropertyStatus.AVAILABLE, result.getStatus());
        assertEquals(landlordId, result.getLandlordId());
        assertNotNull(result.getImageUrls());
        assertEquals(1, result.getImageUrls().size());

        verify(propertyRepository).findById(propertyId);
    }

    @Test
    @DisplayName("Should throw exception when property not found")
    void shouldThrowExceptionWhenPropertyNotFound() {
        // Given
        UUID propertyId = UUID.randomUUID();
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getPropertyByIdUseCase.execute(propertyId)
        );

        assertEquals("Property not found", exception.getMessage());
        verify(propertyRepository).findById(propertyId);
    }

    @Test
    @DisplayName("Should call repository with correct property ID")
    void shouldCallRepositoryWithCorrectId() {
        // Given
        UUID propertyId = UUID.randomUUID();
        Property property = createSampleProperty(propertyId);
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // When
        getPropertyByIdUseCase.execute(propertyId);

        // Then
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(propertyRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Should return property with RENTED status")
    void shouldReturnRentedProperty() {
        // Given
        UUID propertyId = UUID.randomUUID();
        Property rentedProperty = createSampleProperty(propertyId)
                .toBuilder()
                .status(PropertyStatus.RENTED)
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(rentedProperty));

        // When
        Property result = getPropertyByIdUseCase.execute(propertyId);

        // Then
        assertNotNull(result);
        assertEquals(PropertyStatus.RENTED, result.getStatus());
        verify(propertyRepository).findById(propertyId);
    }

    @Test
    @DisplayName("Should return property with empty image URLs list")
    void shouldReturnPropertyWithEmptyImageUrls() {
        // Given
        UUID propertyId = UUID.randomUUID();
        Property property = createSampleProperty(propertyId)
                .toBuilder()
                .imageUrls(List.of())
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // When
        Property result = getPropertyByIdUseCase.execute(propertyId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getImageUrls());
        assertTrue(result.getImageUrls().isEmpty());
        verify(propertyRepository).findById(propertyId);
    }

    @Test
    @DisplayName("Should handle different property prices correctly")
    void shouldHandleDifferentPricesCorrectly() {
        // Given
        UUID propertyId = UUID.randomUUID();
        BigDecimal expensivePrice = new BigDecimal("50000000");
        Property expensiveProperty = createSampleProperty(propertyId)
                .toBuilder()
                .price(expensivePrice)
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(expensiveProperty));

        // When
        Property result = getPropertyByIdUseCase.execute(propertyId);

        // Then
        assertNotNull(result);
        assertEquals(expensivePrice, result.getPrice());
        verify(propertyRepository).findById(propertyId);
    }

    @Test
    @DisplayName("Should preserve all property fields when retrieved")
    void shouldPreserveAllPropertyFields() {
        // Given
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(5);
        LocalDateTime updatedAt = LocalDateTime.now();

        Property property = Property.builder()
                .id(propertyId)
                .title("Complete Property")
                .description("Property with all fields")
                .address("Full Address 123")
                .price(new BigDecimal("3000000"))
                .status(PropertyStatus.AVAILABLE)
                .landlordId(landlordId)
                .imageUrls(List.of("img1.jpg", "img2.jpg", "img3.jpg"))
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // When
        Property result = getPropertyByIdUseCase.execute(propertyId);

        // Then
        assertNotNull(result);
        assertEquals(propertyId, result.getId());
        assertEquals("Complete Property", result.getTitle());
        assertEquals("Property with all fields", result.getDescription());
        assertEquals("Full Address 123", result.getAddress());
        assertEquals(new BigDecimal("3000000"), result.getPrice());
        assertEquals(PropertyStatus.AVAILABLE, result.getStatus());
        assertEquals(landlordId, result.getLandlordId());
        assertEquals(3, result.getImageUrls().size());
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(updatedAt, result.getUpdatedAt());

        verify(propertyRepository).findById(propertyId);
    }

    // Helper method
    private Property createSampleProperty(UUID propertyId) {
        return Property.builder()
                .id(propertyId)
                .title("Sample Property")
                .description("Sample description")
                .address("Sample address")
                .price(new BigDecimal("2000000"))
                .status(PropertyStatus.AVAILABLE)
                .landlordId(UUID.randomUUID())
                .imageUrls(List.of("http://example.com/image.jpg"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
