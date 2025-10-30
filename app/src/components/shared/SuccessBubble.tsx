import React from "react";
import Typography from "@mui/joy/Typography";
import Box from "@mui/joy/Box";

interface SuccessBubbleProps {
  message: string;
}

const SuccessBubble: React.FC<SuccessBubbleProps> = ({ message }) => (
  <Box
    sx={{
      position: "fixed",
      top: 24,
      right: 24,
      zIndex: 9999,
      background: "linear-gradient(135deg, #22c55e, #16a34a)",
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

export default SuccessBubble;
