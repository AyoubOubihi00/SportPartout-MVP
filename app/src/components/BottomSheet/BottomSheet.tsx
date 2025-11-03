import { useEffect, useMemo, useRef, useState } from "react";
import { Sheet, Box } from "@mui/joy";

type BottomSheetProps = {
  open: boolean;
  onClose: () => void;
  title?: React.ReactNode;
  initialHeight?: number; // px
  children: React.ReactNode;
  topOffset?: number;
};

export default function BottomSheet({
  open,
  onClose,
  title,
  initialHeight,
  children,
  topOffset,
}: BottomSheetProps) {
  const NAVBAR_HEIGHT = 72; // px
  const [height, setHeight] = useState(0);
  const [dragging, setDragging] = useState(false);
  const startY = useRef(0);
  const startH = useRef(0);

  const maxHeight = useMemo(() => {
    if (typeof window === "undefined") return 600 - NAVBAR_HEIGHT;
    const isMobile = window.innerWidth <= 640;
    const ratio = isMobile ? 0.85 : 0.86; // limite douce
    const ratioHeight = Math.round(window.innerHeight * ratio) - NAVBAR_HEIGHT;
    const top = Math.max(0, topOffset ?? 0);
    const belowTabsHeight = Math.round(
      window.innerHeight - NAVBAR_HEIGHT - top,
    );
    const clamped = Math.max(140, Math.min(ratioHeight, belowTabsHeight));
    return clamped;
  }, [topOffset]);

  const collapsedHeight = useMemo(() => {
    return Math.min(380, Math.round(maxHeight * 0.7));
  }, [maxHeight]);

  useEffect(() => {
    if (open) {
      setHeight(
        Math.max(160, Math.min(initialHeight ?? collapsedHeight, maxHeight)),
      );
    } else {
      setHeight(0);
    }
  }, [open, initialHeight, maxHeight, collapsedHeight]);

  useEffect(() => {
    function onKey(e: KeyboardEvent) {
      if (e.key === "Escape" && open) onClose();
    }
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open, onClose]);

  function onPointerDown(e: React.PointerEvent) {
    if (!open) return;
    setDragging(true);
    startY.current = e.clientY;
    startH.current = height;
    (e.target as HTMLElement).setPointerCapture?.(e.pointerId);
  }

  function onPointerMove(e: React.PointerEvent) {
    if (!dragging) return;
    const dy = e.clientY - startY.current;
    // Autorise le drag sous 420px pour permettre la fermeture
    const next = Math.max(96, Math.min(startH.current - dy, maxHeight));
    setHeight(next);
  }

  function onPointerUp() {
    if (!dragging) return;
    setDragging(false);
    // Snap behavior
    const minHeight = 420;
    if (height < minHeight) {
      onClose();
      return;
    }
    const mid = (collapsedHeight + maxHeight) / 2;
    if (height < mid) setHeight(minHeight);
    else setHeight(maxHeight);
  }

  return (
    <Box
      sx={{
        position: "fixed",
        left: 0,
        right: 0,
        bottom: `${NAVBAR_HEIGHT + 8}px`, // s'arrête juste au-dessus de la NavBar
        pointerEvents: open ? "auto" : "none",
        zIndex: 50,
      }}
      aria-hidden={!open}
    >
      <Sheet
        variant="plain"
        sx={(theme) => ({
          position: "absolute",
          left: 0,
          right: 0,
          bottom: 0,
          height: open ? height : 0,
          background:
            theme.palette.mode === "dark"
              ? "linear-gradient(135deg, #0f172a, #1e293b)"
              : "linear-gradient(135deg, #f8fafc, #e2e8f0)",
          borderRadius: 0,
          boxShadow: "md",
          overflow: "hidden",
          transition: dragging ? "none" : "height 220ms ease",
          display: "flex",
          flexDirection: "column",
          border: "none",
          p: 0,
        })}
        role="dialog"
        aria-modal="false"
        aria-label={typeof title === "string" ? title : "Détails"}
      >
        <Box
          sx={{
            p: "8px 12px 8px",
            touchAction: "none",
            cursor: "grab",
            userSelect: "none",
          }}
          onPointerDown={onPointerDown}
          onPointerMove={onPointerMove}
          onPointerUp={onPointerUp}
        >
          <Box
            sx={{
              width: 40,
              height: 4,
              bgcolor: "neutral.outlinedBorder",
              borderRadius: 999,
              m: "6px auto 6px",
            }}
          />
        </Box>
        <Box
          sx={{
            overflow: "auto",
            p: "8px 12px 12px",
            pb: `${NAVBAR_HEIGHT}px`, // Ajoute un padding-bottom égal à la navbar
          }}
        >
          {children}
        </Box>
      </Sheet>
    </Box>
  );
}
