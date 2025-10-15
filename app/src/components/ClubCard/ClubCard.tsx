import React, { useRef, useEffect } from "react";
import type { Club } from "../../model/Club";
import { MapPin, Star, Users } from "lucide-react";
import {
  FaFootballBall,
  FaTableTennis,
  FaBasketballBall,
} from "react-icons/fa";
import { GiSoccerBall } from "react-icons/gi";
import { Card, Typography, Button, Chip, Box, Stack } from "@mui/joy";
import { useTheme } from "@mui/joy/styles";

const sportIcons: Record<string, React.ReactNode> = {
  Football: <GiSoccerBall size={49} color="#1E90FF" />,
  Basketball: <FaBasketballBall size={50} color="#FF8C00" />,
  Tennis: <FaTableTennis size={50} color="#32CD32" />,
  Rugby: <FaFootballBall size={49} color="#1E90FF" />,
};

type ClubCardProps = Club & {
  onJoin: () => void;
  onDetails: () => void;
  closing?: boolean;
  onCloseComplete?: () => void;
};

export default function ClubCard({
  name,
  sportType,
  distance,
  notation,
  addresse,
  members,
  onJoin,
  onDetails,
  closing,
  onCloseComplete,
}: ClubCardProps) {
  const theme = useTheme();

  // Animation slide up/down
  const cardRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    const card = cardRef.current;
    if (card) {
      // enter
      card.style.transform = "translateY(100%)";
      card.style.opacity = "0";
      // small timeout to allow initial styles to apply
      setTimeout(() => {
        card.style.transition =
          "transform 0.35s cubic-bezier(.4,0,.2,1), opacity 0.25s";
        card.style.transform = "translateY(0)";
        card.style.opacity = "1";
      }, 10);
    }
  }, []);

  // handle external closing request
  useEffect(() => {
    const card = cardRef.current;
    if (!card) return;
    let onTransitionEnd: ((e: TransitionEvent) => void) | null = null;
    if (closing) {
      // trigger exit animation
      card.style.transition =
        "transform 0.35s cubic-bezier(.4,0,.2,1), opacity 0.25s";
      card.style.transform = "translateY(100%)";
      card.style.opacity = "0";

      onTransitionEnd = (e: TransitionEvent) => {
        if (e.propertyName === "transform" || e.propertyName === "opacity") {
          if (typeof onCloseComplete === "function") {
            onCloseComplete();
          }
        }
      };
      card.addEventListener("transitionend", onTransitionEnd);
    }
    return () => {
      if (onTransitionEnd)
        card.removeEventListener("transitionend", onTransitionEnd);
    };
  }, [closing, onCloseComplete]);

  return (
    <Card
      ref={cardRef}
      variant="soft"
      sx={{
        display: "flex",
        gap: 2,
        bgcolor: "background.surface",
        background: "transparent",
        boxShadow: "sm",
        borderRadius: 12,
        p: 3,
        minWidth: 0,
        width: "100%",
        maxWidth: 720,
        mx: "auto",
        willChange: "transform, opacity",
        border: "1px solid var(--joy-palette-neutral-200, #e5e7eb)",
      }}
    >
      <Box
        sx={{
          minWidth: 60,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        {sportIcons[sportType.name]}
      </Box>
      <Stack spacing={1} sx={{ flex: 1, alignItems: "flex-start" }}>
        <Box
          sx={{ display: "flex", alignItems: "center", gap: 1, width: "100%" }}
        >
          <Typography level="h3" sx={{ fontWeight: 700, textAlign: "left" }}>
            {name}
          </Typography>
          <Chip color="primary" variant="soft">
            Ouvert
          </Chip>
        </Box>
        <Box
          sx={{ display: "flex", gap: 1, alignItems: "center", width: "100%" }}
        >
          <Typography
            level="body-sm"
            sx={{ color: "text.secondary", textAlign: "left" }}
          >
            {distance} km
          </Typography>
          <Chip size="sm" variant="soft" color="neutral">
            {sportType.name}
          </Chip>
          <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
            <Star size={16} fill="gold" color="gold" />
            <Typography level="body-sm" sx={{ textAlign: "left" }}>
              {notation}
            </Typography>
          </Box>
        </Box>
        <Box
          sx={{ display: "flex", alignItems: "center", gap: 1, width: "100%" }}
        >
          <MapPin
            size={16}
            color={theme.palette.mode === "dark" ? "#f8fafc" : "#1e293b"}
          />
          <Typography
            level="body-sm"
            sx={{ color: "text.secondary", textAlign: "left" }}
          >
            {addresse}
          </Typography>
        </Box>
        <Box
          sx={{ display: "flex", alignItems: "center", gap: 1, width: "100%" }}
        >
          <Users
            size={16}
            color={theme.palette.mode === "dark" ? "#f8fafc" : "#1e293b"}
          />
          <Typography
            level="body-sm"
            sx={{ color: "text.secondary", textAlign: "left" }}
          >
            {members} membres
          </Typography>
        </Box>
        <Box sx={{ display: "flex", gap: 2, mt: 1 }}>
          <Button variant="solid" color="primary" size="sm" onClick={onJoin}>
            Rejoindre
          </Button>
          <Button
            variant="outlined"
            color="neutral"
            size="sm"
            onClick={onDetails}
          >
            Voir plus
          </Button>
        </Box>
      </Stack>
    </Card>
  );
}
