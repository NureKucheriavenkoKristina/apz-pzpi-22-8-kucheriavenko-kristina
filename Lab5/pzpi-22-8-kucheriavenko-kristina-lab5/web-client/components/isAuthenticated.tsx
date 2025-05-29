import React, {type JSX} from 'react';
import { Navigate } from 'react-router-dom';

interface ProtectedRouteProps {
    children: JSX.Element;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
    const isAuthenticated = localStorage.getItem('isAuthenticated') === 'true';

    return isAuthenticated ? children : <Navigate to="/" replace />;
};

export default ProtectedRoute;
