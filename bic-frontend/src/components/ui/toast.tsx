import { useCallback, useState, type ReactNode } from "react";
import { ToastContext, type ToastItem } from "@/hooks/use-toast";

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const toast = useCallback((t: Omit<ToastItem, "id">) => {
    const id = Date.now();
    setToasts((prev) => [...prev, { ...t, id }]);
    setTimeout(() => setToasts((prev) => prev.filter((x) => x.id !== id)), 4000);
  }, []);

  return (
    <ToastContext.Provider value={{ toast }}>
      {children}
      <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2">
        {toasts.map((t) => (
          <div
            key={t.id}
            className={`animate-in slide-in-from-bottom-4 fade-in rounded-lg px-4 py-3 text-sm font-medium text-white shadow-lg duration-300 ${
              t.variant === "destructive" ? "bg-red-600" : "bg-slate-900"
            }`}
          >
            {t.title}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}