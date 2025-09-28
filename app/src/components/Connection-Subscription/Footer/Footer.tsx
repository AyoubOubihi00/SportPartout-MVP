import Typography from "@mui/joy/Typography";

interface FooterProps {
  color?: string;
}

export function Footer({ color = "#000" }: FooterProps) {
  return (
    <Typography level="body-xs" textAlign="center" sx={{ mt: 2, color }}>
      © SportPartout 2025
    </Typography>
  );
}
