import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useTheme } from "@mui/joy/styles";
import BaseLayout from "../../layouts/BaseLayout";
import MiniClubDetail from "./MiniClubDetail";
import ManagedClubDetail from "./ManagedClubDetail";
import { getFullClub } from "../../services/clubService";
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
  const location = useLocation();
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
          // Enrichir l'état actif/inactif avec la source des détails (évite tout décalage de BFF)
          let enriched = managed;
          try {
            const fulls = await Promise.all(
              managed.map(async (c) => {
                if (!c.id) return null;
                try {
                  const fc = await getFullClub(c.id, token);
                  return { id: c.id, deletedAt: fc.deletedAt ?? null } as {
                    id: string;
                    deletedAt: string | null;
                  };
                } catch {
                  return null;
                }
              }),
            );
            const byId = new Map(
              fulls.filter(Boolean).map((x) => [x!.id, x!.deletedAt]),
            );
            enriched = managed.map((c) =>
              c.id && byId.has(c.id)
                ? { ...c, deletedAt: byId.get(c.id) ?? null }
                : c,
            );
          } catch {
            // Si enrichissement échoue, on garde les données BFF telles quelles
          }
          if (!cancelled) {
            // Trier par nom pour un ordre stable (les clubs modifiés restent à leur place)
            enriched.sort((a, b) => {
              const nameA = a.name?.toLowerCase() || "";
              const nameB = b.name?.toLowerCase() || "";
              return nameA.localeCompare(nameB);
            });
            setManagedClubs(enriched);
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
  }, [token, navigate, location.key]);

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
                onViewDetails={() =>
                  navigate(`/clubs/manage`, { state: { clubId: club.id } })
                }
              />
            ))}
          </Stack>
        )}
      </div>
    </BaseLayout>
  );
}
