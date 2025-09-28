import { ReactNode } from "react";
import img from "../../../logo2.png";
import NavBar from "../NavBar/NavBar";
import { useTheme } from "@mui/joy/styles";

type Props = {
  children: ReactNode;
};

export function AuthLayout({ children }: Props) {
  const theme = useTheme();
  const mainColor = theme.palette.mode == "dark" ? "#fff" : "#000";

  return (
        <div
        className={`main-container ${theme.palette.mode == "dark" ? "dark" : "light"}`}
        style={{
            minHeight: "100vh",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            backgroundColor: theme.palette.mode == "dark" ? "#191919" : "#fff",
            color: mainColor,
            paddingBottom: "120px",
            paddingTop: "60px", 
        }}
        >
      <div style={{ display: "flex", justifyContent: "center", marginBottom: "32px" }}>
        <img src={img} alt="Logo" width={300} />
      </div>

    
      <div style={{ width: "100%", maxWidth: 400 }}>{children}</div>

      <div className="fixed bottom-0 left-0 w-full z-50 flex justify-center">
        <div className="w-full max-w-md">
          <NavBar />
        </div>
      </div>
    </div>
  );
}
