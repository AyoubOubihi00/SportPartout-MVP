import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import "./CommonStyle.css";
import Input from "@mui/joy/Input";
import Button from "@mui/joy/Button";
import Typography from "@mui/joy/Typography";
import BaseLayout from "../../layouts/BaseLayout";
import { useTheme } from "@mui/joy/styles";

export function ResetPassword() {
  const { token } = useParams();
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();
  const theme = useTheme();

  const darkGradient = "linear-gradient(135deg, #0f172a, #1e293b)";
  const lightGradient = "linear-gradient(135deg, #f8fafc, #e2e8f0)";

  const mainColor = theme.palette.mode ? "#fff" : "#000";

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!password || !confirmPassword) {
      setMessage("Veuillez remplir tous les champs.");
      return;
    }

    if (password !== confirmPassword) {
      setMessage("Les mots de passe ne correspondent pas.");
      return;
    }

    if (password.length < 6) {
      setMessage("Le mot de passe doit contenir au moins 6 caractères.");
      return;
    }

    setMessage("");
    console.log("Réinitialisation du mot de passe avec token:", token);
    console.log("Nouveau mot de passe:", password);
  };

  return (
    <BaseLayout>
      <div
        className="main-container"
        style={{
          flexGrow: 1,
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          width: "100%",
          maxWidth: 400,
          margin: "0 auto",
          backgroundColor: theme.palette.mode == "dark" ? "#121212" : "#fff",
          color: mainColor,
          paddingBottom: "120px",
        }}
      >
        <Typography level="h4" sx={{ color: mainColor, mb: 2 }}>
          Réinitialiser le mot de passe
        </Typography>
        <Typography level="body-sm" sx={{ color: mainColor, mb: 3 }}>
          Entrez votre nouveau mot de passe.
        </Typography>

        <form onSubmit={handleSubmit}>
          <Input
            type="password"
            placeholder="Nouveau mot de passe"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            sx={{ mb: 2 }}
          />
          <Input
            type="password"
            placeholder="Confirmer le mot de passe"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
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

          <Button
            type="submit"
            variant="solid"
            sx={{
              mt: 1,
              width: "100%",
              backgroundColor: mainColor,
              color: theme.palette.mode == "dark" ? "#121212" : "#fff",
              "&:hover": { backgroundColor: mainColor },
              "&:focus": { backgroundColor: mainColor, boxShadow: "none" },
              "&:active": { backgroundColor: mainColor },
            }}
          >
            Réinitialiser
          </Button>
        </form>

        {message && (
          <Typography level="body-sm" color="danger" textAlign="center" mt={2}>
            {message}
          </Typography>
        )}

        <Typography
          level="body-sm"
          textAlign="center"
          sx={{ mt: 3, color: mainColor }}
        >
          <Button
            variant="plain"
            onClick={() => navigate("/login")}
            sx={{ color: mainColor }}
          >
            Retour à la connexion
          </Button>
        </Typography>
      </div>
    </BaseLayout>
  );
}
