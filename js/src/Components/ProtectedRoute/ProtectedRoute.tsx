import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { userStore } from "../../Utility/Store/UserStore";


export function ProtectedRoute({ children }: { children: React.ReactNode }) {
    const location = useLocation();
    const isAuthenticated = userStore.getIsLoggedIn()
    const hasTokenExpired = userStore.hasTokenExpired();
    const returnUrl = `${location.pathname}${location.search}${location.hash}`;

    if (!isAuthenticated || hasTokenExpired) {
        userStore.clear();
        return <Navigate to={`/login?returnUrl=${encodeURIComponent(returnUrl)}`} replace />;
    }

    return <>{children}</>;
}