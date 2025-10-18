import ProfileLayout from "../ProfileLayout";
import Typography from "@mui/joy/Typography";
import Avatar from "@mui/joy/Avatar";
import Button from "@mui/joy/Button";
import Chip from "@mui/joy/Chip";
import Divider from "@mui/joy/Divider";
import { Calendar, MapPin, Trophy, CreditCard } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { useTheme } from "@mui/joy/styles";
import "../ProfileCommon.css";

export function MemberProfile() {
  const theme = useTheme();
  const navigate = useNavigate();
  const mainColor = theme.palette.mode == "dark" ? "#fff" : "#000";

  const darkGradient = "linear-gradient(135deg, #0f172a, #1e293b)";
  const lightGradient = "linear-gradient(135deg, #f8fafc, #e2e8f0)";
  const bgColor = theme.palette.mode === "dark" ? darkGradient : lightGradient;

  const userData = {
    name: "Marie Dubois",
    email: "marie.dubois@email.com",
    memberSince: "Janvier 2024",
    city: "Paris",
    role: "Membre",
    phoneNumber: "06 12 34 56 78",
  };

  const activeClubs = [
    {
      id: 1,
      name: "Tennis Club Parisien",
      sport: "Tennis",
      memberSince: "Mars 2024",
      status: "Actif",
    },
    {
      id: 2,
      name: "Running Club 16",
      sport: "Course à pied",
      memberSince: "Avril 2024",
      status: "Actif",
    },
  ];

  const recentActivity = [
    { date: "15 Nov 2024", action: "Adhésion au Tennis Club Parisien" },
    { date: "08 Nov 2024", action: "Participation tournoi Running Club 16" },
    { date: "01 Nov 2024", action: "Paiement cotisation mensuelle" },
  ];

  return (
    <ProfileLayout>
      {/* Profil */}
      <div style={{ textAlign: "center", marginBottom: 32 }}>
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            marginBottom: 16,
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
            {userData.name
              .split(" ")
              .map((n) => n[0])
              .join("")}
          </Avatar>
        </div>
        <Typography level="title-md" sx={{ color: mainColor }}>
          {userData.name}
        </Typography>
        <Typography level="body-sm" sx={{ color: "neutral.400" }}>
          {userData.email}
        </Typography>
        <Typography level="body-sm" sx={{ color: "neutral.400" }}>
          {userData.phoneNumber}
        </Typography>
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            gap: 24,
            marginTop: 16,
          }}
        >
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: 4,
              color: "#94a3b8",
            }}
          >
            <Calendar size={16} />
            <Typography level="body-xs">{userData.memberSince}</Typography>
          </div>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: 4,
              color: "#94a3b8",
            }}
          >
            <MapPin size={16} />
            <Typography level="body-xs">{userData.city}</Typography>
          </div>
        </div>
        <Button
          fullWidth
          size="md"
          onClick={() => navigate("/editmemberprofile")}
          variant="solid"
          sx={{
            mt: 2,
            backgroundColor: mainColor,
            color: theme.palette.mode === "dark" ? "#121212" : "#fff",
            borderRadius: 2,
            fontWeight: "bold",
            boxShadow: 0,
            "&:hover": { backgroundColor: mainColor },
            "&:focus": { backgroundColor: mainColor, boxShadow: "none" },
            "&:active": { backgroundColor: mainColor },
          }}
        >
          Modifier
        </Button>
      </div>

      {/* Clubs actifs */}
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
          <Trophy size={16} /> Mes Clubs ({activeClubs.length})
        </Typography>
        {activeClubs.map((club) => (
          <div key={club.id} style={{ marginBottom: 16 }}>
            <Typography
              level="body-sm"
              fontWeight="md"
              sx={{ color: mainColor }}
            >
              {club.name}{" "}
              <Chip size="sm" variant="soft">
                {club.sport}
              </Chip>
            </Typography>
            <Typography level="body-xs" sx={{ color: "neutral.500" }}>
              Membre depuis {club.memberSince} • {club.status}
            </Typography>
            <Button
              size="sm"
              variant="solid"
              sx={{
                mt: 1,
                backgroundColor: mainColor,
                color: theme.palette.mode === "dark" ? "#121212" : "#fff",
                borderRadius: 2,
                fontWeight: "bold",
                boxShadow: 0,
                "&:hover": { backgroundColor: mainColor },
                "&:focus": { backgroundColor: mainColor, boxShadow: "none" },
                "&:active": { backgroundColor: mainColor },
              }}
              fullWidth
            >
              Voir le club
            </Button>
          </div>
        ))}
        <Button
          variant="solid"
          fullWidth
          sx={{
            mt: 2,
            backgroundColor: mainColor,
            color: theme.palette.mode === "dark" ? "#121212" : "#fff",
            borderRadius: 2,
            fontWeight: "bold",
            boxShadow: 0,
            "&:hover": { backgroundColor: mainColor },
            "&:focus": { backgroundColor: mainColor, boxShadow: "none" },
            "&:active": { backgroundColor: mainColor },
          }}
        >
          + Rejoindre un nouveau club
        </Button>
      </div>

      {/* Activité récente */}
      <div style={{ width: "100%", maxWidth: 480 }}>
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
          <CreditCard size={16} /> Activité Récente
        </Typography>
        {recentActivity.map((activity, index) => (
          <div key={index} style={{ marginBottom: 16 }}>
            <Typography level="body-sm" sx={{ color: mainColor }}>
              {activity.action}
            </Typography>
            <Typography level="body-xs" sx={{ color: "neutral.500" }}>
              {activity.date}
            </Typography>
            {index < recentActivity.length - 1 && <Divider sx={{ my: 1 }} />}
          </div>
        ))}
      </div>
    </ProfileLayout>
  );
}
