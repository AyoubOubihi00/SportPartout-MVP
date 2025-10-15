import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import { CssVarsProvider } from "@mui/joy/styles";
import "./App.css"; // ou './index.css'

import App from "./App.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <CssVarsProvider defaultMode="system">
      <App />
    </CssVarsProvider>
  </StrictMode>,
);
