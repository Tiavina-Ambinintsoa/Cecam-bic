import { createContext, useContext, useState, type ReactNode } from "react";

interface DemandeEnCoursState {
  clientId?: number;
  contratId?: number;
  setClientId: (id?: number) => void;
  setContratId: (id?: number) => void;
}

const DemandeEnCoursContext = createContext<DemandeEnCoursState | null>(null);

export function DemandeEnCoursProvider({ children }: { children: ReactNode }) {
  const [clientId, setClientId] = useState<number | undefined>();
  const [contratId, setContratId] = useState<number | undefined>();
  return (
    <DemandeEnCoursContext.Provider value={{ clientId, setClientId, contratId, setContratId }}>
      {children}
    </DemandeEnCoursContext.Provider>
  );
}

export function useDemandeEnCours() {
  const ctx = useContext(DemandeEnCoursContext);
  if (!ctx) throw new Error("useDemandeEnCours doit être utilisé dans DemandeEnCoursProvider");
  return ctx;
}