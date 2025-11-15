import Card from "@mui/joy/Card";
import Chip from "@mui/joy/Chip";
import Stack from "@mui/joy/Stack";
import Typography from "@mui/joy/Typography";
import Button from "@mui/joy/Button";
import Divider from "@mui/joy/Divider";
import Box from "@mui/joy/Box";
import { MapPin, Phone, Mail, Briefcase, Power } from "lucide-react";
import type { ClubSnapshot } from "../../services/membershipService";

type ManagedClubDetailProps = {
  club: ClubSnapshot;
  onViewDetails?: (clubId: string) => void;
};

import { useTheme } from "@mui/joy/styles";

export default function ManagedClubDetail({
  club,
  onViewDetails,
}: ManagedClubDetailProps) {
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
            {club.name ?? "Club sans nom"}
          </Typography>
          <Stack direction="row" gap={1} alignItems="center">
            <Chip
              color={club.isActive ? "success" : "danger"}
              variant="soft"
              size="sm"
              startDecorator={<Power size={14} />}
            >
              {club.isActive ? "Actif" : "Inactif"}
            </Chip>
          </Stack>
        </Stack>

        <Stack
          direction="row"
          spacing={1}
          alignItems="center"
          sx={{ color: "text.secondary" }}
        >
          {club.typeName && (
            <Chip
              size="sm"
              variant="soft"
              color="primary"
              startDecorator={<Briefcase size={14} />}
            >
              {club.typeName}
            </Chip>
          )}
        </Stack>

        {(club.address || club.postalCode || club.city) && (
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
              {[club.address, club.postalCode, club.city]
                .filter(Boolean)
                .join(" ")}
            </Typography>
          </Stack>
        )}

        <Divider />

        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            gap: 0.75,
            color:
              theme.palette.mode === "dark" ? "neutral.400" : "text.secondary",
          }}
        >
          {club.email && (
            <Stack direction="row" spacing={1} alignItems="center">
              <Mail size={16} />
              <Typography level="body-sm">{club.email}</Typography>
            </Stack>
          )}
          {club.phoneNumber && (
            <Stack direction="row" spacing={1} alignItems="center">
              <Phone size={16} />
              <Typography level="body-sm">{club.phoneNumber}</Typography>
            </Stack>
          )}
        </Box>

        {onViewDetails && club.id && (
          <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
            <Button
              size="sm"
              variant="solid"
              onClick={() => onViewDetails(club.id)}
              sx={{
                backgroundColor: mainColor,
                color: theme.palette.mode === "dark" ? "#121212" : "#fff",
                fontWeight: 600,
                boxShadow: "none",
                "&:hover": { backgroundColor: mainColor },
              }}
            >
              Gérer le club
            </Button>
          </Box>
        )}
      </Stack>
    </Card>
  );
}
