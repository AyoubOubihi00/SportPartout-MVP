import { useEffect, useMemo, useRef, useState } from "react";
import FullCalendar from "@fullcalendar/react";
import type { EventClickArg } from "@fullcalendar/core";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
// CSS for FullCalendar (v6)
// Note: FullCalendar v6.1.19 doesn't export CSS via package exports.
// We avoid importing CSS files from node_modules to keep Vite happy.
// Basic styles are provided below via a <style> tag to ensure usability.
import frLocale from "@fullcalendar/core/locales/fr";
import {
  Chip,
  IconButton,
  Stack,
  Typography,
  Button,
  Sheet,
  Alert,
} from "@mui/joy";
import { ChevronLeft, ChevronRight, RotateCw } from "lucide-react";
import type { ScheduleProps, SlotStatus, TimeSlot } from "./Schedule";

/*
  Backend integration (commented scaffolding)
  -----------------------------------------
  Plan to replace `slots` prop with server data when backend is ready.

  Recommended endpoints
  - GET /clubs/:clubId/schedule?date=YYYY-MM-DD&tz=Europe/Paris
    -> { slots: BackendSlot[] }
  - POST /clubs/:clubId/slots/:slotId/join
  - POST /clubs/:clubId/slots/:slotId/reserve

  Suggested client types
  // type BackendSlot = {
  //   id: string;
  //   start: string; // ISO 8601 in UTC, e.g. "2025-10-18T10:00:00Z"
  //   end: string;   // ISO 8601 in UTC
  //   status: 'booked' | 'session' | 'available' | 'closed';
  //   title?: string;
  //   capacity?: number;
  //   reserved?: number;
  // };

  Example fetcher (axios/fetch)
  // async function fetchSchedule(clubId: string, dateISO: string, tz = 'Europe/Paris'): Promise<BackendSlot[]> {
  //   const res = await fetch(`/api/clubs/${clubId}/schedule?date=${dateISO}&tz=${tz}`);
  //   if (!res.ok) throw new Error('Failed to load schedule');
  //   const data = await res.json();
  //   return data.slots as BackendSlot[];
  // }

  Mapping server -> FullCalendar events
  // function mapBackendSlotsToEvents(slots: BackendSlot[]) {
  //   return slots.map((s) => ({
  //     id: s.id,
  //     title: s.title || (s.status === 'session' ? 'Séance' : s.status === 'booked' ? 'Pris' : s.status === 'available' ? 'Disponible' : ''),
  //     start: new Date(s.start),
  //     end: new Date(s.end),
  //     classNames: [s.status === 'closed' ? 'fc-closed-slot-bg' : `fc-${s.status}-slot`],
  //     display: s.status === 'closed' ? 'background' : undefined,
  //     extendedProps: { status: s.status, slotId: s.id },
  //   }));
  // }

  React Query (optional)
  // import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
  // const q = useQuery({
  //   queryKey: ['schedule', clubId, dateISO],
  //   queryFn: () => fetchSchedule(clubId, dateISO),
  //   staleTime: 60_000,
  // });
  // const events = useMemo(() => q.data ? mapBackendSlotsToEvents(q.data) : [], [q.data]);

  Actions
  // const join = useMutation({
  //   mutationFn: async (slotId: string) => { await fetch(`/api/clubs/${clubId}/slots/${slotId}/join`, { method: 'POST' }); },
  //   onSuccess: () => queryClient.invalidateQueries({ queryKey: ['schedule', clubId] }),
  // });
  // const reserve = useMutation({
  //   mutationFn: async (slotId: string) => { await fetch(`/api/clubs/${clubId}/slots/${slotId}/reserve`, { method: 'POST' }); },
  //   onSuccess: () => queryClient.invalidateQueries({ queryKey: ['schedule', clubId] }),
  // });

  Notes
  - Conserver les dates en ISO UTC côté API et transmettre un paramètre tz (Europe/Paris) pour éviter les décalages.
  - Ici, la vue semaine réplique le pattern de la journée locale. Avec le backend, renvoyer les créneaux pour toute la période demandée.
  - En cas de chargement/erreur, envisager Skeleton Joy + message d’erreur avec bouton "Réessayer".
*/

