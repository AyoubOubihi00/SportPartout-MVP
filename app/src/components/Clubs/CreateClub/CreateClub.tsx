import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import BaseLayout from "../../../layouts/BaseLayout";
import {
  Alert,
  Button,
  Divider,
  Grid,
  IconButton,
  Input,
  Select,
  Option,
  Sheet,
  Stack,
  Typography,
  useTheme,
} from "@mui/joy";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import CategoryIcon from "@mui/icons-material/Category";
import { Sport } from "../../../model/Sport";
import { createClub, UpdateClubInput } from "../../../services/clubService";
import { useAuth } from "../../shared/useAuth";
import adminService from "../../../services/adminService";
import { getSportById } from "../../../services/sportService";
import { fetchAddress } from "../../../services/cityApiService";
import { City } from "../../../model/City";
import { getMe } from "../../../services/auth/authService";

export default function CreateClub() {
  const navigate = useNavigate();
  const theme = useTheme();
  const mainColor = theme.palette.mode === "dark" ? "#fff" : "#000";
  const { token } = useAuth();

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [form, setForm] = useState<Partial<UpdateClubInput>>({});
  const [availableSportTypes, setAvailableSportTypes] = useState<Sport[]>([]);
  const [addressSuggestions, setAddressSuggestions] = useState<City[]>([]);
  const [addressQuery, setAddressQuery] = useState("");
  const [addressValid, setAddressValid] = useState(false);
  const [ownerId, setOwnerId] = useState<string | null>(null);

  // Check auth and role
  useEffect(() => {
    if (!token) {
      navigate("/login");
      return;
    }
    getMe(token)
      .then((profile) => {
        if (profile?.role?.toUpperCase() !== "OWNER") {
          navigate("/clubs"); // Redirect if not owner
        } else {
          setOwnerId(profile.id);
        }
      })
      .catch(() => navigate("/login"));
  }, [token, navigate]);

  // Fetch sports
  useEffect(() => {
    adminService
      .fetchSportTypes()
      .then((types) => {
        setAvailableSportTypes(
          types.map((t) => ({ id: t.id, name: t.name, icon: "" }) as Sport),
        );
      })
      .catch(console.error);
  }, []);

  // Address autocomplete
  useEffect(() => {
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
  }, [addressQuery]);

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
      form.typeId,
    ];
    return requiredValues.every(
      (value) => typeof value === "string" && value.trim().length > 0,
    );
  }, [form]);

  function handleChange<K extends keyof UpdateClubInput>(
    key: K,
    value: UpdateClubInput[K],
  ) {
    if (key === "typeId") {
      const newTypeId = value as string;
      setForm((prev) => ({ ...prev, typeId: newTypeId }));
      getSportById(newTypeId).then((sport) => {
        setForm((prev) => ({ ...prev, typeName: sport?.name }));
      });
      return;
    }
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  function applyAddressSelection(suggestion: City) {
    handleChange("address", suggestion.name);
    handleChange("city", suggestion.city ?? "");
    handleChange("postalCode", suggestion.postalCode ?? "");
    handleChange("country", suggestion.country ?? "");
    if (suggestion.coordinates) {
      handleChange("latitude", suggestion.coordinates[0]);
      handleChange("longitude", suggestion.coordinates[1]);
    }
    setAddressSuggestions([]);
    setAddressQuery(suggestion.name);
    setAddressValid(true);
  }

  async function handleCreate() {
    if (!token || !ownerId) return;
    setLoading(true);
    setError(null);

    if (!isNameValid || !areMandatoryFieldsFilled || !addressValid) {
      setError(
        "Veuillez remplir tous les champs obligatoires et sélectionner une adresse valide.",
      );
      setLoading(false);
      return;
    }

    try {
      console.log("Creating club with data:", form, "and ownerId:", ownerId);
      await createClub(form as UpdateClubInput, ownerId, token);
      navigate("/clubs");
    } catch (e: unknown) {
      setError(
        e instanceof Error
          ? e.message
          : String(e) || "Erreur lors de la création du club",
      );
    } finally {
      setLoading(false);
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
      <Stack gap={2} sx={{ maxWidth: 800, mx: "auto", px: 2, py: 3 }}>
        <Stack direction="row" alignItems="center" gap={1}>
          <IconButton
            variant="solid"
            color="neutral"
            size="md"
            onClick={() => navigate("/clubs")}
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
            Créer un nouveau club
          </Typography>
        </Stack>

        {error && (
          <Alert color="danger" variant="soft">
            {error}
          </Alert>
        )}

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
          <Stack gap={2}>
            <Typography
              level="title-lg"
              sx={{ color: mainColor, fontWeight: 700 }}
            >
              Informations générales
            </Typography>
            <Input
              placeholder="Nom du club"
              value={form.name || ""}
              onChange={(e) => handleChange("name", e.target.value)}
              error={!isNameValid && form.name !== undefined}
              sx={{
                fontWeight: 600,
                fontSize: "1.1rem",
              }}
            />

            <Select
              placeholder="Type de sport"
              onChange={(_, val) => handleChange("typeId", val as string)}
              value={form.typeId}
              startDecorator={<CategoryIcon fontSize="small" />}
            >
              {availableSportTypes.map((s) => (
                <Option key={s.id} value={s.id}>
                  {s.name}
                </Option>
              ))}
            </Select>

            <Divider />

            <Typography
              level="title-lg"
              sx={{ color: mainColor, fontWeight: 700 }}
            >
              Localisation
            </Typography>
            <div style={{ position: "relative" }}>
              <Input
                placeholder="Rechercher une adresse..."
                value={addressQuery}
                onChange={(e) => {
                  setAddressQuery(e.target.value);
                  setAddressValid(false);
                }}
              />
              {addressSuggestions.length > 0 && (
                <Sheet
                  sx={{
                    position: "absolute",
                    zIndex: 10,
                    width: "100%",
                    maxHeight: 200,
                    overflow: "auto",
                    mt: 1,
                    p: 1,
                    boxShadow: "md",
                    borderRadius: "sm",
                    border: "1px solid",
                    borderColor: "neutral.outlinedBorder",
                  }}
                >
                  {addressSuggestions.map((s) => (
                    <Button
                      key={s.id}
                      variant="plain"
                      fullWidth
                      sx={{ justifyContent: "flex-start", textAlign: "left" }}
                      onClick={() => applyAddressSelection(s)}
                    >
                      {s.name} {s.city ? ` — ${s.city}` : ""}
                    </Button>
                  ))}
                </Sheet>
              )}
            </div>
            {!addressValid && addressQuery.length > 0 && (
              <Typography level="body-sm" color="danger">
                Veuillez sélectionner une adresse dans la liste.
              </Typography>
            )}

            <Grid container spacing={2}>
              <Grid xs={6}>
                <Input
                  placeholder="Ville"
                  value={form.city || ""}
                  disabled
                  variant="soft"
                />
              </Grid>
              <Grid xs={6}>
                <Input
                  placeholder="Code postal"
                  value={form.postalCode || ""}
                  disabled
                  variant="soft"
                />
              </Grid>
              <Grid xs={12}>
                <Input
                  placeholder="Pays"
                  value={form.country || ""}
                  disabled
                  variant="soft"
                />
              </Grid>
            </Grid>

            <Divider />

            <Typography
              level="title-lg"
              sx={{ color: mainColor, fontWeight: 700 }}
            >
              Contact
            </Typography>
            <Input
              placeholder="Email"
              value={form.email || ""}
              onChange={(e) => handleChange("email", e.target.value)}
            />
            <Input
              placeholder="Téléphone"
              value={form.phoneNumber || ""}
              onChange={(e) => handleChange("phoneNumber", e.target.value)}
            />

            <Button
              loading={loading}
              onClick={handleCreate}
              disabled={
                !isNameValid || !areMandatoryFieldsFilled || !addressValid
              }
              sx={{
                mt: 2,
                backgroundColor: mainColor,
                color: theme.palette.mode === "dark" ? "#121212" : "#fff",
                fontWeight: 600,
                "&:hover": { backgroundColor: mainColor },
              }}
            >
              Créer le club
            </Button>
          </Stack>
        </Sheet>
      </Stack>
    </BaseLayout>
  );
}
