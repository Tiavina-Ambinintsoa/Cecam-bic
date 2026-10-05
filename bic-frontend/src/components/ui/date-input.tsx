import { forwardRef, useState, useEffect, type ChangeEvent } from "react";
import { Input } from "@/components/ui/input";

interface DateInputProps {
  value?: string;
  onValueChange: (isoValue: string) => void;
  className?: string;
  id?: string;
}

function isoVersAffichage(iso?: string): string {
  if (!iso) return "";
  const [a, m, j] = iso.split("-");
  if (!a || !m || !j) return "";
  return `${j}/${m}/${a}`;
}

function affichageVersIso(affichage: string): string {
  const [j, m, a] = affichage.split("/");
  if (!j || !m || !a || a.length !== 4) return "";
  return `${a}-${m.padStart(2, "0")}-${j.padStart(2, "0")}`;
}

export const DateInput = forwardRef<HTMLInputElement, DateInputProps>(
  ({ value, onValueChange, className, id }, ref) => {
    const [texte, setTexte] = useState(isoVersAffichage(value));

    useEffect(() => setTexte(isoVersAffichage(value)), [value]);

    function onChange(e: ChangeEvent<HTMLInputElement>) {
      const chiffres = e.target.value.replace(/\D/g, "").slice(0, 8);
      let formate = chiffres;
      if (chiffres.length > 2) formate = chiffres.slice(0, 2) + "/" + chiffres.slice(2);
      if (chiffres.length > 4) formate = formate.slice(0, 5) + "/" + chiffres.slice(4, 8);
      setTexte(formate);
      const iso = affichageVersIso(formate);
      if (iso) onValueChange(iso);
    }

    return (
      <Input ref={ref} id={id} className={className} value={texte} onChange={onChange}
        placeholder="jj/mm/aaaa" inputMode="numeric" maxLength={10} />
    );
  }
);
DateInput.displayName = "DateInput";