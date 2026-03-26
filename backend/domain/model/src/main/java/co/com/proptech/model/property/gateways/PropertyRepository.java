package co.com.proptech.model.property.gateways;

import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.PropertyStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Property repository gateway (port)
 * Following Dependency Inversion Principle (SOLID)
 */
public interface PropertyRepository {
    
    /**
     * Save a property (create or update)
     * @param property Property to save
     * @return Saved property
     */
    Property save(Property property);
    
    /**
     * Find property by ID
     * @param id Property ID
     * @return Optional property
     */
    Optional<Property> findById(UUID id);
    
    /**
     * Find all properties with optional filters
     * @param status Optional status filter
     * @param landlordId Optional landlord ID filter
     * @return List of properties
     */
    List<Property> findAll(PropertyStatus status, UUID landlordId);
    
    /**
     * Find all available properties
     * @return List of available properties
     */
    List<Property> findAllAvailable();
    
    /**
     * Find properties by landlord
     * @param landlordId Landlord ID
     * @return List of landlord's properties
     */
    List<Property> findByLandlordId(UUID landlordId);
    
    /**
     * Delete property by ID
     * @param id Property ID
     */
    void deleteById(UUID id);
    
    /**
     * Check if property exists
     * @param id Property ID
     * @return true if exists
     */
    boolean existsById(UUID id);
}
