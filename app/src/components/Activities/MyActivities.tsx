import { useState, useEffect, useRef, useMemo } from "react";
import {
  Sheet,
  Stack,
  Typography,
  Card,
  Chip,
  Button,
  Divider,
  Box,
  Tabs,
  TabList,
  Tab,
  TabPanel,
  AspectRatio,
  LinearProgress,
} from "@mui/joy";
import { useNavigate } from "react-router-dom";
import {
  CalendarRange,
  MapPin,
  Clock,
  Users,
  User,
  ChevronLeft,
  ChevronRight,
  HomeIcon,
} from "lucide-react";
import BaseLayout from "../../layouts/BaseLayout";
import BottomSheet from "../BottomSheet/BottomSheet";
import type {
  Activity,
  ActivityDto,
  ActivityFull,
} from "../../types/activities";
import {
  fetchMySeances,
  leaveSeance,
  fetchSeanceById,
} from "../../services/activitiesService";
import type {
  Seance,
  SeanceWithAvailability,
} from "../../services/activitiesService";
import { useBubble } from "../shared/useBubble";
import { useAuth } from "../shared/useAuth";
import { mapDtoToActivity, mapSeanceToActivity } from "../../types/activities";

function formatDateFR(isoDate: string): string {
  const d = new Date(isoDate);
  return d.toLocaleDateString("fr-FR", {
    weekday: "long",
    day: "2-digit",
    month: "long",
  });
}

function formatHoursFR(isoDate: string): string {
  const d = new Date(isoDate);
  return d.toLocaleTimeString("fr-FR", {
    hour: "2-digit",
    minute: "2-digit",
  });
}

function ActivityCard({
  a,
  onDetails,
  onLeave,
}: {
  a: Activity;
  onDetails: (a: Activity) => void;
  onLeave?: (a: Activity) => void | Promise<void>;
}) {
  const isPast = a.status === "past";
  return (
    <Card
      variant="soft"
      sx={{
        p: 0,
        borderRadius: 16,
        bgcolor: "transparent",
        overflow: "hidden",
        border: "1px solid var(--joy-palette-neutral-200, #e5e7eb)",
        boxShadow: "sm",
      }}
    >
      {/* Bandeau visuel: affiché uniquement si image fournie */}
      {a.imageUrl && (
        <AspectRatio ratio={16 / 9} sx={{ m: 0, maxHeight: 200 }}>
          <img
            src={a.imageUrl}
            alt="Illustration de l'activité"
            style={{ width: "100%", height: "100%", objectFit: "cover" }}
          />
        </AspectRatio>
      )}

      <Box sx={{ p: 1.25 }}>
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
          sx={{ mb: 0.5 }}
        >
          <Typography level="title-md" sx={{ fontWeight: 900 }}>
            {a.title}
          </Typography>
          <Chip size="sm" variant="soft" color={isPast ? "neutral" : "primary"}>
            {isPast ? "Passée" : "À venir"}
          </Chip>
        </Stack>

        <Stack
          direction="row"
          gap={1}
          alignItems="center"
          sx={{ color: "text.secondary", mb: 0.25 }}
        >
          <Chip size="sm" variant="soft" color="neutral">
            {a.sport}
          </Chip>
          <Divider orientation="vertical" />
          <Stack direction="row" gap={0.5} alignItems="center">
            <CalendarRange size={14} />
            <Typography level="body-sm">
              {formatDateFR(a.date)} • {a.start} – {a.end}
            </Typography>
          </Stack>
        </Stack>

        <Stack
          direction="row"
          gap={0.5}
          alignItems="center"
          sx={{ color: "text.secondary" }}
        >
          <MapPin size={14} />
          <Typography level="body-sm">
            {a.club}
            {a.place ? ` — ${a.place}` : ""}
          </Typography>
        </Stack>

        <Stack direction="row" gap={1} sx={{ mt: 1 }}>
          <Button
            size="sm"
            variant="outlined"
            color="neutral"
            onClick={() => onDetails(a)}
          >
            Détails
          </Button>
          {!isPast && (
            <Button
              size="sm"
              variant="soft"
              color="danger"
              onClick={() => {
                if (onLeave) onLeave(a);
              }}
            >
              Se désinscrire
            </Button>
          )}
        </Stack>
        {isPast && (
          <Stack
            direction="row"
            gap={1}
            alignItems="center"
            sx={{ mt: 0.75, color: "text.tertiary" }}
          >
            <Clock size={14} />
            <Typography level="body-sm">Terminé</Typography>
          </Stack>
        )}
      </Box>
    </Card>
  );
}

