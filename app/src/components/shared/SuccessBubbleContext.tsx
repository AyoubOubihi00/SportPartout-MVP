import React, { useState, useCallback } from "react";
import { BubbleContext } from "./BubbleContextContext";
import SuccessBubble from "./SuccessBubble";
import ErrorBubble from "./ErrorBubble";

type BubbleType = "success" | "error";

export interface BubbleContextType {
  showSuccessBubble: (message: string) => void;
  showErrorBubble: (message: string) => void;
}

export const BubbleProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [visible, setVisible] = useState(false);
  const [message, setMessage] = useState("");
  const [type, setType] = useState<BubbleType>("success");

  const showSuccessBubble = useCallback((msg: string) => {
    setMessage(msg);
    setType("success");
    setVisible(true);
    setTimeout(() => setVisible(false), 1500);
  }, []);

  const showErrorBubble = useCallback((msg: string) => {
    setMessage(msg);
    setType("error");
    setVisible(true);
    setTimeout(() => setVisible(false), 2000);
  }, []);

  return (
    <BubbleContext.Provider value={{ showSuccessBubble, showErrorBubble }}>
      {children}
      {visible &&
        (type === "success" ? (
          <SuccessBubble message={message} />
        ) : (
          <ErrorBubble message={message} />
        ))}
    </BubbleContext.Provider>
  );
};
