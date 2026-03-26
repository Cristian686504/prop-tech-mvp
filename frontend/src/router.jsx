import { createBrowserRouter, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Register from './pages/Register';
import Login from './pages/Login';
import PropertyList from './pages/PropertyList';
import PublishProperty from './pages/PublishProperty';

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
    element: <Login />,
  },
  {
    path: '/register',
    element: <Register />,
  },
  {
    path: '/properties',
    element: (
      <ProtectedRoute>
        <PropertyList />
      </ProtectedRoute>
    ),
  },
  {
    path: '/publish-property',
    element: (
      <ProtectedRoute>
        <PublishProperty />
      </ProtectedRoute>
    ),
  },
]);