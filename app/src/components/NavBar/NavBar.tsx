import { useState } from "react";
import { Dumbbell, Map, User } from "lucide-react";
import Sheet from "@mui/joy/Sheet";
import Box from "@mui/joy/Box";
import IconButton from "@mui/joy/IconButton";
import Typography from "@mui/joy/Typography";
import { useNavigate, useLocation } from "react-router-dom";

export default function NavBar() {
  const [activeIndex, setActiveIndex] = useState(0);
  const navigate = useNavigate();
  //On ça car on doit le récupérer sur les différentes pages
  const location = useLocation();


  const items = [
    { icon: <Dumbbell size={28} />, label: "MON CLUB", paths:["/random"] },
    { icon: <Map size={28} />, label: "EXPLORER", paths:["/"] },
    { 
      icon: <User size={28} />, 
      label: "MON COMPTE", 
      paths: ["/login", "/signup", "/forgot-password", "/resetpassword", "/resetpasswordsuccess"]   },
  ];



  return (
    <Sheet
      variant="outlined"
      sx={(theme) => ({
        position: "fixed",
        bottom: 0,
        left: 0,
        right: 0,
        zIndex: 1400, // Toujours au-dessus du BottomSheet
        borderRadius: "24px 24px 0 0",
        boxShadow: "lg",
        background:
    theme.palette.mode === "dark"
        ? "linear-gradient(135deg, #0f172a, #1e293b)" // dégradé sombre
        : "linear-gradient(135deg, #f8fafc, #e2e8f0)", // dégradé clair
        p: 2,
      })}
    >
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-around",
          alignItems: "center",
        }}
      >
        {items.map((item, index) => {
        const active = item.paths.includes(location.pathname);
          return (
            <Box
              key={index}
              onClick={
                () => 
                { 
                  setActiveIndex(index);   
                  if (item.paths && item.paths.length > 0) 
                  {
                    navigate(item.paths[0]); 
                  }                
                }
              }

              sx={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                cursor: "pointer",
                minWidth: 80,
                gap: 0.5,
                transition: "all 0.3s",
              }}
            >
              <IconButton
                size="lg"
                variant={active ? "solid" : "soft"}
                sx={(theme) => ({
                  borderRadius: "xl",
                  boxShadow: "none",
                  background: active
                    ? theme.palette.mode === "dark"
                      ? "white" // actif = blanc en dark
                      : "linear-gradient(135deg, #334155, #1e293b)" // actif = sombre en light
                    : "transparent",
                  color: active
                    ? theme.palette.mode === "dark"
                      ? "neutral.900" // icône noire si fond blanc
                      : "white"
                    : theme.palette.mode === "dark"
                    ? "neutral.400" // icône grise en dark
                    : "neutral.600", // icône grise en light
                  "&:hover": {
                    background: active
                      ? theme.palette.mode === "dark"
                        ? "white"
                        : "linear-gradient(135deg, #334155, #1e293b)"
                      : "transparent",
                    color: active
                      ? theme.palette.mode === "dark"
                        ? "neutral.900"
                        : "white"
                      : theme.palette.mode === "dark"
                      ? "neutral.400"
                      : "neutral.600",
                  },
                  "&:focus-visible": {
                    outline: "none",
                    boxShadow: "none",
                  },
                  "&:focus": {
                    outline: "none",
                    boxShadow: "none",
                  },
                })}
              >
                {item.icon}
              </IconButton>

              <Typography
                level="body-sm"
                fontWeight={500}
                sx={(theme) => ({
                  color: active
                    ? theme.palette.mode === "dark"
                      ? "white" // texte blanc si actif en dark
                      : "neutral.900"
                    : theme.palette.mode === "dark"
                    ? "neutral.400"
                    : "neutral.600",
                  transition: "color 0.3s",
                })}
              >
                {item.label}
              </Typography>
            </Box>
          );
        })}
      </Box>
    </Sheet>
  );
}
