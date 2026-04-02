// Container Component - Orchestrates property publishing with image upload
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import propertyService from '../services/propertyService';
import Navbar from '../components/Navbar';
import './PublishProperty.css';

function PublishProperty() {
  const { user } = useAuth();
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    address: '',
    price: '',
  });
  
  const [images, setImages] = useState([]);
  const [imageUrls, setImageUrls] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [touchedFields, setTouchedFields] = useState({});

  // Redirect if not LANDLORD
  React.useEffect(() => {
    if (user && user.role !== 'LANDLORD') {
      navigate('/properties');
    }
  }, [user, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleBlur = (field) => {
    setTouchedFields(prev => ({ ...prev, [field]: true }));
  };

  const getFieldError = (fieldName) => {
    if (!touchedFields[fieldName]) return '';
    
    switch (fieldName) {
      case 'title':
        return !formData.title.trim() ? 'El título es requerido' : '';
      case 'description':
        return !formData.description.trim() ? 'La descripción es requerida' : '';
      case 'address':
        return !formData.address.trim() ? 'La dirección es requerida' : '';
      case 'price':
        return !formData.price || formData.price <= 0 ? 'El precio debe ser mayor a 0' : '';
      default:
        return '';
    }
  };

  const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB per file
  const MAX_TOTAL_SIZE = 25 * 1024 * 1024; // 25MB total

  const handleImageSelect = async (e) => {
    const files = Array.from(e.target.files);
    
    // Validate file types and individual sizes
    const validFiles = files.filter(file => {
      if (!file.type.startsWith('image/')) {
        setError('Solo se permiten archivos de imagen');
        return false;
      }
      if (file.size > MAX_FILE_SIZE) {
        setError('Cada imagen debe ser menor a 5MB');
        return false;
      }
      return true;
    });

    if (validFiles.length === 0) return;

    // Validate total size (existing + new files)
    const currentTotalSize = images.reduce((sum, img) => sum + img.size, 0);
    const newFilesSize = validFiles.reduce((sum, file) => sum + file.size, 0);
    if (currentTotalSize + newFilesSize > MAX_TOTAL_SIZE) {
      setError('El tamaño total de las imágenes no puede superar 25MB');
      return;
    }
    
    setUploading(true);
    setError('');
    
    try {
      // Upload all images in a single request
      const urls = await propertyService.uploadImages(validFiles);
      setImageUrls(prev => [...prev, ...urls]);
      setImages(prev => [...prev, ...validFiles]);
      
    } catch (err) {
      setError(err.message || 'Error al subir imágenes. Intenta de nuevo.');
      console.error('Error uploading images:', err);
    } finally {
      setUploading(false);
    }
  };

  const removeImage = (index) => {
    setImages(prev => prev.filter((_, i) => i !== index));
    setImageUrls(prev => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate all fields
    const errors = {
      title: getFieldError('title'),
      description: getFieldError('description'),
      address: getFieldError('address'),
      price: getFieldError('price'),
    };
    
    setTouchedFields({
      title: true,
      description: true,
      address: true,
      price: true,
    });
    
    if (Object.values(errors).some(err => err)) {
      setError('Por favor completa todos los campos correctamente');
      return;
    }
    
    if (imageUrls.length === 0) {
      setError('Debes subir al menos una imagen');
      return;
    }
    
    setLoading(true);
    setError('');
    
    try {
      await propertyService.publishProperty({
        ...formData,
        price: parseFloat(formData.price),
        imageUrls,
      });
      
      navigate('/properties');
    } catch (err) {
      setError(err.message || 'Error al publicar propiedad');
      console.error('Error publishing property:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Navbar />
      <div className="publish-container">
        <div className="publish-content">
        <div className="publish-header">
          <button onClick={() => navigate('/properties')} className="back-button">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path d="M19 12H5M12 19l-7-7 7-7"/>
            </svg>
            Volver
          </button>
          <h1>Publicar Propiedad</h1>
          <p>Completa la información de tu propiedad</p>
        </div>

        <form onSubmit={handleSubmit} className="publish-form">
          {error && (
            <div className="error-banner">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              {error}
            </div>
          )}

          <div className="form-group">
            <label htmlFor="title">Título *</label>
            <input
              type="text"
              id="title"
              name="title"
              value={formData.title}
              onChange={handleChange}
              onBlur={() => handleBlur('title')}
              placeholder="Ej: Apartamento moderno en Chapinero"
              maxLength={255}
            />
            {touchedFields.title && getFieldError('title') && (
              <span className="field-error">{getFieldError('title')}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="description">Descripción *</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              onBlur={() => handleBlur('description')}
              placeholder="Describe tu propiedad (características, comodidades, etc.)"
              rows={5}
              maxLength={2000}
            />
            <span className="char-count">{formData.description.length}/2000</span>
            {touchedFields.description && getFieldError('description') && (
              <span className="field-error">{getFieldError('description')}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="address">Dirección *</label>
            <input
              type="text"
              id="address"
              name="address"
              value={formData.address}
              onChange={handleChange}
              onBlur={() => handleBlur('address')}
              placeholder="Ej: Calle 72 # 10-34, Bogotá"
              maxLength={255}
            />
            {touchedFields.address && getFieldError('address') && (
              <span className="field-error">{getFieldError('address')}</span>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="price">Precio Mensual (COP) *</label>
            <input
              type="number"
              id="price"
              name="price"
              value={formData.price}
              onChange={handleChange}
              onBlur={() => handleBlur('price')}
              placeholder="Ej: 1500000"
              min="0"
              step="10000"
            />
            {touchedFields.price && getFieldError('price') && (
              <span className="field-error">{getFieldError('price')}</span>
            )}
          </div>

          <div className="form-group">
            <label>Imágenes *</label>
            <div className="image-upload-area">
              <input
                type="file"
                id="images"
                multiple
                accept="image/*"
                onChange={handleImageSelect}
                disabled={uploading}
                style={{ display: 'none' }}
              />
              <label htmlFor="images" className="upload-button">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                  <circle cx="8.5" cy="8.5" r="1.5"/>
                  <polyline points="21 15 16 10 5 21"/>
                </svg>
                {uploading ? 'Subiendo...' : 'Seleccionar Imágenes'}
              </label>
              <span className="upload-hint">Max 5MB por imagen • PNG, JPG</span>
            </div>

            {images.length > 0 && (
              <div className="image-preview-grid">
                {images.map((image, index) => (
                  <div key={index} className="image-preview">
                    <img src={URL.createObjectURL(image)} alt={`Preview ${index + 1}`} />
                    <button
                      type="button"
                      onClick={() => removeImage(index)}
                      className="remove-image-button"
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <line x1="18" y1="6" x2="6" y2="18"/>
                        <line x1="6" y1="6" x2="18" y2="18"/>
                      </svg>
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          <button 
            type="submit" 
            className="submit-button"
            disabled={loading || uploading}
          >
            {loading ? (
              <>
                <div className="spinner-small"></div>
                Publicando...
              </>
            ) : (
              'Publicar Propiedad'
            )}
          </button>
        </form>
      </div>
    </div>
    </>
  );
}

export default PublishProperty;
