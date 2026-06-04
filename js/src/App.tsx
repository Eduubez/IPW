import ".././i18n";
import { Routes, Route, BrowserRouter, useLocation, Navigate } from "react-router-dom";
import Login from "./Pages/Login/Login";
import SideBar from "./Components/SideBar/SideBar";
import { DashboardRoute } from "./Routes/DashboardRoute";
import Profile from "./Pages/Profile/Profile";
import RoleSelection from "./Pages/RoleSelection/RoleSelection";
import NewProcess from "./Pages/NewProcess/NewProcess";
import { AuthenticatedRoute } from "./Components/AuthenticatedRoute/AuthenticatedRoute";
import { SnackbarProvider } from "notistack";
import ProcessPage from "./Pages/ProcessPage/ProcessPage";
import HistoryPage from "./Pages/History/HistoryPage";
import { ProtectedRoute } from "./Components/ProtectedRoute/ProtectedRoute";
import { NotAuthorizedPage } from "./Pages/NotAuthorized/NotAuthorizedPage";
function AppLayout() {
  const location = useLocation();
  const pathsWithoutSidebar = ["/login", "/role-selection"];

  const hideSidebar = pathsWithoutSidebar.includes(location.pathname);

  return (
    <div className="app-container">
      {!hideSidebar && <SideBar />}
      <div className="root">
        <Routes>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          <Route
            path="/dashboard"
            element={
              <AuthenticatedRoute>
                <DashboardRoute />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <AuthenticatedRoute>
                <Profile />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/role-selection"
            element={
              <AuthenticatedRoute>
                <RoleSelection />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/processes/new"
            element={
              <AuthenticatedRoute>
                <ProtectedRoute requiredRole={["triator"]}>
                  <NewProcess />
                </ProtectedRoute>
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/processes/:id"
            element={
              <AuthenticatedRoute>
                <ProtectedRoute
                  requiredRole={["investigator", "supervisor", "manager"]}>
                  <ProcessPage />
                </ProtectedRoute>
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/user/history"
            element={
              <AuthenticatedRoute>
                <ProtectedRoute
                  requiredRole={[
                    "investigator",
                    "supervisor",
                    "manager",
                    "triator",
                  ]}>
                  <HistoryPage />
                </ProtectedRoute>
              </AuthenticatedRoute>
            }
          />
          <Route path="/not-authorized" element={<NotAuthorizedPage />} />
        </Routes>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <SnackbarProvider
        maxSnack={3}
        autoHideDuration={1500}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}>
        <AppLayout />
      </SnackbarProvider>
    </BrowserRouter>
  );
}