function toTimeStr(h: number) {
  return `${String(h).padStart(2, "0")}:00:00`;
}

function dayLabelFR(d: Date) {
  const today = new Date();
  const onlyDate = (x: Date) =>
    new Date(x.getFullYear(), x.getMonth(), x.getDate()).getTime();
  const diff = Math.round((onlyDate(d) - onlyDate(today)) / 86400000);
  if (diff === 0) return "Aujourd'hui";
  if (diff === -1) return "Hier";
  if (diff === 1) return "Demain";
  return d.toLocaleDateString("fr-FR", {
    weekday: "long",
    day: "2-digit",
    month: "short",
  });
}

function weekRangeLabelFR(d: Date) {
  const base = new Date(d.getFullYear(), d.getMonth(), d.getDate());
  const start = new Date(base);
  const dow = (start.getDay() + 6) % 7; // Monday=0
  start.setDate(start.getDate() - dow);
  const end = new Date(start);
  end.setDate(start.getDate() + 6);
  const fmt = (x: Date) =>
    x.toLocaleDateString("fr-FR", { day: "2-digit", month: "short" });
  return `${fmt(start)} – ${fmt(end)}`;
}

export default function FullCalendarSchedule({
  date,
  startHour = 8,
  endHour = 20,
  slots,
  onJoin,
  onReserve,
  onPrevDay,
  onNextDay,
}: ScheduleProps) {
  const calRef = useRef<FullCalendar | null>(null);
  const [view, setView] = useState<"timeGridDay" | "timeGridWeek">(
    "timeGridDay",
  );
  const [isMobile, setIsMobile] = useState(false);
  const [rotateHint, setRotateHint] = useState<null | "landscape" | "portrait">(
    null,
  );

  const events = useMemo(() => {
    const base = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    const arr: Array<{
      id: string;
      title: string;
      start: Date;
      end: Date;
      extendedProps: { status: SlotStatus; slot?: TimeSlot };
      display?: "background";
      classNames?: string[];
    }> = [];
    const days = view === "timeGridWeek" ? 7 : 1;
    // find Monday of the week for FR locale
    const monday = new Date(base);
    const day = (monday.getDay() + 6) % 7; // 0 for Monday
    monday.setDate(monday.getDate() - day);

    for (let d = 0; d < days; d++) {
      const theDay =
        view === "timeGridWeek"
          ? new Date(
              monday.getFullYear(),
              monday.getMonth(),
              monday.getDate() + d,
            )
          : base;
      for (let h = startHour; h < endHour; h++) {
        const slot = slots.find((s) => s.hour === h);
        const st: SlotStatus = slot?.status ?? "closed";
        const start = new Date(
          theDay.getFullYear(),
          theDay.getMonth(),
          theDay.getDate(),
          h,
          0,
          0,
        );
        const end = new Date(
          theDay.getFullYear(),
          theDay.getMonth(),
          theDay.getDate(),
          h + 1,
          0,
          0,
        );
        if (st === "closed") {
          // Grise les créneaux indisponibles en arrière-plan (jour + semaine)
          arr.push({
            id: `d${d}-h${h}`,
            title: "",
            start,
            end,
            display: "background",
            classNames: ["fc-closed-slot-bg"],
            extendedProps: { status: st },
          });
        } else {
          const title =
            slot?.title ||
            (st === "session"
              ? "Séance"
              : st === "booked"
                ? "Pris"
                : "Disponible");
          arr.push({
            id: `d${d}-h${h}`,
            title,
            start,
            end,
            classNames: [`fc-${st}-slot`],
            extendedProps: { status: st, slot },
          });
        }
      }
    }
    return arr;
  }, [date, startHour, endHour, slots, view]);

  useEffect(() => {
    const api = calRef.current?.getApi?.();
    if (api) api.gotoDate(date);
  }, [date]);

  useEffect(() => {
    const api = calRef.current?.getApi?.();
    if (api) api.changeView(view);
  }, [view]);

  useEffect(() => {
    if (typeof window === "undefined") return;
    const mq = window.matchMedia("(max-width: 640px)");
    const handler = () => setIsMobile(mq.matches);
    handler();
    mq.addEventListener("change", handler);
    return () => mq.removeEventListener("change", handler);
  }, []);

  // Orientation changes are handled with best-effort screen.orientation.lock when toggling views.

  type OrientationLock =
    | "landscape"
    | "portrait"
    | "landscape-primary"
    | "landscape-secondary"
    | "portrait-primary"
    | "portrait-secondary";

  async function tryLock(mode: "landscape" | "portrait"): Promise<boolean> {
    try {
      type ScreenWithLock = Screen & {
        orientation?: ScreenOrientation & {
          lock?: (m: OrientationLock) => Promise<void>;
        };
      };
      const scr = screen as ScreenWithLock;
      if (scr.orientation?.lock) {
        await scr.orientation.lock(mode);
        return true;
      }
    } catch {
      // no-op: orientation lock is best-effort and may fail on some platforms
    }
    return false;
  }

  function handleSetDay() {
    if (isMobile) {
      // Best-effort: try to rotate to portrait
      tryLock("portrait").then((ok) => setRotateHint(ok ? null : "portrait"));
    }
    setView("timeGridDay");
  }

  function handleSetWeek() {
    if (isMobile) {
      // Best-effort: try to rotate to landscape
      tryLock("landscape").then((ok) => setRotateHint(ok ? null : "landscape"));
    }
    setView("timeGridWeek");
  }

  function onEventClick(arg: EventClickArg) {
    type Ext = { status: SlotStatus; slot?: TimeSlot };
    const e = arg.event.extendedProps as unknown as Ext;
    const status: SlotStatus = e.status;
    const slot: TimeSlot | undefined = e.slot;
    if (status === "session" && slot) onJoin?.(slot);
    if (status === "available" && slot) onReserve?.(slot);
  }

  return (
    <Sheet variant="soft" sx={{ p: 1.25, borderRadius: 12 }}>
      <Stack direction="column" sx={{ mb: 1, gap: 0.5 }}>
        <Stack
          direction="row"
          alignItems="center"
          justifyContent="space-between"
        >
          <Stack direction="row" alignItems="center" gap={1}>
            <IconButton
              size="sm"
              variant="soft"
              onClick={() => {
                if (view === "timeGridWeek") {
                  for (let i = 0; i < 7; i++) onPrevDay?.();
                } else {
                  onPrevDay?.();
                }
              }}
              aria-label={
                view === "timeGridWeek"
                  ? "Semaine précédente"
                  : "Jour précédent"
              }
            >
              <ChevronLeft size={16} />
            </IconButton>
            <Typography level="title-md">
              {view === "timeGridDay"
                ? dayLabelFR(date)
                : weekRangeLabelFR(date)}
            </Typography>
            <IconButton
              size="sm"
              variant="soft"
              onClick={() => {
                if (view === "timeGridWeek") {
                  for (let i = 0; i < 7; i++) onNextDay?.();
                } else {
                  onNextDay?.();
                }
              }}
              aria-label={
                view === "timeGridWeek" ? "Semaine suivante" : "Jour suivant"
              }
            >
              <ChevronRight size={16} />
            </IconButton>
          </Stack>
          <Stack direction="row" gap={0.75}>
            <Button
              size="sm"
              variant={view === "timeGridDay" ? "solid" : "outlined"}
              color="neutral"
              onClick={handleSetDay}
            >
              Jour
            </Button>
            <Button
              size="sm"
              variant={view === "timeGridWeek" ? "solid" : "outlined"}
              color="neutral"
              onClick={handleSetWeek}
            >
              Semaine
            </Button>
          </Stack>
        </Stack>
        <Stack
          direction="row"
          gap={1}
          alignItems="center"
          sx={{ flexWrap: "wrap" }}
        >
          <Chip size="sm" variant="soft" color="danger">
            Pris
          </Chip>
          <Chip size="sm" variant="soft" color="primary">
            Séance
          </Chip>
          <Chip size="sm" variant="soft" color="success">
            Disponible
          </Chip>
          <Chip size="sm" variant="soft" color="neutral">
            Indisponible
          </Chip>
        </Stack>
      </Stack>

      {rotateHint && isMobile && (
        <Alert
          variant="soft"
          color="warning"
          size="sm"
          sx={{ mb: 1, alignItems: "center" }}
        >
          <RotateCw size={16} style={{ marginRight: 8 }} />
          {rotateHint === "landscape"
            ? "Pour la vue semaine, tournez votre appareil en paysage."
            : "Pour la vue jour, tournez votre appareil en portrait."}
        </Alert>
      )}

      <FullCalendar
        ref={calRef}
        plugins={[timeGridPlugin, interactionPlugin]}
        initialView={view}
        headerToolbar={false}
        locales={[frLocale]}
        locale="fr"
        allDaySlot={false}
        nowIndicator={true}
        slotEventOverlap={false}
        height="auto"
        slotDuration={"01:00:00"}
        slotLabelInterval={"01:00:00"}
        slotMinTime={toTimeStr(startHour)}
        slotMaxTime={toTimeStr(endHour)}
        dayHeaderFormat={
          view === "timeGridDay"
            ? { weekday: "long", day: "2-digit", month: "long" }
            : { weekday: "short", day: "2-digit" }
        }
        slotLabelFormat={{ hour: "2-digit", minute: "2-digit", hour12: false }}
        events={events}
        eventClick={onEventClick}
        eventContent={(arg) => {
          type Ext = { status: SlotStatus; slot?: TimeSlot };
          const ext = arg.event.extendedProps as unknown as Ext;
          const status: SlotStatus = ext.status;
          const slot: TimeSlot | undefined = ext.slot;
          const isWeek = view === "timeGridWeek";
          const isDay = !isWeek;
          const showJoin = isDay && status === "session" && slot;
          const showReserve = isDay && status === "available" && slot;
          const subtitle: string | undefined = slot?.subtitle;
          if (isDay) {
            // Vue Jour: contenu aligné à gauche, sous-titre sur la même ligne: "Séance — Entraînement U17"
            return (
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  width: "100%",
                  gap: 8,
                  color: "var(--joy-palette-text-primary)",
                }}
              >
                <span
                  style={{
                    display: "flex",
                    alignItems: "baseline",
                    gap: 6,
                    minWidth: 0,
                  }}
                >
                  <span style={{ fontSize: "0.92em", fontWeight: 800 }}>
                    {arg.event.title}
                  </span>
                  {subtitle ? (
                    <span
                      style={{
                        fontSize: "0.86em",
                        color: "var(--joy-palette-text-secondary)",
                        whiteSpace: "nowrap",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                      }}
                    >
                      — {subtitle}
                    </span>
                  ) : null}
                </span>
                {showJoin ? (
                  <Button
                    size="sm"
                    variant="solid"
                    color="primary"
                    onClick={(e) => {
                      e.stopPropagation();
                      onJoin?.(slot!);
                    }}
                  >
                    Rejoindre
                  </Button>
                ) : showReserve ? (
                  <Button
                    size="sm"
                    variant="solid"
                    color="success"
                    onClick={(e) => {
                      e.stopPropagation();
                      onReserve?.(slot!);
                    }}
                  >
                    Réserver
                  </Button>
                ) : null}
              </div>
            );
          }
          // Vue Semaine: contenu compact et centré (encore plus compact en mobile)
          return (
            <div
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                width: "100%",
                color: "var(--joy-palette-text-primary)",
              }}
            >
              {isMobile ? (
                <span
                  style={{
                    fontSize: "0.8em",
                    fontWeight: 800,
                    textAlign: "center",
                    whiteSpace: "nowrap",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    maxWidth: "100%",
                  }}
                >
                  {arg.event.title}
                  {subtitle ? ` — ${subtitle}` : ""}
                </span>
              ) : (
                <span
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: 1,
                    textAlign: "center",
                  }}
                >
                  <span style={{ fontSize: "0.86em", fontWeight: 800 }}>
                    {arg.event.title}
                  </span>
                  {subtitle ? (
                    <span
                      style={{
                        fontSize: "0.74em",
                        color: "var(--joy-palette-text-secondary)",
                      }}
                    >
                      {subtitle}
                    </span>
                  ) : null}
                </span>
              )}
            </div>
          );
        }}
        eventClassNames={(arg) => {
          const status: SlotStatus = (
            arg.event.extendedProps as unknown as { status: SlotStatus }
          ).status;
          return [`fc-slot`, `fc-slot-${status}`];
        }}
      />

      <style>{`
        /* Minimal FullCalendar basics (subset of upstream styles) */
        :root {
          --fc-border-color: var(--joy-palette-neutral-200);
          --fc-now-indicator-color: var(--joy-palette-danger-500);
          --fc-event-text-color: var(--joy-palette-text-primary);
          --fc-today-bg-color: transparent; /* remove yellow/orange today background */
          --fc-highlight-color: rgba(0,0,0,0.06); /* subtle select highlight */
        }
        .fc { display: flex; flex-direction: column; font-size: 1em; }
        .fc, .fc *, .fc *::before, .fc *::after { box-sizing: border-box; }
        .fc table { border-collapse: collapse; border-spacing: 0; width: 100%; }
        .fc th { text-align: center; }
        .fc td, .fc th { padding: 0; vertical-align: top; }
        .fc-theme-standard td, .fc-theme-standard th { border: 1px solid var(--fc-border-color); }
        .fc .fc-scrollgrid { border-collapse: separate; border-right-width: 0; border-bottom-width: 0; }
        .fc .fc-scrollgrid, .fc .fc-view-harness, .fc .fc-view-harness-active { border-radius: 12px; overflow: hidden; }
        .fc .fc-col-header { position: sticky; top: 0; z-index: 2; background: var(--joy-palette-neutral-100); }
        .fc .fc-timegrid-axis { background: var(--joy-palette-neutral-100); }
        .fc .fc-col-header-cell-cushion { display: block; padding: 6px 8px; font-weight: 600; text-transform: capitalize; }
        .fc .fc-timegrid-slot { height: 32px; }
        .fc .fc-timegrid-axis-cushion, .fc .fc-scrollgrid-shrink-cushion { padding: 6px 8px; color: var(--joy-palette-text-secondary); }
        .fc .fc-timegrid-divider { border-left: 1px solid var(--fc-border-color); }
        .fc .fc-timegrid-slot:hover { background: rgba(0,0,0,0.03); }
        .fc .fc-event { border-radius: 10px; overflow: hidden; border: 1px solid rgba(0,0,0,0.06); padding: 2px 6px; transition: transform .12s ease, box-shadow .12s ease; color: var(--joy-palette-text-primary) !important; }
        .fc .fc-event:hover { transform: translateY(-1px); box-shadow: 0 4px 10px rgba(0,0,0,0.08); }
        .fc .fc-bg-event { opacity: 1; }
        .fc .fc-timegrid-now-indicator-line { border-top: 2px solid var(--fc-now-indicator-color); }
        /* Remove default right-side gutter so events align with bg */
        .fc-direction-ltr .fc-timegrid-col-events { margin: 0 0 0 2px !important; }
        .fc-direction-rtl .fc-timegrid-col-events { margin: 0 2px 0 0 !important; }

        /* Use Joy tokens for categories */
        .fc .fc-event.fc-slot-session { background: var(--joy-palette-primary-softBg); }
        .fc .fc-event.fc-slot-available { background: var(--joy-palette-success-softBg); }
        .fc .fc-event.fc-slot-booked { background: var(--joy-palette-danger-softBg); }
        .fc .fc-bg-event.fc-closed-slot-bg { background: var(--joy-palette-neutral-300); opacity: .25; border: none; }
        /* remove today yellow background fallback */
        .fc .fc-day-today, .fc .fc-timegrid-col.fc-day-today { background: transparent !important; }

        /* Responsive tweaks for small screens */
        @media (max-width: 640px) {
          .fc .fc-timegrid-slot { height: 28px; }
          .fc .fc-col-header-cell-cushion { padding: 4px 6px; font-size: 0.95em; }
          .fc .fc-event { padding: 2px 4px; }
        }
      `}</style>
    </Sheet>
  );
}
