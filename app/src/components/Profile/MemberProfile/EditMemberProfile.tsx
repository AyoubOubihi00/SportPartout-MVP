import React, { useState, useRef, useEffect } from "react";
import { getMe, UserProfile } from "../../../services/auth/authService";
import { useNavigate } from "react-router-dom";
import Input from "@mui/joy/Input";
import Button from "@mui/joy/Button";
import Typography from "@mui/joy/Typography";
import Avatar from "@mui/joy/Avatar";
import IconButton from "@mui/joy/IconButton";
import { Camera, X } from "lucide-react";
import BaseLayout from "../../../layouts/BaseLayout";
import { useAuth } from "../../shared/useAuth";

export function EditMemberProfile() {
  const { token } = useAuth();
  const [loading, setLoading] = useState(true);
  const [, setUserData] = useState<UserProfile | null>(null);
  const navigate = useNavigate();

  const [values, setValues] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    birthDate: "",
    city: "",
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
            // Suprrimer T00:00Z et formatage à une date dd/MM/YYYY
            birthDate: Date.parse(data.birthDate)
              ? new Intl.DateTimeFormat("fr-FR").format(
                  new Date(data.birthDate),
                )
              : "",
            city: "", // à adapter si tu as la ville dans l'API
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
      return;
    }

    try {
      // Exemple : envoi via FormData si tu as une API qui accepte l'avatar
      const formData = new FormData();
      formData.append("firstName", values.firstName);
      formData.append("lastName", values.lastName);
      formData.append("email", values.email);
      formData.append("phone", values.phone);
      formData.append("city", values.city);
      if (avatarFile) {
        formData.append("avatar", avatarFile);
      }

      console.log("Données envoyées (simulées) :", { values, avatarFile });
      // navigate vers la page profil (ou afficher confirmation)
      navigate("/profile");
    } catch (err) {
      setError(
        err instanceof Error && err.message
          ? err.message
          : "Erreur lors de la sauvegarde.",
      );
    }
  };

  // initials pour l'avatar quand pas d'image
  const initials = (
    (values.firstName?.charAt(0) ?? "") + (values.lastName?.charAt(0) ?? "")
  ).toUpperCase();

  if (loading) {
    return (
      <BaseLayout>
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            minHeight: 300,
          }}
        >
          <span>Chargement du profil...</span>
        </div>
      </BaseLayout>
    );
  }

  return (
    <BaseLayout>
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          padding: 32,
        }}
      >
        <div style={{ width: "100%", maxWidth: 420 }}>
          <Typography level="h4" sx={{ mb: 3, textAlign: "center" }}>
            Modifier mon profil
          </Typography>

          {/* avatar upload */}
          <div
            style={{
              display: "flex",
              alignItems: "center",
              flexDirection: "column",
              marginBottom: 24,
            }}
          >
            <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
              <Avatar
                src={avatarPreview ?? undefined}
                sx={{
                  "--Avatar-size": "90px",
                  border: "2px solid",
                  borderColor: "primary.solidBg",
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
            <Typography level="body-xs" sx={{ mt: 1, color: "neutral.500" }}>
              Taille maximale : 2 Mo. Formats : jpg, png, webp...
            </Typography>
          </div>

          <form onSubmit={handleSubmit}>
            <Input
              name="name"
              value={values.lastName}
              onChange={handleChange}
              placeholder="Nom complet"
              sx={{ mb: 2 }}
            />
            <Input
              name="firstName"
              value={values.firstName}
              onChange={handleChange}
              placeholder="Prénom"
              sx={{ mb: 2 }}
            />

            <Input
              name="email"
              type="email"
              disabled={true}
              value={values.email}
              onChange={handleChange}
              placeholder="Email"
              sx={{ mb: 2 }}
            />
            <Input
              name="phoneNumber"
              type="tel"
              value={values.phone}
              onChange={handleChange}
              placeholder="Numéro de téléphone"
              sx={{ mb: 2 }}
            />
            <Input
              name="birthDate"
              type="date"
              value={values.birthDate}
              onChange={handleChange}
              placeholder="Date de naissance"
              sx={{ mb: 3 }}
            />

            <div style={{ display: "flex", gap: 16 }}>
              <Button
                variant="outlined"
                color="neutral"
                fullWidth
                onClick={() => navigate("/memberprofile")}
              >
                Annuler
              </Button>
              <Button
                /*type="submit"*/ onClick={() => navigate("/memberprofile")}
                variant="solid"
                fullWidth
              >
                Enregistrer
              </Button>
            </div>
          </form>
        </div>
      </div>
    </BaseLayout>
  );
}
