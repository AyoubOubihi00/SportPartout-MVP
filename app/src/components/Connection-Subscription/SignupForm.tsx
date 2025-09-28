import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Input from "@mui/joy/Input";
import Select from "@mui/joy/Select";
import Option from "@mui/joy/Option";
import Button from "@mui/joy/Button";
import LinearProgress from "@mui/joy/LinearProgress";
import Typography from "@mui/joy/Typography";
import { AuthLayout } from "./AuthLayout";
import { useTheme } from "@mui/joy";

export function SignupForm() {
  const [values, setValues] = useState({
    nom: "",
    prenom: "",
    birthDate: "",
    sexe: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [message, setMessage] = useState("");
  const navigate = useNavigate();
  const theme = useTheme();

  const minLength = 12;


  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (
      !values.nom ||
      !values.prenom ||
      !values.email ||
      !values.password ||
      !values.confirmPassword ||
      !values.sexe ||
      !values.birthDate
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
    console.log("Signup data:", values);
  };

  const mainColor = theme.palette.mode == "dark" ? "#fff" : "#000";

  return (
    <AuthLayout >
      <Typography level="h4" sx={{ mb: 2, color: mainColor }}>
        Inscription
      </Typography>

      <form onSubmit={handleSubmit}>
        <Input name="nom" placeholder="Nom" value={values.nom} onChange={handleChange} sx={{ mb: 2 }} />
        <Input name="prenom" placeholder="Prénom" value={values.prenom} onChange={handleChange} sx={{ mb: 2 }} />
        <Input
          name="birthDate"
          type="date"
          placeholder="Date de naissance"
          value={values.birthDate}
          onChange={handleChange}
          sx={{ mb: 2 }}
        />

        <Select
          name="sexe"
          value={values.sexe}
          onChange={(e: any) => setValues({ ...values, sexe: e })}
          placeholder="Sexe"
          sx={{ mb: 2 }}
        >
          <Option value="Homme">Homme</Option>
          <Option value="Femme">Femme</Option>
          <Option value="Autre">Autre</Option>
        </Select>

        <Input name="email" type="email" placeholder="Email" value={values.email} onChange={handleChange} sx={{ mb: 2 }} />

        <Input
          name="password"
          type="password"
          placeholder="Mot de passe"
          value={values.password}
          onChange={handleChange}
          sx={{ mb: 2 }}
        />
        <LinearProgress
          determinate
          size="sm"
          value={Math.min((values.password.length * 100) / minLength, 100)}
          sx={{ bgcolor: "background.level3", color: "hsl(var(--hue) 80% 40%)" }}
        />
        <Typography level="body-xs" sx={{ alignSelf: "flex-end", color: "hsl(var(--hue) 80% 30%)" }}>
          {values.password.length < 3 && "Mot de passe trop court"}
          {values.password.length >= 3 && values.password.length < 6 && "Faible"}
          {values.password.length >= 6 && values.password.length < 10 && "Fort"}
          {values.password.length >= 10 && "Très fort"}
        </Typography>

        <Input
          name="confirmPassword"
          type="password"
          placeholder="Confirmer le mot de passe"
          value={values.confirmPassword}
          onChange={handleChange}
          sx={{ mb: 2 }}
        />

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

      {message && (
        <Typography level="body-sm" color="danger" textAlign="center" mt={2}>
          {message}
        </Typography>
      )}

      <Typography level="body-sm" textAlign="center" sx={{ mt: 2, color: mainColor }}>
        Déjà un compte ?{" "}
        <Button variant="plain" onClick={() => navigate("/login")} sx={{ color: mainColor }}>
          Se connecter
        </Button>
      </Typography>
    </AuthLayout>
  );
}
