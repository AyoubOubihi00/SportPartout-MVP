import { useLocation, useNavigate } from "react-router-dom";
import { useEffect, useMemo, useRef, useState } from "react";
import { useTheme } from "@mui/joy/styles";
import BaseLayout from "../../layouts/BaseLayout";
import MiniClubDetail from "./MiniClubDetail";
import ManagedClubDetail from "./ManagedClubDetail";
import { useAuth } from "../shared/useAuth";
import {
  fetchMyClubs,
  fetchManagedClubs,
  unsubscribeFromClub,
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
import Box from "@mui/joy/Box";
import { ChevronRight } from "lucide-react";
import ClubDetails from "../ClubDetails";
import BottomSheet from "../BottomSheet";
import { fetchClubById } from "../../api/club";
import type { Club as UiClub } from "../../model/Club";

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
  const [leavingMemberships, setLeavingMemberships] = useState<Set<string>>(
    () => new Set<string>(),
  );

  const [clubDetailsOpen, setClubDetailsOpen] = useState(false);
  const [clubDetails, setClubDetails] = useState<UiClub | null>(null);
  const [clubDetailsLoading, setClubDetailsLoading] = useState(false);
  const [clubDetailsError, setClubDetailsError] = useState<string | null>(null);
  const clubDetailsRequestRef = useRef(0);
  const [isOwner, setIsOwner] = useState(false);

  useEffect(() => {
    if (!token) {
      setIsOwner(false);
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
          setIsOwner(false);
          navigate("/login");
          return;
        }
        setIsOwner(profile.role?.toUpperCase() === "OWNER");
        const membershipsData = await fetchMyClubs(profile.id);
        if (!cancelled) {
          setMemberships(membershipsData);
        }
        if (profile.role?.toUpperCase() === "OWNER") {
          const managed = await fetchManagedClubs(profile.id);
          if (!cancelled) {
            // Trier par nom pour un ordre stable (les clubs modifiés restent à leur place)
            managed.sort((a, b) => {
              const nameA = a.name?.toLowerCase() || "";
              const nameB = b.name?.toLowerCase() || "";
              return nameA.localeCompare(nameB);
            });
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
          setIsOwner(false);
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

  const membershipKey = (membership: MemberWithClub) =>
    `${membership.clubId}-${membership.userId}`;

  const handleUnsubscribe = async (membership: MemberWithClub) => {
    if (!membership.clubId || !membership.userId) {
      return;
    }

    const clubName = membership.club?.name ?? "ce club";
    const confirmed = window.confirm(
      `Voulez-vous vraiment vous désinscrire de ${clubName} ?`,
    );
    if (!confirmed) {
      return;
    }

    const key = membershipKey(membership);
    setLeavingMemberships((prev) => {
      const next = new Set(prev);
      next.add(key);
      return next;
    });

    try {
      const success = await unsubscribeFromClub(
        membership.clubId,
        membership.userId,
      );
      if (!success) {
        throw new Error("La désinscription a échoué, veuillez réessayer.");
      }
      setMemberships((prev) =>
        prev.filter((item) => membershipKey(item) !== key),
      );
      setError(null);
    } catch (err) {
      const message =
        err instanceof Error
          ? err.message
          : "Impossible de se désinscrire du club.";
      setError(message);
    } finally {
      setLeavingMemberships((prev) => {
        const next = new Set(prev);
        next.delete(key);
        return next;
      });
    }
  };

  const loadClubDetails = async (clubId: string) => {
    setClubDetailsOpen(true);
    setClubDetails(null);
    setClubDetailsError(null);
    setClubDetailsLoading(true);
    const requestId = ++clubDetailsRequestRef.current;
    try {
      const detail = await fetchClubById(clubId);
      if (clubDetailsRequestRef.current !== requestId) {
        return;
      }
      if (detail) {
        setClubDetails(detail);
        setClubDetailsError(null);
      } else {
        setClubDetailsError("Club introuvable");
      }
    } catch (err) {
      if (clubDetailsRequestRef.current !== requestId) {
        return;
      }
      setClubDetailsError(
        err instanceof Error
          ? err.message
          : "Impossible de charger les details du club",
      );
    } finally {
      if (clubDetailsRequestRef.current === requestId) {
        setClubDetailsLoading(false);
      }
    }
  };

  const handleViewClubDetails = (clubId: string) => {
    void loadClubDetails(clubId);
  };

  const closeClubDetails = () => {
    clubDetailsRequestRef.current += 1;
    setClubDetailsOpen(false);
    setClubDetails(null);
    setClubDetailsError(null);
    setClubDetailsLoading(false);
  };

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

        {isOwner && (
          <Stack direction="row" justifyContent="center" sx={{ mb: 3 }}>
            <Button
              variant="solid"
              color="primary"
              onClick={() => navigate("/clubs/create")}
              endDecorator={<ChevronRight size={16} />}
              sx={{
                backgroundColor: mainColor,
                color: theme.palette.mode === "dark" ? "#121212" : "#fff",
                fontWeight: 600,
                px: 3,
                boxShadow: "none",
                "&:hover": { backgroundColor: mainColor },
              }}
            >
              Créer un club
            </Button>
          </Stack>
        )}

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
              {isOwner
                ? "Vous ne gerez aucun club pour le moment."
                : "Vous n'avez rejoint aucun club pour le moment."}
            </Typography>
            <Typography
              level="body-sm"
              sx={{ color: labelColor, maxWidth: 360, lineHeight: 1.4 }}
            >
              {isOwner
                ? "Ajoutez ou importez un club pour le retrouver ici et acceder rapidement a sa gestion."
                : "Rejoignez un club pour le retrouver ici et acc�der rapidement � ses informations."}
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
                onUnsubscribe={() => handleUnsubscribe(membership)}
                unsubscribing={leavingMemberships.has(
                  membershipKey(membership),
                )}
                onViewDetails={handleViewClubDetails}
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
                  navigate("/clubs/manage", { state: { clubId: club.id } })
                }
              />
            ))}
          </Stack>
        )}
      </div>

      <BottomSheet
        open={clubDetailsOpen}
        onClose={closeClubDetails}
        initialHeight={450}
      >
        {clubDetails ? (
          <Box
            sx={{
            
              borderRadius: 16,
             
            }}
          >
            <ClubDetails club={clubDetails} disableJoin={isOwner} />
          </Box>
        ) : (
          <Sheet
            variant="outlined"
            sx={{
              borderRadius: 16,
              p: 4,
              textAlign: "center",
              minHeight: 200,
              display: "flex",
              flexDirection: "column",
              justifyContent: "center",
              gap: 2,
              bgcolor: "transparent"
            }}
          >
            <Typography level="body-md" sx={{ color: mainColor }}>
              {clubDetailsError ??
                (clubDetailsLoading
                  ? "Chargement du club..."
                  : "Club introuvable.")}
            </Typography>
            {clubDetailsLoading && (
              <LinearProgress
                variant="soft"
                color="primary"
                sx={{ borderRadius: 8 }}
              />
            )}
          </Sheet>
        )}
      </BottomSheet>
    </BaseLayout>
  );
}
