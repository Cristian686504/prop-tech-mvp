package co.com.proptech.api.controller;

import co.com.proptech.api.dto.PropertyResponse;
import co.com.proptech.api.dto.PublishPropertyRequestDto;
import co.com.proptech.model.property.Property;
import co.com.proptech.usecase.property.GetPropertiesUseCase;
import co.com.proptech.usecase.property.GetPropertyByIdUseCase;
import co.com.proptech.usecase.property.PublishPropertyUseCase;
import co.com.proptech.usecase.property.dto.PublishPropertyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PublishPropertyUseCase publishPropertyUseCase;
    private final GetPropertiesUseCase getPropertiesUseCase;
    private final GetPropertyByIdUseCase getPropertyByIdUseCase;

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<PropertyResponse> publishProperty(
            @Valid @RequestBody PublishPropertyRequestDto request,
            @RequestAttribute("userId") UUID userId) {
        
        Property property = publishPropertyUseCase.execute(
                PublishPropertyRequest.builder()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .address(request.getAddress())
                        .price(request.getPrice())
                        .imageUrls(request.getImageUrls())
                        .landlordId(userId)
                        .build()
        );

        return ResponseEntity.ok(mapToResponse(property));
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        List<Property> properties = getPropertiesUseCase.execute();
        List<PropertyResponse> response = properties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable UUID id) {
        Property property = getPropertyByIdUseCase.execute(id);
        return ResponseEntity.ok(mapToResponse(property));
    }

    private PropertyResponse mapToResponse(Property property) {
        return PropertyResponse.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .address(property.getAddress())
                .price(property.getPrice())
                .status(property.getStatus())
                .landlordId(property.getLandlordId())
                .imageUrls(property.getImageUrls())
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .build();
    }
}
