import { useState, useEffect, useRef } from "react";
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
import { CalendarRange, MapPin, Clock, Users, User } from "lucide-react";
import BaseLayout from "../../layouts/BaseLayout";
import BottomSheet from "../BottomSheet/BottomSheet";
import type { Activity } from "../../types/activities";
import { useActivities } from "../../services/activities/useActivities";

function formatDateFR(isoDate: string): string {
  const d = new Date(isoDate);
  return d.toLocaleDateString("fr-FR", {
    weekday: "long",
    day: "2-digit",
    month: "long",
  });
}

function ActivityCard({
  a,
  onDetails,
}: {
  a: Activity;
  onDetails: (a: Activity) => void;
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
            <Button size="sm" variant="soft" color="danger">
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
  const { upcoming, past } = useActivities();
  const [tab, setTab] = useState<number>(0);
  const [detailOpen, setDetailOpen] = useState<boolean>(false);
  const [selected, setSelected] = useState<Activity | null>(null);
  const [sheetTopOffset, setSheetTopOffset] = useState<number>(0);
  const tabsAnchorRef = useRef<HTMLDivElement | null>(null);

  const openDetails = (a: Activity) => {
    setSelected(a);
    setDetailOpen(true);
  };

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
                  <ActivityCard key={a.id} a={a} onDetails={openDetails} />
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
                {past.map((a) => (
                  <ActivityCard key={a.id} a={a} onDetails={openDetails} />
                ))}
                {past.length === 0 && (
                  <Card variant="soft" sx={{ p: 1.5 }}>
                    <Typography level="body-sm">
                      Pas d’historique pour le moment.
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
                    {formatDateFR(selected.date)} • {selected.start} –{" "}
                    {selected.end}
                  </Typography>
                </Stack>
              </Stack>
              <Stack
                direction="row"
                gap={0.5}
                alignItems="center"
                sx={{ color: "text.secondary", mt: 0.5 }}
              >
                <MapPin size={14} />
                <Typography level="body-sm">
                  {selected.club}
                  {selected.place ? ` — ${selected.place}` : ""}
                </Typography>
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
                    <Button size="sm" variant="soft" color="danger">
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
