import Avatar from "@mui/joy/Avatar";
import Typography from "@mui/joy/Typography";
import Button from "@mui/joy/Button";
import { useTheme } from "@mui/joy/styles";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useAuth } from "../../shared/useAuth";
// ProfileLayout is no longer used here; the layout wrapper is provided by
// `ProfileSwitch` so that the BaseLayout/NavBar stays mounted between
// Owner/User switches and avoids a full-page remount visual effect.
//
import CircularProgress from "@mui/joy/CircularProgress";
import { getMe, type UserProfile } from "../../../services/auth/authService";
import { getAge } from "../../../services/auth/memberService";

export default function OwnerProfile() {
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
  if (!token) return null;

  const managedClubs = [
    {
      id: 1,
      name: "Tennis Club Parisien",
      sport: "Tennis",
      members: 142,
      maxCapacity: 200,
      revenue: 3200,
    },
    {
      id: 2,
      name: "Football Club Nord",
      sport: "Football",
      members: 89,
      maxCapacity: 150,
      revenue: 2100,
    },
  ];

  if (loading) {
    return (
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
    );
  }

  const initials = (
    (userData?.firstName?.charAt(0) ?? "") +
    (userData?.lastName?.charAt(0) ?? "")
  ).toUpperCase();

  return (
    // Profil (content only). The surrounding ProfileLayout is provided
    // by `ProfileSwitch` so BaseLayout/NavBar remain mounted.
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
            {initials}
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
              gridTemplateColumns: "120px 1fr",
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

      {/* Clubs gérés */}
      <div style={{ width: "100%", maxWidth: 480, marginBottom: 32 }}>
        <Typography
          level="title-sm"
          sx={{
            mb: 2,
            display: "flex",
            alignItems: "center",
            gap: 1,
            color: mainColor,
          }}
        >
          Clubs gérés ({managedClubs.length})
        </Typography>
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fill, minmax(160px, 1fr))",
            gap: 12,
          }}
        >
          {managedClubs.map((club) => (
            <div
              key={club.id}
              style={{
                border: "2px solid",
                borderColor: mainColor,
                borderRadius: 8,
                padding: 12,
                minHeight: 120,
                display: "flex",
                flexDirection: "column",
                justifyContent: "space-between",
                gap: 8,
              }}
            >
              <div>
                <Typography
                  level="body-sm"
                  fontWeight="md"
                  sx={{ color: mainColor }}
                >
                  {club.name}
                </Typography>
                <Typography
                  level="body-xs"
                  sx={{
                    color:
                      theme.palette.mode === "dark"
                        ? "neutral.400"
                        : "neutral.600",
                  }}
                >
                  {club.sport} • {club.members} membres
                </Typography>
              </div>
              <Button
                size="sm"
                variant="solid"
                onClick={() => navigate(`/clubs/${club.id}`)}
                sx={{
                  backgroundColor: mainColor,
                  color: theme.palette.mode === "dark" ? "#121212" : "#fff",
                  boxShadow: 0,
                  "&:hover": { backgroundColor: mainColor },
                  "&:focus": {
                    backgroundColor: mainColor,
                    boxShadow: "none",
                  },
                  "&:active": { backgroundColor: mainColor },
                }}
              >
                Gérer
              </Button>
            </div>
          ))}
        </div>
        <Button
          variant="solid"
          sx={{
            mt: 1,
            backgroundColor: mainColor,
            color: theme.palette.mode === "dark" ? "#121212" : "#fff",
            boxShadow: 0,
            "&:hover": { backgroundColor: mainColor },
            "&:focus": { backgroundColor: mainColor, boxShadow: "none" },
            "&:active": { backgroundColor: mainColor },
          }}
        >
          + Nouveau Club
        </Button>
      </div>
    </div>
  );
}
