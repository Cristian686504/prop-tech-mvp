// Container Component - Orchestrates data fetching and presentation
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import propertyService from '../services/propertyService';
import { applyForProperty, getMyApplications } from '../services/applicationService';
import PropertyCard from '../components/PropertyCard';
import Navbar from '../components/Navbar';
import './PropertyList.css';

function PropertyList() {
  const { user } = useAuth();
  const [properties, setProperties] = useState([]);
  const [applications, setApplications] = useState([]);
  const [applicationsWithProperties, setApplicationsWithProperties] = useState([]);
  const [propertyApplications, setPropertyApplications] = useState([]); // For landlord
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [activeTab, setActiveTab] = useState('properties');

  useEffect(() => {
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user]);

  const loadData = async () => {
    await loadProperties();
    if (user?.role === 'TENANT') {
      await loadApplications();
    } else if (user?.role === 'LANDLORD') {
      await loadLandlordApplications();
    }
  };

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

  const loadApplications = async () => {
    try {
      const data = await getMyApplications();
      setApplications(data);

      // Load property details for each application
      const enrichedApplications = await Promise.all(
        data.map(async (app) => {
          try {
            const property = await propertyService.getPropertyById(app.propertyId);
            return { ...app, property };
          } catch (error) {
            console.error('Error loading property:', error);
            return app;
          }
        })
      );
      
      setApplicationsWithProperties(enrichedApplications);
    } catch (err) {
      console.error('Error loading applications:', err);
    }
  };

  const loadLandlordApplications = async () => {
    try {
      // Get all properties owned by landlord
      const allProperties = await propertyService.getProperties();
      const myProperties = allProperties.filter(p => p.landlordId === user.id);
      
      // Get applications for each property
      const allApplications = [];
      for (const property of myProperties) {
        try {
          const apps = await propertyService.getPropertyApplications(property.id);
          // Add property info to each application
          const appsWithProperty = apps.map(app => ({ ...app, property }));
          allApplications.push(...appsWithProperty);
        } catch (error) {
          console.error(`Error loading applications for property ${property.id}:`, error);
        }
      }
      
      setPropertyApplications(allApplications);
    } catch (err) {
      console.error('Error loading landlord applications:', err);
    }
  };

  const handleApply = async (propertyId) => {
    try {
      setError('');
      setSuccessMessage('');
      await applyForProperty(propertyId);
      setSuccessMessage('¡Aplicación enviada exitosamente!');
      await loadApplications(); // Reload stats
      
      setTimeout(() => {
        setSuccessMessage('');
      }, 5000);
    } catch (err) {
      setError(err.message || 'Error al aplicar para la propiedad');
      
      setTimeout(() => {
        setError('');
      }, 5000);
      
      console.error('Error applying for property:', err);
    }
  };

  const stats = {
    total: applications.length,
    pending: applications.filter(app => app.status === 'PENDING').length,
    approved: applications.filter(app => app.status === 'APPROVED').length
  };

  const landlordStats = {
    totalProperties: properties.filter(p => p.landlordId === user?.id).length,
    availableProperties: properties.filter(p => p.landlordId === user?.id && p.status === 'AVAILABLE').length,
    totalApplications: propertyApplications.length,
    pendingApplications: propertyApplications.filter(app => app.status === 'PENDING').length
  };

  // Get status label in Spanish
  const getStatusLabel = (status) => {
    if (status === 'PENDING') return 'Pendiente';
    if (status === 'APPROVED') return 'Aprobada';
    return 'Rechazada';
  };

  return (
    <>
      <Navbar />
      <div className="property-list-container">
        {/* Dashboard Header for Tenants */}
        {user?.role === 'TENANT' ? (
          <>
            <div className="dashboard-header">
              <div>
                <h1>Dashboard Arrendatario</h1>
                <p className="user-name">{user.email || user.name}</p>
              </div>
            </div>

            {/* Statistics Cards */}
            <div className="stats-grid">
              <div className="stat-card">
                <div className="stat-icon blue">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                  </svg>
                </div>
                <div className="stat-content">
                  <p className="stat-label">Solicitudes Totales</p>
                  <p className="stat-value">{stats.total}</p>
                </div>
              </div>

              <div className="stat-card">
                <div className="stat-icon orange">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12 6 12 12 16 14"/>
                  </svg>
                </div>
                <div className="stat-content">
                  <p className="stat-label">Pendientes</p>
                  <p className="stat-value">{stats.pending}</p>
                </div>
              </div>

              <div className="stat-card">
                <div className="stat-icon green">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
                    <polyline points="22 4 12 14.01 9 11.01"/>
                  </svg>
                </div>
                <div className="stat-content">
                  <p className="stat-label">Aprobadas</p>
                  <p className="stat-value">{stats.approved}</p>
                </div>
              </div>
            </div>

            {/* Tabs */}
            <div className="tabs-container">
              <button 
                className={`tab ${activeTab === 'properties' ? 'active' : ''}`}
                onClick={() => setActiveTab('properties')}
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="11" cy="11" r="8"/>
                  <path d="m21 21-4.35-4.35"/>
                </svg>
                Buscar Propiedades
              </button>
              <button 
                className={`tab ${activeTab === 'applications' ? 'active' : ''}`}
                onClick={() => setActiveTab('applications')}
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                </svg>
                Mis Solicitudes
              </button>
            </div>
          </>
        ) : (
          user?.role === 'LANDLORD' ? (
            <>
              <div className="dashboard-header">
                <div>
                  <h1>Dashboard Arrendador</h1>
                  <p className="user-name">{user.email || user.name}</p>
                </div>
              </div>

              {/* Statistics Cards for Landlord */}
              <div className="stats-grid">
                <div className="stat-card">
                  <div className="stat-icon blue">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                      <polyline points="9 22 9 12 15 12 15 22"/>
                    </svg>
                  </div>
                  <div className="stat-content">
                    <p className="stat-label">Propiedades</p>
                    <p className="stat-value">{landlordStats.totalProperties}</p>
                  </div>
                </div>

                <div className="stat-card">
                  <div className="stat-icon green">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                      <path d="m9 12 2 2 4-4"/>
                    </svg>
                  </div>
                  <div className="stat-content">
                    <p className="stat-label">Disponibles</p>
                    <p className="stat-value">{landlordStats.availableProperties}</p>
                  </div>
                </div>

                <div className="stat-card">
                  <div className="stat-icon purple">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                      <circle cx="9" cy="7" r="4"/>
                      <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                      <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                    </svg>
                  </div>
                  <div className="stat-content">
                    <p className="stat-label">Solicitudes</p>
                    <p className="stat-value">{landlordStats.totalApplications}</p>
                  </div>
                </div>

                <div className="stat-card">
                  <div className="stat-icon orange">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <circle cx="12" cy="12" r="10"/>
                      <polyline points="12 6 12 12 16 14"/>
                    </svg>
                  </div>
                  <div className="stat-content">
                    <p className="stat-label">Pendientes</p>
                    <p className="stat-value">{landlordStats.pendingApplications}</p>
                  </div>
                </div>
              </div>

              {/* Tabs for Landlord */}
              <div className="tabs-container">
                <button 
                  className={`tab ${activeTab === 'properties' ? 'active' : ''}`}
                  onClick={() => setActiveTab('properties')}
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                    <polyline points="9 22 9 12 15 12 15 22"/>
                  </svg>
                  Mis Propiedades
                </button>
                <button 
                  className={`tab ${activeTab === 'applications' ? 'active' : ''}`}
                  onClick={() => setActiveTab('applications')}
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                  </svg>
                  Solicitudes
                </button>
              </div>
            </>
          ) : (
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
          )
        )}

      {successMessage && (
        <div className="success-message">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
            <polyline points="22 4 12 14.01 9 11.01"/>
          </svg>
          {successMessage}
        </div>
      )}

      {/* Show properties or applications based on active tab for tenants */}
      {user?.role === 'TENANT' && activeTab === 'applications' ? (
        <div className="applications-view">
          <h2 className="section-title">Mis Solicitudes</h2>
          {applicationsWithProperties.length === 0 ? (
            <div className="empty-state">
              <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
              </svg>
              <h2>No tienes solicitudes</h2>
              <p>Comienza aplicando a una propiedad</p>
            </div>
          ) : (
            <div className="applications-list">
              {applicationsWithProperties.map(app => (
                <div key={app.id} className="application-card">
                  <div className="application-header">
                    <div>
                      <h3 className="application-property-title">
                        {app.property?.title || 'Propiedad no encontrada'}
                      </h3>
                      <p className="application-property-address">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                          <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                          <circle cx="12" cy="10" r="3"/>
                        </svg>
                        {app.property?.address || 'Dirección no disponible'}
                      </p>
                    </div>
                    <span className={`status-badge-large status-${app.status.toLowerCase()}`}>
                      {getStatusLabel(app.status)}
                    </span>
                  </div>

                  <div className="application-details-grid">
                    <div className="application-detail">
                      <p className="detail-label">Ingresos Mensuales</p>
                      <p className="detail-value">
                        {user.monthlyIncome ? new Intl.NumberFormat('es-CO', {
                          style: 'currency',
                          currency: 'COP',
                          minimumFractionDigits: 0,
                        }).format(user.monthlyIncome) : 'No registrado'}
                      </p>
                    </div>

                    <div className="application-detail">
                      <p className="detail-label">Score Crediticio</p>
                      <p className="detail-value">{user.creditScore || 'No registrado'}</p>
                    </div>

                    <div className="application-detail">
                      <p className="detail-label">Precio Mensual</p>
                      <p className="detail-value">
                        {app.property?.price ? new Intl.NumberFormat('es-CO', {
                          style: 'currency',
                          currency: 'COP',
                          minimumFractionDigits: 0,
                        }).format(app.property.price) : 'No disponible'}
                      </p>
                    </div>

                    <div className="application-detail">
                      <p className="detail-label">Fecha de Solicitud</p>
                      <p className="detail-value">
                        {app.appliedAt ? new Date(app.appliedAt).toLocaleDateString('es-ES', {
                          day: 'numeric',
                          month: 'numeric',
                          year: 'numeric'
                        }) : 'No disponible'}
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      ) : user?.role === 'LANDLORD' && activeTab === 'applications' ? (
        <div className="applications-view">
          <h2 className="section-title">Solicitudes Recibidas</h2>
          {propertyApplications.length === 0 ? (
            <div className="empty-state">
              <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
              </svg>
              <h2>No hay solicitudes</h2>
              <p>Aún no has recibido solicitudes para tus propiedades</p>
            </div>
          ) : (
            <div className="applications-list">
              {propertyApplications.map(app => (
                <div key={app.id} className="application-card">
                  <div className="application-header">
                    <div>
                      <h3 className="application-property-title">
                        {app.property?.title || 'Propiedad no encontrada'}
                      </h3>
                      <p className="application-property-address">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                          <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                          <circle cx="12" cy="10" r="3"/>
                        </svg>
                        {app.property?.address || 'Dirección no disponible'}
                      </p>
                    </div>
                    <span className={`status-badge-large status-${app.status.toLowerCase()}`}>
                      {getStatusLabel(app.status)}
                    </span>
                  </div>

                  <div className="application-details-grid">
                    <div className="application-detail">
                      <p className="detail-label">Solicitante</p>
                      <p className="detail-value">{app.tenantName || app.tenantEmail || 'N/A'}</p>
                    </div>

                    <div className="application-detail">
                      <p className="detail-label">Precio Mensual</p>
                      <p className="detail-value">
                        {app.property?.price ? new Intl.NumberFormat('es-CO', {
                          style: 'currency',
                          currency: 'COP',
                          minimumFractionDigits: 0,
                        }).format(app.property.price) : 'No disponible'}
                      </p>
                    </div>

                    <div className="application-detail">
                      <p className="detail-label">Ingresos Mensuales</p>
                      <p className="detail-value">
                        {app.monthlyIncome ? new Intl.NumberFormat('es-CO', {
                          style: 'currency',
                          currency: 'COP',
                          minimumFractionDigits: 0,
                        }).format(app.monthlyIncome) : 'No disponible'}
                      </p>
                    </div>

                    <div className="application-detail">
                      <p className="detail-label">Score Crediticio</p>
                      <p className="detail-value">{app.creditScore || 'No disponible'}</p>
                    </div>

                    <div className="application-detail">
                      <p className="detail-label">Depósito de Garantía</p>
                      <p className="detail-value">
                        {app.securityDeposit ? new Intl.NumberFormat('es-CO', {
                          style: 'currency',
                          currency: 'COP',
                          minimumFractionDigits: 0,
                        }).format(app.securityDeposit) : 'No calculado'}
                      </p>
                    </div>

                    <div className="application-detail">
                      <p className="detail-label">Fecha de Solicitud</p>
                      <p className="detail-value">
                        {app.appliedAt ? new Date(app.appliedAt).toLocaleDateString('es-ES', {
                          day: 'numeric',
                          month: 'numeric',
                          year: 'numeric'
                        }) : 'No disponible'}
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      ) : (
        <>
          {user?.role === 'TENANT' && <h2 className="section-title">Propiedades Disponibles</h2>}
          
          {user?.role === 'LANDLORD' && (
            <div className="section-header">
              <h2 className="section-title">Propiedades Publicadas</h2>
              <Link to="/publish-property" className="add-property-button">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <line x1="12" y1="5" x2="12" y2="19"/>
                  <line x1="5" y1="12" x2="19" y2="12"/>
                </svg>
                Agregar Propiedad
              </Link>
            </div>
          )}

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
              {(user?.role === 'LANDLORD' 
                ? properties.filter(p => p.landlordId === user.id)
                : properties
              ).map((property) => (
                <PropertyCard 
                  key={property.id} 
                  property={property}
                  userRole={user?.role}
                  onApply={handleApply}
                />
              ))}
            </div>
          )}
        </>
      )}
    </div>
    </>
  );
}

export default PropertyList;
