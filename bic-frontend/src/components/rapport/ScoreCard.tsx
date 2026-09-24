import type { Score, GrilleScoreItem } from "@/types/rapport";

interface ScoreCardProps {
  score: Score;
  grille: GrilleScoreItem[];
}

export function ScoreCard({ score, grille }: ScoreCardProps) {
  if (!score.calculable) {
    return (
      <section className="border border-border bg-card p-6">
        <h2 className="font-heading text-lg text-foreground">Score non calculé</h2>
        <p className="mt-2 max-w-prose text-sm leading-relaxed text-muted-foreground">
          {score.message}
        </p>
      </section>
    );
  }

  const min = 300;
  const max = 850;
  const position = Math.min(100, Math.max(0, ((score.valeur! - min) / (max - min)) * 100));

  return (
    <section className="border border-border bg-card p-6">
      <div className="flex items-baseline gap-4">
        <span className="tabulaire font-heading text-6xl leading-none text-primary">
          {score.valeur}
        </span>
        <div>
          <div className="text-sm text-foreground">
            Intervalle {score.intervalle} — {score.categorieRisque}
          </div>
          <div className="text-xs text-muted-foreground">sur une échelle de 300 à 850</div>
        </div>
      </div>

      <div className="mt-7">
        <div className="flex h-2.5 overflow-hidden rounded-full">
          {grille.map((g) => (
            <div key={g.intervalle} className="flex-1" style={{ backgroundColor: g.couleurHex }} />
          ))}
        </div>
        <div className="relative mt-1 h-4">
          <div
            className="absolute top-0 -translate-x-1/2 text-xs text-foreground"
            style={{ left: `${position}%` }}
            aria-hidden
          >
            ▲
          </div>
        </div>
        <div className="flex justify-between text-xs text-muted-foreground">
          {grille.map((g) => (
            <span key={g.intervalle} className={g.intervalle === score.intervalle ? "text-foreground" : ""}>
              {g.intervalle}
            </span>
          ))}
        </div>
      </div>

      {score.detail && (
        <table className="mt-7 w-full text-sm">
          <caption className="mb-2 text-left text-xs text-muted-foreground">
            D'où viennent les points
          </caption>
          <tbody className="divide-y divide-border">
            <LigneAxe libelle="Comportement de paiement" valeur={score.detail.pointsPaiement} />
            <LigneAxe libelle="Taux d'endettement" valeur={score.detail.pointsEndettement} />
            <LigneAxe libelle="Exposition déjà portée" valeur={score.detail.pointsExposition} />
            <LigneAxe libelle="Ancienneté de la relation" valeur={score.detail.pointsAnciennete} />
            <LigneAxe libelle="Nouveaux crédits" valeur={score.detail.pointsNouveauxCredits} />
            <LigneAxe libelle="Mixité des produits" valeur={score.detail.pointsMixite} />
            {score.detail.malusSurendettement > 0 && (
              <LigneAxe
                libelle="Malus de surendettement"
                valeur={-score.detail.malusSurendettement}
                alerte
              />
            )}
          </tbody>
        </table>
      )}
    </section>
  );
}

function LigneAxe({ libelle, valeur, alerte }: { libelle: string; valeur: number; alerte?: boolean }) {
  return (
    <tr>
      <td className="py-2 text-muted-foreground">{libelle}</td>
      <td className={`tabulaire py-2 text-right ${alerte ? "text-destructive" : "text-foreground"}`}>
        {valeur.toFixed(2)}
      </td>
    </tr>
  );
}