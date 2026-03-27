// Presentational Component - Single Responsibility Principle
import React from 'react';
import './PropertyCard.css';

function PropertyCard({ property, userRole, onApply }) {
  const formatPrice = (price) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const getImageUrl = (imageUrls) => {
    if (!imageUrls || imageUrls.length === 0) {
      return '/placeholder-property.jpg';
    }
    return imageUrls[0];
  };

  const isAvailable = property.status === 'AVAILABLE';
  const isTenant = userRole === 'TENANT';

  return (
    <div className="property-card">
      <div className="property-image">
        <img 
          src={getImageUrl(property.imageUrls)} 
          alt={property.title}
          onError={(e) => {
            if (!e.target.src.includes('placeholder')) {
              e.target.src = '/placeholder-property.jpg';
            } else {
              e.target.style.display = 'none';
            }
          }}
        />
        {isAvailable && <span className="property-status-badge">Disponible</span>}
      </div>
      
      <div className="property-content">
        <h3 className="property-title">{property.title}</h3>
        <p className="property-address">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
            <circle cx="12" cy="10" r="3"/>
          </svg>
          {property.address}
        </p>
        <p className="property-description">{property.description}</p>
        
        <div className="property-details">
          <div className="property-detail-item">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
            <span>{property.bedrooms || 2}</span>
          </div>
          <div className="property-detail-item">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="3"/>
              <path d="M12 1v6m0 6v6m5.2-13.6l-4.2 4.2m-2 2l-4.2 4.2M23 12h-6m-6 0H5m13.6 5.2l-4.2-4.2m-2-2l-4.2-4.2"/>
            </svg>
            <span>{property.bathrooms || 2}</span>
          </div>
          <div className="property-detail-item">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
            </svg>
            <span>{property.area || 85}m²</span>
          </div>
        </div>

        <div className="property-footer">
          <div className="property-price-section">
            <p className="price-label">Precio Mensual</p>
            <p className="property-price">{formatPrice(property.price)}</p>
          </div>
          {isTenant && isAvailable && (
            <button 
              className="property-button-apply"
              onClick={() => onApply(property.id)}
            >
              Solicitar Arriendo
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default PropertyCard;
