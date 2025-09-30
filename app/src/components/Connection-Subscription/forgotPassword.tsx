import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Input from "@mui/joy/Input";
import Button from "@mui/joy/Button";
import Typography from "@mui/joy/Typography";
import AuthLayout from "./AuthLayout";
import { useTheme } from "@mui/joy";

export function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const theme = useTheme();

  const darkGradient = 'linear-gradient(135deg, #0f172a, #1e293b)';
  const lightGradient = 'linear-gradient(135deg, #f8fafc, #e2e8f0)';

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email) {
      setMessage("Veuillez entrer votre email.");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setMessage("Email non valide.");
      return;
    }

    setMessage("");
    console.log("Réinitialisation du mot de passe pour:", email);
  };

  const mainColor = theme.palette.mode == "dark"  ? "#fff" : "#000";

  return (
    <AuthLayout >
      <Typography level="h4" sx={{ mb: 2, color: mainColor }}>
        Réinitialiser le mot de passe
      </Typography>
      <Typography level="body-sm" sx={{ mb: 3, color: mainColor }}>
        Entrez votre email pour recevoir un lien de réinitialisation.
      </Typography>

      <form onSubmit={handleSubmit}>
        <Input
          type="email"
          placeholder="Votre email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          sx={{
          mb: 2,
          background: theme.palette.mode === 'dark' ? darkGradient : lightGradient,
          color: theme.palette.mode === 'dark' ? 'white' : 'black',
          '& input::placeholder': {
            color: theme.palette.mode === 'dark' ? 'grey.400' : 'grey.500',
          },
        }}
        />

        <Button
          type="submit"
          variant="solid"
          sx={{
            mt: 1,
            width: "100%",
            backgroundColor: mainColor,
            color: theme.palette.mode == "dark"  ? "#121212" : "#fff",
            "&:hover": { backgroundColor: mainColor },
            "&:focus": { backgroundColor: mainColor, boxShadow: "none" },
            "&:active": { backgroundColor: mainColor },
          }}
        >
          Envoyer
        </Button>
      </form>

      {message && (
        <Typography level="body-sm" color="danger" textAlign="center" mt={2}>
          {message}
        </Typography>
      )}

      <Typography level="body-sm" textAlign="center" sx={{ mt: 3, color: mainColor }}>
        <Button variant="plain" onClick={() => navigate("/login")} sx={{ color: mainColor }}>
          Retour à la connexion
        </Button>
      </Typography>
    </AuthLayout>
  );
}
