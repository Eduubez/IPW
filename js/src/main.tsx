import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import '../src/StyleGuide/root.css'
import '../src/StyleGuide/typography.css'
import App from './App.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)