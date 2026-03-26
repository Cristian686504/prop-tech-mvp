// API client with httpOnly cookie authentication

const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api';

/**
 * Fetch wrapper with automatic cookie credentials
 * JWT is stored in httpOnly cookie by backend
 */
export async function apiFetch(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  const config = {
    ...options,
    headers,
    credentials: 'include', // Include cookies in cross-origin requests
  };

  const response = await fetch(`${API_BASE}${path}`, config);

  // Handle errors
  if (!response.ok) {
    const error = await response.json().catch(() => ({
      message: `HTTP ${response.status}: ${response.statusText}`
    }));
    throw new Error(error.message || 'Request failed');
  }

  // Handle empty responses (204 No Content)
  if (response.status === 204) {
    return null;
  }

  return response.json();
}

/**
 * HTTP methods helpers
 */
export const api = {
  get: (path, options = {}) => 
    apiFetch(path, { ...options, method: 'GET' }),

  post: (path, data, options = {}) =>
    apiFetch(path, {
      ...options,
      method: 'POST',
      body: JSON.stringify(data),
    }),

  put: (path, data, options = {}) =>
    apiFetch(path, {
      ...options,
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  delete: (path, options = {}) =>
    apiFetch(path, { ...options, method: 'DELETE' }),
};
