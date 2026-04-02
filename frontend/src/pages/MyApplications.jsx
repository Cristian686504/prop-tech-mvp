import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { getMyApplications } from '../services/applicationService';
import propertyService from '../services/propertyService';
import Navbar from '../components/Navbar';
import './MyApplications.css';

function MyApplications() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [applications, setApplications] = useState([]);
  const [properties, setProperties] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (user?.role !== 'TENANT') {
      navigate('/properties');
      return;
    }
    loadApplications();
  }, [user, navigate]);

  const loadApplications = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await getMyApplications();
      setApplications(data);

      // Load property details for each application
      const propertyPromises = data.map(app => 
        propertyService.getPropertyById(app.propertyId).catch(() => null)
      );
      const propertyData = await Promise.all(propertyPromises);
      
      const propertyMap = {};
      propertyData.forEach((prop, index) => {
        if (prop) {
          propertyMap[data[index].propertyId] = prop;
        }
      });
      setProperties(propertyMap);
    } catch (err) {
      setError('Error al cargar tus solicitudes');
      console.error('Error loading applications:', err);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    const statusConfig = {
      PENDING: { label: 'Pendiente', className: 'status-pending' },
      APPROVED: { label: 'Aprobada', className: 'status-approved' },
      REJECTED: { label: 'Rechazada', className: 'status-rejected' }
    };
    const config = statusConfig[status] || statusConfig.PENDING;
    return <span className={`status-badge ${config.className}`}>{config.label}</span>;
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
    }).format(price);
  };

  return (
    <>
      <Navbar />
      <div className="my-applications-container">
        <div className="applications-header">
          <h1>Mis Solicitudes</h1>
          <p>Seguimiento de tus aplicaciones de alquiler</p>
        </div>

        {loading && (
          <div className="loading-container">
            <div className="spinner"></div>
            <p>Cargando solicitudes...</p>
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
          </div>
        )}

        {!loading && !error && applications.length === 0 && (
          <div className="empty-state">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
            </svg>
            <h2>No tienes solicitudes</h2>
            <p>Explora propiedades y aplica para empezar tu búsqueda</p>
            <button 
              id="my-applications-btn-browse"
              className="browse-button"
              onClick={() => navigate('/properties')}
            >
              Ver Propiedades
            </button>
          </div>
        )}

        {!loading && !error && applications.length > 0 && (
          <div className="applications-list">
            {applications.map((application) => {
              const property = properties[application.propertyId];
              return (
                <div key={application.id} className="application-card">
                  <div className="application-info">
                    <div className="application-header-row">
                      <h3>{property?.title || 'Propiedad'}</h3>
                      {getStatusBadge(application.status)}
                    </div>
                    
                    {property && (
                      <>
                        <p className="property-address">
                          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                            <circle cx="12" cy="10" r="3"/>
                          </svg>
                          {property.address}
                        </p>
                        <p className="property-price">{formatPrice(property.price)}</p>
                      </>
                    )}
                    
                    <div className="application-dates">
                      <p>
                        <strong>Aplicado:</strong> {formatDate(application.appliedAt)}
                      </p>
                      {application.evaluatedAt && (
                        <p>
                          <strong>Evaluado:</strong> {formatDate(application.evaluatedAt)}
                        </p>
                      )}
                    </div>
                  </div>
                  
                  {property?.imageUrls && property.imageUrls[0] && (
                    <div className="application-image">
                      <img 
                        src={property.imageUrls[0]} 
                        alt={property.title}
                        onError={(e) => {
                          e.target.src = '/placeholder-property.jpg';
                        }}
                      />
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>
    </>
  );
}

export default MyApplications;
