import React, { useState, useRef, useEffect } from "react";
import {
  getMe,
  UserProfile,
  updateUser,
} from "../../../services/auth/authService";
import { useNavigate } from "react-router-dom";
import Input from "@mui/joy/Input";
import Button from "@mui/joy/Button";
import Typography from "@mui/joy/Typography";
import Avatar from "@mui/joy/Avatar";
import IconButton from "@mui/joy/IconButton";
import FormControl from "@mui/joy/FormControl";
import FormLabel from "@mui/joy/FormLabel";
import Stack from "@mui/joy/Stack";
import CircularProgress from "@mui/joy/CircularProgress";
import { useTheme } from "@mui/joy/styles";
import { Camera, X } from "lucide-react";
import ProfileLayout from "../../Profile/ProfileLayout";
import { useAuth } from "../../shared/useAuth";
import { useBubble } from "../../shared/useBubble";

export function EditMemberProfile() {
  const { token } = useAuth();
  const { showSuccessBubble, showErrorBubble } = useBubble();
  const theme = useTheme();
  const mainColor = theme.palette.mode == "dark" ? "#fff" : "#000";
  const darkGradient = "linear-gradient(135deg, #0f172a, #1e293b)";
  const lightGradient = "linear-gradient(135deg, #f8fafc, #e2e8f0)";
  const bgColor = theme.palette.mode === "dark" ? darkGradient : lightGradient;
  const textOnMain = theme.palette.mode === "dark" ? "#121212" : "#fff";
  const [loading, setLoading] = useState(true);
  const [, setUserData] = useState<UserProfile | null>(null);
  const navigate = useNavigate();

  const [values, setValues] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    birthDate: "",
    gender: "",
  });
  useEffect(() => {
    if (!token) return;
    setLoading(true);
    getMe(token)
      .then((data) => {
        setUserData(data);
        if (data) {
          setValues({
            firstName: data.firstName || "",
            lastName: data.lastName || "",
            email: data.email || "",
            phone: data.phone || "",
            // Stocker birthDate au format yyyy-MM-dd pour l'input type="date"
            birthDate: Date.parse(data.birthDate)
              ? new Date(data.birthDate).toISOString().slice(0, 10)
              : "",
            gender: "", // à adapter si tu as la ville dans l'API
          });
        }
      })
      .finally(() => setLoading(false));
  }, [token]);

  // avatar
  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [avatarPreview, setAvatarPreview] = useState<string | null>(null); // blob url ou url distante
  const prevBlobRef = useRef<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [, setSuccess] = useState<string | null>(null);

  const MAX_SIZE = 2 * 1024 * 1024; // 2MB

  useEffect(() => {
    return () => {
      // cleanup du blob URL si créé
      if (prevBlobRef.current) {
        URL.revokeObjectURL(prevBlobRef.current);
        prevBlobRef.current = null;
      }
    };
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setError(null);
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith("image/")) {
      setError("Format non supporté (choisir une image).");
      return;
    }
    if (file.size > MAX_SIZE) {
      setError("Image trop volumineuse (max 2 Mo).");
      return;
    }

    // revoke ancien blob si nécessaire
    if (prevBlobRef.current) {
      URL.revokeObjectURL(prevBlobRef.current);
      prevBlobRef.current = null;
    }

    const blobUrl = URL.createObjectURL(file);
    prevBlobRef.current = blobUrl;
    setAvatarFile(file);
    setAvatarPreview(blobUrl);
  };

  const handleRemoveAvatar = () => {
    if (prevBlobRef.current) {
      URL.revokeObjectURL(prevBlobRef.current);
      prevBlobRef.current = null;
    }
    setAvatarFile(null);
    setAvatarPreview(null);
    setError(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    // validation basique
    if (!values.firstName || !values.lastName || !values.email) {
      setError("Le prénom, le nom et l’email sont obligatoires.");
      showErrorBubble("Le prénom, le nom et l’email sont obligatoires.");
      return;
    }

    try {
      // Appel du service updateUser (GraphQL mutation)
      // Préparer l'input en respectant le format attendu (ISO pour la date)
      const input = {
        firstName: values.firstName,
        lastName: values.lastName,
        phone: values.phone || undefined,
        birthDate: values.birthDate
          ? new Date(values.birthDate).toISOString()
          : undefined,
      };

      // appel API
      setLoading(true);
      const res = await updateUser(input);
      setLoading(false);

      if (res.message === "User updated successfully") {
        // afficher message succès
        setError(null);
        setSuccess("Profil mis à jour avec succès.");
        showSuccessBubble("Profil mis à jour avec succès.");
        // attendre un court instant pour laisser l'utilisateur voir le message, puis naviguer
        setTimeout(() => navigate("/profile"), 1200);
      } else {
        const msg = res.message || "Erreur lors de la mise à jour du profil.";
        setError(msg);
        showErrorBubble(msg);
      }
    } catch (err) {
      setLoading(false);
      const msg =
        err instanceof Error && err.message
          ? err.message
          : "Erreur lors de la sauvegarde.";
      setError(msg);
      showErrorBubble(msg);
    }
  };

  // initials pour l'avatar quand pas d'image
  const initials = (
    (values.firstName?.charAt(0) ?? "") + (values.lastName?.charAt(0) ?? "")
  ).toUpperCase();

  if (loading) {
    return (
      <ProfileLayout showRoleSwitch={false}>
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            minHeight: 300,
          }}
        >
          <CircularProgress size="lg" variant="plain" />
        </div>
      </ProfileLayout>
    );
  }

  return (
    <ProfileLayout showRoleSwitch={false}>
      <div
        style={{
          margin: "0 auto",
          padding: "0 16px",
          paddingTop: 32,
          width: "100%",
          maxWidth: 480,
        }}
      >
        <Typography
          level="h4"
          sx={{ mb: 3, textAlign: "center", color: mainColor }}
        >
          Modifier mon profil
        </Typography>

        {/* avatar upload */}
        <div>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: 12,
              justifyContent: "center",
              marginBottom: 16,
            }}
          >
            <Avatar
              src={avatarPreview ?? undefined}
              sx={{
                "--Avatar-size": "90px",
                border: "2px solid",
                borderColor: mainColor,
                background: bgColor,
                color: theme.palette.mode === "dark" ? "white" : "black",
              }}
            >
              {!avatarPreview && initials}
            </Avatar>

            <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
              {/* bouton pour ouvrir le file input */}
              <label htmlFor="avatar-upload">
                <input
                  id="avatar-upload"
                  type="file"
                  accept="image/*"
                  style={{ display: "none" }}
                  onChange={handleFileChange}
                />
                <IconButton
                  aria-label="Changer la photo"
                  component="span"
                  size="sm"
                >
                  <Camera size={16} />
                </IconButton>
              </label>

              {/* supprimer / restaurer */}
              {(avatarPreview || avatarFile) && (
                <IconButton
                  aria-label="Supprimer la photo"
                  color="neutral"
                  size="sm"
                  onClick={handleRemoveAvatar}
                >
                  <X size={16} />
                </IconButton>
              )}
            </div>
          </div>

          {error && (
            <Typography level="body-xs" color="danger" sx={{ mt: 1 }}>
              {error}
            </Typography>
          )}
          <Typography
            level="body-xs"
            sx={{ mt: 1, color: "neutral.500", textAlign: "center" }}
          >
            Taille maximale : 2 Mo. Formats : jpg, png, webp...
          </Typography>
          <form onSubmit={handleSubmit}>
            <Stack spacing={2} sx={{ mb: 3, mt: 1 }}>
              <FormControl>
                <FormLabel
                  sx={{
                    textTransform: "uppercase",
                    letterSpacing: ".08em",
                    fontSize: 12,
                  }}
                >
                  Nom
                </FormLabel>
                <Input
                  name="lastName"
                  value={values.lastName}
                  onChange={handleChange}
                  placeholder="Votre nom"
                />
              </FormControl>
              <FormControl>
                <FormLabel
                  sx={{
                    textTransform: "uppercase",
                    letterSpacing: ".08em",
                    fontSize: 12,
                  }}
                >
                  Prénom
                </FormLabel>
                <Input
                  name="firstName"
                  value={values.firstName}
                  onChange={handleChange}
                  placeholder="Votre prénom"
                />
              </FormControl>
              <FormControl>
                <FormLabel
                  sx={{
                    textTransform: "uppercase",
                    letterSpacing: ".08em",
                    fontSize: 12,
                  }}
                >
                  Email
                </FormLabel>
                <Input
                  name="email"
                  type="email"
                  disabled
                  value={values.email}
                  onChange={handleChange}
                  placeholder="Email"
                />
              </FormControl>
              <FormControl>
                <FormLabel
                  sx={{
                    textTransform: "uppercase",
                    letterSpacing: ".08em",
                    fontSize: 12,
                  }}
                >
                  Téléphone
                </FormLabel>
                <Input
                  name="phone"
                  type="tel"
                  value={values.phone}
                  onChange={handleChange}
                  placeholder="06 12 34 56 78"
                />
              </FormControl>
              <FormControl>
                <FormLabel
                  sx={{
                    textTransform: "uppercase",
                    letterSpacing: ".08em",
                    fontSize: 12,
                  }}
                >
                  Naissance
                </FormLabel>
                <Input
                  name="birthDate"
                  type="date"
                  value={values.birthDate}
                  onChange={handleChange}
                />
              </FormControl>
            </Stack>

            <div style={{ display: "flex", gap: 16 }}>
              <Button
                variant="outlined"
                color="neutral"
                fullWidth
                type="button"
                onClick={() => navigate("/profile")}
                disabled={loading}
              >
                Annuler
              </Button>
              <Button
                type="submit"
                variant="solid"
                fullWidth
                disabled={loading}
                sx={{
                  backgroundColor: mainColor,
                  color: textOnMain,
                  fontWeight: "bold",
                  boxShadow: 0,
                  "&:hover": { backgroundColor: mainColor },
                  "&:focus": { backgroundColor: mainColor, boxShadow: "none" },
                  "&:active": { backgroundColor: mainColor },
                }}
              >
                {loading ? "Enregistrement..." : "Enregistrer"}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </ProfileLayout>
  );
}
