package co.com.proptech.usecase.property;

import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.gateways.PropertyRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Use case for retrieving all available properties
 * Following Single Responsibility Principle (SOLID)
 */
@RequiredArgsConstructor
public class GetPropertiesUseCase {
    
    private final PropertyRepository propertyRepository;
    
    /**
     * Execute the use case to get all available properties
     * @return List of available properties
     */
    public List<Property> execute() {
        return propertyRepository.findAllAvailable();
    }
}
