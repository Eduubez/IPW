import { TriatorDashboard } from "../Pages/Dashboard/TriatorDashboard/TriatorDashboard";
import AdminDashboard from "../Pages/AdminDashboard/AdminDashboard";
import { userStore } from "../Utility/Store/UserStore";
import { InvestigatorDashboard } from "../Pages/Dashboard/InvestigatorDashboard/InvestigatorDashboard";

export function DashboardRoute() {
  const activeRole = userStore.getActiveRole();

  switch (activeRole) {
    case "admin":
      return <AdminDashboard />;

    case "triator":
      return <TriatorDashboard />;
    case "investigator":
      return <InvestigatorDashboard />;

    default:
      return <div>Invalid role</div>;
  }
}