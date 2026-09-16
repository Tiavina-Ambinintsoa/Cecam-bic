// mg/cecam/bic/rapport/CalendrierService.java  — RÉÉCRIT
package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.common.util.LabelMapper;
import mg.cecam.bic.common.util.MontantFormatUtil;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.Echeance;
import mg.cecam.bic.rapport.dto.CalendrierCreditDTO;
import mg.cecam.bic.rapport.dto.CelluleMoisDTO;
import mg.cecam.bic.rapport.dto.EncoursCategorieDTO;
import mg.cecam.bic.rapport.dto.LigneAnneeDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Deux grilles distinctes, là où la version initiale n'en produisait qu'une
 * qui mélangeait les deux :
 *
 *  - grilleStatut(...)      : une grille PAR CONTRAT, cellule = OK / R / IMP,
 *                             équivalent des bandeaux verts de CRIF page 3.
 *  - encoursParCategorie(...) : une grille PAR CATÉGORIE, cellule = encours
 *                             restant au mois considéré. C'est la section
 *                             « Situation Financière par Catégorie » de CRIF,
 *                             qui était totalement absente.
 *
 * Deux corrections de fond :
 *  - Collectors.toMap recevait des clés en doublon dès que deux échéances
 *    tombaient dans le même mois, ce qui levait IllegalStateException.
 *    Les échéances d'un même mois sont désormais agrégées.
 *  - Une échéance À VENIR était peinte en vert avec un montant vide : le mois
 *    apparaissait « validé » alors que rien n'était payé. Le futur reste neutre.
 */
@Service
@RequiredArgsConstructor
public class CalendrierService {

    private static final String[] MOIS =
            {"JAN", "FÉV", "MARS", "AVR", "MAI", "JUIN", "JUILL", "AOÛT", "SEP", "OCT", "NOV", "DÉC"};

    private static final String VERT        = "#C8E6C9";
    private static final String ORANGE      = "#FFE0B2";
    private static final String ROUGE       = "#FFCDD2";
    private static final String NEUTRE      = "#F5F5F5";
    private static final String TRANSPARENT = "transparent";

    // ------------------------------------------------------------------
    // Grille de statut, par contrat
    // ------------------------------------------------------------------
    public CalendrierCreditDTO grilleStatut(Contrat contrat, List<Echeance> echeances, LocalDate reference) {
        if (echeances.isEmpty()) {
            return new CalendrierCreditDTO(contrat.getCodeContratCb(), contrat.getCodeContratEtablissement(),
                    contrat.getTypeContrat(), null, contrat.getMontantFinance(), List.of());
        }

        // Une échéance par mois au pire des cas : on garde la PIRE du mois.
        Map<YearMonth, StatutEcheance> parMois = new HashMap<>();
        for (Echeance e : echeances) {
            YearMonth ym = YearMonth.from(e.getDateEcheance());
            StatutEcheance courant = e.statutEffectif(reference);
            parMois.merge(ym, courant, CalendrierService::pire);
        }

        YearMonth debut = YearMonth.from(Collections.min(
                echeances.stream().map(Echeance::getDateEcheance).toList()));
        YearMonth fin = YearMonth.from(Collections.max(
                echeances.stream().map(Echeance::getDateEcheance).toList()));

        List<LigneAnneeDTO> lignes = new ArrayList<>();
        for (int annee = debut.getYear(); annee <= fin.getYear(); annee++) {
            List<CelluleMoisDTO> cellules = new ArrayList<>(12);
            for (int m = 1; m <= 12; m++) {
                YearMonth courant = YearMonth.of(annee, m);
                StatutEcheance statut = parMois.get(courant);
                if (statut == null) {
                    cellules.add(CelluleMoisDTO.vide(MOIS[m - 1]));
                    continue;
                }
                cellules.add(new CelluleMoisDTO(
                        MOIS[m - 1], true, null, statut.name(),
                        LabelMapper.codeGrille(statut), couleur(statut)));
            }
            lignes.add(new LigneAnneeDTO(annee, cellules));
        }

        return new CalendrierCreditDTO(contrat.getCodeContratCb(), contrat.getCodeContratEtablissement(),
                contrat.getTypeContrat(), null, contrat.getMontantFinance(), lignes);
    }

    // ------------------------------------------------------------------
    // Grille d'encours, par catégorie
    // ------------------------------------------------------------------

    /**
     * Encours restant au dernier jour de chaque mois, tous contrats de la
     * catégorie confondus. Un mois sans aucun contrat en vie reste vide.
     */
    public EncoursCategorieDTO encours(String categorie, String codeEtablissement,
                                       List<Contrat> contrats,
                                       Map<Long, List<Echeance>> echeancesParContrat) {
        List<Echeance> toutes = contrats.stream()
                .flatMap(c -> echeancesParContrat.getOrDefault(c.getId(), List.of()).stream())
                .toList();
        if (toutes.isEmpty()) {
            return new EncoursCategorieDTO(categorie, codeEtablissement, List.of());
        }

        YearMonth debut = YearMonth.from(Collections.min(toutes.stream().map(Echeance::getDateEcheance).toList()));
        YearMonth fin   = YearMonth.from(Collections.max(toutes.stream().map(Echeance::getDateEcheance).toList()));

        List<LigneAnneeDTO> lignes = new ArrayList<>();
        for (int annee = debut.getYear(); annee <= fin.getYear(); annee++) {
            List<CelluleMoisDTO> cellules = new ArrayList<>(12);
            for (int m = 1; m <= 12; m++) {
                YearMonth courant = YearMonth.of(annee, m);
                LocalDate finDeMois = courant.atEndOfMonth();

                BigDecimal encours = BigDecimal.ZERO;
                for (Contrat c : contrats) {
                    LocalDate debutContrat = c.getDateDebutContrat() != null
                            ? c.getDateDebutContrat() : c.getDateDemande();
                    if (debutContrat.isAfter(finDeMois)) continue;   // contrat pas encore né
                    for (Echeance e : echeancesParContrat.getOrDefault(c.getId(), List.of())) {
                        if (estSolde(e, finDeMois)) continue;        // déjà remboursée à cette date
                        encours = encours.add(e.getMontantDu());
                    }
                }

                if (encours.signum() == 0) {
                    cellules.add(CelluleMoisDTO.vide(MOIS[m - 1]));
                } else {
                    cellules.add(new CelluleMoisDTO(MOIS[m - 1], true, encours, null,
                            MontantFormatUtil.formatMontant(encours), NEUTRE));
                }
            }
            lignes.add(new LigneAnneeDTO(annee, cellules));
        }
        return new EncoursCategorieDTO(categorie, codeEtablissement, lignes);
    }

    /** Une échéance est soldée à une date donnée si elle a été payée avant. */
    private boolean estSolde(Echeance e, LocalDate date) {
        return e.estPayee() && e.getDatePaiement() != null && !e.getDatePaiement().isAfter(date);
    }

    private static StatutEcheance pire(StatutEcheance a, StatutEcheance b) {
        return rang(a) >= rang(b) ? a : b;
    }

    private static int rang(StatutEcheance s) {
        return switch (s) {
            case A_VENIR -> 0;
            case PAYE_A_TEMPS -> 1;
            case EN_RETARD -> 2;
            case IMPAYE -> 3;
        };
    }

    private String couleur(StatutEcheance s) {
        return switch (s) {
            case PAYE_A_TEMPS -> VERT;
            case EN_RETARD -> ORANGE;
            case IMPAYE -> ROUGE;
            case A_VENIR -> TRANSPARENT;   // le futur n'est pas un succès
        };
    }
}