package co.com.proptech.usecase.property;

import co.com.proptech.model.common.PageRequest;
import co.com.proptech.model.common.PageResponse;
import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.gateways.PropertyRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Use case for retrieving all available properties
 * Following Single Responsibility Principle (SOLID)
 * Domain layer with no framework dependencies (Clean Architecture)
 */
@RequiredArgsConstructor
public class GetPropertiesUseCase {
    
    private final PropertyRepository propertyRepository;
    
    /**
     * Execute the use case to get all available properties with pagination
     * @param pageRequest Pagination parameters (domain POJO)
     * @return Page of available properties
     */
    public PageResponse<Property> execute(PageRequest pageRequest) {
        return propertyRepository.findAllAvailable(pageRequest);
    }
    
    /**
     * Execute the use case to get all available properties (no pagination)
     * @return List of available properties
     * @deprecated Use execute(PageRequest) for better performance
     */
    @Deprecated
    public List<Property> execute() {
        return propertyRepository.findAllAvailable();
    }
}
