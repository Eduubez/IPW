import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import SideBar from './Components/SideBar/SideBar.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)


createRoot(document.getElementById('sideBar')!).render(
  <StrictMode>
    <SideBar />
  </StrictMode>,
)