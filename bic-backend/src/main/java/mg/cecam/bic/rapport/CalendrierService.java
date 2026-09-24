package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.common.util.LabelMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        YearMonth debut = moisDebutDeclaration(contrat);
        YearMonth fin = moisFinDeclaration(contrat, reference);
        if (debut == null || fin == null || debut.isAfter(fin)) return List.of();

        List<LigneAnneeDTO> lignes = new ArrayList<>();
        for (int annee = debut.getYear(); annee <= fin.getYear(); annee++) {
            List<CelluleMoisDTO> cellules = new ArrayList<>(12);
            for (int m = 1; m <= 12; m++) {
                YearMonth courant = YearMonth.of(annee, m);
                if (courant.isBefore(debut) || courant.isAfter(fin)) {
                    cellules.add(CelluleMoisDTO.vide(MOIS[m - 1]));
                    continue;
                }
                StatutEcheance statut = statutAuMois(echeances, courant, reference);
                cellules.add(new CelluleMoisDTO(MOIS[m - 1], true, null, statut.name(),
                        LabelMapper.codeGrille(statut), couleur(statut)));
            }
            lignes.add(new LigneAnneeDTO(annee, cellules));
        }
        return lignes;
    }

    private StatutEcheance statutAuMois(List<Echeance> echeances, YearMonth mois, LocalDate reference) {
        LocalDate finDeMois = mois.atEndOfMonth();
        StatutEcheance pire = StatutEcheance.PAYE_A_TEMPS;
        for (Echeance e : echeances) {
            if (e.getDateEcheance().isAfter(finDeMois)) continue;
            LocalDate dateObservation = finDeMois.isAfter(reference) ? reference : finDeMois;
            StatutEcheance s = e.statutEffectif(dateObservation);
            if (s == StatutEcheance.A_VENIR) continue;
            if (rang(s) > rang(pire)) pire = s;
        }
        return pire;
    }

    public EncoursCategorieDTO encours(String categorie, String codeEtablissement,
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
            return new EncoursCategorieDTO(categorie, codeEtablissement, List.of());
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
                        if (estSolde(e, finDeMois)) continue;
                        encours = encours.add(e.getMontantDu());
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
        return new EncoursCategorieDTO(categorie, codeEtablissement, lignes);
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

    private boolean estSolde(Echeance e, LocalDate date) {
        return e.estPayee() && e.getDatePaiement() != null && !e.getDatePaiement().isAfter(date);
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