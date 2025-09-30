import { ReactNode } from "react";
import sport_partout from "../../assets/sport_partout.svg"
import sport_partout_reverse from "../../assets/sport_partout_reverse.svg"
import BaseLayout from "../../layouts/BaseLayout";
import { useTheme } from "@mui/joy/styles";

export default function AuthLayout({ children }: { children: ReactNode }) {
  const theme = useTheme();
  const mainColor = theme.palette.mode == "dark" ? "#fff" : "#000";

  return (
    <BaseLayout>
      <div
        className={`main-container ${theme.palette.mode == "dark" ? "dark" : "light"}`}
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          background:
            theme.palette.mode === "dark"
              ? "linear-gradient(135deg, #0f172a, #1e293b)"
              : "linear-gradient(135deg, #f8fafc, #e2e8f0)",
          color: mainColor,
        }}
      >
        <div style={{ display: "flex", justifyContent: "center", margin: "32px 0 16px 0" }}>
          <img
            src={theme.palette.mode === "dark" ? sport_partout : sport_partout_reverse}
            alt="Logo"
            width={300}
          />
        </div>
        <div style={{ width: "100%", maxWidth: 400 }}>{children}</div>
      </div>
    </BaseLayout>
  );
}
