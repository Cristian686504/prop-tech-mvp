// Container Component - Orchestrates data fetching and presentation
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import propertyService from '../services/propertyService';
import PropertyCard from '../components/PropertyCard';
import './PropertyList.css';

function PropertyList() {
  const { user } = useAuth();
  const [properties, setProperties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadProperties();
  }, []);

  const loadProperties = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await propertyService.getProperties();
      setProperties(data);
    } catch (err) {
      setError('Error al cargar propiedades. Intenta de nuevo.');
      console.error('Error loading properties:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="property-list-container">
      <div className="property-list-header">
        <div className="header-content">
          <h1>Propiedades Disponibles</h1>
          <p>Encuentra tu hogar ideal</p>
        </div>
        
        {user?.role === 'LANDLORD' && (
          <Link to="/publish-property" className="publish-button">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <line x1="12" y1="5" x2="12" y2="19"/>
              <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            Publicar Propiedad
          </Link>
        )}
      </div>

      {loading && (
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Cargando propiedades...</p>
        </div>
      )}

      {error && (
        <div className="error-message">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="8" x2="12" y2="12"/>
            <line x1="12" y1="16" x2="12.01" y2="16"/>
          </svg>
          {error}
          <button onClick={loadProperties} className="retry-button">
            Reintentar
          </button>
        </div>
      )}

      {!loading && !error && properties.length === 0 && (
        <div className="empty-state">
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
          <h2>No hay propiedades disponibles</h2>
          <p>Sé el primero en publicar una propiedad</p>
          {user?.role === 'LANDLORD' && (
            <Link to="/publish-property" className="publish-button-empty">
              Publicar Propiedad
            </Link>
          )}
        </div>
      )}

      {!loading && !error && properties.length > 0 && (
        <div className="properties-grid">
          {properties.map((property) => (
            <PropertyCard key={property.id} property={property} />
          ))}
        </div>
      )}
    </div>
  );
}

export default PropertyList;
