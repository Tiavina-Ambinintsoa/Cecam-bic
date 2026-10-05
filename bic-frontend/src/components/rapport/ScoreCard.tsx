import { cn } from "@/lib/utils";
import type { Score, GrilleScoreItem } from "@/types/rapport";

interface ScoreCardProps { score: Score; grille: GrilleScoreItem[]; }

export function ScoreCard({ score, grille }: ScoreCardProps) {
  if (!score.calculable) {
    return <p className="text-sm text-slate-500">Le score n'est pas calculé : {score.message}</p>;
  }

  return (
    <div className="flex items-center justify-between gap-6">
      <div className="space-y-1 text-sm">
        <p><span className="text-slate-500">Score : </span><span className="font-semibold">{score.valeur}</span></p>
        <p><span className="text-slate-500">Intervalle de score : </span><span className="font-semibold">{score.intervalle}</span></p>
        <p><span className="text-slate-500">Catégorie de score : </span><span className="font-semibold">{score.categorieRisque}</span></p>
      </div>
      <div className="flex items-end gap-2">
        {grille.map((g) => {
          const actif = g.intervalle === score.intervalle;
          return (
            <div key={g.intervalle} className="flex flex-col items-center gap-1">
              <div className="rounded-lg transition-all" style={{ backgroundColor: g.couleurHex, width: actif ? 40 : 26, height: actif ? 40 : 26 }} />
              <span className={cn("text-xs", actif ? "font-bold text-slate-900" : "text-slate-400")}>{g.intervalle}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
}