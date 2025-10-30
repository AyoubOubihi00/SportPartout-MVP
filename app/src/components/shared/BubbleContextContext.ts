import { createContext } from "react";
import type { BubbleContextType } from "./SuccessBubbleContext";

export const BubbleContext = createContext<BubbleContextType | undefined>(
  undefined,
);
