import { useContext } from "react";
import { BubbleContext } from "./BubbleContextContext";

export const useBubble = () => {
  const ctx = useContext(BubbleContext);
  if (!ctx) throw new Error("useBubble must be used within a BubbleProvider");
  return ctx;
};
