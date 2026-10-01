import { forwardRef } from "react";
import { Input } from "@/components/ui/input";

function formatAffichage(valeur: string): string {
  const chiffres = valeur.replace(/\D/g, "");
  return chiffres.replace(/\B(?=(\d{3})+(?!\d))/g, " ");
}

interface MontantInputProps {
  value?: string;
  onValueChange: (valeurBrute: string) => void;
  className?: string;
}

export const MontantInput = forwardRef<HTMLInputElement, MontantInputProps>(
  ({ value, onValueChange, className }, ref) => (
    <Input
      ref={ref}
      inputMode="numeric"
      className={className}
      value={formatAffichage(value ?? "")}
      onChange={(e) => onValueChange(e.target.value.replace(/\D/g, ""))}
    />
  )
);
MontantInput.displayName = "MontantInput";