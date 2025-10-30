import React from "react";
import Typography from "@mui/joy/Typography";
import Box from "@mui/joy/Box";

interface ErrorBubbleProps {
  message: string;
}

const ErrorBubble: React.FC<ErrorBubbleProps> = ({ message }) => (
  <Box
    sx={{
      position: "fixed",
      top: 24,
      right: 24,
      zIndex: 9999,
      background: "linear-gradient(135deg, #ef4444, #b91c1c)",
      color: "white",
      px: 3,
      py: 2,
      borderRadius: "16px",
      boxShadow: 3,
      fontWeight: "bold",
      fontSize: "1rem",
      minWidth: "180px",
      textAlign: "center",
    }}
  >
    <Typography level="body-md">{message}</Typography>
  </Box>
);

export default ErrorBubble;
