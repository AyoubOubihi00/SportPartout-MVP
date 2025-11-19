import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import BaseLayout from "../../../layouts/BaseLayout";
import {
  Alert,
  Button,
  Card,
  Chip,
  Divider,
  IconButton,
  Input,
  Modal,
  ModalClose,
  ModalDialog,
  Sheet,
  Skeleton,
  Stack,
  Table,
  Typography,
  useTheme,
} from "@mui/joy";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import SearchIcon from "@mui/icons-material/Search";
import PersonIcon from "@mui/icons-material/Person";
import EmailIcon from "@mui/icons-material/Email";
import PhoneIcon from "@mui/icons-material/Phone";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CancelIcon from "@mui/icons-material/Cancel";
import { useAuth } from "../../shared/useAuth";
import {
  fetchClubMembers,
  forcedUnsubscribe,
  type MemberWithClub,
} from "../../../services/membershipService";
import { getFullClub, type FullClub } from "../../../services/clubService";

function formatDate(iso?: string | null) {
  if (!iso) return "—";
  try {
    const d = new Date(iso);
    return d.toLocaleDateString("fr-FR", {
      year: "numeric",
      month: "short",
      day: "2-digit",
    });
  } catch {
    return iso;
  }
}

function getUnsubDate(m: MemberWithClub): string | null | undefined {
  // Tolérance pour noms possibles:  (schema) vs dateUnsubscription (backend)
  return m?.dateUnsubscription ?? null;
}

