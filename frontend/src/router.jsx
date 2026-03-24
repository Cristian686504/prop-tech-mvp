import { createBrowserRouter, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';

// Placeholder pages - to be implemented
function LoginPage() {
  return <div>Login Page - TODO</div>;
}

function RegisterPage() {
  return <div>Register Page - TODO</div>;
}

function PropertiesPage() {
  return <div>Properties Page - TODO</div>;
}

function PublishPropertyPage() {
  return <div>Publish Property Page - TODO</div>;
}

// Protected route wrapper
function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

// Router configuration
export const router = createBrowserRouter([
  {
    path: '/',
    element: <Navigate to="/properties" replace />,
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    path: '/properties',
    element: (
      <ProtectedRoute>
        <PropertiesPage />
      </ProtectedRoute>
    ),
  },
  {
    path: '/publish',
    element: (
      <ProtectedRoute>
        <PublishPropertyPage />
      </ProtectedRoute>
    ),
  },
]);
