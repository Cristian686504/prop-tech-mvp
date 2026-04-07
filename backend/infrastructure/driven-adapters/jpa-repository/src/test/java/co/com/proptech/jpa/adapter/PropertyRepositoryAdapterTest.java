package co.com.proptech.jpa.adapter;

import co.com.proptech.jpa.entity.PropertyEntity;
import co.com.proptech.jpa.repository.PropertyJpaRepository;
import co.com.proptech.model.common.PageRequest;
import co.com.proptech.model.common.PageResponse;
import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.PropertyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PropertyRepositoryAdapter - Pagination Tests")
class PropertyRepositoryAdapterTest {

    @Mock
    private PropertyJpaRepository jpaRepository;

    private PropertyRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PropertyRepositoryAdapter(jpaRepository);
    }

    @Test
    @DisplayName("Should convert domain PageRequest to Spring Pageable correctly")
    void shouldConvertPageRequestToPageable() {
        // Given
        PageRequest pageRequest = new PageRequest(1, 10, "createdAt", PageRequest.SortDirection.DESC);
        List<PropertyEntity> entities = createSampleEntities();
        Page<PropertyEntity> springPage = new PageImpl<>(entities, 
                org.springframework.data.domain.PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "createdAt")), 
                25L);
        
        when(jpaRepository.findAllByStatus(eq(PropertyStatus.AVAILABLE), any(Pageable.class)))
                .thenReturn(springPage);

        // When
        PageResponse<Property> result = adapter.findAllAvailable(pageRequest);

        // Then
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(jpaRepository).findAllByStatus(eq(PropertyStatus.AVAILABLE), pageableCaptor.capture());
        
        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(1, capturedPageable.getPageNumber());
        assertEquals(10, capturedPageable.getPageSize());
        assertEquals(Sort.Direction.DESC, capturedPageable.getSort().getOrderFor("createdAt").getDirection());
    }

    @Test
    @DisplayName("Should convert Spring Page to domain PageResponse correctly")
    void shouldConvertSpringPageToPageResponse() {
        // Given
        PageRequest pageRequest = new PageRequest(0, 20);
        List<PropertyEntity> entities = createSampleEntities();
        Page<PropertyEntity> springPage = new PageImpl<>(entities, 
                org.springframework.data.domain.PageRequest.of(0, 20), 
                entities.size());
        
        when(jpaRepository.findAllByStatus(eq(PropertyStatus.AVAILABLE), any(Pageable.class)))
                .thenReturn(springPage);

        // When
        PageResponse<Property> result = adapter.findAllAvailable(pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(20, result.getSize());
        assertEquals(3L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        
        // Verify domain objects are correctly mapped
        Property firstProperty = result.getContent().get(0);
        assertEquals("Apartamento en El Poblado", firstProperty.getTitle());
        assertEquals(PropertyStatus.AVAILABLE, firstProperty.getStatus());
    }

    @Test
    @DisplayName("Should handle ascending sort direction")
    void shouldHandleAscendingSortDirection() {
        // Given
        PageRequest pageRequest = new PageRequest(0, 20, "price", PageRequest.SortDirection.ASC);
        List<PropertyEntity> entities = createSampleEntities();
        Page<PropertyEntity> springPage = new PageImpl<>(entities, 
                org.springframework.data.domain.PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "price")), 
                entities.size());
        
        when(jpaRepository.findAllByStatus(eq(PropertyStatus.AVAILABLE), any(Pageable.class)))
                .thenReturn(springPage);

        // When
        adapter.findAllAvailable(pageRequest);

        // Then
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(jpaRepository).findAllByStatus(eq(PropertyStatus.AVAILABLE), pageableCaptor.capture());
        
        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(Sort.Direction.ASC, capturedPageable.getSort().getOrderFor("price").getDirection());
    }

    @Test
    @DisplayName("Should handle empty page from database")
    void shouldHandleEmptyPage() {
        // Given
        PageRequest pageRequest = new PageRequest(5, 20); // Page beyond available data
        Page<PropertyEntity> emptySpringPage = new PageImpl<>(List.of(), 
                org.springframework.data.domain.PageRequest.of(5, 20), 
                0L);
        
        when(jpaRepository.findAllByStatus(eq(PropertyStatus.AVAILABLE), any(Pageable.class)))
                .thenReturn(emptySpringPage);

        // When
        PageResponse<Property> result = adapter.findAllAvailable(pageRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(5, result.getPage());
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    @DisplayName("Should preserve all property fields during entity-to-domain conversion")
    void shouldPreserveAllFieldsDuringConversion() {
        // Given
        PageRequest pageRequest = new PageRequest(0, 20);
        UUID propertyId = UUID.randomUUID();
        UUID landlordId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        PropertyEntity entity = PropertyEntity.builder()
                .id(propertyId)
                .title("Test Property")
                .description("Test Description")
                .address("Test Address 123")
                .price(new BigDecimal("2500000"))
                .status(PropertyStatus.AVAILABLE)
                .landlordId(landlordId)
                .imageUrls(List.of("image1.jpg", "image2.jpg"))
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        Page<PropertyEntity> springPage = new PageImpl<>(List.of(entity), 
                org.springframework.data.domain.PageRequest.of(0, 20), 
                1L);
        
        when(jpaRepository.findAllByStatus(eq(PropertyStatus.AVAILABLE), any(Pageable.class)))
                .thenReturn(springPage);

        // When
        PageResponse<Property> result = adapter.findAllAvailable(pageRequest);

        // Then
        Property property = result.getContent().get(0);
        assertEquals(propertyId, property.getId());
        assertEquals("Test Property", property.getTitle());
        assertEquals("Test Description", property.getDescription());
        assertEquals("Test Address 123", property.getAddress());
        assertEquals(new BigDecimal("2500000"), property.getPrice());
        assertEquals(PropertyStatus.AVAILABLE, property.getStatus());
        assertEquals(landlordId, property.getLandlordId());
        assertEquals(2, property.getImageUrls().size());
        assertEquals(now, property.getCreatedAt());
        assertEquals(now, property.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle large page sizes")
    void shouldHandleLargePageSizes() {
        // Given - Request 1000 items per page (max allowed)
        PageRequest pageRequest = new PageRequest(0, 1000);
        List<PropertyEntity> entities = createSampleEntities();
        Page<PropertyEntity> springPage = new PageImpl<>(entities, 
                org.springframework.data.domain.PageRequest.of(0, 1000), 
                entities.size());
        
        when(jpaRepository.findAllByStatus(eq(PropertyStatus.AVAILABLE), any(Pageable.class)))
                .thenReturn(springPage);

        // When
        PageResponse<Property> result = adapter.findAllAvailable(pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(1000, result.getSize());
        verify(jpaRepository).findAllByStatus(eq(PropertyStatus.AVAILABLE), any(Pageable.class));
    }

    @Test
    @DisplayName("Should use correct default sort (createdAt DESC)")
    void shouldUseCorrectDefaultSort() {
        // Given
        PageRequest pageRequest = new PageRequest(0, 20); // Uses default sort
        List<PropertyEntity> entities = createSampleEntities();
        Page<PropertyEntity> springPage = new PageImpl<>(entities, 
                org.springframework.data.domain.PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")), 
                entities.size());
        
        when(jpaRepository.findAllByStatus(eq(PropertyStatus.AVAILABLE), any(Pageable.class)))
                .thenReturn(springPage);

        // When
        adapter.findAllAvailable(pageRequest);

        // Then
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(jpaRepository).findAllByStatus(eq(PropertyStatus.AVAILABLE), pageableCaptor.capture());
        
        Pageable capturedPageable = pageableCaptor.getValue();
        assertTrue(capturedPageable.getSort().getOrderFor("createdAt") != null);
        assertEquals(Sort.Direction.DESC, capturedPageable.getSort().getOrderFor("createdAt").getDirection());
    }

    // Helper method
    private List<PropertyEntity> createSampleEntities() {
        return List.of(
                PropertyEntity.builder()
                        .id(UUID.randomUUID())
                        .title("Apartamento en El Poblado")
                        .description("Moderno apartamento")
                        .address("Calle 10 #45-67")
                        .price(new BigDecimal("2500000"))
                        .status(PropertyStatus.AVAILABLE)
                        .landlordId(UUID.randomUUID())
                        .imageUrls(List.of("image1.jpg"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                PropertyEntity.builder()
                        .id(UUID.randomUUID())
                        .title("Casa en Laureles")
                        .description("Amplia casa")
                        .address("Carrera 70 #15-30")
                        .price(new BigDecimal("3500000"))
                        .status(PropertyStatus.AVAILABLE)
                        .landlordId(UUID.randomUUID())
                        .imageUrls(List.of("image2.jpg"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                PropertyEntity.builder()
                        .id(UUID.randomUUID())
                        .title("Estudio en Envigado")
                        .description("Cómodo estudio")
                        .address("Calle 30 Sur #25-10")
                        .price(new BigDecimal("1500000"))
                        .status(PropertyStatus.AVAILABLE)
                        .landlordId(UUID.randomUUID())
                        .imageUrls(List.of("image3.jpg"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
    }
}