export default function MyActivities() {
  // Hook prêt pour la liaison back: mappe des DTO -> Activity
  // Pour le test: on appelle fetchMySeances et on fournit les DTOs au hook
  const { token } = useAuth();
  const navigate = useNavigate();

  const [remoteDtos, setRemoteDtos] = useState<ActivityDto[] | undefined>(
    undefined,
  );
  // DTOs récupérés pour le mois affiché (historique)
  const [monthDtos, setMonthDtos] = useState<ActivityDto[] | undefined>(
    undefined,
  );

  const activities = useMemo(
    () => (remoteDtos ?? []).map((d) => mapDtoToActivity(d)),
    [remoteDtos],
  );

  const upcoming = useMemo(
    () => activities.filter((a) => a.status === "upcoming"),
    [activities],
  );

  const past = useMemo(
    () => activities.filter((a) => a.status === "past"),
    [activities],
  );

  // Appel simple pour tester le service planning (exécuté au montage)
  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    let mounted = true;
    // Sélection minimale/transformation vers ActivityDto
    fetchMySeances<SeanceWithAvailability>(
      `id idClub idTerrain idCoach startTime endTime durationMinutes capacite capaSeance remainingPlaces full idSport nameSport description status createdBy`,
      null,
      null,
    )
      .then((seances) => {
        if (!mounted) return;
        const dtos: ActivityDto[] = seances.map(
          (s: SeanceWithAvailability) => ({
            id: s.id,
            title: s.nameSport ?? "Séance",
            club: s.idClub ?? "",
            sport: s.nameSport ?? "",
            startIso: s.startTime,
            endIso: s.endTime,
            place: undefined,
            imageUrl: undefined,
            capacity:
              typeof s.capacite === "number" ? s.capacite : s.capaSeance,
            booked:
              typeof s.capaSeance === "number" &&
              typeof s.remainingPlaces === "number"
                ? Math.max(0, s.capaSeance - s.remainingPlaces)
                : undefined,
            coach: s.idCoach ?? undefined,
            level: undefined,
            description: s.description ?? undefined,
          }),
        );
        setRemoteDtos(dtos);
      })
      .catch((err) => {
        // pour debug rapide

        console.error("fetchMySeances failed:", err);
      });
    return () => {
      mounted = false;
    };
  }, [navigate, token]);
  const [tab, setTab] = useState<number>(0);
  // Mois affiché dans l'historique (1er jour du mois)
  const [displayedMonth, setDisplayedMonth] = useState<Date>(() => {
    const now = new Date();
    return new Date(now.getFullYear(), now.getMonth() - 1, 1);
  });
  const { showSuccessBubble, showErrorBubble } = useBubble();
  const [detailOpen, setDetailOpen] = useState<boolean>(false);
  const [selected, setSelected] = useState<ActivityFull | null>(null);
  const [sheetTopOffset, setSheetTopOffset] = useState<number>(0);
  const tabsAnchorRef = useRef<HTMLDivElement | null>(null);

  const openDetails = async (a: Activity) => {
    try {
      const seance: Seance = await fetchSeanceById(a.id);
      if (!seance) return;
      setSelected(mapSeanceToActivity(seance));
      setDetailOpen(true);
    } catch (err) {
      console.error("fetchSeanceById error", err);
      showErrorBubble?.(err instanceof Error ? err.message : String(err));
    }
  };

  const formatMonthYear = (d: Date) =>
    d.toLocaleDateString("fr-FR", { month: "long", year: "numeric" });

  const goPrevMonth = () =>
    setDisplayedMonth((m) => new Date(m.getFullYear(), m.getMonth() - 1, 1));
  const goNextMonth = () =>
    setDisplayedMonth((m) => new Date(m.getFullYear(), m.getMonth() + 1, 1));

  // Interdire d'aller au-delà du mois courant
  const now = new Date();
  const currentMonthStart = new Date(now.getFullYear(), now.getMonth(), 1);

  // Activities mappées depuis les DTOs du mois sélectionné
  const monthActivities = useMemo(
    () => (monthDtos ?? []).map((d) => mapDtoToActivity(d)),
    [monthDtos],
  );

  // Lorsque l'utilisateur affiche l'onglet Historique, charger les séances du mois sélectionné
  useEffect(() => {
    if (tab !== 1) return;
    if (!token) return;

    let mounted = true;
    const selection =
      "id idClub idTerrain idCoach startTime endTime durationMinutes capacite capaSeance remainingPlaces full idSport nameSport description status createdBy";

    const startDate = new Date(
      displayedMonth.getFullYear(),
      displayedMonth.getMonth(),
      1,
    );
    const endDate = new Date(
      displayedMonth.getFullYear(),
      displayedMonth.getMonth() + 1,
      0,
      23,
      59,
      59,
      999,
    );

    const startIso = startDate.toISOString();
    const endIso = endDate.toISOString();

    fetchMySeances<SeanceWithAvailability>(selection, startIso, endIso)
      .then((seances) => {
        if (!mounted) return;
        const dtos: ActivityDto[] = seances.map(
          (s: SeanceWithAvailability) => ({
            id: s.id,
            title: s.nameSport ?? "Séance",
            club: s.idClub ?? "",
            sport: s.nameSport ?? "",
            startIso: s.startTime,
            endIso: s.endTime,
            place: undefined,
            imageUrl: undefined,
            capacity:
              typeof s.capacite === "number" ? s.capacite : s.capaSeance,
            booked:
              typeof s.capaSeance === "number" &&
              typeof s.remainingPlaces === "number"
                ? Math.max(0, s.capaSeance - s.remainingPlaces)
                : undefined,
            coach: s.idCoach ?? undefined,
            level: undefined,
            description: s.description ?? undefined,
          }),
        );
        setMonthDtos(dtos);
      })
      .catch((err) => {
        console.error("fetchMySeances (month) failed:", err);
      });

    return () => {
      mounted = false;
    };
  }, [displayedMonth, tab, token]);

  // Calcule la hauteur maximale du BottomSheet pour qu'il s'arrête sous les onglets
  useEffect(() => {
    const update = () => {
      const rect = tabsAnchorRef.current?.getBoundingClientRect();
      if (rect) setSheetTopOffset(rect.bottom + 20); // 8px de marge visuelle
    };
    update();
    window.addEventListener("resize", update);
    return () => window.removeEventListener("resize", update);
  }, [tab]);

  return (
    <BaseLayout>
      <Sheet sx={{ bgcolor: "transparent", pb: 12 }}>
        <Box sx={{ px: 1.5, pt: 1.5 }}>
          {/* En‑tête soigné */}
          <Sheet
            variant="soft"
            sx={(t) => ({
              borderRadius: 14,
              p: 1.25,
              mb: 1,
              background:
                t.palette.mode === "dark"
                  ? "linear-gradient(135deg, #0f172a, #1e293b)"
                  : "linear-gradient(135deg, #f8fafc, #e2e8f0)",
              border: "1px solid var(--joy-palette-neutral-200, #e5e7eb)",
            })}
          >
            <Stack
              direction="row"
              alignItems="center"
              justifyContent="space-between"
            >
              <Stack direction="row" alignItems="center" gap={1}>
                <Box
                  sx={(t) => ({
                    width: 40,
                    height: 40,
                    borderRadius: "50%",
                    display: "grid",
                    placeItems: "center",
                    background:
                      t.palette.mode === "dark"
                        ? "linear-gradient(135deg, #1e293b, #334155)"
                        : "linear-gradient(135deg, #dbeafe, #bfdbfe)",
                  })}
                >
                  <CalendarRange size={18} />
                </Box>
                <Box>
                  <Typography
                    level="h3"
                    sx={{ fontWeight: 900, lineHeight: 1 }}
                  >
                    Mes activités
                  </Typography>
                  <Typography level="body-sm" sx={{ color: "text.secondary" }}>
                    {upcoming.length} à venir • {past.length} passées
                  </Typography>
                </Box>
              </Stack>
            </Stack>
          </Sheet>

          {/* Onglets segmentés */}
          <Tabs
            value={tab}
            onChange={(_, v) => setTab(Number(v))}
            sx={{ mt: 0.5, background: "transparent" }}
          >
            <TabList
              variant="soft"
              sx={{
                borderRadius: 999,
                p: 0.5,
                gap: 0.5,
                boxShadow: "sm",
                display: "inline-flex",
                width: "fit-content",
                alignSelf: "flex-start",
              }}
              ref={tabsAnchorRef}
            >
              <Tab
                sx={{
                  minHeight: 36,
                  px: 1.25,
                  borderRadius: 999,
                  fontWeight: 700,
                  "&[aria-selected='true']": {
                    bgcolor: "neutral.800",
                    color: "#fff",
                  },
                }}
              >
                À venir
              </Tab>
              <Tab
                sx={{
                  minHeight: 36,
                  px: 1.25,
                  borderRadius: 999,
                  fontWeight: 700,
                  "&[aria-selected='true']": {
                    bgcolor: "neutral.800",
                    color: "#fff",
                  },
                }}
              >
                Historique
              </Tab>
            </TabList>

            <TabPanel value={0} sx={{ px: 0, pt: 1 }}>
              <Box
                sx={{
                  display: "grid",
                  gap: 1.5,
                  gridTemplateColumns: {
                    xs: "1fr",
                    sm: "1fr 1fr",
                    lg: "1fr 1fr 1fr",
                  },
                  alignItems: "start",
                }}
              >
                {upcoming.map((a) => (
                  <ActivityCard
                    key={a.id}
                    a={a}
                    onDetails={openDetails}
                    onLeave={async (act) => {
                      try {
                        await leaveSeance(act.id);
                        setRemoteDtos((prev) =>
                          prev ? prev.filter((d) => d.id !== act.id) : prev,
                        );
                        showSuccessBubble?.("Désinscription réussie.");
                      } catch (err) {
                        console.error("leaveSeance error", err);
                        showErrorBubble?.(
                          err instanceof Error ? err.message : String(err),
                        );
                      }
                    }}
                  />
                ))}
                {upcoming.length === 0 && (
                  <Card variant="soft" sx={{ p: 1.5 }}>
                    <Typography level="body-sm">
                      Aucune activité à venir.
                    </Typography>
                  </Card>
                )}
              </Box>
            </TabPanel>

            <TabPanel
              value={1}
              sx={{ px: 0, pt: 1, background: "transparent" }}
            >
              {/* Navigation mois */}
              <Stack
                direction="row"
                alignItems="center"
                justifyContent="center"
                gap={1}
                sx={{ mb: 1 }}
              >
                <Button
                  size="sm"
                  variant="plain"
                  color="neutral"
                  onClick={goPrevMonth}
                >
                  <ChevronLeft size={16} />
                </Button>
                <Typography level="body-md" sx={{ fontWeight: 800 }}>
                  {formatMonthYear(displayedMonth)}
                </Typography>
                <Button
                  size="sm"
                  variant="plain"
                  color="neutral"
                  onClick={goNextMonth}
                  disabled={
                    displayedMonth.getFullYear() ===
                      currentMonthStart.getFullYear() &&
                    displayedMonth.getMonth() === currentMonthStart.getMonth()
                  }
                >
                  <ChevronRight size={16} />
                </Button>
              </Stack>

              <Box
                sx={{
                  display: "grid",
                  gap: 1.5,
                  gridTemplateColumns: {
                    xs: "1fr",
                    sm: "1fr 1fr",
                    lg: "1fr 1fr 1fr",
                  },
                  alignItems: "start",
                }}
              >
                {monthActivities.map((a) => (
                  <ActivityCard key={a.id} a={a} onDetails={openDetails} />
                ))}
                {monthActivities.length === 0 && (
                  <Card variant="soft" sx={{ p: 1.5 }}>
                    <Typography level="body-sm">
                      Pas d’historique pour ce mois.
                    </Typography>
                  </Card>
                )}
              </Box>
            </TabPanel>
          </Tabs>
        </Box>

        {/* Détail dans un BottomSheet */}
        <BottomSheet
          open={detailOpen}
          onClose={() => setDetailOpen(false)}
          title={selected ? selected.title : "Détails"}
          initialHeight={520}
          topOffset={sheetTopOffset}
        >
          {selected && (
            <Box sx={{ px: 1 }}>
              {selected.imageUrl && (
                <AspectRatio ratio={16 / 9} sx={{ mb: 1, maxHeight: 220 }}>
                  <img
                    src={selected.imageUrl}
                    alt="Illustration de l'activité"
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "cover",
                    }}
                  />
                </AspectRatio>
              )}
              <Typography level="title-lg" sx={{ fontWeight: 900 }}>
                {selected.title}
              </Typography>
              <Stack
                direction="row"
                gap={1}
                alignItems="center"
                sx={{ color: "text.secondary", mt: 0.5 }}
              >
                <Chip size="sm" variant="soft" color="neutral">
                  {selected.sport}
                </Chip>
                <Divider orientation="vertical" />
                <Stack direction="row" gap={0.5} alignItems="center">
                  <CalendarRange size={14} />
                  <Typography level="body-sm">
                    {formatDateFR(selected.startIso)} •{" "}
                    {formatHoursFR(selected.startIso)} –{" "}
                    {formatHoursFR(selected.endIso)}
                  </Typography>
                </Stack>
              </Stack>
              <Stack
                direction="row"
                gap={0.5}
                alignItems="center"
                sx={{ color: "text.secondary", mt: 0.5 }}
              >
                <HomeIcon size={14} />
                <Typography level="body-sm">{selected.club}</Typography>
              </Stack>
              <Stack
                direction="row"
                gap={0.5}
                alignItems="center"
                sx={{ color: "text.secondary", mt: 0.5 }}
              >
                <MapPin size={14} />
                <Typography level="body-sm">{selected.place}</Typography>
              </Stack>

              {(typeof selected.capacity === "number" ||
                typeof selected.booked === "number" ||
                selected.coach ||
                selected.description) && (
                <Card variant="soft" sx={{ mt: 1, p: 1 }}>
                  <Stack gap={1}>
                    {(typeof selected.capacity === "number" ||
                      typeof selected.booked === "number") && (
                      <Box>
                        <Stack
                          direction="row"
                          justifyContent="space-between"
                          alignItems="center"
                        >
                          <Stack direction="row" gap={0.5} alignItems="center">
                            <Users size={16} />
                            <Typography level="body-sm">Capacité</Typography>
                          </Stack>
                          <Typography level="body-sm">
                            {selected.booked ?? 0}/{selected.capacity ?? 0}
                          </Typography>
                        </Stack>
                        <LinearProgress
                          determinate
                          value={
                            selected.capacity && selected.capacity > 0
                              ? Math.min(
                                  100,
                                  Math.round(
                                    ((selected.booked ?? 0) /
                                      selected.capacity) *
                                      100,
                                  ),
                                )
                              : 0
                          }
                          sx={{ mt: 0.5 }}
                        />
                      </Box>
                    )}
                    {selected.coach && (
                      <Stack
                        direction="row"
                        gap={0.5}
                        alignItems="center"
                        sx={{ color: "text.secondary" }}
                      >
                        <User size={16} />
                        <Typography level="body-sm">
                          Coach: {selected.coach}
                        </Typography>
                      </Stack>
                    )}
                    {selected.description && (
                      <Typography
                        level="body-sm"
                        sx={{ color: "text.secondary" }}
                      >
                        {selected.description}
                      </Typography>
                    )}
                  </Stack>
                </Card>
              )}

              <Stack direction="row" gap={1} sx={{ mt: 1 }}>
                {selected.status === "upcoming" && (
                  <>
                    <Button size="sm" variant="outlined" color="neutral">
                      Se rendre au club
                    </Button>
                    <Button
                      size="sm"
                      variant="soft"
                      color="danger"
                      onClick={async () => {
                        try {
                          await leaveSeance(selected.id);
                          setRemoteDtos((prev) =>
                            prev
                              ? prev.filter((d) => d.id !== selected.id)
                              : prev,
                          );
                          setDetailOpen(false);
                          showSuccessBubble?.("Désinscription réussie.");
                        } catch (err) {
                          console.error("leaveSeance error", err);
                          showErrorBubble?.(
                            err instanceof Error ? err.message : String(err),
                          );
                        }
                      }}
                    >
                      Se désinscrire
                    </Button>
                  </>
                )}
                {selected.status === "past" && (
                  <Stack
                    direction="row"
                    gap={1}
                    alignItems="center"
                    sx={{ color: "text.tertiary" }}
                  >
                    <Clock size={14} />
                    <Typography level="body-sm">Séance passée</Typography>
                  </Stack>
                )}
              </Stack>
            </Box>
          )}
        </BottomSheet>
      </Sheet>
    </BaseLayout>
  );
}
