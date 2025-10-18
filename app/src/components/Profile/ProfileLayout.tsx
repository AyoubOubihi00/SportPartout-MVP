import React from "react";
import { useTheme } from "@mui/joy/styles";
import NavBar from "../NavBar/NavBar";
import BaseLayout from "../../layouts/BaseLayout";
import "./ProfileCommon.css";

export default function ProfileLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const theme = useTheme();
  const mainColor = theme.palette.mode == "dark" ? "#fff" : "#000";
  const bgGradient =
    theme.palette.mode === "dark"
      ? "linear-gradient(135deg, #0f172a, #1e293b)"
      : "linear-gradient(135deg, #f8fafc, #e2e8f0)";

  return (
    <BaseLayout>
      <div
        className={`main-container ${theme.palette.mode == "dark" ? "dark" : "light"}`}
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          background: bgGradient,
          color: mainColor,
        }}
      >
        <div style={{ width: "100%", maxWidth: 400 }}>{children}</div>
        <div className="profile-navbar">
          <div className="profile-navbar-inner">
            <NavBar />
          </div>
        </div>
      </div>
    </BaseLayout>
  );
}
