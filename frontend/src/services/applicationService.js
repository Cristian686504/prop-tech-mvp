const API_URL = 'http://localhost:8080/api/applications';

/**
 * Apply for a property rental
 * @param {string} propertyId - UUID of the property
 * @returns {Promise<Object>} Application response
 */
export const applyForProperty = async (propertyId) => {
    const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({ propertyId }),
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Error al aplicar para la propiedad');
    }

    return response.json();
};

/**
 * Get my applications (TENANT only)
 * @returns {Promise<Array>} List of applications
 */
export const getMyApplications = async () => {
    const response = await fetch(`${API_URL}/my-applications`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
    });

    if (!response.ok) {
        throw new Error('Error al obtener las aplicaciones');
    }

    return response.json();
};

/**
 * Get applications for a property (LANDLORD only)
 * @param {string} propertyId - UUID of the property
 * @returns {Promise<Array>} List of applications
 */
export const getPropertyApplications = async (propertyId) => {
    const response = await fetch(`${API_URL}/property/${propertyId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
    });

    if (!response.ok) {
        throw new Error('Error al obtener las aplicaciones de la propiedad');
    }

    return response.json();
};
