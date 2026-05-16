import { Navigate } from "react-router-dom";
import { userStore } from "../../Utility/Store/UserStore";

export function ProtectedRoute({
  children,
  requiredRole,
}: {
  children: React.ReactNode;
  requiredRole: string[];
}) {
  const activeRole = userStore.getActiveRole();
  if (!requiredRole.includes(activeRole)) {
    return (
      <Navigate
        to={`/not-authorized`}
        replace
      />
    );
  }
  return <>{children}</>;
}
