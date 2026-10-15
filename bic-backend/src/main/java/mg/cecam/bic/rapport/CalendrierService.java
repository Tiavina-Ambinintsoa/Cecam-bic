package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.common.enums.BucketRetard;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.common.util.LabelMapper;
import mg.cecam.bic.common.util.MontantFormatUtil;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.Echeance;
import mg.cecam.bic.contrat.HistoriquePaiementMensuel;
import mg.cecam.bic.rapport.dto.CelluleMoisDTO;
import mg.cecam.bic.rapport.dto.EncoursCategorieDTO;
import mg.cecam.bic.rapport.dto.LigneAnneeDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendrierService {

    private static final String[] MOIS =
            {"JAN", "FÉV", "MARS", "AVR", "MAI", "JUIN", "JUILL", "AOÛT", "SEP", "OCT", "NOV", "DÉC"};

    private static final String VERT   = "#C8E6C9";
    private static final String ORANGE = "#FFE0B2";
    private static final String ROUGE  = "#FFCDD2";
    private static final String GRIS   = "#E8E8E4";   

    public List<LigneAnneeDTO> grilleStatut(Contrat contrat, List<Echeance> echeances, LocalDate reference) {
        return grilleStatut(contrat, echeances, List.of(), reference);
    }

    public List<LigneAnneeDTO> grilleStatut(Contrat contrat, List<Echeance> echeances,
                                            List<HistoriquePaiementMensuel> historiques, LocalDate reference) {
        YearMonth debut = moisDebutDeclaration(contrat, historiques);
        YearMonth fin = moisFinDeclaration(contrat, historiques, reference);
        if (debut == null || fin == null || debut.isAfter(fin)) return List.of();

        Map<YearMonth, HistoriquePaiementMensuel> mapHisto = (historiques == null) ? Map.of() :
                historiques.stream().collect(Collectors.toMap(h -> YearMonth.from(h.getMois()), Function.identity(), (a, b) -> a));

        List<LigneAnneeDTO> lignes = new ArrayList<>();
        for (int annee = debut.getYear(); annee <= fin.getYear(); annee++) {
            List<CelluleMoisDTO> cellules = new ArrayList<>(12);
            for (int m = 1; m <= 12; m++) {
                YearMonth courant = YearMonth.of(annee, m);
                if (courant.isBefore(debut) || courant.isAfter(fin)) {
                    cellules.add(CelluleMoisDTO.vide(MOIS[m - 1]));
                    continue;
                }

                if (!mapHisto.isEmpty() && mapHisto.containsKey(courant)) {
                    HistoriquePaiementMensuel h = mapHisto.get(courant);
                    String code = LabelMapper.codeGrilleCrif(h.getStatut(), h.getJoursDeRetard());
                    String color = LabelMapper.couleurCodeCrif(code);
                    cellules.add(new CelluleMoisDTO(MOIS[m - 1], true, null, code, code, color));
                } else {
                    boolean echeanceObservee = aEcheanceEchue(echeances, courant, reference);
                    Integer maxJours = maxRetardAuMois(echeances, courant, reference);
                    StatutEcheance statut = statutAuMois(echeances, courant, reference);
                    String code = !echeanceObservee ? "NOUV"
                            : statut == null ? "NDIS"
                            : statut == StatutEcheance.PAYE_A_TEMPS ? "OK"
                            : maxJours != null && maxJours > 0 ? LabelMapper.codeGrilleCrif(null, maxJours) : "NDIS";
                    String color = LabelMapper.couleurCodeCrif(code);
                    cellules.add(new CelluleMoisDTO(MOIS[m - 1], true, null,
                            !echeanceObservee ? "NOUV" : statut == null ? "NDIS" : statut.name(), code, color));
                }
            }
            lignes.add(new LigneAnneeDTO(annee, cellules));
        }
        return lignes;
    }

    private Integer maxRetardAuMois(List<Echeance> echeances, YearMonth mois, LocalDate reference) {
        LocalDate dateObservation = dateObservation(mois, reference);
        List<Echeance> dues = echeances.stream()
                .filter(e -> e != null && e.getDateEcheance() != null)
                .filter(e -> !e.getDateEcheance().isAfter(dateObservation))
                .toList();
        if (dues.isEmpty()) return 0;
        List<Integer> retards = dues.stream().map(e -> joursDeRetardObserve(e, dateObservation)).toList();
        if (retards.stream().anyMatch(Objects::isNull)) return null;
        return retards.stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    private StatutEcheance statutAuMois(List<Echeance> echeances, YearMonth mois, LocalDate reference) {
        LocalDate dateObservation = dateObservation(mois, reference);
        StatutEcheance pire = null;
        for (Echeance e : echeances) {
            if (e == null || e.getDateEcheance() == null) continue;
            if (e.getDateEcheance().isAfter(dateObservation)) continue;
            Integer jours = joursDeRetardObserve(e, dateObservation);
            if (jours == null) return null;
            StatutEcheance s;
            if (e.estPayee() && e.getDatePaiement() != null && !e.getDatePaiement().isAfter(dateObservation)) {
                s = jours > 0 ? StatutEcheance.EN_RETARD : StatutEcheance.PAYE_A_TEMPS;
            } else {
                s = jours > Echeance.SEUIL_IMPAYE_JOURS ? StatutEcheance.IMPAYE : StatutEcheance.EN_RETARD;
            }
            if (pire == null || rang(s) > rang(pire)) pire = s;
        }
        return pire;
    }

    private boolean aEcheanceEchue(List<Echeance> echeances, YearMonth mois, LocalDate reference) {
        LocalDate observation = dateObservation(mois, reference);
        return echeances.stream().anyMatch(e -> e != null && e.getDateEcheance() != null
                && !e.getDateEcheance().isAfter(observation));
    }

    private LocalDate dateObservation(YearMonth mois, LocalDate reference) {
        LocalDate finDeMois = mois.atEndOfMonth();
        return finDeMois.isAfter(reference) ? reference : finDeMois;
    }

    private Integer joursDeRetardObserve(Echeance echeance, LocalDate observation) {
        if (echeance.estPayee() && echeance.getDatePaiement() == null) return null;
        LocalDate fin = echeance.estPayee() && !echeance.getDatePaiement().isAfter(observation)
                ? echeance.getDatePaiement() : observation;
        if (!fin.isAfter(echeance.getDateEcheance())) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(echeance.getDateEcheance(), fin);
    }

    public EncoursCategorieDTO encours(String categorie, String codeEtablissement, String devise,
                                       List<Contrat> contrats,
                                       Map<Long, List<Echeance>> echeancesParContrat,
                                       LocalDate reference) {
        YearMonth debut = null;
        YearMonth fin = null;
        for (Contrat c : contrats) {
            YearMonth d = moisDebutDeclaration(c);
            YearMonth f = moisFinDeclaration(c, reference);
            if (d == null || f == null) continue;
            if (debut == null || d.isBefore(debut)) debut = d;
            if (fin == null || f.isAfter(fin)) fin = f;
        }
        if (debut == null || fin == null) {
            return new EncoursCategorieDTO(categorie, codeEtablissement, devise, List.of());
        }

        List<LigneAnneeDTO> lignes = new ArrayList<>();
        for (int annee = debut.getYear(); annee <= fin.getYear(); annee++) {
            List<CelluleMoisDTO> cellules = new ArrayList<>(12);
            for (int m = 1; m <= 12; m++) {
                YearMonth courant = YearMonth.of(annee, m);
                LocalDate finDeMois = courant.atEndOfMonth();

                boolean declare = false;
                BigDecimal encours = BigDecimal.ZERO;

                for (Contrat c : contrats) {
                    YearMonth d = moisDebutDeclaration(c);
                    YearMonth f = moisFinDeclaration(c, reference);
                    if (d == null || f == null) continue;
                    if (courant.isBefore(d) || courant.isAfter(f)) continue;
                    declare = true;
                    for (Echeance e : echeancesParContrat.getOrDefault(c.getId(), List.of())) {
                        if (e == null || e.getDateEcheance() == null || e.getMontantDu() == null) continue;
                        BigDecimal paye = BigDecimal.ZERO;
                        if (e.getDatePaiement() != null && !e.getDatePaiement().isAfter(finDeMois)) {
                            paye = e.getMontantPaye() == null ? e.getMontantDu() : e.getMontantPaye();
                        } else if (e.getDatePaiement() == null
                                && courant.equals(YearMonth.from(reference)) && e.estPayee()) {
                            paye = e.getMontantPaye() == null ? e.getMontantDu() : e.getMontantPaye();
                        }
                        BigDecimal restant = e.getMontantDu().subtract(paye).max(BigDecimal.ZERO);
                        encours = encours.add(restant);
                    }
                }

                if (!declare) {
                    cellules.add(CelluleMoisDTO.vide(MOIS[m - 1]));
                } else if (encours.signum() == 0) {
                    cellules.add(new CelluleMoisDTO(MOIS[m - 1], true, BigDecimal.ZERO, null, "", GRIS));
                } else {
                    cellules.add(new CelluleMoisDTO(MOIS[m - 1], true, encours, null,
                            MontantFormatUtil.formatMontant(encours), GRIS));
                }
            }
            lignes.add(new LigneAnneeDTO(annee, cellules));
        }
        return new EncoursCategorieDTO(categorie, codeEtablissement, devise, lignes);
    }

    public EncoursCategorieDTO impayes(String categorie, String codeEtablissement, String devise,
                                       List<Contrat> contrats,
                                       Map<Long, List<Echeance>> echeancesParContrat,
                                       LocalDate reference) {
        YearMonth debut = null;
        YearMonth fin = null;
        for (Contrat c : contrats) {
            YearMonth d = moisDebutDeclaration(c);
            YearMonth f = moisFinDeclaration(c, reference);
            if (d == null || f == null) continue;
            if (debut == null || d.isBefore(debut)) debut = d;
            if (fin == null || f.isAfter(fin)) fin = f;
        }
        if (debut == null || fin == null) {
            return new EncoursCategorieDTO(categorie, codeEtablissement, devise, List.of());
        }

        List<LigneAnneeDTO> lignes = new ArrayList<>();
        for (int annee = debut.getYear(); annee <= fin.getYear(); annee++) {
            List<CelluleMoisDTO> cellules = new ArrayList<>(12);
            for (int m = 1; m <= 12; m++) {
                YearMonth courant = YearMonth.of(annee, m);
                LocalDate finDeMois = courant.atEndOfMonth();

                boolean declare = false;
                BigDecimal montantImpayes = BigDecimal.ZERO;
                int retardMaximum = 0;

                for (Contrat c : contrats) {
                    YearMonth d = moisDebutDeclaration(c);
                    YearMonth f = moisFinDeclaration(c, reference);
                    if (d == null || f == null) continue;
                    if (courant.isBefore(d) || courant.isAfter(f)) continue;
                    declare = true;
                    for (Echeance e : echeancesParContrat.getOrDefault(c.getId(), List.of())) {
                        if (e == null || e.getDateEcheance() == null || e.getMontantDu() == null) continue;
                        if (e.getDateEcheance().isAfter(finDeMois)) continue;
                        BigDecimal paye = BigDecimal.ZERO;
                        if (e.getDatePaiement() != null && !e.getDatePaiement().isAfter(finDeMois)) {
                            paye = e.getMontantPaye() == null ? e.getMontantDu() : e.getMontantPaye();
                        } else if (e.getDatePaiement() == null
                                && courant.equals(YearMonth.from(reference)) && e.estPayee()) {
                            paye = e.getMontantPaye() == null ? e.getMontantDu() : e.getMontantPaye();
                        }
                        BigDecimal restant = e.getMontantDu().subtract(paye).max(BigDecimal.ZERO);
                        montantImpayes = montantImpayes.add(restant);
                        if (restant.signum() > 0) {
                            retardMaximum = Math.max(retardMaximum,
                                    (int) java.time.temporal.ChronoUnit.DAYS.between(e.getDateEcheance(), finDeMois));
                        }
                    }
                }

                if (!declare) {
                    cellules.add(CelluleMoisDTO.vide(MOIS[m - 1]));
                } else {
                    String code = codeCouleurImpayes(retardMaximum);
                    cellules.add(new CelluleMoisDTO(MOIS[m - 1], true, montantImpayes, code,
                            montantImpayes.signum() == 0 ? "" : MontantFormatUtil.formatMontant(montantImpayes),
                            LabelMapper.couleurCodeCrif(code)));
                }
            }
            lignes.add(new LigneAnneeDTO(annee, cellules));
        }
        return new EncoursCategorieDTO(categorie, codeEtablissement, devise, lignes);
    }

    private String codeCouleurImpayes(int joursRetard) {
        if (joursRetard <= 0) return "OK";
        BucketRetard bucket = BucketRetard.depuisJours(joursRetard);
        return switch (bucket) {
            case JOURS_1_29 -> "1-29";
            case JOURS_30_59 -> "30+";
            case JOURS_60_89 -> "60+";
            case JOURS_90_119 -> "90+";
            case JOURS_120_149 -> "120+";
            case JOURS_150_179 -> "150+";
            case JOURS_180_PLUS -> "180+";
            case A_JOUR -> "OK";
        };
    }

    public YearMonth moisDebutDeclaration(Contrat c) {
        return moisDebutDeclaration(c, List.of());
    }

    public YearMonth moisDebutDeclaration(Contrat c, List<HistoriquePaiementMensuel> historiques) {
        YearMonth minHisto = (historiques == null || historiques.isEmpty()) ? null :
                historiques.stream().map(h -> YearMonth.from(h.getMois())).min(Comparator.naturalOrder()).orElse(null);
        LocalDate debut = c.getDateDebutContrat();
        if (debut == null) debut = c.getDateDemande();
        if (debut == null) return minHisto;
        YearMonth ym = YearMonth.from(debut);
        if (minHisto != null && minHisto.isBefore(ym)) return minHisto;
        return ym;
    }

    public YearMonth moisFinDeclaration(Contrat c, LocalDate reference) {
        return moisFinDeclaration(c, List.of(), reference);
    }

    public YearMonth moisFinDeclaration(Contrat c, List<HistoriquePaiementMensuel> historiques, LocalDate reference) {
        YearMonth maxHisto = (historiques == null || historiques.isEmpty()) ? null :
                historiques.stream().map(h -> YearMonth.from(h.getMois())).max(Comparator.naturalOrder()).orElse(null);
        if (c.getDateDebutContrat() == null && maxHisto != null) return maxHisto;
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
        YearMonth ym = YearMonth.from(borne);
        if (maxHisto != null && maxHisto.isAfter(ym)) return maxHisto;
        return ym;
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
            case A_VENIR -> GRIS;
        };
    }
}
