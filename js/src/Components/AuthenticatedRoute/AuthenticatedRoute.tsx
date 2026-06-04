import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { userStore } from "../../Utility/Store/UserStore";

export function AuthenticatedRoute({
  children,
}: {
  children: React.ReactNode;
}) {
  const location = useLocation();
  const returnUrl = `${location.pathname}${location.search}${location.hash}`;

  if (userStore.hasTokenExpired()) {
    userStore.clear();
    return (
      <Navigate
        to={`/login?returnUrl=${encodeURIComponent(returnUrl)}`}
        replace
      />
    );
  }

  return <>{children}</>;
}
