import type { Club } from "../../model/Club";
import wazeIcon from "../../assets/waze-icon.svg";
import mapsIcon from "../../assets/maps-icon.png";
import {
  Sheet,
  Stack,
  Box,
  Typography,
  Chip,
  Button,
  Dropdown,
  MenuButton,
  Menu,
  MenuItem,
  List,
  ListItem,
  ListItemDecorator,
  Link,
  Tabs,
  TabList,
  Tab,
  TabPanel,
} from "@mui/joy";
import FullCalendarSchedule from "../Schedule/FullCalendarSchedule";
import { getScheduleForClub } from "../../mock/schedule";
import { useMemo, useState } from "react";

type ClubDetailsProps = { club: Club };

function sportEmoji(sport?: string) {
  const s = (sport || "").toLowerCase();
  if (s.includes("foot")) return "⚽";
  if (s.includes("basket")) return "🏀";
  if (s.includes("tennis")) return "🎾";
  if (s.includes("rugby")) return "🏉";
  return "🏅";
}

export default function ClubDetails({ club }: ClubDetailsProps) {
  const phoneHref = club.phoneNumber ? `tel:${club.phoneNumber}` : undefined;
  const mailHref = club.email ? `mailto:${club.email}` : undefined;
  const fullAddress = `${club.addresse}${club.city ? `, ${club.city}` : ""}${club.postalCode ? ` ${club.postalCode}` : ""}${club.country ? `, ${club.country}` : ""}`;

  const [tab, setTab] = useState(0);
  const [currentDate, setCurrentDate] = useState<Date>(new Date());
  const scheduleSlots = useMemo(
    () => getScheduleForClub(club.id, currentDate),
    [club.id, currentDate],
  );
  const START_HOUR = 8; // customize here
  const END_HOUR = 20; // customize here

  function openExternalApp(primaryUrl: string, fallbackUrl: string) {
    const onVisibility = () => {
      if (document.hidden) {
        window.clearTimeout(timeoutId);
        document.removeEventListener("visibilitychange", onVisibility);
      }
    };
    document.addEventListener("visibilitychange", onVisibility);
    const timeoutId = window.setTimeout(() => {
      document.removeEventListener("visibilitychange", onVisibility);
      window.open(fallbackUrl, "_blank");
    }, 1200);
    window.location.href = primaryUrl;
  }

  function openWaze() {
    const [lat, lon] = club.position;
    openExternalApp(
      `waze://?ll=${lat}%2C${lon}&navigate=yes`,
      `https://waze.com/ul?ll=${lat}%2C${lon}&navigate=yes`,
    );
  }
  function openGoogleMaps() {
    const [lat, lon] = club.position;
    openExternalApp(
      `comgooglemaps://?daddr=${lat}%2C${lon}&directionsmode=driving`,
      `https://www.google.com/maps/dir/?api=1&destination=${lat}%2C${lon}`,
    );
  }

  return (
    <Sheet
      variant="plain"
      sx={{
        width: "100%",
        maxWidth: 720,
        mx: "auto",
        borderRadius: 12,
        p: 1,
        bgcolor: "background.surface",
        background: "transparent",
        border: "1px solid var(--joy-palette-neutral-200, #e5e7eb)",
        boxShadow: "sm",
      }}
    >
      {/* Header */}
      <Stack
        direction="row"
        alignItems="center"
        justifyContent="space-between"
        sx={{ px: 1, py: 1 }}
      >
        <Stack
          direction="row"
          gap={1.25}
          alignItems="center"
          sx={{ minWidth: 0, flex: 1 }}
        >
          <Box
            aria-hidden
            sx={{
              width: 42,
              height: 42,
              display: "grid",
              placeItems: "center",
              bgcolor: "#f1f3f5",
              borderRadius: 12,
              fontSize: 20,
            }}
          >
            {sportEmoji(club.sportType.name)}
          </Box>
          <Box sx={{ minWidth: 0 }}>
            <Typography
              level="title-lg"
              sx={{
                mb: 0.5,
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
              }}
            >
              {club.name}
            </Typography>
            <Stack
              direction="row"
              alignItems="center"
              gap={1}
              sx={{ color: "#555", flexWrap: "wrap" }}
            >
              <Chip
                size="sm"
                variant="soft"
                color="primary"
                sx={{ fontWeight: 600 }}
              >
                {club.sportType.name}
              </Chip>
              <Typography
                level="body-sm"
                sx={{ fontWeight: 600 }}
                aria-label={`Note ${club.notation}/5`}
              >
                <Typography component="span" sx={{ color: "#f5a623", mr: 0.5 }}>
                  ★
                </Typography>
                {club.notation?.toFixed
                  ? club.notation.toFixed(1)
                  : club.notation}
              </Typography>
              <Typography level="body-sm" sx={{ color: "#666" }}>
                à {club.distance} km de vous
              </Typography>
            </Stack>
          </Box>
        </Stack>

        <Dropdown>
          <MenuButton
            variant="outlined"
            sx={{ borderRadius: 10, px: 1.25, py: 0.5 }}
            endDecorator={
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                aria-hidden
              >
                <path d="M7 10l5 5 5-5" stroke="#222" strokeWidth="1.5" />
              </svg>
            }
          >
            Itinéraire
          </MenuButton>
          <Menu
            placement="bottom-end"
            aria-label="Choisir l'application de navigation"
            disablePortal
          >
            <MenuItem onClick={openWaze}>
              <ListItemDecorator>
                <img
                  src={wazeIcon}
                  width={20}
                  height={20}
                  alt=""
                  aria-hidden="true"
                />
              </ListItemDecorator>
              Waze
            </MenuItem>
            <MenuItem onClick={openGoogleMaps}>
              <ListItemDecorator>
                <img
                  src={mapsIcon}
                  width={20}
                  height={20}
                  alt=""
                  aria-hidden="true"
                />
              </ListItemDecorator>
              Google Maps
            </MenuItem>
          </Menu>
        </Dropdown>
      </Stack>

      {/* Tabs en haut */}
      <Tabs
        value={tab}
        onChange={(_, v) => setTab(Number(v))}
        sx={{ mx: 0.5, mt: 1 }}
      >
        <TabList
          variant="soft"
          color="neutral"
          sx={{
            width: "100%",
            p: 0,
            borderRadius: 12,
            bgcolor: "var(--variant-softBg)",
            background: "var(--variant-softBg)",
          }}
        >
          <Tab>Description</Tab>
          <Tab>Planning</Tab>
        </TabList>

        <TabPanel value={0} sx={{ px: 0, pt: 1 }}>
          {club.description && (
            <Typography
              level="body-sm"
              sx={{ fontStyle: "italic", color: "#7c7c7cff", mx: 1.25, mb: 1 }}
            >
              {club.description}
            </Typography>
          )}

          {/* Contact */}
          <Sheet
            variant="soft"
            sx={{
              p: 1.25,
              m: "8px 4px",
              borderRadius: 12,
              bgcolor: "background.level1",
              background: (t) =>
                t.palette.mode === "light"
                  ? "#ced8e5ff"
                  : "rgba(33, 44, 60, 1)",
              backdropFilter: "saturate(180%) blur(20px)",
            }}
          >
            <Typography level="title-md" sx={{ mb: 1 }}>
              Contact
            </Typography>
            <List sx={{ "--List-gap": "8px" }}>
              {club.phoneNumber && (
                <ListItem>
                  <ListItemDecorator>
                    <svg
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                      aria-hidden
                    >
                      <path
                        d="M6.6 10.2c1.1 2.2 3 4.1 5.2 5.2l2-2c.2-.2.5-.3.8-.2 1 .3 2 .5 3 .5.4 0 .7.3.7.7V18c0 .4-.3.7-.7.7C9.7 18.7 5.3 14.3 5.3 8.7c0-.4.3-.7.7-.7h2.6c.4 0 .7.3.7.7 0 1 .2 2 .5 3 .1.3 0 .6-.2.8l-2 2z"
                        fill="#555"
                      />
                    </svg>
                  </ListItemDecorator>
                  <Link href={phoneHref} level="body-sm">
                    {club.phoneNumber}
                  </Link>
                </ListItem>
              )}

              {club.email && (
                <ListItem>
                  <ListItemDecorator>
                    <svg
                      width="18"
                      height="18"
                      viewBox="0 0 24 24"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                      aria-hidden
                    >
                      <path
                        d="M4 6h16a1 1 0 0 1 1 1v10a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1Z"
                        stroke="#555"
                        strokeWidth="1.5"
                      />
                      <path
                        d="m3.5 7.5 7.6 5.1c.55.37 1.25.37 1.8 0l7.6-5.1"
                        stroke="#555"
                        strokeWidth="1.5"
                      />
                    </svg>
                  </ListItemDecorator>
                  <Link href={mailHref} level="body-sm">
                    {club.email}
                  </Link>
                </ListItem>
              )}
            </List>
          </Sheet>

          <Sheet
            variant="soft"
            sx={{
              p: 1.25,
              m: "8px 4px",
              borderRadius: 12,
              bgcolor: "background.level5",
              background: (t) =>
                t.palette.mode === "light"
                  ? "#ced8e5ff"
                  : "rgba(33, 44, 60, 1)",
              backdropFilter: "saturate(180%) blur(20px)",
            }}
          >
            <Typography level="title-md" sx={{ mb: 1 }}>
              Informations
            </Typography>
            <Box
              sx={{
                display: "grid",
                gridTemplateColumns: "repeat(2, minmax(0,1fr))",
                gap: "10px 14px",
              }}
            >
              {club.addresse && (
                <Box sx={{ gridColumn: "1 / -1" }}>
                  <Typography level="body-xs" sx={{ color: "#666" }}>
                    Adresse
                  </Typography>
                  <Typography level="body-sm" sx={{ fontWeight: 600 }}>
                    {fullAddress}
                  </Typography>
                </Box>
              )}
              {club.city && (
                <Box>
                  <Typography level="body-xs" sx={{ color: "#666" }}>
                    Ville
                  </Typography>
                  <Typography level="body-sm" sx={{ fontWeight: 600 }}>
                    {club.city}
                  </Typography>
                </Box>
              )}
              {club.country && (
                <Box>
                  <Typography level="body-xs" sx={{ color: "#666" }}>
                    Pays
                  </Typography>
                  <Typography level="body-sm" sx={{ fontWeight: 600 }}>
                    {club.country}
                  </Typography>
                </Box>
              )}
              {club.postalCode && (
                <Box>
                  <Typography level="body-xs" sx={{ color: "#666" }}>
                    Code postal
                  </Typography>
                  <Typography level="body-sm" sx={{ fontWeight: 600 }}>
                    {club.postalCode}
                  </Typography>
                </Box>
              )}
              <Box>
                <Typography level="body-xs" sx={{ color: "#666" }}>
                  Membres
                </Typography>
                <Typography level="body-sm" sx={{ fontWeight: 600 }}>
                  {club.members}
                </Typography>
              </Box>
              {club.dateCreation && (
                <Box>
                  <Typography level="body-xs" sx={{ color: "#666" }}>
                    Date de création
                  </Typography>
                  <Typography level="body-sm" sx={{ fontWeight: 600 }}>
                    {new Date(club.dateCreation).toLocaleDateString("fr-FR", {
                      year: "numeric",
                      month: "long",
                      day: "numeric",
                    })}
                  </Typography>
                </Box>
              )}
            </Box>
          </Sheet>

          {/* Action join bottom full width – only in Description tab */}
          <Box sx={{ p: 1 }}>
            <Button
              fullWidth
              variant="solid"
              color="neutral"
              sx={{
                bgcolor: "#0a0a0a",
                color: "#fff",
                fontWeight: 700,
                borderRadius: 10,
              }}
              onClick={() => alert("Rejoindre le club")}
            >
              Rejoindre
            </Button>
          </Box>
        </TabPanel>

        <TabPanel value={1} sx={{ px: 0, pt: 1 }}>
          <FullCalendarSchedule
            date={currentDate}
            startHour={START_HOUR}
            endHour={END_HOUR}
            slots={scheduleSlots}
            onPrevDay={() =>
              setCurrentDate(
                (d) => new Date(d.getFullYear(), d.getMonth(), d.getDate() - 1),
              )
            }
            onNextDay={() =>
              setCurrentDate(
                (d) => new Date(d.getFullYear(), d.getMonth(), d.getDate() + 1),
              )
            }
            onJoin={(slot) =>
              alert(
                `Rejoindre la séance ${slot.title ?? ""} à ${String(slot.hour).padStart(2, "0")}:00`,
              )
            }
            onReserve={(slot) =>
              alert(
                `Réserver le créneau à ${String(slot.hour).padStart(2, "0")}:00`,
              )
            }
          />
        </TabPanel>
      </Tabs>
    </Sheet>
  );
}