export default function ManageMembers() {
  const location = useLocation();
  const id = (location.state as unknown as { clubId?: string })?.clubId;
  const navigate = useNavigate();
  const theme = useTheme();
  const mainColor = theme.palette.mode === "dark" ? "#fff" : "#000";
  const { token } = useAuth();

  const [club, setClub] = useState<FullClub | null>(null);
  const [members, setMembers] = useState<MemberWithClub[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [filterStatus, setFilterStatus] = useState<
    "all" | "active" | "inactive"
  >("active");
  const [selectedMember, setSelectedMember] = useState<MemberWithClub | null>(
    null,
  );
  const [unsubByFilter, setUnsubByFilter] = useState<
    "all" | "self" | "manager"
  >("all");

  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }
    if (!id) {
      setError("Identifiant du club manquant");
      setLoading(false);
      return;
    }
    let cancelled = false;
    (async () => {
      try {
        const [clubData, membersData] = await Promise.all([
          getFullClub(id, token),
          fetchClubMembers(id),
        ]);
        if (!cancelled) {
          setClub(clubData);
          setMembers(membersData);
          if (process.env.NODE_ENV !== "production") {
            console.debug(
              "[ManageMembers] sample members:",
              membersData.slice(0, 3),
            );
            if (membersData[0]) {
              console.debug(
                "[ManageMembers] first member keys:",
                Object.keys(membersData[0]),
              );
            }
          }
        }
      } catch (e: unknown) {
        if (!cancelled)
          setError(
            e instanceof Error
              ? e.message
              : String(e) || "Erreur de chargement",
          );
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [id, token, navigate]);

  const filteredMembers = useMemo(() => {
    let result = [...members];

    // Filtre par statut
    if (filterStatus === "active") {
      result = result.filter((m) => m.isActif);
    } else if (filterStatus === "inactive") {
      result = result.filter((m) => !m.isActif);
    }

    // Filtre par origine de désinscription (uniquement pertinent pour les inactifs)
    if (filterStatus === "inactive") {
      if (unsubByFilter === "self") {
        result = result.filter((m) => !m.isActif && m.himself);
      } else if (unsubByFilter === "manager") {
        result = result.filter((m) => !m.isActif && !m.himself);
      }
    }

    // Filtre par recherche
    if (searchQuery.trim()) {
      const query = searchQuery.toLowerCase();
      result = result.filter(
        (m) =>
          m.firstName?.toLowerCase().includes(query) ||
          m.lastName?.toLowerCase().includes(query) ||
          m.email?.toLowerCase().includes(query),
      );
    }

    // Tri alphabétique
    result.sort((a, b) => {
      const nameA = `${a.lastName} ${a.firstName}`.toLowerCase();
      const nameB = `${b.lastName} ${b.firstName}`.toLowerCase();
      return nameA.localeCompare(nameB);
    });

    return result;
  }, [members, searchQuery, filterStatus, unsubByFilter]);

  // Log des dates de désinscription pour aider au debug côté front
  useEffect(() => {
    if (process.env.NODE_ENV !== "production" && filterStatus === "inactive") {
      const sample = filteredMembers
        .filter((m) => !m.isActif)
        .slice(0, 10)
        .map((m) => ({
          userId: m.userId,
          fullName: `${m.firstName} ${m.lastName}`,
          dateUnsubscription: m.dateUnsubscription,
          isActif: m.isActif,
        }));
      console.log("[DEBUG] Inactifs – dates de désinscription:", sample);
    }
  }, [filteredMembers, filterStatus]);

  const stats = useMemo(() => {
    const active = members.filter((m) => m.isActif).length;
    const inactive = members.filter((m) => !m.isActif).length;
    return { total: members.length, active, inactive };
  }, [members]);

  const gradientBg = useMemo(
    () =>
      theme.palette.mode === "dark"
        ? "linear-gradient(135deg, rgba(15,23,42,0.55), rgba(30,41,59,0.55))"
        : "linear-gradient(135deg, #f8fafc, #e2e8f0)",
    [theme],
  );

  async function handleForcedUnsubscribe(member: MemberWithClub) {
    if (!id || !token) return;

    const confirmed = window.confirm(
      `Confirmer la désinscription de ${member.firstName} ${member.lastName} ?`,
    );
    if (!confirmed) return;

    try {
      // 1️⃣ Appel API pour forcer la désinscription
      await forcedUnsubscribe(id, member.userId);

      // 2️⃣ Rafraîchir la liste complète des membres depuis l’API
      const updatedMembers = await fetchClubMembers(id);
      console.log("Membres mis à jour :", updatedMembers);

      // 3️⃣ Mettre à jour l’état local avec la réponse réelle du backend
      setMembers(updatedMembers);
      setSelectedMember(null);
    } catch (e: unknown) {
      const msg = e instanceof Error ? e.message : String(e);
      alert(msg || "Erreur lors de la désinscription");
    }
  }

  return (
    <BaseLayout>
      <Stack
        gap={2}
        sx={{ maxWidth: 1200, mx: "auto", px: { xs: 2, md: 3 }, py: 3 }}
      >
        {/* En-tête */}
        <Stack direction="row" alignItems="center" gap={1}>
          <IconButton
            variant="solid"
            color="neutral"
            size="md"
            onClick={() => navigate("/clubs/manage", { state: { clubId: id } })}
            aria-label="Retour"
            sx={{
              bgcolor:
                theme.palette.mode === "dark" ? "neutral.700" : "neutral.200",
              color: theme.palette.mode === "dark" ? "#fff" : "#000",
              border: "1px solid",
              borderColor:
                theme.palette.mode === "dark" ? "neutral.600" : "neutral.300",
              boxShadow:
                theme.palette.mode === "dark"
                  ? "0 2px 6px -1px rgba(0,0,0,0.6)"
                  : "0 2px 6px -1px rgba(0,0,0,0.25)",
              "&:hover": {
                bgcolor:
                  theme.palette.mode === "dark" ? "neutral.600" : "neutral.300",
              },
            }}
          >
            <ArrowBackIcon />
          </IconButton>
          <Typography level="h3" sx={{ fontWeight: 600, letterSpacing: 0.5 }}>
            Gestion des membres
          </Typography>
        </Stack>

        {loading && (
          <Card variant="outlined" sx={{ p: 3, display: "grid", gap: 2 }}>
            <Skeleton variant="text" width={240} />
            <Skeleton variant="rectangular" height={120} />
          </Card>
        )}

        {!loading && error && (
          <Alert color="danger" variant="soft">
            {error}
          </Alert>
        )}

        {!loading && !error && club && (
          <Stack gap={3}>
            {/* Infos du club */}
            <Sheet
              variant="outlined"
              sx={{
                p: 2,
                borderRadius: "md",
                background: gradientBg,
                borderColor:
                  theme.palette.mode === "dark"
                    ? theme.palette.neutral[700]
                    : theme.palette.neutral[300],
              }}
            >
              <Stack
                direction="row"
                justifyContent="space-between"
                alignItems="center"
                flexWrap="wrap"
                gap={2}
              >
                <Stack gap={0.5}>
                  <Typography
                    level="h4"
                    sx={{ fontWeight: 700, color: mainColor }}
                  >
                    {club.name}
                  </Typography>
                  <Typography level="body-sm" sx={{ opacity: 0.8 }}>
                    {club.city} • {club.typeName}
                  </Typography>
                </Stack>
                <Stack direction="row" gap={1.5} flexWrap="wrap">
                  <Chip variant="soft" color="primary" size="lg">
                    {stats.total} membre{stats.total > 1 ? "s" : ""}
                  </Chip>
                  <Chip
                    variant="soft"
                    color="success"
                    size="lg"
                    startDecorator={<CheckCircleIcon />}
                  >
                    {stats.active} actif{stats.active > 1 ? "s" : ""}
                  </Chip>
                  {stats.inactive > 0 && (
                    <Chip
                      variant="soft"
                      color="neutral"
                      size="lg"
                      startDecorator={<CancelIcon />}
                    >
                      {stats.inactive} inactif{stats.inactive > 1 ? "s" : ""}
                    </Chip>
                  )}
                </Stack>
              </Stack>
            </Sheet>

            {/* Barre de recherche et filtres */}
            <Stack
              direction={{ xs: "column", md: "row" }}
              gap={2}
              alignItems={{ md: "center" }}
            >
              <Input
                placeholder="Rechercher un membre..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                startDecorator={<SearchIcon />}
                sx={{ flexGrow: 1 }}
              />
              <Stack direction="row" gap={1}>
                <Button
                  variant={filterStatus === "active" ? "solid" : "soft"}
                  color="success"
                  onClick={() => setFilterStatus("active")}
                >
                  Actifs
                </Button>
                <Button
                  variant={filterStatus === "inactive" ? "solid" : "soft"}
                  color="neutral"
                  onClick={() => setFilterStatus("inactive")}
                >
                  Inactifs
                </Button>
              </Stack>
            </Stack>

            {/* Filtre: Désinscrit par (visible uniquement quand Inactifs est sélectionné) */}
            {filterStatus === "inactive" && (
              <Stack
                direction={{ xs: "column", sm: "row" }}
                gap={1}
                alignItems={{ sm: "center" }}
              >
                <Typography
                  level="body-sm"
                  sx={{ opacity: 0.8, minWidth: 130 }}
                >
                  Désinscrit par
                </Typography>
                <Stack direction="row" gap={1}>
                  <Button
                    variant={unsubByFilter === "all" ? "solid" : "soft"}
                    color="neutral"
                    onClick={() => setUnsubByFilter("all")}
                  >
                    Tous
                  </Button>
                  <Button
                    variant={unsubByFilter === "self" ? "solid" : "soft"}
                    color="primary"
                    onClick={() => setUnsubByFilter("self")}
                  >
                    Lui-même
                  </Button>
                  <Button
                    variant={unsubByFilter === "manager" ? "solid" : "soft"}
                    color="primary"
                    onClick={() => setUnsubByFilter("manager")}
                  >
                    Gérant
                  </Button>
                </Stack>
              </Stack>
            )}

            {/* Liste des membres */}
            {filteredMembers.length === 0 ? (
              <Alert color="neutral" variant="soft">
                Aucun membre trouvé pour les critères sélectionnés.
              </Alert>
            ) : (
              <Sheet
                variant="outlined"
                sx={{
                  borderRadius: "md",
                  overflow: "hidden",
                  borderColor:
                    theme.palette.mode === "dark"
                      ? theme.palette.neutral[700]
                      : theme.palette.neutral[300],
                }}
              >
                <Table
                  sx={{
                    "& thead th": {
                      bgcolor:
                        theme.palette.mode === "dark"
                          ? "neutral.800"
                          : "neutral.100",
                      fontWeight: 700,
                      textTransform: "uppercase",
                      fontSize: "0.75rem",
                      letterSpacing: "0.05em",
                    },
                    "& tbody tr:hover": {
                      bgcolor:
                        theme.palette.mode === "dark"
                          ? "neutral.800"
                          : "neutral.50",
                    },
                  }}
                >
                  <thead>
                    <tr>
                      <th style={{ width: "28%" }}>Membre</th>
                      <th style={{ width: "24%" }}>Contact</th>
                      <th style={{ width: "14%" }}>Statut</th>
                      <th style={{ width: "14%" }}>Désinscrit par</th>
                      <th style={{ width: "12%" }}>
                        {filterStatus === "inactive"
                          ? "Date désinscription"
                          : "Date inscription"}
                      </th>
                      <th style={{ width: "8%", textAlign: "center" }}>
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredMembers.map((member) => (
                      <tr key={`${member.userId}-${member.clubId}`}>
                        <td>
                          <Stack direction="row" gap={1.5} alignItems="center">
                            <PersonIcon
                              fontSize="small"
                              sx={{ opacity: 0.6 }}
                            />
                            <Stack gap={0.25}>
                              <Typography
                                level="body-sm"
                                sx={{ fontWeight: 600 }}
                              >
                                {member.firstName} {member.lastName}
                              </Typography>
                              {member.himself && (
                                <Chip size="sm" variant="soft" color="primary">
                                  Vous
                                </Chip>
                              )}
                            </Stack>
                          </Stack>
                        </td>
                        <td>
                          <Stack gap={0.5}>
                            <Stack
                              direction="row"
                              gap={0.5}
                              alignItems="center"
                            >
                              <EmailIcon
                                fontSize="small"
                                sx={{ fontSize: "0.9rem", opacity: 0.6 }}
                              />
                              <Typography level="body-sm">
                                {member.email}
                              </Typography>
                            </Stack>
                            {member.phoneNumber && (
                              <Stack
                                direction="row"
                                gap={0.5}
                                alignItems="center"
                              >
                                <PhoneIcon
                                  fontSize="small"
                                  sx={{ fontSize: "0.9rem", opacity: 0.6 }}
                                />
                                <Typography level="body-sm">
                                  {member.phoneNumber}
                                </Typography>
                              </Stack>
                            )}
                          </Stack>
                        </td>
                        <td>
                          <Chip
                            size="sm"
                            variant="soft"
                            color={member.isActif ? "success" : "neutral"}
                            startDecorator={
                              member.isActif ? (
                                <CheckCircleIcon />
                              ) : (
                                <CancelIcon />
                              )
                            }
                          >
                            {member.isActif ? "Actif" : "Inactif"}
                          </Chip>
                        </td>
                        <td>
                          <Typography level="body-sm">
                            {member.isActif
                              ? "—"
                              : member.himself
                                ? "lui-même"
                                : "le gérant"}
                          </Typography>
                        </td>
                        <td>
                          <Typography level="body-sm">
                            {formatDate(
                              member.isActif
                                ? member.dateSubscription
                                : getUnsubDate(member),
                            )}
                          </Typography>
                        </td>
                        <td style={{ textAlign: "center" }}>
                          <Button
                            size="sm"
                            variant="solid"
                            onClick={() => {
                              if (process.env.NODE_ENV !== "production") {
                                console.log(
                                  "[DEBUG] Membre sélectionné – dates:",
                                  {
                                    userId: member.userId,
                                    isActif: member.isActif,
                                    dateSubscription: member.dateSubscription,
                                    dateUnsubscription:
                                      member.dateUnsubscription,
                                  },
                                );
                              }
                              setSelectedMember(member);
                            }}
                            sx={{
                              backgroundColor: mainColor,
                              color:
                                theme.palette.mode === "dark"
                                  ? "#121212"
                                  : "#fff",
                              fontWeight: 600,
                              boxShadow: "none",
                              "&:hover": { backgroundColor: mainColor },
                            }}
                          >
                            Gérer
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </Sheet>
            )}

            {/* Pied de page */}
            <Stack
              direction="row"
              gap={1}
              justifyContent="space-between"
              flexWrap="wrap"
            ></Stack>
          </Stack>
        )}
      </Stack>

      {/* MODAL DÉTAILS MEMBRE */}
      <Modal open={!!selectedMember} onClose={() => setSelectedMember(null)}>
        <ModalDialog
          variant="outlined"
          sx={{ minWidth: 400, maxWidth: "90vw", p: 3 }}
        >
          <ModalClose />
          {selectedMember && (
            <Stack gap={2}>
              <Typography level="h4" sx={{ fontWeight: 700 }}>
                {selectedMember.firstName} {selectedMember.lastName}
              </Typography>

              <Divider />

              <Stack gap={1}>
                <Typography level="body-sm">
                  Email : <b>{selectedMember.email}</b>
                </Typography>
                {selectedMember.phoneNumber && (
                  <Typography level="body-sm">
                    Téléphone : <b>{selectedMember.phoneNumber}</b>
                  </Typography>
                )}
                {selectedMember.birthDate && (
                  <Typography level="body-sm">
                    Date de naissance :{" "}
                    <b>{formatDate(selectedMember.birthDate)}</b>
                  </Typography>
                )}
                <Typography level="body-sm">
                  Inscrit le :{" "}
                  <b>{formatDate(selectedMember.dateSubscription)}</b>
                </Typography>
                {!selectedMember.isActif && (
                  <Typography level="body-sm">
                    Désinscrit le :{" "}
                    <b>{formatDate(getUnsubDate(selectedMember))}</b>
                  </Typography>
                )}
                <Typography level="body-sm">
                  Statut :{" "}
                  <Chip
                    size="sm"
                    variant="soft"
                    color={selectedMember.isActif ? "success" : "neutral"}
                    startDecorator={
                      selectedMember.isActif ? (
                        <CheckCircleIcon />
                      ) : (
                        <CancelIcon />
                      )
                    }
                  >
                    {selectedMember.isActif ? "Actif" : "Inactif"}
                  </Chip>
                </Typography>

                {/* Désabonné par */}
                {!selectedMember.isActif && (
                  <Typography level="body-sm" sx={{ color: "danger.500" }}>
                    Désabonné par :{" "}
                    <b>{selectedMember.himself ? "lui-même" : "le gérant"}</b>
                  </Typography>
                )}
              </Stack>

              <Divider />

              <Stack direction="row" justifyContent="flex-end" gap={1}>
                <Button
                  variant="plain"
                  color="neutral"
                  onClick={() => setSelectedMember(null)}
                >
                  Fermer
                </Button>

                {selectedMember.isActif && (
                  <Button
                    variant="solid"
                    color="danger"
                    onClick={() => handleForcedUnsubscribe(selectedMember)}
                  >
                    Désabonner
                  </Button>
                )}
              </Stack>
            </Stack>
          )}
        </ModalDialog>
      </Modal>
    </BaseLayout>
  );
}
