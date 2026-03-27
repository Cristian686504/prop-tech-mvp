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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPropertiesUseCase Tests")
class GetPropertiesUseCaseTest {

    @Mock
    private PropertyRepository propertyRepository;

    private GetPropertiesUseCase getPropertiesUseCase;

    @BeforeEach
    void setUp() {
        getPropertiesUseCase = new GetPropertiesUseCase(propertyRepository);
    }

    @Test
    @DisplayName("Should return all available properties")
    void shouldReturnAllAvailableProperties() {
        // Given
        List<Property> properties = createSampleProperties();
        when(propertyRepository.findAllAvailable()).thenReturn(properties);

        // When
        List<Property> result = getPropertiesUseCase.execute();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Apartamento en El Poblado", result.get(0).getTitle());
        assertEquals("Casa en Laureles", result.get(1).getTitle());
        assertEquals("Estudio en Envigado", result.get(2).getTitle());

        verify(propertyRepository).findAllAvailable();
    }

    @Test
    @DisplayName("Should return empty list when no properties available")
    void shouldReturnEmptyListWhenNoProperties() {
        // Given
        when(propertyRepository.findAllAvailable()).thenReturn(new ArrayList<>());

        // When
        List<Property> result = getPropertiesUseCase.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(propertyRepository).findAllAvailable();
    }

    @Test
    @DisplayName("Should only return available properties, not rented ones")
    void shouldOnlyReturnAvailableProperties() {
        // Given
        List<Property> availableProperties = List.of(
                createProperty("Property 1", PropertyStatus.AVAILABLE),
                createProperty("Property 2", PropertyStatus.AVAILABLE)
        );
        
        when(propertyRepository.findAllAvailable()).thenReturn(availableProperties);

        // When
        List<Property> result = getPropertiesUseCase.execute();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getStatus() == PropertyStatus.AVAILABLE));
        verify(propertyRepository).findAllAvailable();
    }

    @Test
    @DisplayName("Should handle repository returning null gracefully")
    void shouldHandleNullFromRepository() {
        // Given
        when(propertyRepository.findAllAvailable()).thenReturn(null);

        // When
        List<Property> result = getPropertiesUseCase.execute();

        // Then
        assertNull(result);
        verify(propertyRepository).findAllAvailable();
    }

    @Test
    @DisplayName("Should call repository only once")
    void shouldCallRepositoryOnlyOnce() {
        // Given
        List<Property> properties = createSampleProperties();
        when(propertyRepository.findAllAvailable()).thenReturn(properties);

        // When
        getPropertiesUseCase.execute();

        // Then
        verify(propertyRepository, times(1)).findAllAvailable();
    }

    // Helper methods
    private List<Property> createSampleProperties() {
        return List.of(
                Property.builder()
                        .id(UUID.randomUUID())
                        .title("Apartamento en El Poblado")
                        .description("Moderno apartamento con excelente ubicación")
                        .address("Calle 10 #45-67, El Poblado")
                        .price(new BigDecimal("2500000"))
                        .status(PropertyStatus.AVAILABLE)
                        .landlordId(UUID.randomUUID())
                        .imageUrls(List.of("http://example.com/image1.jpg"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                Property.builder()
                        .id(UUID.randomUUID())
                        .title("Casa en Laureles")
                        .description("Amplia casa familiar")
                        .address("Carrera 70 #15-30, Laureles")
                        .price(new BigDecimal("3500000"))
                        .status(PropertyStatus.AVAILABLE)
                        .landlordId(UUID.randomUUID())
                        .imageUrls(List.of("http://example.com/image2.jpg"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                Property.builder()
                        .id(UUID.randomUUID())
                        .title("Estudio en Envigado")
                        .description("Cómodo estudio cerca al metro")
                        .address("Calle 30 Sur #25-10, Envigado")
                        .price(new BigDecimal("1500000"))
                        .status(PropertyStatus.AVAILABLE)
                        .landlordId(UUID.randomUUID())
                        .imageUrls(List.of("http://example.com/image3.jpg"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
    }

    private Property createProperty(String title, PropertyStatus status) {
        return Property.builder()
                .id(UUID.randomUUID())
                .title(title)
                .description("Sample description")
                .address("Sample address")
                .price(new BigDecimal("2000000"))
                .status(status)
                .landlordId(UUID.randomUUID())
                .imageUrls(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
