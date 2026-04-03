package co.com.proptech.usecase.property;

import co.com.proptech.model.common.PageRequest;
import co.com.proptech.model.common.PageResponse;
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

    // ========== Pagination Tests (New Feature) ==========

    @Test
    @DisplayName("Should return paginated properties with correct metadata")
    void shouldReturnPaginatedPropertiesWithCorrectMetadata() {
        // Given
        PageRequest pageRequest = new PageRequest(0, 20, "createdAt", PageRequest.SortDirection.DESC);
        List<Property> properties = createSampleProperties();
        PageResponse<Property> pageResponse = new PageResponse<>(properties, 0, 20, 3L);
        
        when(propertyRepository.findAllAvailable(pageRequest)).thenReturn(pageResponse);

        // When
        PageResponse<Property> result = getPropertiesUseCase.execute(pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(20, result.getSize());
        assertEquals(3L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertFalse(result.hasNext());
        assertFalse(result.hasPrevious());
        
        verify(propertyRepository).findAllAvailable(pageRequest);
    }

    @Test
    @DisplayName("Should handle pagination for multiple pages")
    void shouldHandlePaginationForMultiplePages() {
        // Given - Page 2 of 3 (21-40 of 50 items)
        PageRequest pageRequest = new PageRequest(1, 20);
        List<Property> properties = createSampleProperties();
        PageResponse<Property> pageResponse = new PageResponse<>(properties, 1, 20, 50L);
        
        when(propertyRepository.findAllAvailable(pageRequest)).thenReturn(pageResponse);

        // When
        PageResponse<Property> result = getPropertiesUseCase.execute(pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPage()); // Second page (0-indexed)
        assertEquals(20, result.getSize());
        assertEquals(50L, result.getTotalElements());
        assertEquals(3, result.getTotalPages()); // ceil(50/20) = 3
        assertFalse(result.isFirst());
        assertFalse(result.isLast());
        assertTrue(result.hasNext());
        assertTrue(result.hasPrevious());
        
        verify(propertyRepository).findAllAvailable(pageRequest);
    }

    @Test
    @DisplayName("Should return empty page when no properties available")
    void shouldReturnEmptyPageWhenNoProperties() {
        // Given
        PageRequest pageRequest = new PageRequest(0, 20);
        PageResponse<Property> emptyPage = new PageResponse<>(new ArrayList<>(), 0, 20, 0L);
        
        when(propertyRepository.findAllAvailable(pageRequest)).thenReturn(emptyPage);

        // When
        PageResponse<Property> result = getPropertiesUseCase.execute(pageRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        
        verify(propertyRepository).findAllAvailable(pageRequest);
    }

    @Test
    @DisplayName("Should respect custom page size in PageRequest")
    void shouldRespectCustomPageSize() {
        // Given - Request 5 items per page
        PageRequest pageRequest = new PageRequest(0, 5);
        List<Property> properties = createSampleProperties().subList(0, 3);
        PageResponse<Property> pageResponse = new PageResponse<>(properties, 0, 5, 25L);
        
        when(propertyRepository.findAllAvailable(pageRequest)).thenReturn(pageResponse);

        // When
        PageResponse<Property> result = getPropertiesUseCase.execute(pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getSize());
        assertEquals(25L, result.getTotalElements());
        assertEquals(5, result.getTotalPages()); // ceil(25/5) = 5
        
        verify(propertyRepository).findAllAvailable(pageRequest);
    }

    @Test
    @DisplayName("Should handle last page with partial results")
    void shouldHandleLastPageWithPartialResults() {
        // Given - Last page with only 3 items (page 2 of 2, with 23 total items)
        PageRequest pageRequest = new PageRequest(1, 20);
        List<Property> properties = createSampleProperties(); // 3 items
        PageResponse<Property> pageResponse = new PageResponse<>(properties, 1, 20, 23L);
        
        when(propertyRepository.findAllAvailable(pageRequest)).thenReturn(pageResponse);

        // When
        PageResponse<Property> result = getPropertiesUseCase.execute(pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(1, result.getPage());
        assertEquals(23L, result.getTotalElements());
        assertEquals(2, result.getTotalPages()); // ceil(23/20) = 2
        assertTrue(result.isLast());
        assertFalse(result.hasNext());
        
        verify(propertyRepository).findAllAvailable(pageRequest);
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
