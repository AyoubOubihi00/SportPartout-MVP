import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Input from "@mui/joy/Input";
import Select from "@mui/joy/Select";
import Option from "@mui/joy/Option";
import Button from "@mui/joy/Button";
import LinearProgress from "@mui/joy/LinearProgress";
import Typography from "@mui/joy/Typography";
import AuthLayout from "./AuthLayout";
import { useTheme } from "@mui/joy";
import { register } from "../../services/auth/authService";
import { useBubble } from "../shared/useBubble";

export function SignupForm() {
  const [values, setValues] = useState({
    nom: "",
    prenom: "",
    birthDate: "",
    sexe: "",
    email: "",
    password: "",
    confirmPassword: "",
    phone: "",
  });
  const [message, setMessage] = useState("");
  const navigate = useNavigate();
  const theme = useTheme();

  const { showSuccessBubble, showErrorBubble } = useBubble();
  const minLength = 12;

  const darkGradient = "linear-gradient(135deg, #0f172a, #1e293b)";
  const lightGradient = "linear-gradient(135deg, #f8fafc, #e2e8f0)";

  const handleChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >,
  ) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (
      !values.nom ||
      !values.prenom ||
      !values.email ||
      !values.password ||
      !values.confirmPassword ||
      !values.sexe ||
      !values.birthDate ||
      !values.phone
    ) {
      setMessage("Tous les champs sont obligatoires.");
      return;
    }
    if (values.password !== values.confirmPassword) {
      setMessage("Les mots de passe ne correspondent pas.");
      return;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(values.email)) {
      setMessage("Email non valide.");
      return;
    }

    setMessage("");

    // Appel du service d'inscription
    const result = await register({
      email: values.email,
      password: values.password,
      lastName: values.nom,
      firstName: values.prenom,
      birthDate: values.birthDate + "T00:00:00Z",
      phone: values.phone,
    });

    if (result.success) {
      showSuccessBubble("Compte créé !");
      setTimeout(() => {
        navigate("/login");
      }, 1500);
    } else {
      showErrorBubble(result.message || "Erreur lors de l’inscription");
    }
  };

  const mainColor = theme.palette.mode == "dark" ? "#fff" : "#000";

  return (
    <AuthLayout>
      {/* plus de gestion locale d'erreur ici, tout est global */}
      <Typography level="h4" sx={{ mb: 2, color: mainColor }}>
        Inscription
      </Typography>

      <form onSubmit={handleSubmit}>
        <Input
          name="nom"
          placeholder="Nom"
          value={values.nom}
          onChange={handleChange}
          required
          error={!values.nom && message !== ""}
          sx={{
            mb: 2,
            background:
              theme.palette.mode === "dark" ? darkGradient : lightGradient,
            color: theme.palette.mode === "dark" ? "white" : "black",
            "& input::placeholder": {
              color: theme.palette.mode === "dark" ? "grey.400" : "grey.500",
            },
          }}
        />
        {!values.nom && message !== "" && (
          <Typography level="body-xs" color="danger" sx={{ mb: 1 }}>
            Nom obligatoire
          </Typography>
        )}
        <Input
          name="prenom"
          placeholder="Prénom"
          value={values.prenom}
          onChange={handleChange}
          required
          error={!values.prenom && message !== ""}
          sx={{
            mb: 2,
            background:
              theme.palette.mode === "dark" ? darkGradient : lightGradient,
            color: theme.palette.mode === "dark" ? "white" : "black",
            "& input::placeholder": {
              color: theme.palette.mode === "dark" ? "grey.400" : "grey.500",
            },
          }}
        />
        {!values.prenom && message !== "" && (
          <Typography level="body-xs" color="danger" sx={{ mb: 1 }}>
            Prénom obligatoire
          </Typography>
        )}
        <Input
          name="birthDate"
          type="date"
          placeholder="Date de naissance"
          value={values.birthDate}
          onChange={handleChange}
          required
          error={
            (!values.birthDate && message !== "") ||
            (values.birthDate
              ? new Date(values.birthDate) >=
                new Date(new Date().toISOString().slice(0, 10))
              : false)
          }
          sx={{
            mb: 2,
            background:
              theme.palette.mode === "dark" ? darkGradient : lightGradient,
            color: theme.palette.mode === "dark" ? "white" : "black",
            "& input::placeholder": {
              color: theme.palette.mode === "dark" ? "grey.400" : "grey.500",
            },
          }}
        />
        {!values.birthDate && message !== "" && (
          <Typography level="body-xs" color="danger" sx={{ mb: 1 }}>
            Champ obligatoire
          </Typography>
        )}
        {values.birthDate &&
          new Date(values.birthDate) >=
            new Date(new Date().toISOString().slice(0, 10)) && (
            <Typography level="body-xs" color="danger" sx={{ mb: 1 }}>
              La date de naissance doit être antérieure à aujourd'hui
            </Typography>
          )}
        <Select
          value={values.sexe || null}
          onChange={(_event, newValue) =>
            setValues({ ...values, sexe: newValue || "" })
          }
          placeholder="Sexe"
          sx={{
            mb: 2,
            background:
              theme.palette.mode === "dark" ? darkGradient : lightGradient,
            color: theme.palette.mode === "dark" ? "white" : "black",
            border: "none",
            boxShadow: "none",
            "& input::placeholder": {
              color: theme.palette.mode === "dark" ? "grey.400" : "grey.500",
            },
            "&:hover, &.Mui-focused": {
              border: "none",
              boxShadow: "none",
            },
            "& button:focus, & button:focus-visible": {
              outline: "none",
              boxShadow: "none",
            },
          }}
        >
          <Option value="Homme">Homme</Option>
          <Option value="Femme">Femme</Option>
          <Option value="Autre">Autre</Option>
        </Select>
        {!values.sexe && message !== "" && (
          <Typography level="body-xs" color="danger" sx={{ mb: 1 }}>
            Champ obligatoire
          </Typography>
        )}
        <Input
          name="email"
          type="email"
          placeholder="Email"
          value={values.email}
          onChange={handleChange}
          required
          error={
            (!values.email && message !== "") ||
            (values.email
              ? !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email)
              : false)
          }
          sx={{
            mb: 2,
            background:
              theme.palette.mode === "dark" ? darkGradient : lightGradient,
            color: theme.palette.mode === "dark" ? "white" : "black",
            "& input::placeholder": {
              color: theme.palette.mode === "dark" ? "grey.400" : "grey.500",
            },
          }}
        />
        {!values.email && message !== "" && (
          <Typography level="body-xs" color="danger" sx={{ mb: 1 }}>
            Champ obligatoire
          </Typography>
        )}
        {values.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email) && (
          <Typography level="body-xs" color="danger" sx={{ mb: 1 }}>
            Email non valide
          </Typography>
        )}
        <Input
          name="phone"
          placeholder="Téléphone"
          value={values.phone}
          onChange={handleChange}
          required
          error={!values.phone && message !== ""}
          sx={{
            mb: 2,
            background:
              theme.palette.mode === "dark" ? darkGradient : lightGradient,
            color: theme.palette.mode === "dark" ? "white" : "black",
            "& input::placeholder": {
              color: theme.palette.mode === "dark" ? "grey.400" : "grey.500",
            },
          }}
        />

        {!values.phone && message !== "" && (
          <Typography level="body-xs" color="danger" sx={{ mb: 1 }}>
            Téléphone obligatoire
          </Typography>
        )}
        <Input
          name="password"
          type="password"
          placeholder="Mot de passe"
          value={values.password}
          onChange={handleChange}
          required
          error={values.password.length < 6 && message !== ""}
          sx={{
            mb: 2,
            background:
              theme.palette.mode === "dark" ? darkGradient : lightGradient,
            color: theme.palette.mode === "dark" ? "white" : "black",
            "& input::placeholder": {
              color: theme.palette.mode === "dark" ? "grey.400" : "grey.500",
            },
          }}
        />
        {values.password.length < 6 && message !== "" && (
          <Typography level="body-xs" color="danger" sx={{ mb: 1 }}>
            Mot de passe trop court (min 6 caractères)
          </Typography>
        )}
        <LinearProgress
          determinate
          size="sm"
          value={Math.min((values.password.length * 100) / minLength, 100)}
          sx={{
            bgcolor: "background.level3",
            color: "hsl(var(--hue) 80% 40%)",
          }}
        />
        <Typography
          level="body-xs"
          sx={{ alignSelf: "flex-end", color: "hsl(var(--hue) 80% 30%)" }}
        >
          {values.password.length < 3 && "Mot de passe trop court"}
          {values.password.length >= 3 &&
            values.password.length < 6 &&
            "Faible"}
          {values.password.length >= 6 && values.password.length < 10 && "Fort"}
          {values.password.length >= 10 && "Très fort"}
        </Typography>
        <Input
          name="confirmPassword"
          type="password"
          placeholder="Confirmer le mot de passe"
          value={values.confirmPassword}
          onChange={handleChange}
          required
          error={values.password !== values.confirmPassword && message !== ""}
          sx={{
            mt: 2,
            mb: 2,
            background:
              theme.palette.mode === "dark" ? darkGradient : lightGradient,
            color: theme.palette.mode === "dark" ? "white" : "black",
            "& input::placeholder": {
              color: theme.palette.mode === "dark" ? "grey.400" : "grey.500",
            },
          }}
        />
        {values.password !== values.confirmPassword && message !== "" && (
          <Typography level="body-xs" color="danger" sx={{ mb: 1 }}>
            Les mots de passe ne correspondent pas
          </Typography>
        )}

        <Button
          type="submit"
          variant="solid"
          sx={{
            mt: 2,
            width: "100%",
            backgroundColor: mainColor,
            color: theme.palette.mode == "dark" ? "#121212" : "#fff",
            "&:hover": { backgroundColor: mainColor },
            "&:focus": { backgroundColor: mainColor, boxShadow: "none" },
            "&:active": { backgroundColor: mainColor },
          }}
        >
          S'inscrire
        </Button>
      </form>

      <Typography
        level="body-sm"
        textAlign="center"
        sx={{ mt: 2, color: mainColor }}
      >
        Déjà un compte ?{" "}
        <Button
          variant="plain"
          onClick={() => navigate("/login")}
          sx={{ color: mainColor }}
        >
          Se connecter
        </Button>
      </Typography>
    </AuthLayout>
  );
}
