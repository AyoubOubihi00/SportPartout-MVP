import { useNavigate } from "react-router-dom";
import './CommonStyle.css';
import Button from "@mui/joy/Button";
import Typography from "@mui/joy/Typography";
import NavBar from "../NavBar/NavBar";
import { useTheme } from "@mui/joy/styles";

export function ResetPasswordSuccess() {
  const navigate = useNavigate();
  const theme = useTheme();



  const mainColor = theme.palette.mode == "dark"  ? "#fff" : "#000";

  return (
    <div
      className="main-container"
        style={{
        backgroundColor: theme.palette.mode == "dark"  ? '#121212' : '#fff',
        color: mainColor,
        paddingBottom: '120px',
      }}
    >

      <div style={{ flexGrow: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', width: '100%', maxWidth: 400 }}>
        <Typography level="h4" sx={{ color: mainColor, mb: 2, textAlign: 'center' }}>
          Mot de passe réinitialisé
        </Typography>
        <Typography level="body-sm" sx={{ color: mainColor, mb: 3, textAlign: 'center' }}>
          Votre mot de passe a été modifié avec succès. Vous pouvez maintenant vous connecter.
        </Typography>

        <Button
          variant="solid"
          onClick={() => navigate("/login")}
          sx={{
            mt: 2,
            width: '100%',
            backgroundColor: mainColor,
            color: theme.palette.mode == "dark"  ? '#121212' : '#fff',
            '&:hover': { backgroundColor: mainColor },
            '&:focus': { backgroundColor: mainColor, boxShadow: 'none' },
            '&:active': { backgroundColor: mainColor },
          }}
        >
          Aller à la connexion
        </Button>
      </div>

        <div className="fixed bottom-0 left-0 w-full z-50 flex justify-center">
            <div className="w-full max-w-md">
            <NavBar />
            </div>
        </div>
    </div>
  );
}
