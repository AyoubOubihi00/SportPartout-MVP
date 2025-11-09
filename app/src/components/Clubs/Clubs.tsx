import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTheme } from "@mui/joy/styles";
import BaseLayout from "../../layouts/BaseLayout";
import MiniClubDetail from "./MiniClubDetail";
import ManagedClubDetail from "./ManagedClubDetail";
import { useAuth } from "../shared/useAuth";
import {
  fetchMyClubs,
  fetchManagedClubs,
  type ClubSnapshot,
  type MemberWithClub,
} from "../../services/membershipService";
import { getMe } from "../../services/auth/authService";
import Sheet from "@mui/joy/Sheet";
import Typography from "@mui/joy/Typography";
import Stack from "@mui/joy/Stack";
import LinearProgress from "@mui/joy/LinearProgress";
import Alert from "@mui/joy/Alert";
import Button from "@mui/joy/Button";
import { ChevronRight } from "lucide-react";

export default function Clubs() {
  const { token } = useAuth();
  const navigate = useNavigate();
  const theme = useTheme();
  const mainColor = theme.palette.mode === "dark" ? "#fff" : "#000";
  const labelColor =
    theme.palette.mode === "dark" ? "neutral.400" : "neutral.500";

  const [memberships, setMemberships] = useState<MemberWithClub[]>([]);
  const [managedClubs, setManagedClubs] = useState<ClubSnapshot[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    let cancelled = false;

    const load = async () => {
      setLoading(true);
      setError(null);
      try {
        const profile = await getMe(token);
        if (!profile || Array.isArray(profile)) {
          navigate("/login");
          return;
        }
        const membershipsData = await fetchMyClubs(profile.id);
        if (!cancelled) {
          setMemberships(membershipsData);
        }
        if (profile.role?.toUpperCase() === "OWNER") {
          const managed = await fetchManagedClubs(profile.id);
          if (!cancelled) {
            setManagedClubs(managed);
          }
        } else if (!cancelled) {
          setManagedClubs([]);
        }
      } catch (err) {
        if (!cancelled) {
          const message =
            err instanceof Error
              ? err.message
              : "Impossible de charger les clubs";
          setError(message);
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    void load();

    return () => {
      cancelled = true;
    };
  }, [token, navigate]);

  const hasClubs = useMemo(() => memberships.length > 0, [memberships]);
  const hasManagedClubs = useMemo(
    () => managedClubs.length > 0,
    [managedClubs],
  );

  return (
    <BaseLayout>
      <div
        style={{
          margin: "0 auto",
          padding: "32px 16px",
          width: "100%",
          maxWidth: 720,
          boxSizing: "border-box",
        }}
      >
        <Typography
          level="h4"
          sx={{
            mb: 2,
            textAlign: "center",
            color: mainColor,
            fontWeight: 700,
            letterSpacing: ".02em",
          }}
        >
          Mes clubs
        </Typography>

        {loading && (
          <LinearProgress
            variant="soft"
            color="primary"
            sx={{ borderRadius: 12, mb: 3 }}
          />
        )}

        {error && (
          <Alert color="danger" variant="soft" sx={{ mb: 3, borderRadius: 12 }}>
            {error}
          </Alert>
        )}

        {!loading && !error && !hasClubs && !hasManagedClubs && (
          <Sheet
            variant="outlined"
            sx={{
              borderRadius: 16,
              p: 3,
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              justifyContent: "center",
              gap: 14,
              minHeight: 190,
              textAlign: "center",
              color: mainColor,
              background:
                theme.palette.mode === "dark"
                  ? "linear-gradient(135deg, rgba(15,23,42,0.55), rgba(30,41,59,0.55))"
                  : "linear-gradient(135deg, #f8fafc, #e2e8f0)",
              border: "2px solid",
              borderColor:
                theme.palette.mode === "dark" ? "neutral.700" : "neutral.300",
              backdropFilter:
                theme.palette.mode === "dark" ? "blur(4px)" : "none",
              boxShadow:
                theme.palette.mode === "dark"
                  ? "0 4px 16px -4px rgba(0,0,0,0.4)"
                  : "0 4px 16px -4px rgba(0,0,0,0.08)",
            }}
          >
            <Typography
              level="body-md"
              sx={{ color: mainColor, fontWeight: 600 }}
            >
              Vous n&apos;avez rejoint aucun club pour le moment.
            </Typography>
            <Typography
              level="body-sm"
              sx={{ color: labelColor, maxWidth: 360, lineHeight: 1.4 }}
            >
              Rejoignez un club pour le retrouver ici et accéder rapidement à
              ses informations.
            </Typography>
            <Button
              variant="solid"
              color="primary"
              endDecorator={<ChevronRight size={16} />}
              onClick={() => navigate("/")}
              sx={{
                backgroundColor: mainColor,
                color: theme.palette.mode === "dark" ? "#121212" : "#fff",
                fontWeight: 600,
                "&:hover": { backgroundColor: mainColor },
              }}
            >
              Explorer les clubs
            </Button>
          </Sheet>
        )}

        {hasClubs && (
          <Stack spacing={2.5}>
            {memberships.map((membership) => (
              <MiniClubDetail
                key={`${membership.clubId}-${membership.userId}`}
                membership={membership}
                onViewDetails={() => navigate("/")}
              />
            ))}
          </Stack>
        )}

        {hasManagedClubs && (
          <Stack spacing={2.5} sx={{ mt: hasClubs ? 4 : 0 }}>
            <Typography
              level="title-md"
              sx={{
                color: mainColor,
                fontWeight: 700,
                letterSpacing: ".04em",
                textTransform: "uppercase",
              }}
            >
              Clubs gérés
            </Typography>
            {managedClubs.map((club) => (
              <ManagedClubDetail
                key={club.id}
                club={club}
                onViewDetails={() => navigate("/")}
              />
            ))}
          </Stack>
        )}
      </div>
    </BaseLayout>
  );
}
