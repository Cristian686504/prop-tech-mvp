import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { getPropertyApplications } from '../services/applicationService';
import propertyService from '../services/propertyService';
import Navbar from '../components/Navbar';
import './PropertyApplications.css';

function PropertyApplications() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [myProperties, setMyProperties] = useState([]);
  const [selectedProperty, setSelectedProperty] = useState(null);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingApplications, setLoadingApplications] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (user?.role !== 'LANDLORD') {
      navigate('/properties');
      return;
    }
    loadMyProperties();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user, navigate]);

  const loadMyProperties = async () => {
    try {
      setLoading(true);
      setError('');
      const allProperties = await propertyService.getProperties();
      // Filter only properties owned by current user
      const ownedProperties = allProperties.filter(prop => prop.landlordId === user.id);
      setMyProperties(ownedProperties);
      
      // If there's only one property, auto-select it
      if (ownedProperties.length === 1) {
        handlePropertySelect(ownedProperties[0]);
      }
    } catch (err) {
      setError('Error al cargar tus propiedades');
      console.error('Error loading properties:', err);
    } finally {
      setLoading(false);
    }
  };

  const handlePropertySelect = async (property) => {
    setSelectedProperty(property);
    try {
      setLoadingApplications(true);
      setError('');
      const data = await getPropertyApplications(property.id);
      setApplications(data);
    } catch (err) {
      setError('Error al cargar las solicitudes');
      console.error('Error loading applications:', err);
    } finally {
      setLoadingApplications(false);
    }
  };

  const getRiskBadge = (riskLevel) => {
    const riskConfig = {
      LOW: { label: 'Riesgo Bajo', className: 'risk-low' },
      MEDIUM: { label: 'Riesgo Medio', className: 'risk-medium' },
      HIGH: { label: 'Riesgo Alto', className: 'risk-high' }
    };
    const config = riskConfig[riskLevel] || riskConfig.HIGH;
    return <span className={`risk-badge ${config.className}`}>{config.label}</span>;
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

  const formatRatio = (ratio) => {
    return ratio ? `${ratio.toFixed(1)}x` : 'N/A';
  };

  return (
    <>
      <Navbar />
      <div className="property-applications-container">
        <div className="applications-header">
          <h1>Solicitudes de Alquiler</h1>
          <p>Revisa y evalúa las solicitudes de tus propiedades</p>
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
          </div>
        )}

        {!loading && !error && myProperties.length === 0 && (
          <div className="empty-state">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
            <h2>No tienes propiedades publicadas</h2>
            <p>Publica una propiedad para recibir solicitudes</p>
            <button 
              id="property-applications-btn-publish"
              className="publish-button"
              onClick={() => navigate('/publish')}
            >
              Publicar Propiedad
            </button>
          </div>
        )}

        {!loading && !error && myProperties.length > 0 && (
          <>
            <div className="property-selector">
              <h2>Mis Propiedades ({myProperties.length})</h2>
              <div className="property-list">
                {myProperties.map((property) => (
                  <button
                    key={property.id}
                    id={`property-applications-btn-select-${property.id}`}
                    className={`property-item ${selectedProperty?.id === property.id ? 'active' : ''}`}
                    onClick={() => handlePropertySelect(property)}
                  >
                    <div className="property-info">
                      <h3>{property.title}</h3>
                      <p className="property-address">{property.address}</p>
                      <p className="property-price">{formatPrice(property.price)}</p>
                    </div>
                    {selectedProperty?.id === property.id && (
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <polyline points="20 6 9 17 4 12"/>
                      </svg>
                    )}
                  </button>
                ))}
              </div>
            </div>

            {selectedProperty && (
              <div className="applications-section">
                <h2>
                  Solicitudes para: {selectedProperty.title}
                  {!loadingApplications && <span className="count">({applications.length})</span>}
                </h2>

                {loadingApplications && (
                  <div className="loading-container">
                    <div className="spinner"></div>
                    <p>Cargando solicitudes...</p>
                  </div>
                )}

                {!loadingApplications && applications.length === 0 && (
                  <div className="empty-applications">
                    <svg width="60" height="60" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                      <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                    </svg>
                    <p>No hay solicitudes para esta propiedad</p>
                  </div>
                )}

                {!loadingApplications && applications.length > 0 && (
                  <div className="applications-grid">
                    {applications.map((application) => (
                      <div key={application.id} className="application-card">
                        <div className="card-header">
                          <div className="tenant-info">
                            <div className="tenant-avatar">
                              {application.tenantName.charAt(0).toUpperCase()}
                            </div>
                            <div>
                              <h3>{application.tenantName}</h3>
                              <p className="contact-info">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                                  <polyline points="22,6 12,13 2,6"/>
                                </svg>
                                {application.tenantEmail}
                              </p>
                              {application.tenantPhone && (
                                <p className="contact-info">
                                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"/>
                                  </svg>
                                  {application.tenantPhone}
                                </p>
                              )}
                            </div>
                          </div>
                          <div className="badges">
                            {getRiskBadge(application.riskLevel)}
                            {getStatusBadge(application.status)}
                          </div>
                        </div>

                        <div className="financial-evaluation">
                          <h4>Evaluación Financiera</h4>
                          <div className="financial-grid">
                            <div className="financial-item">
                              <span className="label">Ingresos Mensuales</span>
                              <span className="value">
                                {application.monthlyIncome ? formatPrice(application.monthlyIncome) : 'N/A'}
                              </span>
                            </div>
                            <div className="financial-item">
                              <span className="label">Puntaje de Crédito</span>
                              <span className="value score">
                                {application.creditScore || 'N/A'}
                              </span>
                            </div>
                            <div className="financial-item">
                              <span className="label">Ratio Ingreso/Renta</span>
                              <span className="value ratio">
                                {formatRatio(application.incomeToRentRatio)}
                              </span>
                            </div>
                            <div className="financial-item highlight">
                              <span className="label">Depósito de Garantía</span>
                              <span className="value deposit">
                                {application.securityDeposit ? formatPrice(application.securityDeposit) : 'N/A'}
                              </span>
                            </div>
                          </div>
                        </div>

                        <div className="application-footer">
                          <p className="application-date">
                            Aplicó el {formatDate(application.appliedAt)}
                          </p>
                          <div className="action-buttons">
                            <button id={`property-applications-btn-approve-${application.id}`} className="btn-approve" disabled>
                              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <polyline points="20 6 9 17 4 12"/>
                              </svg>
                              Aprobar
                            </button>
                            <button id={`property-applications-btn-reject-${application.id}`} className="btn-reject" disabled>
                              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <line x1="18" y1="6" x2="6" y2="18"/>
                                <line x1="6" y1="6" x2="18" y2="18"/>
                              </svg>
                              Rechazar
                            </button>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            )}
          </>
        )}
      </div>
    </>
  );
}

export default PropertyApplications;
