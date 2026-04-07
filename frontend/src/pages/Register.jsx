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
    countryCode: '+57',
    documentType: 'CC',
    documentId: '',
    role: 'TENANT',
  });

  const countryCodes = [
    { value: '+57', label: '+57 🇨🇴', country: 'Colombia' },
    { value: '+1', label: '+1 🇺🇸', country: 'USA/Canadá' },
    { value: '+52', label: '+52 🇲🇽', country: 'México' },
    { value: '+54', label: '+54 🇦🇷', country: 'Argentina' },
    { value: '+55', label: '+55 🇧🇷', country: 'Brasil' },
    { value: '+56', label: '+56 🇨🇱', country: 'Chile' },
    { value: '+51', label: '+51 🇵🇪', country: 'Perú' },
    { value: '+593', label: '+593 🇪🇨', country: 'Ecuador' },
    { value: '+58', label: '+58 🇻🇪', country: 'Venezuela' },
    { value: '+34', label: '+34 🇪🇸', country: 'España' },
  ];

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
      // Remove confirmPassword and combine countryCode with phone before sending to API
      // eslint-disable-next-line no-unused-vars
      const { confirmPassword, countryCode, phone, ...rest } = formData;
      const registerData = {
        ...rest,
        phone: `${countryCode}${phone}`,
      };
      
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
      <div className="register-card">
        <div className="register-logo">
          <svg width="32" height="32" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M24 4L6 16V42H18V30H30V42H42V16L24 4Z" fill="#2563EB" stroke="#2563EB" strokeWidth="2" strokeLinejoin="round"/>
            <circle cx="24" cy="23" r="3" fill="white"/>
          </svg>
          <span>PropTech Colombia</span>
        </div>

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
          {/* Role Selection */}
          <div className="form-group">
            <label>Tipo de Usuario *</label>
            <div className="role-options">
              <label className="role-option">
                <input
                  id="register-radio-tenant"
                  type="radio"
                  name="role"
                  value="TENANT"
                  checked={formData.role === 'TENANT'}
                  onChange={handleChange}
                  disabled={loading}
                />
                <span className="role-card">
                  <div className="role-icon tenant">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M21 21V7.5L12 3L3 7.5V21"/>
                      <path d="M3 21H21"/>
                      <path d="M9 21V15H15V21"/>
                    </svg>
                  </div>
                  <strong>Arrendatario</strong>
                  <small>Busco propiedades</small>
                </span>
              </label>

              <label className="role-option">
                <input
                  id="register-radio-landlord"
                  type="radio"
                  name="role"
                  value="LANDLORD"
                  checked={formData.role === 'LANDLORD'}
                  onChange={handleChange}
                  disabled={loading}
                />
                <span className="role-card">
                  <div className="role-icon landlord">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M15 7C15 8.65685 13.6569 10 12 10C10.3431 10 9 8.65685 9 7V6C9 4.34315 10.3431 3 12 3C13.6569 3 15 4.34315 15 6V7Z"/>
                      <path d="M12 10V14"/>
                      <rect x="7" y="14" width="10" height="7" rx="1"/>
                      <circle cx="12" cy="17" r="1" fill="currentColor"/>
                    </svg>
                  </div>
                  <strong>Arrendador</strong>
                  <small>Publico propiedades</small>
                </span>
              </label>
            </div>
          </div>

          {/* Name */}
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

          {/* Email */}
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

          {/* Passwords */}
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

          {/* Phone */}
          <div className="form-group">
            <label htmlFor="phone">Teléfono *</label>
            <div className="phone-input-group">
              <select
                id="register-select-country-code"
                name="countryCode"
                value={formData.countryCode}
                onChange={handleChange}
                disabled={loading}
                className="country-code-select"
              >
                {countryCodes.map(code => (
                  <option key={code.value} value={code.value}>
                    {code.label}
                  </option>
                ))}
              </select>
              <input
                type="tel"
                id="register-input-phone"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
                onBlur={() => handleBlur('phone')}
                placeholder="3001234567"
                required
                disabled={loading}
                className={getFieldError('phone') ? 'input-error' : ''}
              />
            </div>
            {getFieldError('phone') && <span className="field-error">{getFieldError('phone')}</span>}
          </div>

          {/* Document */}
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="documentType">Tipo de Documento *</label>
              <select
                id="register-select-document-type"
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
                id="register-input-document-id"
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

          <button
            id="register-btn-submit"
            type="submit"
            className="submit-button"
            disabled={loading}
          >
            {loading ? 'Registrando...' : 'Crear Cuenta'}
          </button>
        </form>

        <div className="register-footer">
          <p>
            ¿Ya tienes cuenta? <Link to="/login" className="login-link">Inicia sesión aquí</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
