import { Link } from 'react-router-dom';
import './Home.css';

export default function Home() {
  return (
    <div className="home-container">
      {/* Header */}
      <header className="home-header">
        <div className="header-content">
          <Link to="/" className="logo-link">
            <svg width="32" height="32" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M24 4L6 16V42H18V30H30V42H42V16L24 4Z" fill="#2563EB" stroke="#2563EB" strokeWidth="2" strokeLinejoin="round"/>
              <circle cx="24" cy="23" r="3" fill="white"/>
            </svg>
            <span>PropTech Colombia</span>
          </Link>
          <div className="header-actions">
            <Link to="/login" className="btn-secondary">Iniciar Sesión</Link>
            <Link to="/register" className="btn-primary">Registrarse</Link>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-content">
          <div className="hero-text">
            <h1>Arrendamiento Inteligente para Colombia</h1>
            <p>
              Acelera y protege el alquiler de propiedades con evaluación financiera 
              automatizada y decisiones en menos de 24 horas.
            </p>
            <div className="hero-actions">
              <Link to="/register?role=arrendatario" className="btn-primary btn-large">
                Buscar Propiedad
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M5 12h14M12 5l7 7-7 7"/>
                </svg>
              </Link>
              <Link to="/register?role=arrendador" className="btn-outline btn-large">
                Publicar Propiedad
              </Link>
            </div>
          </div>
          <div className="hero-image">
            <img src="https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=600&h=700&fit=crop" alt="Modern building" />
          </div>
        </div>
      </section>

      {/* Why PropTech Section */}
      <section className="why-section">
        <h2>¿Por qué PropTech Colombia?</h2>
        <p className="section-subtitle">
          Transformamos el proceso de arrendamiento con tecnología avanzada
        </p>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon blue">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12 6 12 12 16 14"/>
              </svg>
            </div>
            <h3>Rápido</h3>
            <p>
              Proceso de alquiler completado en menos de 24 horas gracias a la 
              automatización digital.
            </p>
          </div>
          <div className="feature-card">
            <div className="feature-icon green">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
              </svg>
            </div>
            <h3>Seguro</h3>
            <p>
              Evaluación crediticia automatizada que reduce el riesgo de 
              incumplimiento en un 10%.
            </p>
          </div>
          <div className="feature-card">
            <div className="feature-icon purple">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <line x1="12" y1="1" x2="12" y2="23"/>
                <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
              </svg>
            </div>
            <h3>Rentable</h3>
            <p>
              Incrementa tus alquileres activos en un 20% con mejor experiencia 
              y exposición.
            </p>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section className="how-it-works">
        <h2>Cómo Funciona</h2>
        <div className="how-it-works-grid">
          <div className="how-column">
            <h3>Para Propietarios</h3>
            <div className="steps">
              <div className="step">
                <div className="step-number">1</div>
                <div className="step-content">
                  <h4>Publica tu propiedad</h4>
                  <p>Crea un perfil detallado con fotos y características</p>
                </div>
              </div>
              <div className="step">
                <div className="step-number">2</div>
                <div className="step-content">
                  <h4>Recibe solicitudes</h4>
                  <p>Los inquilinos aplican y son evaluados automáticamente</p>
                </div>
              </div>
              <div className="step">
                <div className="step-number">3</div>
                <div className="step-content">
                  <h4>Toma decisiones informadas</h4>
                  <p>Revisa el score crediticio y depósito calculado</p>
                </div>
              </div>
            </div>
          </div>
          <div className="how-column">
            <h3>Para Inquilinos</h3>
            <div className="steps">
              <div className="step">
                <div className="step-number">1</div>
                <div className="step-content">
                  <h4>Busca tu hogar ideal</h4>
                  <p>Explora propiedades disponibles en toda Colombia</p>
                </div>
              </div>
              <div className="step">
                <div className="step-number">2</div>
                <div className="step-content">
                  <h4>Aplica en segundos</h4>
                  <p>Completa la evaluación financiera automática</p>
                </div>
              </div>
              <div className="step">
                <div className="step-number">3</div>
                <div className="step-content">
                  <h4>Recibe tu contrato</h4>
                  <p>Descarga el contrato generado automáticamente</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="cta-section">
        <h2>Comienza Hoy</h2>
        <p>Únete a la plataforma que está transformando el arrendamiento en Colombia</p>
        <Link to="/register" className="btn-primary btn-large">Crear Cuenta Gratis</Link>
      </section>

      {/* Footer */}
      <footer className="home-footer">
        <div className="footer-content">
          <div className="footer-logo">
            <svg width="28" height="28" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M24 4L6 16V42H18V30H30V42H42V16L24 4Z" fill="#2563EB" stroke="#2563EB" strokeWidth="2" strokeLinejoin="round"/>
              <circle cx="24" cy="23" r="3" fill="white"/>
            </svg>
            <span>PropTech Colombia</span>
          </div>
          <p>© 2026 PropTech Colombia. Plataforma de arrendamiento inteligente.</p>
        </div>
      </footer>
    </div>
  );
}
