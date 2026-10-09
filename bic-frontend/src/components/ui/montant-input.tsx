import { forwardRef, useImperativeHandle, useRef } from "react";
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
  ({ value, onValueChange, className }, ref) => {
    const inputRef = useRef<HTMLInputElement>(null);
    useImperativeHandle(ref, () => inputRef.current as HTMLInputElement);

    function onChange(e: React.ChangeEvent<HTMLInputElement>) {
      const position = e.target.selectionStart ?? e.target.value.length;
      const nombreChiffresAvantCurseur = e.target.value.slice(0, position).replace(/\D/g, "").length;
      const brut = e.target.value.replace(/\D/g, "");
      onValueChange(brut);
      requestAnimationFrame(() => {
        const input = inputRef.current;
        if (!input) return;
        const formate = formatAffichage(brut);
        let curseur = 0, chiffres = 0;
        while (curseur < formate.length && chiffres < nombreChiffresAvantCurseur) {
          if (/\d/.test(formate[curseur])) chiffres++;
          curseur++;
        }
        input.setSelectionRange(curseur, curseur);
      });
    }

    return <Input ref={inputRef} inputMode="numeric" className={className}
      value={formatAffichage(value ?? "")} onChange={onChange} />;
  },
);
MontantInput.displayName = "MontantInput";
