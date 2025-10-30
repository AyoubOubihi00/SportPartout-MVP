import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Input from "@mui/joy/Input";
import Button from "@mui/joy/Button";
import Typography from "@mui/joy/Typography";
import AuthLayout from "./AuthLayout";
import { useTheme } from "@mui/joy/styles";
import { useAuth } from "../shared/useAuth";
import { useBubble } from "../shared/useBubble";

export function LoginForm() {
  const [values, setValues] = useState({ email: "", password: "" });
  const [message, setMessage] = useState("");
  const navigate = useNavigate();
  const theme = useTheme();

  const darkGradient = "linear-gradient(135deg, #0f172a, #1e293b)";
  const lightGradient = "linear-gradient(135deg, #f8fafc, #e2e8f0)";

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const { login } = useAuth();
  const { showSuccessBubble, showErrorBubble } = useBubble();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!values.email || !values.password) {
      setMessage("Email et mot de passe sont obligatoires");
      showErrorBubble("Email et mot de passe sont obligatoires");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(values.email)) {
      setMessage("Email non valide");
      showErrorBubble("Email non valide");
      return;
    }

    setMessage("");
    const result = await login({
      email: values.email,
      password: values.password,
    });
    if (result.success) {
      showSuccessBubble("Connexion réussie !");
      setTimeout(() => navigate("/"), 1000);
    } else {
      setMessage(result.message || "Erreur lors de la connexion");
      showErrorBubble(result.message || "Erreur lors de la connexion");
    }
  };

  const mainColor = theme.palette.mode == "dark" ? "#fff" : "#000";

  return (
    <AuthLayout>
      <Typography level="h4" sx={{ mb: 2, color: mainColor }}>
        Connexion
      </Typography>

      <Typography level="body-sm" sx={{ mb: 2, color: mainColor }}>
        Pas encore de compte ?{" "}
        <Button
          variant="plain"
          onClick={() => navigate("/signup")}
          sx={{ color: mainColor }}
        >
          S'inscrire
        </Button>
      </Typography>

      <form onSubmit={handleSubmit}>
        <Input
          name="email"
          type="email"
          value={values.email || ""}
          onChange={handleChange}
          placeholder="Email"
          required
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
        <Input
          name="password"
          type="password"
          value={values.password || ""}
          onChange={handleChange}
          placeholder="Mot de passe"
          required
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
          variant="plain"
          onClick={() => navigate("/forgot-password")}
          sx={{
            alignSelf: "flex-end",
            mb: 2,
            padding: 0,
            minWidth: 0,
            color: mainColor,
          }}
        >
          Mot de passe oublié ?
        </Button>

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
          Se connecter
        </Button>
      </form>

      {message && (
        <Typography level="body-sm" color="danger" textAlign="center" mt={2}>
          {message}
        </Typography>
      )}
    </AuthLayout>
  );
}
