import {
  Box,
  Button,
  Chip,
  IconButton,
  Sheet,
  Stack,
  Typography,
} from "@mui/joy";

export type SlotStatus = "booked" | "session" | "closed" | "available";

export type TimeSlot = {
  hour: number; // 0-23
  status: SlotStatus;
  title?: string;
  // Optional extra info for display (e.g., "Entraînement U17", "Seniors 1")
  subtitle?: string;
};

export type ScheduleProps = {
  date: Date;
  startHour?: number; // inclusive
  endHour?: number; // exclusive
  slots: TimeSlot[];
  onJoin?: (slot: TimeSlot) => void;
  onReserve?: (slot: TimeSlot) => void;
  onPrevDay?: () => void;
  onNextDay?: () => void;
};

const statusStyles: Record<
  SlotStatus,
  { bg: string; fg: string; border?: string }
> = {
  booked: {
    bg: "var(--joy-palette-danger-softBg)",
    fg: "var(--joy-palette-danger-800)",
  },
  session: {
    bg: "var(--joy-palette-primary-softBg)",
    fg: "var(--joy-palette-primary-800)",
  },
  closed: {
    bg: "var(--joy-palette-neutral-100)",
    fg: "var(--joy-palette-neutral-500)",
    border: "1px dashed var(--joy-palette-neutral-300)",
  },
  available: {
    bg: "var(--joy-palette-success-softBg)",
    fg: "var(--joy-palette-success-800)",
  },
};

export default function Schedule({
  date,
  startHour = 8,
  endHour = 20,
  slots,
  onJoin,
  onReserve,
  onPrevDay,
  onNextDay,
}: ScheduleProps) {
  const hours: number[] = [];
  for (let h = startHour; h < endHour; h++) hours.push(h);

  const findSlot = (h: number) => slots.find((s) => s.hour === h);

  const labelForDate = (d: Date) => {
    const today = new Date();
    const onlyDate = (x: Date) =>
      new Date(x.getFullYear(), x.getMonth(), x.getDate());
    const diff = Math.round(
      (onlyDate(d).getTime() - onlyDate(today).getTime()) / 86400000,
    );
    if (diff === 0) return "Aujourd'hui";
    if (diff === -1) return "Hier";
    if (diff === 1) return "Demain";
    return d.toLocaleDateString("fr-FR", {
      weekday: "long",
      day: "2-digit",
      month: "short",
    });
  };

  return (
    <Sheet variant="soft" sx={{ p: 1.25, borderRadius: 12 }}>
      <Stack
        direction="row"
        alignItems="center"
        justifyContent="space-between"
        sx={{ mb: 1 }}
      >
        <Stack direction="row" alignItems="center" gap={1}>
          <IconButton
            size="sm"
            variant="soft"
            onClick={onPrevDay}
            aria-label="Jour précédent"
          >
            ◀
          </IconButton>
          <Typography level="title-md">
            Planning — {labelForDate(date)}
          </Typography>
          <IconButton
            size="sm"
            variant="soft"
            onClick={onNextDay}
            aria-label="Jour suivant"
          >
            ▶
          </IconButton>
        </Stack>
        <Stack direction="row" gap={1}>
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

      <Stack gap={1}>
        {hours.map((h) => {
          const slot = findSlot(h);
          const label = `${String(h).padStart(2, "0")}:00`;
          const status: SlotStatus = slot?.status || "closed";
          const st = statusStyles[status];
          return (
            <Box
              key={h}
              sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                px: 1,
                py: 1,
                borderRadius: 10,
                background: st.bg,
                color: st.fg,
                border: st.border,
              }}
            >
              <Stack direction="row" gap={1} alignItems="center">
                <Typography level="title-sm" sx={{ minWidth: 64 }}>
                  {label}
                </Typography>
                <Typography level="body-sm">
                  {slot?.title ||
                    (status === "session"
                      ? "Séance"
                      : status === "booked"
                        ? "Déjà pris"
                        : "Indisponible")}
                </Typography>
              </Stack>
              {status === "session" ? (
                <Button
                  size="sm"
                  variant="solid"
                  color="primary"
                  onClick={() => slot && onJoin?.(slot)}
                >
                  Rejoindre
                </Button>
              ) : status === "available" ? (
                <Button
                  size="sm"
                  variant="solid"
                  color="success"
                  onClick={() => slot && onReserve?.(slot)}
                >
                  Réserver
                </Button>
              ) : null}
            </Box>
          );
        })}
      </Stack>
    </Sheet>
  );
}
