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
      display?: "background";
      classNames: string[];
      extendedProps: { status: SlotStatus; slot?: TimeSlot };
    }> = [];

    const days = view === "timeGridWeek" ? 7 : 1;

    // Trouver le lundi de la semaine
    const monday = new Date(base);
    const dayOffset = (monday.getDay() + 6) % 7;
    monday.setDate(monday.getDate() - dayOffset);

    for (let d = 0; d < days; d++) {
      const theDay =
        view === "timeGridWeek"
          ? new Date(
              monday.getFullYear(),
              monday.getMonth(),
              monday.getDate() + d,
            )
          : base;

      // 1) Créneaux fermés (background) par heure non couverte
      for (let h = startHour; h < endHour; h++) {
        const start = new Date(theDay);
        start.setHours(h, 0, 0);

        const end = new Date(theDay);
        end.setHours(h + 1, 0, 0);

        const isCovered = slots.some(
          (s) => h >= s.startHour! && h < s.endHour && s.status !== "closed",
        );

        if (!isCovered) {
          arr.push({
            id: `closed-${d}-${h}`,
            title: "",
            start,
            end,
            display: "background",
            classNames: ["fc-closed-slot-bg"],
            extendedProps: { status: "closed" },
          });
        }
      }

      // 2) Créneaux réels multi-heures
      for (const slot of slots) {
        const { startHour, endHour, status } = slot;

        const start = new Date(theDay);
        start.setHours(startHour, 0, 0);

        const end = new Date(theDay);
        end.setHours(endHour, 0, 0);

        const title =
          slot.title ??
          (status === "session"
            ? "Séance"
            : status === "booked"
              ? "Pris"
              : "Disponible");

        arr.push({
          id: `slot-${d}-${startHour}-${endHour}`,
          title,
          start,
          end,
          classNames: [`fc-${status}-slot`],
          extendedProps: { status, slot },
        });
      }
    }

    return arr;
  }, [date, startHour, endHour, slots, view]);

  useEffect(() => {
    const api = calRef.current?.getApi?.();
    console.log("Navigating to date:", date);
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
    <Sheet
      variant="soft"
      sx={{ p: 1.25, borderRadius: 12, background: "transparent" }}
    >
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
            // Vue Jour: titre + sous-titre empilés (sous-titre en dessous)
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
                <div
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: 4,
                    minWidth: 0,
                    overflow: "hidden",
                  }}
                >
                  <span
                    style={{
                      fontSize: "0.95em",
                      fontWeight: 800,
                      lineHeight: 1.1,
                      whiteSpace: "nowrap",
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                    }}
                  >
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
                      {subtitle}
                    </span>
                  ) : null}
                </div>
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
                    display: "flex",
                    flexDirection: "column",
                    gap: 2,
                    fontSize: "0.8em",
                    textAlign: "center",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                    maxWidth: "100%",
                  }}
                >
                  <span style={{ fontWeight: 800 }}>{arg.event.title}</span>
                  {subtitle ? (
                    <span
                      style={{
                        fontSize: "0.75em",
                        color: "var(--joy-palette-text-secondary)",
                      }}
                    >
                      {subtitle}
                    </span>
                  ) : null}
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
          return ["fc-slot", `fc-slot-${status}`];
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
  .fc .fc-timegrid-slot { height: 40px; }
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
          .fc .fc-timegrid-slot { height: 36px; }
          .fc .fc-col-header-cell-cushion { padding: 4px 6px; font-size: 0.95em; }
          .fc .fc-event { padding: 2px 4px; }
        }
        .fc .fc-timegrid-slot {
          background: transparent !important;
        }

        .fc .fc-timegrid-axis {
          background: transparent !important;
        }

        .fc .fc-scrollgrid,
        .fc .fc-timegrid-slots {
          background: transparent !important;
        }

        .fc .fc-timegrid-slot:hover {
          background: transparent !important;
        }

        /* En-tête des colonnes (jours/dates) transparent */
.fc .fc-col-header,
.fc .fc-col-header .fc-scrollgrid-sync-table,
.fc .fc-col-header-cell {
  color: remain inherit !important;
  background: transparent !important;
}

.fc .fc-scrollgrid-section-sticky > * {
  background: transparent !important;      
}

.fc-col-header-cell-cushion{
  color: var(--joy-palette-text-primary) !important;
}

      `}</style>
    </Sheet>
  );
}
