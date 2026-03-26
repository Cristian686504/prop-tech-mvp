package co.com.proptech.jpa.repository;

import co.com.proptech.jpa.entity.PropertyEntity;
import co.com.proptech.model.property.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PropertyJpaRepository extends JpaRepository<PropertyEntity, UUID> {
    
    List<PropertyEntity> findAllByStatus(PropertyStatus status);
    
    List<PropertyEntity> findAllByLandlordId(UUID landlordId);
    
    @Query("SELECT p FROM PropertyEntity p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:landlordId IS NULL OR p.landlordId = :landlordId)")
    List<PropertyEntity> findAllWithFilters(
            @Param("status") PropertyStatus status,
            @Param("landlordId") UUID landlordId
    );
}
