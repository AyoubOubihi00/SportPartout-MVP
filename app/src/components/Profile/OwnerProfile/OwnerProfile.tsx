import Avatar from "@mui/joy/Avatar";
import BaseLayout from "../../../layouts/BaseLayout";
import Typography from "@mui/joy/Typography";
import Badge from "@mui/joy/Badge";
import Button from "@mui/joy/Button";
import Tabs from "@mui/joy/Tabs";
import Tab from "@mui/joy/Tab";
import TabList from "@mui/joy/TabList";
import TabPanel from "@mui/joy/TabPanel";
import { useTheme } from "@mui/joy/styles";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import { useAuth } from "../../shared/useAuth";

export default function OwnerProfile() {
  const { token, logout } = useAuth();
  const theme = useTheme();
  const navigate = useNavigate();
  const mainColor = theme.palette.mode == "dark" ? "#fff" : "#000";
  const bgColor = theme.palette.mode === "dark" ? "#0f172a" : "#f8fafc";

  useEffect(() => {
    if (!token) {
      navigate("/login");
    }
  }, [token, navigate]);
  if (!token) return null;

  const userData = {
    name: "Pierre Martin",
    role: "Gérant",
    memberSince: "Septembre 2023",
    city: "Paris",
  };

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

  return (
    <BaseLayout>
      <div
        style={{
          width: "100%",
          maxWidth: 600,
          margin: "0 auto",
          padding: "0px 16px",
          paddingTop: 32,
        }}
      >
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
            {userData.role}
          </Typography>
          <Typography level="body-sm" sx={{ color: "neutral.400" }}>
            Gérant depuis {userData.memberSince}
          </Typography>
          <Typography level="body-sm" sx={{ color: "neutral.400" }}>
            {userData.city}
          </Typography>
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
          <Button
            fullWidth
            size="md"
            variant="outlined"
            color="danger"
            sx={{ mt: 1, borderRadius: 2, fontWeight: "bold" }}
            onClick={() => {
              logout();
              navigate("/login");
            }}
          >
            Se déconnecter
          </Button>
        </div>

        {/* Stats */}
        <div style={{ width: "100%", maxWidth: 480, marginBottom: 32 }}>
          <div style={{ display: "flex", gap: 16, marginBottom: 16 }}>
            <div
              style={{
                flex: 1,
                padding: 12,
                background: "#f8fafc",
                borderRadius: 8,
              }}
            >
              <Typography level="body-xs" sx={{ color: mainColor }}>
                Total Membres
              </Typography>
              <Typography fontWeight="md">231</Typography>
            </div>
            <div
              style={{
                flex: 1,
                padding: 12,
                background: "#f8fafc",
                borderRadius: 8,
              }}
            >
              <Typography level="body-xs" sx={{ color: mainColor }}>
                Revenus ce mois
              </Typography>
              <Typography fontWeight="md">€5,300</Typography>
            </div>
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
          {managedClubs.map((club) => (
            <div key={club.id} style={{ marginBottom: 16 }}>
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <Typography
                  level="body-sm"
                  fontWeight="md"
                  sx={{ color: mainColor }}
                >
                  {club.name}
                </Typography>
                <Badge color="success">{club.sport}</Badge>
              </div>
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  marginTop: 8,
                }}
              >
                <Typography level="body-xs" sx={{ color: "neutral.500" }}>
                  {club.members}/{club.maxCapacity} membres
                </Typography>
                <Typography level="body-xs" sx={{ color: "neutral.500" }}>
                  €{club.revenue}/mois
                </Typography>
              </div>
            </div>
          ))}
          <Button variant="soft" sx={{ mt: 1 }}>
            + Nouveau Club
          </Button>
        </div>

        {/* Tabs Membres / Activité */}
        <div style={{ width: "100%", maxWidth: 480 }}>
          <Tabs defaultValue={0} sx={{ mt: 3 }}>
            <TabList>
              <Tab>Membres</Tab>
              <Tab>Activité</Tab>
            </TabList>
            <TabPanel value={0} sx={{ mt: 2 }}>
              <Typography>Membres content ici...</Typography>
            </TabPanel>
            <TabPanel value={1} sx={{ mt: 2 }}>
              <Typography>Activité content ici...</Typography>
            </TabPanel>
          </Tabs>
        </div>
      </div>
    </BaseLayout>
  );
}
