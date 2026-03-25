import ".././i18n";
import { Routes, Route, BrowserRouter } from "react-router-dom";
import Login from "./Pages/Login/Login";


export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
      </Routes>
    </BrowserRouter>
  );
}
