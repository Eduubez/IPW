import { TriatorDashboard } from "../Pages/Dashboard/TriatorDashboard/TriatorDashboard";
import AdminDashboard from "../Pages/AdminDashboard/AdminDashboard";
import { userStore } from "../Utility/Store/UserStore";
import { InvestigatorDashboard } from "../Pages/Dashboard/InvestigatorDashboard/InvestigatorDashboard";
import { SupervisorDashboard } from "../Pages/Dashboard/SupervisorDashboard/SupervisorDashboard";
import { ManagerDashboard } from "../Pages/Dashboard/ManagerDashboard/ManagerDashboard";
import { ROLE_KEYS } from "../Config/RolesConfig";

export function DashboardRoute() {
  const activeRole = userStore.getActiveRole();

  switch (activeRole) {
    case ROLE_KEYS.ADMIN:
      return <AdminDashboard />;
    case ROLE_KEYS.TRIATOR:
      return <TriatorDashboard />;
    case ROLE_KEYS.INVESTIGATOR:
      return <InvestigatorDashboard />;
    case ROLE_KEYS.SUPERVISOR:
      return <SupervisorDashboard />;
    case ROLE_KEYS.MANAGER:
      return <ManagerDashboard />;

    default:
      return <div>Invalid role</div>;
  }
}
