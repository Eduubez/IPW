import ".././i18n";
import { Routes, Route, BrowserRouter, useLocation } from "react-router-dom";
import Login from "./Pages/Login/Login";
import SideBar from "./Components/SideBar/SideBar";
import Dashboard from "./Pages/Dashboard/DashBoard";
import Profile from "./Pages/Profile/Profile";
import RoleSelection from "./Pages/RoleSelection/RoleSelection";
import  NewProcess  from "./Pages/NewProcess/NewProcess";
import { ProtectedRoute } from "./Components/ProtectedRoute/ProtectedRoute";
import { SnackbarProvider } from "notistack";
function AppLayout() {
  const location = useLocation();
  const pathsWithoutSidebar = ["/login", "/role-selection"];

  const hideSidebar = pathsWithoutSidebar.includes(location.pathname);

  return (
    <div className="app-container">
      {!hideSidebar && <SideBar />}
      <div className="root">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            }
          />
          <Route path="/role-selection" element={<RoleSelection />} />
          <Route path="/processes/new" element={
            <ProtectedRoute>
              <NewProcess />
            </ProtectedRoute>
          }/>

        </Routes>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
    <SnackbarProvider maxSnack={3} autoHideDuration={1500}>
      <AppLayout />
    </SnackbarProvider>
    </BrowserRouter>
  );
}
