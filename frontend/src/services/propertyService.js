// Service Layer - Infrastructure (API calls)
const API_URL = 'http://localhost:8080/api';

const propertyService = {
  // Get all available properties
  async getProperties() {
    const response = await fetch(`${API_URL}/properties`, {
      credentials: 'include', // Include cookies for auth
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch properties');
    }
    
    return response.json();
  },

  // Get single property by ID
  async getPropertyById(id) {
    const response = await fetch(`${API_URL}/properties/${id}`, {
      credentials: 'include',
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch property');
    }
    
    return response.json();
  },

  // Publish new property (LANDLORD only)
  async publishProperty(propertyData) {
    const response = await fetch(`${API_URL}/properties`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(propertyData),
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Failed to publish property');
    }
    
    return response.json();
  },

  // Upload property image
  async uploadImage(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await fetch(`${API_URL}/properties/upload-image`, {
      method: 'POST',
      credentials: 'include',
      body: formData,
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Failed to upload image');
    }
    
    const data = await response.json();
    return data.url;
  },

  // Get applications for a property (LANDLORD only)
  async getPropertyApplications(propertyId) {
    const response = await fetch(`${API_URL}/applications/property/${propertyId}`, {
      credentials: 'include',
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch property applications');
    }
    
    return response.json();
  },
};

export default propertyService;
