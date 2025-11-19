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
import adminService from "../../../services/adminService";
import { getSportById } from "../../../services/sportService";
import { fetchAddress } from "../../../services/cityApiService";
import { City } from "../../../model/City";

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
  const [availableSportTypes, setAvailableSportTypes] = useState<Sport[]>([]);
  const [addressSuggestions, setAddressSuggestions] = useState<City[]>([]);
  const [addressQuery, setAddressQuery] = useState("");
  const [addressValid, setAddressValid] = useState(true);
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

  // Fetch sport types from admin service (fallback to mock kept)
  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const types = await adminService.fetchSportTypes();
        if (!cancelled && types && types.length > 0) {
          setAvailableSportTypes(
            types.map((t) => ({ id: t.id, name: t.name, icon: "" }) as Sport),
          );
        }
      } catch (err) {
        // keep mock fallback; log in dev
        if (process.env.NODE_ENV !== "production") {
          console.warn("[ManageClub] fetchSportTypes failed:", err);
        }
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [token]);

  // Options for the Select: include backend current type if it's unknown locally so it can be shown/preselected
  const selectSportOptions = useMemo(() => {
    const base: Sport[] = [...availableSportTypes];
    if (
      club?.typeId &&
      !availableSportTypes.some((s) => s.id === club.typeId)
    ) {
      base.unshift({
        id: club.typeId,
        name: club.typeName || "Type inconnu",
        icon: "",
      } as Sport);
    }
    return base;
  }, [club, availableSportTypes]);

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
        typeId: club.typeId!,
      });
      const hasCoordinates =
        club.latitude !== null &&
        club.latitude !== undefined &&
        club.longitude !== null &&
        club.longitude !== undefined;
      setAddressValid(Boolean(club.address) && hasCoordinates);
    }
  }, [club, editMode]);

  // Watch addressQuery and fetch suggestions (debounced)
  useEffect(() => {
    if (!editMode) return;
    const q = addressQuery.trim();
    if (q.length < 2) {
      setAddressSuggestions([]);
      return;
    }
    const id = setTimeout(async () => {
      try {
        const results = await fetchAddress(q);
        setAddressSuggestions(results || []);
      } catch {
        setAddressSuggestions([]);
      }
    }, 300);
    return () => clearTimeout(id);
  }, [addressQuery, editMode]);

  const isNameValid = useMemo(
    () => typeof form.name === "string" && form.name.trim().length > 0,
    [form.name],
  );

  const areMandatoryFieldsFilled = useMemo(() => {
    const requiredValues = [
      form.address,
      form.city,
      form.postalCode,
      form.country,
      form.email,
      form.phoneNumber,
    ];
    return requiredValues.every(
      (value) => typeof value === "string" && value.trim().length > 0,
    );
  }, [
    form.address,
    form.city,
    form.postalCode,
    form.country,
    form.email,
    form.phoneNumber,
  ]);

  const isFormComplete = isNameValid && areMandatoryFieldsFilled;

  function handleChange<K extends keyof UpdateClubInput>(
    key: K,
    value: UpdateClubInput[K],
  ) {
    // Special-case when the sport type is changed: set typeId immediately
    // and fetch the sport name to populate typeName asynchronously.
    if (key === "typeId") {
      const newTypeId = value as unknown as string | undefined;
      setForm((prev) => ({ ...prev, typeId: newTypeId }));
      if (newTypeId) {
        getSportById(newTypeId)
          .then((sport) => {
            setForm((prev) => ({
              ...prev,
              typeId: newTypeId,
              typeName: sport?.name ?? undefined,
            }));
          })
          .catch(() => {
            setForm((prev) => ({
              ...prev,
              typeId: newTypeId,
              typeName: undefined,
            }));
          });
      } else {
        setForm((prev) => ({
          ...prev,
          typeId: undefined,
          typeName: undefined,
        }));
      }
      return;
    }

    setForm((prev) => ({ ...prev, [key]: value }));
  }

  function applyAddressSelection(suggestion: City) {
    handleChange("address", suggestion.name);
    handleChange("city", suggestion.city ?? club?.city ?? undefined);
    handleChange(
      "postalCode",
      suggestion.postalCode ?? club?.postalCode ?? undefined,
    );
    handleChange("country", suggestion.country ?? club?.country ?? undefined);
    if (suggestion.coordinates) {
      const [latitude, longitude] = suggestion.coordinates;
      handleChange("latitude", latitude);
      handleChange("longitude", longitude);
    }
    // Keep UI in sync before persisting changes.
    setClub((prev) =>
      prev
        ? {
            ...prev,
            address: suggestion.name,
            city: suggestion.city ?? prev.city,
            postalCode: suggestion.postalCode ?? prev.postalCode,
            country: suggestion.country ?? prev.country,
            latitude: suggestion.coordinates?.[0] ?? prev.latitude,
            longitude: suggestion.coordinates?.[1] ?? prev.longitude,
          }
        : prev,
    );
    setAddressSuggestions([]);
    setAddressQuery("");
    setAddressValid(true);
  }

  async function handleSave() {
    if (!club || !token) return;
    setSaving(true);
    setError(null);
    setSaveMessage(null);
    try {
      const trimmedAddress = (form.address ?? "").trim();
      if (!isNameValid) {
        setError("Le nom du club est obligatoire.");
        setSaving(false);
        return;
      }
      if (!trimmedAddress || !addressValid) {
        setError(
          "Veuillez sélectionner une adresse valide parmi les suggestions.",
        );
        setSaving(false);
        return;
      }
      if (!areMandatoryFieldsFilled) {
        setError("Merci de remplir tous les champs obligatoires.");
        setSaving(false);
        return;
      }
      // Ensure we have a typeName for the selected typeId before saving.
      const formToSave: UpdateClubInput = { ...form };
      if (formToSave.typeId && !formToSave.typeName) {
        try {
          const sport = await getSportById(formToSave.typeId);
          formToSave.typeName = sport?.name ?? undefined;
        } catch {
          formToSave.typeName = undefined;
        }
      }


      const payload: UpdateClubInput = {
        ...form,
        latitude: form.latitude ?? club.latitude,
        longitude: form.longitude ?? club.longitude,
      };
      const updated = await updateFullClub(
        club.id,
        payload,
        club.ownerId,
        token,
      );
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
                      <Stack gap={0.5}>
                        <Input
                          value={form.name ?? ""}
                          onChange={(e) => handleChange("name", e.target.value)}
                          placeholder="Nom du club"
                          size="lg"
                          variant="soft"
                          color={isNameValid ? "neutral" : "danger"}
                          aria-invalid={!isNameValid}
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
                        {!isNameValid && (
                          <Typography level="body-sm" color="danger">
                            Le nom du club est obligatoire.
                          </Typography>
                        )}
                      </Stack>
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
                            !availableSportTypes.some((st) => st.id === s.id)
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
                    {/* City chip removed; location shown in details section */}
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
                    {editMode && (
                      <div style={{ position: "relative" }}>
                        <textarea
                          value={form.address ?? ""}
                          onChange={(e) => {
                            handleChange("address", e.target.value);
                            setAddressQuery(e.target.value);
                            setAddressValid(false);
                          }}
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
                        {addressSuggestions.length > 0 && (
                          <div
                            style={{
                              position: "absolute",
                              left: 8,
                              right: 8,
                              top: "100%",
                              zIndex: 40,
                              background:
                                theme.palette.mode === "dark"
                                  ? "rgba(15,23,42,0.9)"
                                  : "#fff",
                              border:
                                "1px solid var(--joy-palette-neutral-outlinedBorder)",
                              borderRadius: 8,
                              marginTop: 6,
                              maxHeight: 200,
                              overflow: "auto",
                            }}
                          >
                            {addressSuggestions.map((s) => (
                              <button
                                key={s.id}
                                type="button"
                                onClick={() => applyAddressSelection(s)}
                                style={{
                                  display: "block",
                                  width: "100%",
                                  textAlign: "left",
                                  padding: "8px 10px",
                                  border: "none",
                                  background: "transparent",
                                  cursor: "pointer",
                                }}
                              >
                                {`${s.name}${s.city ? ` — ${s.city}` : ""}${s.postalCode ? ` (${s.postalCode})` : ""}`}
                              </button>
                            ))}
                          </div>
                        )}
                        {!addressValid && (
                          <Typography
                            level="body-sm"
                            color="danger"
                            sx={{ mt: 1 }}
                          >
                            Merci de choisir une adresse dans la liste pour
                            valider la localisation.
                          </Typography>
                        )}
                      </div>
                    )}
                    <Typography>
                      {(editMode ? form.address : club.address) || "—"}
                    </Typography>
                    <Typography>
                      {(editMode ? form.city : club.city) || "—"}
                    </Typography>
                    {/* Postal code & country now always read-only (show form values when editing) */}
                    <Typography>
                      {editMode
                        ? `${form.postalCode ?? club.postalCode ?? ""} ${form.country ?? club.country ?? ""}`.trim()
                        : `${club.postalCode ? `${club.postalCode} ` : ""}${club.country}`}
                    </Typography>
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
                    navigate("/clubs/manage/members", {
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
                    disabled={saving || !addressValid || !isFormComplete}
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
                          typeId: club.typeId!,
                          typeName: club.typeName!,
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
