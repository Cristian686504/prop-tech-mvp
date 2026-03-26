import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Register.css';

export default function Register() {
  const navigate = useNavigate();
  const { register } = useAuth();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [touchedFields, setTouchedFields] = useState({});

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
    documentType: 'CC',
    documentId: '',
    role: 'TENANT',
  });

  const documentTypes = [
    { value: 'CC', label: 'Cédula de Ciudadanía' },
    { value: 'CE', label: 'Cédula de Extranjería' },
    { value: 'NIT', label: 'NIT' },
    { value: 'PP', label: 'Pasaporte' },
    { value: 'TI', label: 'Tarjeta de Identidad' },
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    setError(''); // Clear error on change
  };

  const handleBlur = (fieldName) => {
    setTouchedFields(prev => ({ ...prev, [fieldName]: true }));
  };

  const getFieldError = (fieldName) => {
    if (!touchedFields[fieldName]) return '';
    
    switch (fieldName) {
      case 'name':
        return !formData.name.trim() ? 'El nombre es requerido' : '';
      case 'email':
        return !formData.email.includes('@') ? 'Email inválido' : '';
      case 'password':
        return formData.password.length < 8 ? 'Mínimo 8 caracteres' : '';
      case 'confirmPassword':
        return formData.password !== formData.confirmPassword ? 'Las contraseñas no coinciden' : '';
      case 'phone':
        return !formData.phone.trim() ? 'El teléfono es requerido' : '';
      case 'documentId':
        return !formData.documentId.trim() ? 'El documento es requerido' : '';
      default:
        return '';
    }
  };

  const validateForm = () => {
    if (!formData.name.trim()) {
      setError('El nombre es requerido');
      return false;
    }

    if (!formData.email.trim() || !formData.email.includes('@')) {
      setError('Email inválido');
      return false;
    }

    if (formData.password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres');
      return false;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('Las contraseñas no coinciden');
      return false;
    }

    if (!formData.phone.trim()) {
      setError('El teléfono es requerido');
      return false;
    }

    if (!formData.documentId.trim()) {
      setError('El número de documento es requerido');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      // Remove confirmPassword before sending to API
      const { confirmPassword, ...registerData } = formData;
      
      await register(registerData);
      navigate('/properties');
    } catch (err) {
      setError(err.message || 'Error al registrarse');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-container">
      {/* Left Side - Branding */}
      <div className="register-brand">
        <div className="brand-content">
          <div className="brand-logo">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M24 4L6 16V42H18V30H30V42H42V16L24 4Z" fill="white" stroke="white" strokeWidth="2" strokeLinejoin="round"/>
              <circle cx="24" cy="23" r="3" fill="#0f4c81"/>
            </svg>
            <h2>PropTech</h2>
          </div>
          
          <h1 className="brand-title">Tu hogar ideal te está esperando</h1>
          <p className="brand-subtitle">
            Conectamos arrendadores e inquilinos de manera segura y transparente
          </p>
          
          <div className="brand-features">
            <div className="feature-item">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
              <span>Evaluación financiera automatizada</span>
            </div>
            <div className="feature-item">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 15V17M6 21H18C19.1046 21 20 20.1046 20 19V13C20 11.8954 19.1046 11 18 11H6C4.89543 11 4 11.8954 4 13V19C4 20.1046 4.89543 21 6 21ZM16 11V7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7V11H16Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              </svg>
              <span>Procesos seguros y confiables</span>
            </div>
            <div className="feature-item">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M13 10V3L4 14H11L11 21L20 10L13 10Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              </svg>
              <span>Generación de contratos instantánea</span>
            </div>
          </div>
        </div>
      </div>

      {/* Right Side - Form */}
      <div className="register-form-wrapper">
        <div className="register-card">
          <div className="register-header">
            <h1>Crear Cuenta</h1>
            <p>Comienza tu búsqueda o publicación de propiedades</p>
          </div>

        {error && (
          <div className="error-message" role="alert">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="register-form">
          <div className="form-group">
            <label htmlFor="name">Nombre Completo *</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              onBlur={() => handleBlur('name')}
              placeholder="Juan Pérez"
              required
              disabled={loading}
              className={getFieldError('name') ? 'input-error' : ''}
            />
            {getFieldError('name') && <span className="field-error">{getFieldError('name')}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="email">Email *</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              onBlur={() => handleBlur('email')}
              placeholder="juan@example.com"
              required
              disabled={loading}
              className={getFieldError('email') ? 'input-error' : ''}
            />
            {getFieldError('email') && <span className="field-error">{getFieldError('email')}</span>}
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="password">Contraseña *</label>
              <input
                type="password"
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                onBlur={() => handleBlur('password')}
                placeholder="Mínimo 8 caracteres"
                required
                disabled={loading}
                minLength={8}
                className={getFieldError('password') ? 'input-error' : ''}
              />
              {getFieldError('password') && <span className="field-error">{getFieldError('password')}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">Confirmar Contraseña *</label>
              <input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                onBlur={() => handleBlur('confirmPassword')}
                placeholder="Repite la contraseña"
                required
                disabled={loading}
                minLength={8}
                className={getFieldError('confirmPassword') ? 'input-error' : ''}
              />
              {getFieldError('confirmPassword') && <span className="field-error">{getFieldError('confirmPassword')}</span>}
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="phone">Teléfono *</label>
            <input
              type="tel"
              id="phone"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
              onBlur={() => handleBlur('phone')}
              placeholder="+573001234567"
              required
              disabled={loading}
              className={getFieldError('phone') ? 'input-error' : ''}
            />
            {getFieldError('phone') && <span className="field-error">{getFieldError('phone')}</span>}
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="documentType">Tipo de Documento *</label>
              <select
                id="documentType"
                name="documentType"
                value={formData.documentType}
                onChange={handleChange}
                required
                disabled={loading}
              >
                {documentTypes.map(doc => (
                  <option key={doc.value} value={doc.value}>
                    {doc.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="documentId">Número de Documento *</label>
              <input
                type="text"
                id="documentId"
                name="documentId"
                value={formData.documentId}
                onChange={handleChange}
                onBlur={() => handleBlur('documentId')}
                placeholder="1234567890"
                required
                disabled={loading}
                className={getFieldError('documentId') ? 'input-error' : ''}
              />
              {getFieldError('documentId') && <span className="field-error">{getFieldError('documentId')}</span>}
            </div>
          </div>

          <div className="form-group">
            <label>Tipo de Usuario *</label>
            <div className="role-options">
              <label className="role-option">
                <input
                  type="radio"
                  name="role"
                  value="TENANT"
                  checked={formData.role === 'TENANT'}
                  onChange={handleChange}
                  disabled={loading}
                />
                <span className="role-card">
                  <div className="role-icon tenant">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M21 21V7.5L12 3L3 7.5V21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      <path d="M3 21H21" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                      <path d="M9 21V15H15V21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                  </div>
                  <strong>Arrendatario</strong>
                  <small>Busco propiedades para arrendar</small>
                </span>
              </label>

              <label className="role-option">
                <input
                  type="radio"
                  name="role"
                  value="LANDLORD"
                  checked={formData.role === 'LANDLORD'}
                  onChange={handleChange}
                  disabled={loading}
                />
                <span className="role-card">
                  <div className="role-icon landlord">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M15 7C15 8.65685 13.6569 10 12 10C10.3431 10 9 8.65685 9 7V6C9 4.34315 10.3431 3 12 3C13.6569 3 15 4.34315 15 6V7Z" stroke="currentColor" strokeWidth="2"/>
                      <path d="M12 10V14" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                      <rect x="7" y="14" width="10" height="7" rx="1" stroke="currentColor" strokeWidth="2"/>
                      <circle cx="12" cy="17" r="1" fill="currentColor"/>
                    </svg>
                  </div>
                  <strong>Arrendador</strong>
                  <small>Quiero publicar propiedades</small>
                </span>
              </label>
            </div>
          </div>

          <button
            type="submit"
            className="submit-button"
            disabled={loading}
          >
            {loading ? (
              <>
                <svg className="spinner" width="20" height="20" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="3" fill="none" opacity="0.3"/>
                  <path d="M12 2 A10 10 0 0 1 22 12" stroke="currentColor" strokeWidth="3" fill="none" strokeLinecap="round"/>
                </svg>
                Registrando...
              </>
            ) : 'Crear Cuenta'}
          </button>
        </form>

          <div className="register-footer">
            <p>
              ¿Ya tienes cuenta?{' '}
              <Link to="/login" className="login-link">
                Inicia sesión aquí
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
