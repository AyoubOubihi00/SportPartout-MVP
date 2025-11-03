import { useDispatch, useSelector } from "react-redux";
import Button from "@mui/joy/Button";
import Stack from "@mui/joy/Stack";
import ButtonGroup from "@mui/joy/ButtonGroup";
import Sheet from "@mui/joy/Sheet";
import { useTheme } from "@mui/joy/styles";
import type { RootState } from "../../store";
import { setViewMode } from "../../store/rolesSlice";
import { Crown, User as UserIcon } from "lucide-react";

export default function ProfileRoleSwitch() {
  const dispatch = useDispatch();
  const roles = useSelector((s: RootState) => s.roles.roles);
  const viewMode = useSelector((s: RootState) => s.roles.viewMode);
  const theme = useTheme();
  const mainColor = theme.palette.mode == "dark" ? "#fff" : "#000";
  const textOnMain = theme.palette.mode === "dark" ? "#121212" : "#fff";
  const ownerCapable = roles.includes("OWNER");
  // Affiche uniquement si le compte a le rôle OWNER (capacité gérant)
  if (!ownerCapable) return null;

  const scrollTop = () => {
    if (typeof window !== "undefined") {
      // Positionne instantanément en haut pour éviter tout effet de saut visuel
      window.requestAnimationFrame(() => {
        window.scrollTo({ top: 0, behavior: "auto" });
      });
    }
  };
  const setUserOnly = () => {
    dispatch(setViewMode("USER"));
    scrollTop();
  };
  const setOwnerUser = () => {
    dispatch(setViewMode("OWNER"));
    scrollTop();
  };

  return (
    <Stack sx={{ mb: 2, alignItems: "center", width: "100%" }}>
      <Sheet
        variant="outlined"
        sx={{
          p: 0.5,
          borderRadius: 999,
          display: "inline-flex",
          boxShadow: "sm",
          bgcolor: "background.body",
        }}
      >
        <ButtonGroup variant="plain" sx={{ borderRadius: 999 }}>
          <Button
            size="sm"
            onClick={setUserOnly}
            startDecorator={<UserIcon size={16} />}
            variant={viewMode === "USER" ? "solid" : "soft"}
            sx={{
              px: 1.5,
              borderRadius: 999,
              transition: "all .15s ease",
              ...(viewMode === "USER"
                ? {
                    backgroundColor: mainColor,
                    color: textOnMain,
                    boxShadow: 0,
                    "&:hover": { backgroundColor: mainColor },
                    "&:focus": {
                      backgroundColor: mainColor,
                      boxShadow: "none",
                    },
                    "&:active": { backgroundColor: mainColor },
                  }
                : {}),
            }}
          >
            Mode Membre
          </Button>
          <Button
            size="sm"
            onClick={setOwnerUser}
            startDecorator={<Crown size={16} />}
            variant={viewMode === "OWNER" ? "solid" : "soft"}
            sx={{
              px: 1.5,
              borderRadius: 999,
              transition: "all .15s ease",
              ...(viewMode === "OWNER"
                ? {
                    backgroundColor: mainColor,
                    color: textOnMain,
                    boxShadow: 0,
                    "&:hover": { backgroundColor: mainColor },
                    "&:focus": {
                      backgroundColor: mainColor,
                      boxShadow: "none",
                    },
                    "&:active": { backgroundColor: mainColor },
                  }
                : {}),
            }}
          >
            Mode Gérant
          </Button>
        </ButtonGroup>
      </Sheet>
    </Stack>
  );
}
