import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import BaseLayout from "../../../layouts/BaseLayout";
import {
  Alert,
  Button,
  Card,
  Chip,
  Divider,
  Grid,
  IconButton,
  Input,
  Select,
  Option,
  Sheet,
  Skeleton,
  Stack,
  Typography,
  useTheme,
} from "@mui/joy";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import PlaceIcon from "@mui/icons-material/Place";
import EmailIcon from "@mui/icons-material/Email";
import PhoneIcon from "@mui/icons-material/Phone";
import CategoryIcon from "@mui/icons-material/Category";
import { Sport } from "../../../model/Sport";
import {
  getFullClub,
  FullClub,
  updateFullClub,
  UpdateClubInput,
  disableClub,
  reactivateClub,
} from "../../../services/clubService";
import { useAuth } from "../../shared/useAuth";
import { sportTypes } from "../../../mock/sportType";

// Simple date formatter
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

export default function ManageClub() {
  const location = useLocation();
  const id = (location.state as unknown as { clubId?: string })?.clubId as
    | string
    | undefined;
  const navigate = useNavigate();
  const theme = useTheme();
  const mainColor = theme.palette.mode === "dark" ? "#fff" : "#000";
  // labelColor reserved for future subtitle styling if needed
  const { token } = useAuth();

  const [club, setClub] = useState<FullClub | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editMode, setEditMode] = useState(false);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState<UpdateClubInput>({});
  const [saveMessage, setSaveMessage] = useState<string | null>(null);
  const [disabling, setDisabling] = useState(false);
  const [reactivating, setReactivating] = useState(false);

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
        const data = await getFullClub(id, token);
        if (!cancelled) setClub(data);
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

  const isDeleted = !!club?.deletedAt;

  // Initialize form when entering edit mode or club changes
  function resolveSportTypeId(
    fromId?: string | null,
    fromName?: string | null,
  ): string | undefined {
    // Always prefer backend id if provided, even if it's not in our local list
    if (fromId) return fromId;
    // Otherwise, try by name (case-/accent-insensitive) with synonyms
    const normalize = (s: string) =>
      s
        .toLowerCase()
        .normalize("NFD")
        .replace(/\p{Diacritic}/gu, "")
        .replace(/[^a-z0-9]/g, "");
    // Map common english/french synonyms to our labels
    const synonyms: Record<string, string> = {
      football: "football",
      basketball: "basketball",
      tennis: "tennis",
      rugby: "rugby",
      handball: "handball",
      volleyball: "volleyball",
      volley: "volley",
      baseball: "base-ball",
      golf: "golf",
      cycling: "cyclisme",
      swimming: "natation",
      natation: "natation",
      athletism: "athletisme",
      athletisme: "athletisme",
      judo: "judo",
    };

    if (fromName) {
      const key = normalize(fromName);
      const label = synonyms[key] ?? fromName;
      // find any sportType whose name normalized matches
      const byName = sportTypes.find(
        (s) => normalize(s.name) === normalize(label),
      );
      if (byName) return byName.id;
    }
    return undefined;
  }

  // Options for the Select: include backend current type if it's unknown locally so it can be shown/preselected
  const selectSportOptions = useMemo(() => {
    const base: Sport[] = [...sportTypes];
    if (club?.typeId && !sportTypes.some((s) => s.id === club.typeId)) {
      base.unshift({
        id: club.typeId,
        name: club.typeName || "Type inconnu",
        icon: "",
      } as Sport);
    }
    return base;
  }, [club]);

  useEffect(() => {
    if (club && editMode) {
      setForm({
        name: club.name,
        city: club.city,
        address: club.address,
        country: club.country,
        postalCode: club.postalCode ?? undefined,
        email: club.email ?? undefined,
        phoneNumber: club.phoneNumber ?? undefined,
        latitude: club.latitude,
        longitude: club.longitude,
        typeId: resolveSportTypeId(club.typeId, club.typeName),
      });
    }
  }, [club, editMode]);

  function handleChange<K extends keyof UpdateClubInput>(
    key: K,
    value: UpdateClubInput[K],
  ) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  async function handleSave() {
    if (!club || !token) return;
    setSaving(true);
    setError(null);
    setSaveMessage(null);
    try {
      const updated = await updateFullClub(club.id, form, club.ownerId, token);
      setClub(updated);
      setEditMode(false);
      setSaveMessage("Modifications enregistrées");
    } catch (e: unknown) {
      setError(
        e instanceof Error
          ? e.message
          : String(e) || "Erreur lors de la sauvegarde",
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDisable() {
    if (!club || !token || !id) return;
    const confirmed = window.confirm(
      "Êtes-vous sûr de vouloir désactiver ce club ? Cette action peut empêcher les membres d'y accéder.",
    );
    if (!confirmed) return;
    setDisabling(true);
    setError(null);
    setSaveMessage(null);
    try {
      const ok = await disableClub(club.id, club.ownerId, token);
      if (!ok) throw new Error("La désactivation a échoué");
      // Refetch to reflect updated state (e.g., deletedAt set server-side)
      const refreshed = await getFullClub(id, token);
      setClub(refreshed);
      setEditMode(false);
      setSaveMessage("Club désactivé");
    } catch (e: unknown) {
      setError(
        e instanceof Error
          ? e.message
          : String(e) || "Erreur lors de la désactivation",
      );
    } finally {
      setDisabling(false);
    }
  }

  async function handleReactivate() {
    if (!club || !token || !id) return;
    setReactivating(true);
    setError(null);
    setSaveMessage(null);
    try {
      await reactivateClub(club.id, club.ownerId, token);
      const refreshed = await getFullClub(id, token); // Refetch data
      setClub(refreshed);
      setSaveMessage("Club réactivé avec succès !");
    } catch (e: unknown) {
      setError(
        e instanceof Error
          ? e.message
          : String(e) || "Erreur lors de la réactivation",
      );
    } finally {
      setReactivating(false);
    }
  }

  const gradientBg = useMemo(
    () =>
      theme.palette.mode === "dark"
        ? "linear-gradient(135deg, rgba(15,23,42,0.55), rgba(30,41,59,0.55))"
        : "linear-gradient(135deg, #f8fafc, #e2e8f0)",
    [theme],
  );

  return (
    <BaseLayout>
      <Stack
        gap={2}
        sx={{ maxWidth: 1100, mx: "auto", px: { xs: 2, md: 3 }, py: 3 }}
      >
        <Stack direction="row" alignItems="center" gap={1}>
          <IconButton
            variant="solid"
            color="neutral"
            size="md"
            onClick={() => navigate("/clubs", { replace: true })}
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
            Gestion du club
          </Typography>
        </Stack>
        {loading && (
          <Card variant="outlined" sx={{ p: 3, display: "grid", gap: 2 }}>
            <Skeleton variant="text" width={240} />
            <Skeleton variant="rectangular" height={120} />
            <Skeleton variant="rectangular" height={60} />
          </Card>
        )}
        {!loading && error && (
          <Alert color="danger" variant="soft">
            {error}
          </Alert>
        )}
        {!loading && !error && club && (
          <Stack gap={2}>
            <Sheet
              variant="outlined"
              sx={{
                p: { xs: 2, md: 3 },
                borderRadius: "md",
                backdropFilter:
                  theme.palette.mode === "dark" ? "blur(6px)" : "none",
                background: gradientBg,
                borderColor:
                  theme.palette.mode === "dark"
                    ? theme.palette.neutral[700]
                    : theme.palette.neutral[300],
                boxShadow:
                  theme.palette.mode === "dark"
                    ? `0 0 0 1px ${theme.palette.neutral[800]}, 0 8px 24px -12px ${theme.palette.neutral[800]}`
                    : "0 4px 16px -8px rgba(0,0,0,0.08)",
                display: "flex",
                flexDirection: "column",
                gap: 2,
              }}
            >
              {isDeleted && (
                <Alert color="warning" variant="solid" sx={{ mb: 1 }}>
                  Ce club a été supprimé (soft delete).
                </Alert>
              )}
              <Stack
                direction={{ xs: "column", md: "row" }}
                justifyContent="space-between"
                gap={2}
              >
                <Stack gap={1}>
                  <Typography
                    level="h2"
                    sx={{
                      fontWeight: 700,
                      color: mainColor,
                      letterSpacing: ".01em",
                      mb: editMode ? 0.5 : 0,
                    }}
                  >
                    {editMode ? (
                      <Input
                        value={form.name ?? ""}
                        onChange={(e) => handleChange("name", e.target.value)}
                        placeholder="Nom du club"
                        size="lg"
                        variant="soft"
                        color="neutral"
                        sx={{
                          fontWeight: 600,
                          fontSize: "clamp(1.4rem, 3.5vw, 2.2rem)",
                          borderRadius: "md",
                          color: mainColor,
                          bgcolor:
                            theme.palette.mode === "dark"
                              ? "neutral.800"
                              : "neutral.100",
                          "--Input-focusedThickness": "2px",
                          "--Input-focusedHighlight":
                            theme.palette.primary.outlinedBorder,
                        }}
                      />
                    ) : (
                      club.name
                    )}
                  </Typography>
                  <Stack direction="row" gap={1} flexWrap="wrap">
                    {editMode ? (
                      <Select
                        value={
                          selectSportOptions.some((s) => s.id === form.typeId)
                            ? (form.typeId as string)
                            : null
                        }
                        placeholder="Type de sport"
                        onChange={(_e, val) =>
                          handleChange("typeId", val ?? undefined)
                        }
                        sx={{ minWidth: 220 }}
                        startDecorator={<CategoryIcon fontSize="small" />}
                      >
                        {selectSportOptions.map((s) => (
                          <Option key={s.id} value={s.id}>
                            {s.icon ? `${s.icon} ${s.name}` : s.name}
                            {club?.typeId === s.id &&
                            !sportTypes.some((st) => st.id === s.id)
                              ? " (actuel)"
                              : ""}
                          </Option>
                        ))}
                      </Select>
                    ) : (
                      club.typeName && (
                        <Chip
                          variant="soft"
                          color="primary"
                          startDecorator={<CategoryIcon fontSize="small" />}
                        >
                          {club.typeName}
                        </Chip>
                      )
                    )}
                    {editMode ? (
                      <Input
                        value={form.city ?? ""}
                        onChange={(e) => handleChange("city", e.target.value)}
                        placeholder="Ville"
                        size="md"
                        startDecorator={<PlaceIcon fontSize="small" />}
                      />
                    ) : (
                      <Chip
                        variant="soft"
                        color="neutral"
                        startDecorator={<PlaceIcon fontSize="small" />}
                      >
                        {club.city}
                      </Chip>
                    )}
                  </Stack>
                </Stack>
              </Stack>
              <Divider />
              <Grid container spacing={2}>
                <Grid xs={12} md={6}>
                  <Stack gap={1}>
                    <Typography level="title-sm" sx={{ opacity: 0.8 }}>
                      Localisation
                    </Typography>
                    {editMode ? (
                      <textarea
                        value={form.address ?? ""}
                        onChange={(e) =>
                          handleChange("address", e.target.value)
                        }
                        placeholder="Adresse"
                        style={{
                          background: "transparent",
                          border:
                            "1px solid var(--joy-palette-neutral-outlinedBorder)",
                          borderRadius: 8,
                          padding: "6px 8px",
                          resize: "vertical",
                          minHeight: 60,
                          color:
                            theme.palette.mode === "dark" ? "#fff" : "#000",
                        }}
                      />
                    ) : (
                      <Typography>{club.address}</Typography>
                    )}
                    {editMode ? (
                      <Stack direction="row" gap={1} flexWrap="wrap">
                        <input
                          value={form.postalCode ?? ""}
                          onChange={(e) =>
                            handleChange("postalCode", e.target.value)
                          }
                          placeholder="Code postal"
                          style={{
                            background: "transparent",
                            border:
                              "1px solid var(--joy-palette-neutral-outlinedBorder)",
                            borderRadius: 8,
                            padding: "4px 8px",
                            width: 140,
                            color:
                              theme.palette.mode === "dark" ? "#fff" : "#000",
                          }}
                        />
                        <input
                          value={form.country ?? ""}
                          onChange={(e) =>
                            handleChange("country", e.target.value)
                          }
                          placeholder="Pays"
                          style={{
                            background: "transparent",
                            border:
                              "1px solid var(--joy-palette-neutral-outlinedBorder)",
                            borderRadius: 8,
                            padding: "4px 8px",
                            minWidth: 160,
                            color:
                              theme.palette.mode === "dark" ? "#fff" : "#000",
                          }}
                        />
                      </Stack>
                    ) : (
                      <Typography>
                        {club.postalCode ? `${club.postalCode} ` : ""}
                        {club.country}
                      </Typography>
                    )}
                  </Stack>
                </Grid>
                <Grid xs={12} md={6}>
                  <Stack gap={1}>
                    <Typography level="title-sm" sx={{ opacity: 0.8 }}>
                      Contact
                    </Typography>
                    {editMode ? (
                      <Stack gap={1}>
                        <input
                          value={form.email ?? ""}
                          onChange={(e) =>
                            handleChange("email", e.target.value)
                          }
                          placeholder="Email"
                          style={{
                            background: "transparent",
                            border:
                              "1px solid var(--joy-palette-neutral-outlinedBorder)",
                            borderRadius: 8,
                            padding: "4px 8px",
                            width: "100%",
                            color:
                              theme.palette.mode === "dark" ? "#fff" : "#000",
                          }}
                        />
                        <input
                          value={form.phoneNumber ?? ""}
                          onChange={(e) =>
                            handleChange("phoneNumber", e.target.value)
                          }
                          placeholder="Téléphone"
                          style={{
                            background: "transparent",
                            border:
                              "1px solid var(--joy-palette-neutral-outlinedBorder)",
                            borderRadius: 8,
                            padding: "4px 8px",
                            width: "100%",
                            color:
                              theme.palette.mode === "dark" ? "#fff" : "#000",
                          }}
                        />
                      </Stack>
                    ) : (
                      <>
                        <Stack direction="row" alignItems="center" gap={1}>
                          <EmailIcon fontSize="small" />
                          <Typography>{club.email || "—"}</Typography>
                        </Stack>
                        <Stack direction="row" alignItems="center" gap={1}>
                          <PhoneIcon fontSize="small" />
                          <Typography>{club.phoneNumber || "—"}</Typography>
                        </Stack>
                      </>
                    )}
                  </Stack>
                </Grid>
              </Grid>
              <Divider sx={{ my: 1 }} />
              <Grid container spacing={2}>
                <Grid xs={12} md={4}>
                  <Stack gap={0.5}>
                    <Typography level="title-sm" sx={{ opacity: 0.8 }}>
                      Créé le
                    </Typography>
                    <Typography>{formatDate(club.createdAt)}</Typography>
                  </Stack>
                </Grid>
                <Grid xs={12} md={4}>
                  <Stack gap={0.5}>
                    <Typography level="title-sm" sx={{ opacity: 0.8 }}>
                      Mis à jour le
                    </Typography>
                    <Typography>{formatDate(club.updatedAt)}</Typography>
                  </Stack>
                </Grid>
                <Grid xs={12} md={4}>
                  <Stack gap={0.5}>
                    <Typography level="title-sm" sx={{ opacity: 0.8 }}>
                      Supprimé le
                    </Typography>
                    <Typography>{formatDate(club.deletedAt)}</Typography>
                  </Stack>
                </Grid>
              </Grid>
            </Sheet>
            <Stack
              direction="row"
              gap={1}
              flexWrap="wrap"
              justifyContent="space-between"
              alignItems="center"
            >
              <Stack direction="row" gap={1}>
                <Button
                  variant="solid"
                  onClick={() =>
                    club &&
                    navigate(`/clubs/manage/members`, {
                      state: { clubId: club.id },
                    })
                  }
                  sx={{
                    backgroundColor: mainColor,
                    color: theme.palette.mode === "dark" ? "#121212" : "#fff",
                    fontWeight: 600,
                    boxShadow: "none",
                    "&:hover": { backgroundColor: mainColor },
                  }}
                >
                  Gérer les membres
                </Button>
              </Stack>
              {isDeleted ? (
                <Button
                  variant="solid"
                  color="success"
                  disabled={reactivating}
                  onClick={handleReactivate}
                >
                  {reactivating ? "Réactivation..." : "Réactiver le club"}
                </Button>
              ) : editMode ? (
                <Stack direction="row" gap={1}>
                  <Button
                    variant="soft"
                    color="neutral"
                    disabled={saving}
                    onClick={() => setEditMode(false)}
                  >
                    Annuler
                  </Button>
                  <Button
                    variant="solid"
                    disabled={saving}
                    onClick={handleSave}
                    sx={{
                      backgroundColor: mainColor,
                      color: theme.palette.mode === "dark" ? "#121212" : "#fff",
                      fontWeight: 600,
                      boxShadow: "none",
                      "&:hover": { backgroundColor: mainColor },
                    }}
                  >
                    {saving ? "Sauvegarde..." : "Enregistrer"}
                  </Button>
                </Stack>
              ) : (
                <Stack direction="row" gap={1}>
                  <Button
                    variant="solid"
                    onClick={() => {
                      if (club) {
                        setForm({
                          name: club.name,
                          city: club.city,
                          address: club.address,
                          country: club.country,
                          postalCode: club.postalCode ?? undefined,
                          email: club.email ?? undefined,
                          phoneNumber: club.phoneNumber ?? undefined,
                          latitude: club.latitude,
                          longitude: club.longitude,
                          typeId: resolveSportTypeId(
                            club.typeId,
                            club.typeName,
                          ),
                        });
                      }
                      setEditMode(true);
                    }}
                    sx={{
                      backgroundColor: mainColor,
                      color: theme.palette.mode === "dark" ? "#121212" : "#fff",
                      fontWeight: 600,
                      boxShadow: "none",
                      "&:hover": { backgroundColor: mainColor },
                    }}
                  >
                    Modifier
                  </Button>
                  <Button
                    variant="solid"
                    color="danger"
                    disabled={disabling}
                    onClick={handleDisable}
                  >
                    {disabling ? "Désactivation..." : "Désactiver le club"}
                  </Button>
                </Stack>
              )}
              {saveMessage && !error && (
                <Typography
                  level="body-sm"
                  sx={{ color: "success.plainColor", fontWeight: 500 }}
                >
                  {saveMessage}
                </Typography>
              )}
            </Stack>
          </Stack>
        )}
      </Stack>
    </BaseLayout>
  );
}
