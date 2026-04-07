package co.com.proptech.jpa.adapter;

import co.com.proptech.jpa.entity.PropertyEntity;
import co.com.proptech.jpa.repository.PropertyJpaRepository;
import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.PropertyStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PropertyRepositoryAdapter - Remaining Methods Tests")
class PropertyRepositoryAdapterExtraTest {

    @Mock
    private PropertyJpaRepository jpaRepository;

    private PropertyRepositoryAdapter adapter;

    private final UUID propertyId  = UUID.randomUUID();
    private final UUID landlordId  = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new PropertyRepositoryAdapter(jpaRepository);
    }

    private PropertyEntity sampleEntity() {
        return PropertyEntity.builder()
                .id(propertyId)
                .title("Apartamento en Laureles")
                .description("Cómodo apartamento")
                .address("Calle 33 #76-50, Medellín")
                .price(new BigDecimal("1800000"))
                .status(PropertyStatus.AVAILABLE)
                .landlordId(landlordId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Property sampleDomain() {
        return Property.builder()
                .title("Apartamento en Laureles")
                .description("Cómodo apartamento")
                .address("Calle 33 #76-50, Medellín")
                .price(new BigDecimal("1800000"))
                .status(PropertyStatus.AVAILABLE)
                .landlordId(landlordId)
                .build();
    }

    @Test
    @DisplayName("save - should persist and return mapped domain property")
    void shouldSaveAndReturnDomain() {
        PropertyEntity saved = sampleEntity();
        when(jpaRepository.save(any(PropertyEntity.class))).thenReturn(saved);

        Property result = adapter.save(sampleDomain());

        assertNotNull(result);
        assertEquals(propertyId, result.getId());
        assertEquals("Apartamento en Laureles", result.getTitle());
        assertEquals(landlordId, result.getLandlordId());
        verify(jpaRepository).save(any(PropertyEntity.class));
    }

    @Test
    @DisplayName("findById - should return mapped domain when found")
    void shouldReturnDomainWhenFoundById() {
        when(jpaRepository.findById(propertyId)).thenReturn(Optional.of(sampleEntity()));

        Optional<Property> result = adapter.findById(propertyId);

        assertTrue(result.isPresent());
        assertEquals(propertyId, result.get().getId());
    }

    @Test
    @DisplayName("findById - should return empty when not found")
    void shouldReturnEmptyWhenNotFoundById() {
        when(jpaRepository.findById(propertyId)).thenReturn(Optional.empty());

        assertFalse(adapter.findById(propertyId).isPresent());
    }

    @Test
    @DisplayName("findAll - should return filtered list as domain objects")
    void shouldReturnFilteredList() {
        when(jpaRepository.findAllWithFilters(PropertyStatus.AVAILABLE, landlordId))
                .thenReturn(List.of(sampleEntity()));

        List<Property> results = adapter.findAll(PropertyStatus.AVAILABLE, landlordId);

        assertEquals(1, results.size());
        assertEquals(PropertyStatus.AVAILABLE, results.get(0).getStatus());
        assertEquals(landlordId, results.get(0).getLandlordId());
    }

    @Test
    @DisplayName("findAll - should return empty list when no matches")
    void shouldReturnEmptyListWhenNoMatches() {
        when(jpaRepository.findAllWithFilters(null, null)).thenReturn(List.of());

        assertTrue(adapter.findAll(null, null).isEmpty());
    }

    @Test
    @DisplayName("findByLandlordId - should return all properties for landlord")
    void shouldReturnPropertiesByLandlordId() {
        when(jpaRepository.findAllByLandlordId(landlordId)).thenReturn(List.of(sampleEntity()));

        List<Property> results = adapter.findByLandlordId(landlordId);

        assertEquals(1, results.size());
        assertEquals(landlordId, results.get(0).getLandlordId());
    }

    @Test
    @DisplayName("deleteById - should delegate to JPA repository")
    void shouldDelegateDeleteById() {
        adapter.deleteById(propertyId);
        verify(jpaRepository).deleteById(propertyId);
    }

    @Test
    @DisplayName("existsById - should return true when found, false when not")
    void shouldDelegateExistsById() {
        UUID missingId = UUID.randomUUID();
        when(jpaRepository.existsById(propertyId)).thenReturn(true);
        when(jpaRepository.existsById(missingId)).thenReturn(false);

        assertTrue(adapter.existsById(propertyId));
        assertFalse(adapter.existsById(missingId));
    }
}
