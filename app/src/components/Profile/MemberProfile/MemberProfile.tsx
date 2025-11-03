import ProfileLayout from "../ProfileLayout";
import Typography from "@mui/joy/Typography";
import Avatar from "@mui/joy/Avatar";
import Button from "@mui/joy/Button";
import { getAge } from "../../../services/auth/memberService";
import { useNavigate } from "react-router-dom";
import { useTheme } from "@mui/joy/styles";
import "../ProfileCommon.css";
import { useEffect, useState } from "react";
import { getMe, UserProfile } from "../../../services/auth/authService";
import CircularProgress from "@mui/joy/CircularProgress";
import { useAuth } from "../../shared/useAuth";

export function MemberProfile() {
  const { token, logout } = useAuth();
  const theme = useTheme();
  const navigate = useNavigate();
  const mainColor = theme.palette.mode == "dark" ? "#fff" : "#000";
  const labelColor =
    theme.palette.mode === "dark" ? "neutral.400" : "neutral.500";

  const darkGradient = "linear-gradient(135deg, #0f172a, #1e293b)";
  const lightGradient = "linear-gradient(135deg, #f8fafc, #e2e8f0)";
  const bgColor = theme.palette.mode === "dark" ? darkGradient : lightGradient;
  const [userData, setUserData] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }
    setLoading(true);
    getMe(token)
      .then((data) => {
        setUserData(data);
      })
      .finally(() => setLoading(false));
  }, [token, navigate]);

  if (loading) {
    return (
      <ProfileLayout>
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            minHeight: 300,
          }}
        >
          <CircularProgress size="lg" variant="plain" />
        </div>
      </ProfileLayout>
    );
  }

  return (
    <ProfileLayout>
      {/* Profil */}
      <div
        style={{
          margin: "0 auto",
          padding: "0px 16px",
          paddingTop: 32,
        }}
      >
        <div
          style={{
            textAlign: "center",
            marginBottom: 32,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            gap: 24,
          }}
        >
          <div
            style={{
              display: "flex",
              justifyContent: "center",
            }}
          >
            <Avatar
              sx={{
                "--Avatar-size": "90px",
                border: "2px solid",
                borderColor: mainColor,
                background: bgColor,
                color: theme.palette.mode === "dark" ? "white" : "black",
              }}
            >
              {(
                (userData?.firstName?.charAt(0) ?? "") +
                (userData?.lastName?.charAt(0) ?? "")
              ).toUpperCase()}
            </Avatar>
          </div>

          {/* Infos utilisateur (liste simple sans couche visuelle) */}
          <div
            style={{
              maxWidth: 480,
              marginInline: "auto",
              textAlign: "left",
            }}
          >
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "160px 1fr",
                rowGap: 16,
                columnGap: 16,
                alignItems: "center",
                justifyItems: "start",
              }}
            >
              <Typography
                level="body-sm"
                sx={{
                  color: labelColor,
                  fontWeight: 500,
                  textTransform: "uppercase",
                  letterSpacing: "0.08em",
                }}
              >
                Nom
              </Typography>
              <Typography
                level="body-md"
                sx={{ color: mainColor, fontWeight: 600 }}
              >
                {(userData?.lastName ?? "").toUpperCase() || "—"}
              </Typography>

              <Typography
                level="body-sm"
                sx={{
                  color: labelColor,
                  fontWeight: 500,
                  textTransform: "uppercase",
                  letterSpacing: "0.08em",
                }}
              >
                Prénom
              </Typography>
              <Typography
                level="body-md"
                sx={{ color: mainColor, fontWeight: 600 }}
              >
                {userData?.firstName
                  ? userData.firstName.charAt(0).toUpperCase() +
                    userData.firstName.slice(1).toLowerCase()
                  : "—"}
              </Typography>

              <Typography
                level="body-sm"
                sx={{
                  color: labelColor,
                  fontWeight: 500,
                  textTransform: "uppercase",
                  letterSpacing: "0.08em",
                }}
              >
                Sexe
              </Typography>
              <Typography
                level="body-md"
                sx={{ color: mainColor, fontWeight: 600 }}
              >
                —
              </Typography>

              <Typography
                level="body-sm"
                sx={{
                  color: labelColor,
                  fontWeight: 500,
                  textTransform: "uppercase",
                  letterSpacing: "0.08em",
                }}
              >
                Email
              </Typography>
              <Typography
                level="body-md"
                sx={{ color: mainColor, fontWeight: 600 }}
              >
                {userData?.email || "—"}
              </Typography>

              <Typography
                level="body-sm"
                sx={{
                  color: labelColor,
                  fontWeight: 500,
                  textTransform: "uppercase",
                  letterSpacing: "0.08em",
                }}
              >
                Téléphone
              </Typography>
              <Typography
                level="body-md"
                sx={{ color: mainColor, fontWeight: 600 }}
              >
                {userData?.phone || "—"}
              </Typography>

              <Typography
                level="body-sm"
                sx={{
                  color: labelColor,
                  fontWeight: 500,
                  textTransform: "uppercase",
                  letterSpacing: "0.08em",
                }}
              >
                Naissance
              </Typography>
              <Typography
                level="body-md"
                sx={{ color: mainColor, fontWeight: 600 }}
              >
                {userData?.birthDate
                  ? `${new Date(userData.birthDate).toLocaleDateString("fr-FR")} (${getAge(userData.birthDate)} ans)`
                  : "—"}
              </Typography>

              <Typography
                level="body-sm"
                sx={{
                  color: labelColor,
                  fontWeight: 500,
                  textTransform: "uppercase",
                  letterSpacing: "0.08em",
                }}
              >
                Membre depuis
              </Typography>
              <Typography
                level="body-md"
                sx={{ color: mainColor, fontWeight: 600 }}
              >
                {userData?.createdAt
                  ? new Date(userData.createdAt).toLocaleDateString("fr-FR")
                  : "—"}
              </Typography>
            </div>
          </div>
          <div
            style={{
              width: "100%",
              maxWidth: 480,
              marginInline: "auto",
              display: "flex",
              flexDirection: "column",
              gap: 8,
            }}
          >
            <Button
              fullWidth
              size="md"
              onClick={() => navigate("/editmemberprofile")}
              variant="solid"
              sx={{
                backgroundColor: mainColor,
                color: theme.palette.mode === "dark" ? "#121212" : "#fff",
                "&:hover": { backgroundColor: mainColor },
                "&:focus": { backgroundColor: mainColor, boxShadow: "none" },
                "&:active": { backgroundColor: mainColor },
              }}
            >
              Modifier
            </Button>
            <Button
              fullWidth
              size="md"
              variant="outlined"
              color="danger"
              sx={{ fontWeight: "bold" }}
              onClick={() => {
                logout();
                navigate("/login");
              }}
            >
              Se déconnecter
            </Button>
          </div>
        </div>

        {/* Sections supprimées: 'Activité Récente' et '+ Rejoindre un nouveau club' */}
      </div>
    </ProfileLayout>
  );
}
