import { createContext, useContext } from "react";

export interface ToastItem { id: number; title: string; variant?: "default" | "destructive"; }
export interface ToastContextValue { toast: (t: Omit<ToastItem, "id">) => void; }

export const ToastContext = createContext<ToastContextValue | null>(null);

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error("useToast doit être utilisé dans ToastProvider");
  return ctx;
}