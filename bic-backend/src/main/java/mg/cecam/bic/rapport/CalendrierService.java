package mg.cecam.bic.rapport;

import mg.cecam.bic.common.enums.TrancheRetard;
import mg.cecam.bic.common.util.MontantFormatUtil;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.Echeance;
import mg.cecam.bic.rapport.dto.CelluleMoisDTO;
import mg.cecam.bic.rapport.dto.EncoursCategorieDTO;
import mg.cecam.bic.rapport.dto.LigneAnneeDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class CalendrierService {

    private static final String[] MOIS =
            {"JAN", "FÉV", "MARS", "AVR", "MAI", "JUIN", "JUILL", "AOÛT", "SEP", "OCT", "NOV", "DÉC"};

    private static final String GRIS = "#E8E8E4";
    private static final String VERT_SANS_IMPAYE = "#A5D6A7";
    private static final String ROUGE_IMPAYE = "#EF9A9A";
    private static final String LIBELLE_AUTRES = "Autres Instituts";

    /** Photographie d'un contrat à la fin d'un mois (ou à la date de référence pour le mois en cours). */
    public record MoisSuivi(YearMonth mois, LocalDate observation, TrancheRetard tranche, int jours,
                            int nombreEcheancesImpayees, BigDecimal montantImpaye, BigDecimal encours) {}

    /** Série mensuelle d'un contrat, du mois de début au mois de dernière déclaration. */
    public List<MoisSuivi> suivi(Contrat c, List<Echeance> echeances, LocalDate reference) {
        YearMonth debut = moisDebutDeclaration(c);
        YearMonth fin = moisFinDeclaration(c, reference);
        if (debut == null || fin == null || debut.isAfter(fin)) return List.of();

        List<MoisSuivi> serie = new ArrayList<>();
        for (YearMonth m = debut; !m.isAfter(fin); m = m.plusMonths(1)) {
            LocalDate finDeMois = m.atEndOfMonth();
            LocalDate observation = finDeMois.isAfter(reference) ? reference : finDeMois;

            int jours = 0;
            BigDecimal impaye = BigDecimal.ZERO;
            BigDecimal restant = BigDecimal.ZERO;
            Set<LocalDate> datesImpayees = new HashSet<>();

            for (Echeance e : echeances) {
                if (estSoldeAu(e, observation)) continue;
                restant = restant.add(e.getMontantDu());
                if (e.getDateEcheance().isBefore(observation)) {
                    impaye = impaye.add(e.getMontantDu());
                    datesImpayees.add(e.getDateEcheance());
                    jours = Math.max(jours, (int) ChronoUnit.DAYS.between(e.getDateEcheance(), observation));
                }
            }

            TrancheRetard tranche = jours > 0
                    ? TrancheRetard.depuisJours(jours)
                    : (m.equals(debut) && m.equals(YearMonth.from(reference))
                            ? TrancheRetard.TROP_RECENT : TrancheRetard.A_TEMPS);

            // Un contrat en impayé est suivi dans le tableau Impayés, plus dans l'Encours.
            BigDecimal encours = impaye.signum() > 0 ? BigDecimal.ZERO : restant;
            serie.add(new MoisSuivi(m, observation, tranche, jours, datesImpayees.size(), impaye, encours));
        }
        return serie;
    }

    public List<LigneAnneeDTO> grilleStatut(Contrat contrat, List<Echeance> echeances, LocalDate reference) {
        List<MoisSuivi> serie = suivi(contrat, echeances, reference);
        if (serie.isEmpty()) return List.of();

        Map<YearMonth, MoisSuivi> parMois = new HashMap<>();
        for (MoisSuivi m : serie) parMois.put(m.mois(), m);

        int premiere = serie.get(0).mois().getYear();
        int derniere = serie.get(serie.size() - 1).mois().getYear();

        List<LigneAnneeDTO> lignes = new ArrayList<>();
        for (int annee = premiere; annee <= derniere; annee++) {
            List<CelluleMoisDTO> cellules = new ArrayList<>(12);
            for (int m = 1; m <= 12; m++) {
                MoisSuivi ms = parMois.get(YearMonth.of(annee, m));
                if (ms == null) {
                    cellules.add(CelluleMoisDTO.vide(MOIS[m - 1]));
                } else {
                    TrancheRetard t = ms.tranche();
                    cellules.add(new CelluleMoisDTO(MOIS[m - 1], true, null, t.name(), t.getCode(), t.getCouleurHex()));
                }
            }
            lignes.add(new LigneAnneeDTO(annee, cellules));
        }
        return lignes;
    }

    /**
     * Situation financière : un bloc par établissement déclarant (le nôtre, puis « Autres Instituts »),
     * avec le tableau Encours et, s'il y a eu au moins un impayé, le tableau Impayés.
     */
    public List<EncoursCategorieDTO> situationFinanciere(String categorie, String etablissementPropre,
                                                         List<Contrat> contrats,
                                                         Map<Long, List<Echeance>> echeances,
                                                         LocalDate reference) {
        Map<String, List<Contrat>> groupes = new LinkedHashMap<>();
        for (Contrat c : contrats) {
            String code = c.getCodeEtablissementDeclarant();
            String libelle = (code == null || code.equals(etablissementPropre)) ? etablissementPropre : LIBELLE_AUTRES;
            groupes.computeIfAbsent(libelle, k -> new ArrayList<>()).add(c);
        }

        List<String> ordre = new ArrayList<>(groupes.keySet());
        ordre.sort(Comparator.comparing((String l) -> l.equals(LIBELLE_AUTRES)));

        List<EncoursCategorieDTO> resultat = new ArrayList<>();
        for (String libelle : ordre) {
            List<Map<YearMonth, MoisSuivi>> series = new ArrayList<>();
            YearMonth debut = null;
            YearMonth fin = null;

            for (Contrat c : groupes.get(libelle)) {
                List<MoisSuivi> serie = suivi(c, echeances.getOrDefault(c.getId(), List.of()), reference);
                if (serie.isEmpty()) continue;
                Map<YearMonth, MoisSuivi> parMois = new HashMap<>();
                for (MoisSuivi m : serie) parMois.put(m.mois(), m);
                series.add(parMois);

                YearMonth d = serie.get(0).mois();
                YearMonth f = serie.get(serie.size() - 1).mois();
                if (debut == null || d.isBefore(debut)) debut = d;
                if (fin == null || f.isAfter(fin)) fin = f;
            }
            if (series.isEmpty()) continue;
            resultat.add(construireBloc(categorie, libelle, series, debut, fin));
        }
        return resultat;
    }

    private EncoursCategorieDTO construireBloc(String categorie, String libelle,
                                               List<Map<YearMonth, MoisSuivi>> series,
                                               YearMonth debut, YearMonth fin) {
        List<LigneAnneeDTO> encours = new ArrayList<>();
        List<LigneAnneeDTO> impayes = new ArrayList<>();
        boolean aDesImpayes = false;

        for (int annee = debut.getYear(); annee <= fin.getYear(); annee++) {
            List<CelluleMoisDTO> celluleEncours = new ArrayList<>(12);
            List<CelluleMoisDTO> celluleImpayes = new ArrayList<>(12);

            for (int m = 1; m <= 12; m++) {
                YearMonth courant = YearMonth.of(annee, m);
                boolean declare = false;
                BigDecimal sommeEncours = BigDecimal.ZERO;
                BigDecimal sommeImpayes = BigDecimal.ZERO;

                for (Map<YearMonth, MoisSuivi> serie : series) {
                    MoisSuivi ms = serie.get(courant);
                    if (ms == null) continue;
                    declare = true;
                    sommeEncours = sommeEncours.add(ms.encours());
                    sommeImpayes = sommeImpayes.add(ms.montantImpaye());
                }

                String nomMois = MOIS[m - 1];
                if (!declare) {
                    celluleEncours.add(CelluleMoisDTO.vide(nomMois));
                    celluleImpayes.add(CelluleMoisDTO.vide(nomMois));
                    continue;
                }

                celluleEncours.add(new CelluleMoisDTO(nomMois, true, sommeEncours, null,
                        sommeEncours.signum() == 0 ? "" : MontantFormatUtil.formatMontant(sommeEncours), GRIS));

                if (sommeImpayes.signum() > 0) {
                    aDesImpayes = true;
                    celluleImpayes.add(new CelluleMoisDTO(nomMois, true, sommeImpayes, "IMPAYE",
                            MontantFormatUtil.formatMontant(sommeImpayes), ROUGE_IMPAYE));
                } else {
                    celluleImpayes.add(new CelluleMoisDTO(nomMois, true, BigDecimal.ZERO, "OK", "", VERT_SANS_IMPAYE));
                }
            }
            encours.add(new LigneAnneeDTO(annee, celluleEncours));
            impayes.add(new LigneAnneeDTO(annee, celluleImpayes));
        }
        return new EncoursCategorieDTO(categorie, libelle, encours, aDesImpayes ? impayes : List.of());
    }

    public YearMonth moisDebutDeclaration(Contrat c) {
        LocalDate debut = c.getDateDebutContrat();
        if (debut == null) return null;
        return YearMonth.from(debut);
    }

    public YearMonth moisFinDeclaration(Contrat c, LocalDate reference) {
        if (c.getDateDebutContrat() == null) return null;
        LocalDate borne = reference;
        if (c.getDateDerniereModification() != null) {
            LocalDate derniere = c.getDateDerniereModification().toLocalDate();
            if (derniere.isBefore(borne)) borne = derniere;
        }
        if (c.getDateFinContrat() != null && c.getDateFinContrat().isBefore(borne)) {
            borne = c.getDateFinContrat();
        }
        if (borne.isBefore(c.getDateDebutContrat())) borne = c.getDateDebutContrat();
        return YearMonth.from(borne);
    }

    /** Soldée à cette date : payée, et le paiement n'est pas postérieur. Sans date de paiement : payée à l'échéance. */
    public boolean estSoldeAu(Echeance e, LocalDate date) {
        if (!e.estPayee()) return false;
        LocalDate paye = e.getDatePaiement() != null ? e.getDatePaiement() : e.getDateEcheance();
        return !paye.isAfter(date);
    }
}