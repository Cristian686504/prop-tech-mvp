// Presentational Component - Single Responsibility Principle
import React from 'react';
import './PropertyCard.css';

function PropertyCard({ property }) {
  const formatPrice = (price) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  const getImageUrl = (imageUrls) => {
    if (!imageUrls || imageUrls.length === 0) {
      return '/placeholder-property.jpg'; // Fallback image
    }
    return imageUrls[0];
  };

  return (
    <div className="property-card">
      <div className="property-image">
        <img 
          src={getImageUrl(property.imageUrls)} 
          alt={property.title}
          onError={(e) => {
            e.target.src = '/placeholder-property.jpg';
          }}
        />
        <span className="property-status">{property.status}</span>
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
        <div className="property-footer">
          <span className="property-price">{formatPrice(property.price)}</span>
          <button className="property-button">Ver detalles</button>
        </div>
      </div>
    </div>
  );
}

export default PropertyCard;
