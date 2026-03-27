package co.com.proptech.usecase.property;

import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.PropertyStatus;
import co.com.proptech.model.property.gateways.PropertyRepository;
import co.com.proptech.model.user.User;
import co.com.proptech.model.user.enums.DocumentType;
import co.com.proptech.model.user.enums.UserRole;
import co.com.proptech.model.user.gateways.UserRepository;
import co.com.proptech.usecase.property.dto.PublishPropertyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@DisplayName("PublishPropertyUseCase Tests")
class PublishPropertyUseCaseTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    private PublishPropertyUseCase publishPropertyUseCase;

    private UUID landlordId;
    private User landlord;

    @BeforeEach
    void setUp() {
        publishPropertyUseCase = new PublishPropertyUseCase(propertyRepository, userRepository);
        landlordId = UUID.randomUUID();
        landlord = User.builder()
                .id(landlordId)
                .name("John Landlord")
                .email("landlord@example.com")
                .phone("+573001234567")
                .documentType(DocumentType.CC)
                .documentId("1234567890")
                .role(UserRole.LANDLORD)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should publish property successfully with valid data")
    void shouldPublishPropertySuccessfully() {
        // Given
        PublishPropertyRequest request = PublishPropertyRequest.builder()
                .title("Apartamento en El Poblado")
                .description("Hermoso apartamento con vista a la ciudad")
                .address("Calle 10 #45-67, El Poblado, Medellín")
                .price(new BigDecimal("2500000"))
                .imageUrls(List.of("http://example.com/image1.jpg"))
                .landlordId(landlordId)
                .build();

        when(userRepository.findById(landlordId)).thenReturn(Optional.of(landlord));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Property result = publishPropertyUseCase.execute(request);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getAddress(), result.getAddress());
        assertEquals(request.getPrice(), result.getPrice());
        assertEquals(request.getImageUrls(), result.getImageUrls());
        assertEquals(landlordId, result.getLandlordId());
        assertEquals(PropertyStatus.AVAILABLE, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        // Verify interactions
        verify(userRepository).findById(landlordId);
        verify(propertyRepository).save(any(Property.class));

        // Verify saved property structure
        ArgumentCaptor<Property> propertyCaptor = ArgumentCaptor.forClass(Property.class);
        verify(propertyRepository).save(propertyCaptor.capture());
        Property savedProperty = propertyCaptor.getValue();
        assertEquals(PropertyStatus.AVAILABLE, savedProperty.getStatus());
        assertNotNull(savedProperty.getId());
    }

    @Test
    @DisplayName("Should throw exception when landlord does not exist")
    void shouldThrowExceptionWhenLandlordNotFound() {
        // Given
        PublishPropertyRequest request = PublishPropertyRequest.builder()
                .title("Apartamento en El Poblado")
                .description("Hermoso apartamento")
                .address("Calle 10 #45-67")
                .price(new BigDecimal("2500000"))
                .imageUrls(List.of())
                .landlordId(landlordId)
                .build();

        when(userRepository.findById(landlordId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> publishPropertyUseCase.execute(request)
        );

        assertEquals("Landlord not found", exception.getMessage());
        verify(userRepository).findById(landlordId);
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    @DisplayName("Should throw exception when user is not a landlord")
    void shouldThrowExceptionWhenUserNotLandlord() {
        // Given
        User tenant = landlord.toBuilder()
                .role(UserRole.TENANT)
                .build();

        PublishPropertyRequest request = PublishPropertyRequest.builder()
                .title("Apartamento en El Poblado")
                .description("Hermoso apartamento")
                .address("Calle 10 #45-67")
                .price(new BigDecimal("2500000"))
                .imageUrls(List.of())
                .landlordId(landlordId)
                .build();

        when(userRepository.findById(landlordId)).thenReturn(Optional.of(tenant));

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> publishPropertyUseCase.execute(request)
        );

        assertEquals("Only landlords can publish properties", exception.getMessage());
        verify(userRepository).findById(landlordId);
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    @DisplayName("Should throw exception when title is empty")
    void shouldThrowExceptionWhenTitleEmpty() {
        // Given
        PublishPropertyRequest request = PublishPropertyRequest.builder()
                .title("")
                .description("Hermoso apartamento")
                .address("Calle 10 #45-67")
                .price(new BigDecimal("2500000"))
                .imageUrls(List.of())
                .landlordId(landlordId)
                .build();

        when(userRepository.findById(landlordId)).thenReturn(Optional.of(landlord));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> publishPropertyUseCase.execute(request)
        );

        assertEquals("Title is required", exception.getMessage());
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    @DisplayName("Should throw exception when price is zero")
    void shouldThrowExceptionWhenPriceZero() {
        // Given
        PublishPropertyRequest request = PublishPropertyRequest.builder()
                .title("Apartamento en El Poblado")
                .description("Hermoso apartamento")
                .address("Calle 10 #45-67")
                .price(BigDecimal.ZERO)
                .imageUrls(List.of())
                .landlordId(landlordId)
                .build();

        when(userRepository.findById(landlordId)).thenReturn(Optional.of(landlord));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> publishPropertyUseCase.execute(request)
        );

        assertEquals("Price must be greater than zero", exception.getMessage());
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    @DisplayName("Should throw exception when price is negative")
    void shouldThrowExceptionWhenPriceNegative() {
        // Given
        PublishPropertyRequest request = PublishPropertyRequest.builder()
                .title("Apartamento en El Poblado")
                .description("Hermoso apartamento")
                .address("Calle 10 #45-67")
                .price(new BigDecimal("-1000"))
                .imageUrls(List.of())
                .landlordId(landlordId)
                .build();

        when(userRepository.findById(landlordId)).thenReturn(Optional.of(landlord));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> publishPropertyUseCase.execute(request)
        );

        assertEquals("Price must be greater than zero", exception.getMessage());
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    @DisplayName("Should throw exception when description is too long")
    void shouldThrowExceptionWhenDescriptionTooLong() {
        // Given
        String longDescription = "A".repeat(2001);
        PublishPropertyRequest request = PublishPropertyRequest.builder()
                .title("Apartamento en El Poblado")
                .description(longDescription)
                .address("Calle 10 #45-67")
                .price(new BigDecimal("2500000"))
                .imageUrls(List.of())
                .landlordId(landlordId)
                .build();

        when(userRepository.findById(landlordId)).thenReturn(Optional.of(landlord));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> publishPropertyUseCase.execute(request)
        );

        assertEquals("Description cannot exceed 2000 characters", exception.getMessage());
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    @DisplayName("Should publish property with empty image URLs list")
    void shouldPublishPropertyWithEmptyImageUrls() {
        // Given
        PublishPropertyRequest request = PublishPropertyRequest.builder()
                .title("Apartamento sin fotos")
                .description("Propiedad sin imágenes")
                .address("Calle 10 #45-67")
                .price(new BigDecimal("2500000"))
                .imageUrls(List.of())
                .landlordId(landlordId)
                .build();

        when(userRepository.findById(landlordId)).thenReturn(Optional.of(landlord));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Property result = publishPropertyUseCase.execute(request);

        // Then
        assertNotNull(result);
        assertNotNull(result.getImageUrls());
        assertTrue(result.getImageUrls().isEmpty());
        verify(propertyRepository).save(any(Property.class));
    }

    @Test
    @DisplayName("Should publish property with multiple image URLs")
    void shouldPublishPropertyWithMultipleImageUrls() {
        // Given
        List<String> imageUrls = List.of(
                "http://example.com/image1.jpg",
                "http://example.com/image2.jpg",
                "http://example.com/image3.jpg"
        );

        PublishPropertyRequest request = PublishPropertyRequest.builder()
                .title("Apartamento con muchas fotos")
                .description("Propiedad con galería completa")
                .address("Calle 10 #45-67")
                .price(new BigDecimal("2500000"))
                .imageUrls(imageUrls)
                .landlordId(landlordId)
                .build();

        when(userRepository.findById(landlordId)).thenReturn(Optional.of(landlord));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Property result = publishPropertyUseCase.execute(request);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getImageUrls().size());
        assertEquals(imageUrls, result.getImageUrls());
        verify(propertyRepository).save(any(Property.class));
    }
}
