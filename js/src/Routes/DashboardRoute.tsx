import Dashboard from "../Pages/Dashboard/DashBoard";
import AdminDashboard from "../Pages/AdminDashboard/AdminDashboard";
import { userStore } from "../Utility/Store/UserStore";

export function DashboardRoute() {
  const activeRole = userStore.getActiveRole();

  switch (activeRole) {
    case "admin":
      return <AdminDashboard />;

    case "triator":
      return <Dashboard />;

    case "investigator":
      return <Dashboard />;

    case "supervisor":
      return <Dashboard />;

    case "manager":
      return <Dashboard />;

    default:
      return <Dashboard />;
  }
}