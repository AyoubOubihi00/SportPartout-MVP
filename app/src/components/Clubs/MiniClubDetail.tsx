import Card from "@mui/joy/Card";
import Chip from "@mui/joy/Chip";
import Stack from "@mui/joy/Stack";
import Typography from "@mui/joy/Typography";
import Button from "@mui/joy/Button";
import Divider from "@mui/joy/Divider";
import Box from "@mui/joy/Box";
import { CalendarRange, MapPin, ShieldCheck } from "lucide-react";
import type { MemberWithClub } from "../../services/membershipService";

type MiniClubDetailProps = {
  membership: MemberWithClub;
  onViewDetails?: (clubId: string) => void;
};

function formatMembershipDate(date?: string | null): string | null {
  if (!date) return null;
  try {
    const d = new Date(date);
    return d.toLocaleDateString("fr-FR", {
      day: "2-digit",
      month: "long",
      year: "numeric",
    });
  } catch {
    return null;
  }
}

import { useTheme } from "@mui/joy/styles";

export default function MiniClubDetail({
  membership,
  onViewDetails,
}: MiniClubDetailProps) {
  const club = membership.club;
  const joinDate = formatMembershipDate(membership.dateSubscription);
  const statusLabel = membership.isActif ? "Actif" : "En attente";
  const statusColor = membership.isActif ? "success" : "neutral";
  const theme = useTheme();
  const mainColor = theme.palette.mode === "dark" ? "#fff" : "#000";
  const subtleBorder =
    theme.palette.mode === "dark" ? "neutral.700" : "neutral.300";
  const surfaceBg =
    theme.palette.mode === "dark"
      ? "linear-gradient(135deg, rgba(15,23,42,0.55), rgba(30,41,59,0.55))"
      : "linear-gradient(135deg, #ffffff, #f1f5f9)";

  return (
    <Card
      variant="outlined"
      sx={{
        borderRadius: 16,
        p: 3,
        boxShadow:
          theme.palette.mode === "dark"
            ? "0 4px 16px -4px rgba(0,0,0,0.4)"
            : "0 4px 16px -4px rgba(0,0,0,0.08)",
        border: "2px solid",
        borderColor: subtleBorder,
        background: surfaceBg,
        backdropFilter: theme.palette.mode === "dark" ? "blur(4px)" : "none",
        color: mainColor,
      }}
    >
      <Stack spacing={1.5}>
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
          spacing={1}
        >
          <Typography
            level="title-lg"
            sx={{ fontWeight: 700, color: mainColor }}
          >
            {club?.name ?? "Club inconnu"}
          </Typography>
          <Chip color={statusColor} variant="soft" size="sm">
            {statusLabel}
          </Chip>
        </Stack>

        <Stack
          direction="row"
          spacing={1}
          alignItems="center"
          sx={{ color: "text.secondary" }}
        >
          {club?.typeName && (
            <Chip size="sm" variant="soft" color="primary">
              {club.typeName}
            </Chip>
          )}
          {membership.himself && (
            <Chip
              size="sm"
              variant="soft"
              color="neutral"
              startDecorator={<ShieldCheck size={14} />}
            >
              Adhesion directe
            </Chip>
          )}
        </Stack>

        <Stack
          spacing={1.25}
          sx={{
            color:
              theme.palette.mode === "dark" ? "neutral.300" : "text.secondary",
          }}
        >
          {(club?.address || club?.city || club?.postalCode) && (
            <Stack
              direction="row"
              spacing={1}
              alignItems="center"
              sx={{
                color:
                  theme.palette.mode === "dark"
                    ? "neutral.400"
                    : "text.secondary",
              }}
            >
              <MapPin size={16} />
              <Typography level="body-sm">
                {[club?.address, club?.postalCode, club?.city]
                  .filter(Boolean)
                  .join(" ")}
              </Typography>
            </Stack>
          )}

          {joinDate && (
            <Stack
              direction="row"
              spacing={1}
              alignItems="center"
              sx={{
                color:
                  theme.palette.mode === "dark"
                    ? "neutral.400"
                    : "text.secondary",
              }}
            >
              <CalendarRange size={16} />
              <Typography level="body-sm">
                Adherent depuis le {joinDate}
              </Typography>
            </Stack>
          )}
        </Stack>

        <Divider />

        <Box
          sx={{
            display: "flex",
            gap: 1.5,
            flexWrap: "wrap",
            justifyContent: onViewDetails ? "space-between" : "flex-start",
          }}
        >
          {club?.email && (
            <Typography
              level="body-sm"
              sx={{
                color:
                  theme.palette.mode === "dark"
                    ? "neutral.400"
                    : "text.secondary",
              }}
            >
              Email: {club.email}
            </Typography>
          )}
          {club?.phoneNumber && (
            <Typography
              level="body-sm"
              sx={{
                color:
                  theme.palette.mode === "dark"
                    ? "neutral.400"
                    : "text.secondary",
              }}
            >
              Tel: {club.phoneNumber}
            </Typography>
          )}
        </Box>

        {onViewDetails && membership.clubId && (
          <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
            <Button
              size="sm"
              variant="solid"
              onClick={() => onViewDetails(membership.clubId)}
              sx={{
                backgroundColor: mainColor,
                color: theme.palette.mode === "dark" ? "#121212" : "#fff",
                fontWeight: 600,
                boxShadow: "none",
                "&:hover": { backgroundColor: mainColor },
              }}
            >
              Voir le club
            </Button>
          </Box>
        )}
      </Stack>
    </Card>
  );
}
