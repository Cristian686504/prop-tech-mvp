package co.com.proptech.usecase.property;

import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.gateways.PropertyRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Use case for retrieving a property by ID
 * Following Single Responsibility Principle (SOLID)
 */
@RequiredArgsConstructor
public class GetPropertyByIdUseCase {
    
    private final PropertyRepository propertyRepository;
    
    /**
     * Execute the use case to get a property by ID
     * @param propertyId Property ID
     * @return Property
     * @throws IllegalArgumentException if property not found
     */
    public Property execute(UUID propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
    }
}
