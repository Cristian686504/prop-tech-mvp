package co.com.proptech.api.controller;

import co.com.proptech.api.dto.PropertyResponse;
import co.com.proptech.api.dto.PublishPropertyRequestDto;
import co.com.proptech.model.common.PageResponse;
import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.PropertyStatus;
import co.com.proptech.usecase.property.GetPropertiesUseCase;
import co.com.proptech.usecase.property.GetPropertyByIdUseCase;
import co.com.proptech.usecase.property.PublishPropertyUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PropertyController - Unit Tests")
class PropertyControllerTest {

    @Mock private PublishPropertyUseCase publishPropertyUseCase;
    @Mock private GetPropertiesUseCase getPropertiesUseCase;
    @Mock private GetPropertyByIdUseCase getPropertyByIdUseCase;

    @InjectMocks
    private PropertyController controller;

    private final UUID landlordId  = UUID.randomUUID();
    private final UUID propertyId  = UUID.randomUUID();

    private Property sampleProperty() {
        return Property.builder()
                .id(propertyId)
                .title("Casa en El Poblado")
                .description("Hermosa casa")
                .address("Calle 10 #43-55, Medellín")
                .price(new BigDecimal("2500000"))
                .status(PropertyStatus.AVAILABLE)
                .landlordId(landlordId)
                .imageUrls(List.of("http://img.com/1.jpg"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ------------------------------------------------------------------
    // publishProperty
    // ------------------------------------------------------------------

    @Test
    @DisplayName("publishProperty - should return 201 CREATED with mapped response")
    void shouldReturn201OnPublish() {
        PublishPropertyRequestDto request = PublishPropertyRequestDto.builder()
                .title("Casa en El Poblado")
                .description("Hermosa casa")
                .address("Calle 10 #43-55, Medellín")
                .price(new BigDecimal("2500000"))
                .imageUrls(List.of("http://img.com/1.jpg"))
                .build();

        when(publishPropertyUseCase.execute(any())).thenReturn(sampleProperty());

        ResponseEntity<PropertyResponse> response = controller.publishProperty(request, landlordId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(propertyId);
        assertThat(response.getBody().getTitle()).isEqualTo("Casa en El Poblado");
        assertThat(response.getBody().getLandlordId()).isEqualTo(landlordId);
        verify(publishPropertyUseCase).execute(any());
    }

    // ------------------------------------------------------------------
    // getAllProperties
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getAllProperties - should return 200 with Spring Page")
    void shouldReturn200WithPagedProperties() {
        PageResponse<Property> domainPage = new PageResponse<>(
                List.of(sampleProperty()), 0, 20, 1L
        );
        when(getPropertiesUseCase.execute(any())).thenReturn(domainPage);

        ResponseEntity<Page<PropertyResponse>> response = controller.getAllProperties(0, 20);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(1L);
        assertThat(response.getBody().getContent()).hasSize(1);
        assertThat(response.getBody().getContent().get(0).getId()).isEqualTo(propertyId);
        verify(getPropertiesUseCase).execute(any());
    }

    @Test
    @DisplayName("getAllProperties - should return 200 with empty page when no results")
    void shouldReturn200WithEmptyPage() {
        PageResponse<Property> domainPage = new PageResponse<>(List.of(), 0, 20, 0L);
        when(getPropertiesUseCase.execute(any())).thenReturn(domainPage);

        ResponseEntity<Page<PropertyResponse>> response = controller.getAllProperties(0, 20);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).isEmpty();
        assertThat(response.getBody().getTotalElements()).isEqualTo(0L);
    }

    // ------------------------------------------------------------------
    // getPropertyById
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getPropertyById - should return 200 with mapped response")
    void shouldReturn200WithPropertyById() {
        when(getPropertyByIdUseCase.execute(propertyId)).thenReturn(sampleProperty());

        ResponseEntity<PropertyResponse> response = controller.getPropertyById(propertyId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(propertyId);
        assertThat(response.getBody().getStatus()).isEqualTo(PropertyStatus.AVAILABLE);
        verify(getPropertyByIdUseCase).execute(propertyId);
    }
}
