package co.com.proptech.jpa.adapter;

import co.com.proptech.jpa.entity.PropertyEntity;
import co.com.proptech.jpa.repository.PropertyJpaRepository;
import co.com.proptech.model.property.Property;
import co.com.proptech.model.property.PropertyStatus;
import co.com.proptech.model.property.gateways.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PropertyRepositoryAdapter implements PropertyRepository {

    private final PropertyJpaRepository jpaRepository;

    @Override
    public Property save(Property property) {
        PropertyEntity entity = toEntity(property);
        PropertyEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Property> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Property> findAll(PropertyStatus status, UUID landlordId) {
        return jpaRepository.findAllWithFilters(status, landlordId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Property> findAllAvailable() {
        return jpaRepository.findAllByStatus(PropertyStatus.AVAILABLE)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Property> findByLandlordId(UUID landlordId) {
        return jpaRepository.findAllByLandlordId(landlordId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    // Mappers: Entity <-> Domain
    private PropertyEntity toEntity(Property property) {
        return PropertyEntity.builder()
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

    private Property toDomain(PropertyEntity entity) {
        return Property.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .address(entity.getAddress())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .landlordId(entity.getLandlordId())
                .imageUrls(entity.getImageUrls())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
