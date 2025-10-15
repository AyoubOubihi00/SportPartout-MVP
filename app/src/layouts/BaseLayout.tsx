import React from "react";
import NavBar from "../components/NavBar/NavBar";
import { useTheme } from "@mui/joy/styles";

export default function BaseLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const theme = useTheme();

  return (
    <div
      style={{
        minHeight: "100dvh",
        width: "100vw",
        background: "inherit",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <main
        style={{
          flex: 1,
          width: "100vw",
          paddingBottom: "88px", // hauteur estimée de la navbar
          boxSizing: "border-box",
          background:
            theme.palette.mode === "dark"
              ? "linear-gradient(135deg, #0f172a, #1e293b)"
              : "linear-gradient(135deg, #f8fafc, #e2e8f0)",
        }}
      >
        {children}
      </main>
      <NavBar />
    </div>
  );
}
