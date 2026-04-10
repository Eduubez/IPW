import React from "react";
import { Navigate, useLocation } from "react-router-dom";


export function ProtectedRoute({ children }: { children: React.ReactNode }) {
    const location = useLocation();
    const isAuthenticated = localStorage.getItem("loggedIn") === "true"; // Replace with actual authentication logic
    const returnUrl = `${location.pathname}${location.search}${location.hash}`;

    if (!isAuthenticated) {
        return <Navigate to={`/login?returnUrl=${encodeURIComponent(returnUrl)}`} replace />;
    }

    return <>{children}</>;
}