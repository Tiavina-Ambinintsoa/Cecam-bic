package mg.cecam.bic.score;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.Emploi;
import mg.cecam.bic.client.EmploiRepository;
import mg.cecam.bic.client.EvenementNegatifRepository;
import mg.cecam.bic.common.enums.BucketRetard;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.contrat.*;
import mg.cecam.bic.referentiel.GrilleScore;
import mg.cecam.bic.referentiel.GrilleScoreRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final GrilleScoreRepository grilleScoreRepository;
    private final ContratRepository contratRepository;
    private final ContratLienRepository contratLienRepository;
    private final EcheanceRepository echeanceRepository;
    private final HistoriquePaiementMensuelRepository historiquePaiementMensuelRepository;
    private final EmploiRepository emploiRepository;
    private final RevenuDemandeRepository revenuDemandeRepository;
    private final EvenementNegatifRepository evenementNegatifRepository;
    private final ScoreProperties p;

    /** Observation de paiement normalisée : un mois (ou une échéance), un retard en jours, un poids unitaire. */
    private record Observation(LocalDate date, int jours, int nombre) {}

    public ScoreResult calculer(Client client, Contrat contratEnCours, boolean clientTrouve) {
        LocalDate ref = LocalDate.now();
        Long enCoursId = contratEnCours == null ? null : contratEnCours.getId();
        List<String> avertissements = new ArrayList<>();
        List<String> plafonds = new ArrayList<>();
        Map<String, Double> gagne = new LinkedHashMap<>();
        Map<String, Double> dispo = new LinkedHashMap<>();

        List<Contrat> historique = contratRepository.findPourClient(client.getId()).stream()
                .filter(c -> !Objects.equals(c.getId(), enCoursId)).toList();

        // ---- Événements négatifs formels (ne bloquent plus le calcul : ils plafonnent le score)
        boolean evenementNegatif = !evenementNegatifRepository.findByClient_IdAndActifTrue(client.getId()).isEmpty()
                || historique.stream().anyMatch(c -> pireStatutFormel(c.getPireStatut()))
                || (contratEnCours != null && pireStatutFormel(contratEnCours.getPireStatut()));

        List<Contrat> empruntsDecaisses = historique.stream().filter(this::estEmprunteur)
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF || c.getPhaseDemande() == PhaseDemande.FERME)
                .toList();
        List<Contrat> actifs = contratsActifs(historique, contratEnCours);

        // ---- Observations de paiement (mensuelles en priorité, échéances datées pour les autres mois)
        List<Observation> observations = new ArrayList<>();
        Map<Long, List<Echeance>> echeancesParContrat = new HashMap<>();
        int retardCourant = 0;
        for (Contrat contrat : empruntsDecaisses) {
            List<Echeance> echeances = echeanceRepository.findByContrat_IdOrderByNumeroEcheance(contrat.getId());
            echeancesParContrat.put(contrat.getId(), echeances);
            Set<YearMonth> couverts = new HashSet<>();
            List<HistoriquePaiementMensuel> mensuel = historiquePaiementMensuelRepository
                    .findByContrat_IdOrderByMoisDesc(contrat.getId()).stream()
                    .filter(h -> h.getMois() != null && !h.getMois().isAfter(ref)).toList();
            for (HistoriquePaiementMensuel h : mensuel) {
                Integer jours = joursObserves(h);
                if (jours == null) {
                    if (!statutNonObservable(h.getStatut())) {
                        return ScoreResult.nonCalculable("Un statut source ne précise pas un retard exploitable. "
                                + "Saisissez le nombre de jours ou un bucket exact avant le calcul.");
                    }
                    continue;
                }
                observations.add(new Observation(h.getMois(), jours, nombreEcheances(h)));
                couverts.add(YearMonth.from(h.getMois()));
            }
            for (Echeance e : echeances) {
                if (e.getDateEcheance() == null || e.getDateEcheance().isAfter(ref)) continue;
                int jours = e.joursDeRetard(ref);
                // Une échéance échue et non payée compte toujours (même si le mois a une observation source).
                if (!couverts.contains(YearMonth.from(e.getDateEcheance())) || (!e.estPayee() && jours > 0)) {
                    observations.add(new Observation(e.getDateEcheance(), Math.max(0, jours), 1));
                }
                if (contrat.getPhaseDemande() == PhaseDemande.ACTIF && !e.estPayee() && jours > 0) {
                    retardCourant = Math.max(retardCourant, jours);
                }
            }
            if (contrat.getPhaseDemande() == PhaseDemande.ACTIF) {
                retardCourant = Math.max(retardCourant, retardCourantSource(contrat, mensuel, ref));
            }
        }

        // ---- A. Paiement
        boolean aHistorique = !observations.isEmpty();
        if (aHistorique) {
            double poidsTotal = 0, poidsPondere = 0;
            long nbRecentes = 0;
            for (Observation o : observations) {
                double w = poidsAnciennete(o.date(), ref) * o.nombre();
                poidsTotal += w;
                poidsPondere += w * qualite(o.jours());
                long mois = ChronoUnit.MONTHS.between(o.date(), ref);
                if (mois >= 0 && mois < p.getFenetreConfianceMois()) nbRecentes += o.nombre();
            }
            double confiance = Math.min(1.0, (double) nbRecentes / Math.max(1, p.getObservationsPleineConfiance()));
            double facteur = p.getPlancherConfiance() + (1 - p.getPlancherConfiance()) * confiance;
            gagne.put("paiement", p.getPoidsPaiement() * (poidsPondere / poidsTotal) * facteur);
            dispo.put("paiement", p.getPoidsPaiement());
            if (confiance < 1.0) avertissements.add("Historique de remboursement court (" + nbRecentes
                    + " échéance(s) observée(s) sur " + p.getFenetreConfianceMois() + " mois) : score à interpréter avec prudence.");

            // ---- B. Arriérés en cours
            double ratioArr = retardCourant <= 0 ? 1.0 : retardCourant < 30 ? p.getRatioArrieres1a29()
                    : retardCourant < 60 ? p.getRatioArrieres30a59() : 0.0;
            gagne.put("arrieres_en_cours", p.getPoidsArrieresEnCours() * ratioArr);
            dispo.put("arrieres_en_cours", p.getPoidsArrieresEnCours());
            if (retardCourant >= 90) plafonds.add(plafond(p.getPlafondArrieres90(), "retard en cours >= 90 jours"));
            else if (retardCourant >= 60) plafonds.add(plafond(p.getPlafondArrieres60(), "retard en cours 60-89 jours"));
            else if (retardCourant >= 30) plafonds.add(plafond(p.getPlafondArrieres30(), "retard en cours 30-59 jours"));
            if (retardCourant > 0) avertissements.add("Retard en cours sur un crédit actif (" + retardCourant + " jour(s) ou plus).");
        } else {
            plafonds.add(plafond(p.getPlafondSansHistorique(), "aucun remboursement observé"));
        }

        // ---- C. Capacité financière (revenu annuel) : exclue si inconnue/incohérente, jamais « 0 point »
        Emploi emploi = emploiRepository.findByClient_Id(client.getId()).orElse(null);
        Contrat demande = (contratEnCours != null && estEmprunteur(contratEnCours)
                && contratEnCours.getPhaseDemande() == PhaseDemande.DEMANDE_EN_COURS) ? contratEnCours : null;
        RevenuEvaluation revenuEvaluation = evaluerRevenus(emploi, demande, avertissements);
        BigDecimal revenu = revenuEvaluation.montant();
        boolean capaciteRevenu = false;
        if (revenu == null) {
            avertissements.add("Revenu annuel non renseigné : endettement et exposition exclus du calcul (non pénalisés).");
        } else if (revenu.signum() <= 0) {
            avertissements.add("Revenu annuel nul ou négatif : ratios exclus du calcul.");
        } else {
            double[] charge = chargeEtExposition(actifs, demande, echeancesParContrat, revenuEvaluation.devise(), avertissements);
            if (charge != null) {
                double rev = revenu.doubleValue();
                double te = charge[0] / rev, tx = charge[1] / rev;
                gagne.put("endettement", interpoler(te, p.getEndettementSeuilVert(), p.getEndettementSeuilRouge(), p.getPoidsEndettement()));
                dispo.put("endettement", p.getPoidsEndettement());
                gagne.put("exposition", interpoler(tx, p.getExpositionSeuilVert(), p.getExpositionSeuilRouge(), p.getPoidsExposition()));
                dispo.put("exposition", p.getPoidsExposition());
                capaciteRevenu = true;
                if (te > 1.0) plafonds.add(plafond(p.getPlafondCharge12MoisSup100(), "charge de remboursement sur 12 mois supérieure au revenu annuel"));
                else if (te > p.getEndettementSeuilRouge()) plafonds.add(plafond(p.getPlafondCharge12MoisSup80(), "charge sur 12 mois > 80 % du revenu annuel"));
                if (tx > p.getExpositionPlafondRatio()) plafonds.add(plafond(p.getPlafondExpositionExcessive(), "encours + demande > 150 % du revenu annuel"));
            }
        }

        // ---- D. Budget (recettes/dépenses) et garantie de la demande : absents => exclus
        boolean capaciteBudget = false;
        if (contratEnCours != null && contratEnCours.getRatioRestePourcentage() != null) {
            double r = contratEnCours.getRatioRestePourcentage().doubleValue();
            double pts = r <= 0 ? 0 : r <= 25 ? 5 * r / 25 : r <= 50 ? 5 + 10 * (r - 25) / 25
                    : r < 100 ? 15 + 7 * (r - 50) / 50 : 30;
            gagne.put("budget", Math.min(1.0, pts / 30.0) * p.getPoidsBudget());
            dispo.put("budget", p.getPoidsBudget());
            capaciteBudget = true;
            if (r <= 0) plafonds.add(plafond(p.getPlafondBudgetCritique(), "recettes inférieures ou égales aux dépenses"));
            else if (r <= 25) plafonds.add(plafond(p.getPlafondBudgetFaible(), "reste budgétaire <= 25 % des dépenses"));
        } else if (demande != null) {
            avertissements.add("Recettes/dépenses non renseignées : capacité budgétaire exclue du calcul (non pénalisée).");
        }
        if (contratEnCours != null && contratEnCours.getRatioCouvertureGarantie() != null) {
            double g = contratEnCours.getRatioCouvertureGarantie().doubleValue();
            double pts = g <= 100 ? 0 : g <= 120 ? 8 * (g - 100) / 20 : g <= 150 ? 8 + 8 * (g - 120) / 30
                    : g <= 200 ? 16 + 8 * (g - 150) / 50 : 30;
            gagne.put("garantie", Math.min(1.0, pts / 30.0) * p.getPoidsGarantie());
            dispo.put("garantie", p.getPoidsGarantie());
            if (g <= 100) plafonds.add(plafond(p.getPlafondGarantieInsuffisante(), "garantie <= 100 % de la demande + intérêts"));
            else if (g <= 120) plafonds.add(plafond(p.getPlafondGarantieJuste(), "garantie entre 100 % et 120 %"));
        }
        if (contratEnCours != null && contratEnCours.getRatioCouverturePatrimoine() != null) {
            double patrimoine = contratEnCours.getRatioCouverturePatrimoine().doubleValue();
            double pts = pointsCouverture(patrimoine);
            gagne.put("patrimoine", Math.min(1.0, pts / 30.0) * p.getPoidsPatrimoine());
            dispo.put("patrimoine", p.getPoidsPatrimoine());
            if (patrimoine <= 100) plafonds.add(plafond(p.getPlafondGarantieInsuffisante(),
                    "patrimoine <= 100 % de la demande + intérêts"));
            else if (patrimoine <= 120) plafonds.add(plafond(p.getPlafondGarantieJuste(),
                    "patrimoine entre 100 % et 120 % de la demande + intérêts"));
        }

        boolean preuveCapacite = capaciteRevenu || capaciteBudget;
        if (!aHistorique && !preuveCapacite && !dispo.containsKey("garantie") && !dispo.containsKey("patrimoine")) {
            return ScoreResult.nonCalculable(clientTrouve
                    ? "Aucun remboursement observé, ni revenu, ni budget, ni garantie : score non calculable."
                    : "Ce client vient d'être créé et ne possède ni historique de remboursement ni données de capacité.");
        }
        if (!preuveCapacite) plafonds.add(plafond(p.getPlafondSansCapacite(), "capacité de remboursement non vérifiable (ni revenu, ni budget)"));

        // ---- E/F/G. Ancienneté, nouveaux crédits, mixité
        LocalDate debut = client.getDateAdhesion();
        if (debut != null) {
            long mois = Math.max(0, ChronoUnit.MONTHS.between(debut, ref));
            gagne.put("anciennete", p.getPoidsAnciennete() * Math.min(1.0, (double) mois / Math.max(1, p.getAncienneteMoisPlein())));
            dispo.put("anciennete", p.getPoidsAnciennete());
        }
        long recents = empruntsDecaisses.stream().filter(c -> c.getDateDemande() != null).filter(c -> {
            long m = ChronoUnit.MONTHS.between(c.getDateDemande(), ref);
            return m >= 0 && m <= p.getFenetreContratRecentMois();
        }).count();
        long dormantes = historique.stream().filter(this::estEmprunteur)
                .filter(c -> c.getPhaseDemande() == PhaseDemande.DEMANDE_EN_COURS && c.getDateDemande() != null)
                .filter(c -> ChronoUnit.DAYS.between(c.getDateDemande(), ref) > p.getSeuilDemandeEnAttenteJours()).count();
        gagne.put("nouveaux_credits", Math.max(0, p.getPoidsNouveaux()
                - Math.min(p.getMalusContratsRecentsMax(), recents * p.getMalusParContratRecent())
                - dormantes * p.getMalusParDemandeEnAttente()));
        dispo.put("nouveaux_credits", p.getPoidsNouveaux());
        long types = empruntsDecaisses.stream().map(Contrat::getTypeContrat).filter(Objects::nonNull).distinct().count();
        if (types > 0) {
            gagne.put("mixite", p.getPoidsMixite() * (types >= 3 ? 1.0 : types == 2 ? p.getMixiteRatioDeuxTypes() : p.getMixiteRatioUnType()));
            dispo.put("mixite", p.getPoidsMixite());
        }

        // ---- Score brut renormalisé + risque de groupe
        double obtenu = gagne.values().stream().mapToDouble(Double::doubleValue).sum();
        double possible = dispo.values().stream().mapToDouble(Double::doubleValue).sum();
        double malusGroupe = Math.min(p.getMalusCoObligesMax(),
                p.getMalusParCoObligeARisque() * coObligesARisque(client, actifs));
        if (malusGroupe > 0) avertissements.add("Co-titulaire(s) d'un crédit de groupe en situation négative.");
        double brut = p.getScoreBase() + p.getEchellePoints() * (possible <= 0 ? 0 : obtenu / possible)
                - malusGroupe;

        // ---- Plafonds, règle « mauvais »
        boolean mauvais = evenementNegatif || retardCourant >= 180;
        if (mauvais) plafonds.add(plafond(p.getPlafondBandeTresEleve(), "événement négatif formel ou retard en cours >= 180 jours"));
        double plafondMin = plafonds.stream().mapToInt(this::valeurPlafond).min().orElse(p.getScoreMax());
        int score = (int) Math.round(Math.max(p.getScoreBase(), Math.min(p.getScoreMax(), Math.min(brut, plafondMin))));

        GrilleScore grille = grilleScorePour(score);
        // « Mauvais » seulement dans la bande très élevée (garanti par le plafond ci-dessus ; vérifié par prudence).
        if (mauvais && score > p.getPlafondBandeTresEleve()) mauvais = false;
        if (mauvais) avertissements.add("Client mauvais : événement négatif formel ou retard >= 180 jours.");

        Map<String, Double> composantes = new LinkedHashMap<>();
        gagne.forEach((k, v) -> composantes.put(k, Math.round(v * 10) / 10.0));
        composantes.put("points_disponibles", Math.round(possible * 10) / 10.0);
        composantes.put("malus_groupe", malusGroupe);
        composantes.put("score_avant_plafonds", Math.round(brut * 10) / 10.0);
        boolean partiel = !dispo.containsKey("endettement") || !dispo.containsKey("paiement");
        return ScoreResult.calcule(score, grille.getIntervalle(), grille.getCategorieRisque(), grille.getCouleur(),
                avertissements.isEmpty() ? null : String.join(" ", avertissements), mauvais, partiel,
                plafonds, composantes);
    }

    // ------------------------------------------------------------------ outils

    private String plafond(int valeur, String motif) { return valeur + "|" + motif; }
    private int valeurPlafond(String s) { return Integer.parseInt(s.substring(0, s.indexOf('|'))); }

    private boolean estEmprunteur(Contrat c) { return c.getRoleClient() != RoleClient.GARANT; }

    private boolean pireStatutFormel(String statut) {
        if (statut == null) return false;
        String s = statut.toUpperCase(Locale.ROOT);
        return s.contains("CONTENTIEUX") || s.contains("FAILLITE") || s.contains("DECHEANCE");
    }

    private List<Contrat> contratsActifs(List<Contrat> historique, Contrat enCours) {
        List<Contrat> l = new ArrayList<>(historique.stream().filter(this::estEmprunteur)
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF).toList());
        if (enCours != null && estEmprunteur(enCours) && enCours.getPhaseDemande() == PhaseDemande.ACTIF
                && l.stream().noneMatch(c -> Objects.equals(c.getId(), enCours.getId()))) l.add(enCours);
        return l;
    }

    /** {charge de remboursement sur 12 mois, exposition totale} avec la demande évaluée (pro-forma), ou null si incomplet. */
    private double[] chargeEtExposition(List<Contrat> actifs, Contrat demande, Map<Long, List<Echeance>> echeances,
                                        String deviseRevenu, List<String> avertissements) {
        if (deviseRevenu == null) { avertissements.add("Devise du revenu absente : ratios exclus."); return null; }
        double charge = 0, expo = 0;
        boolean totalCreditActifDeclare = demande != null && demande.getTotalCreditActif() != null;
        for (Contrat c : actifs) {
            String d = normaliserDevise(c.getDevise());
            if (!deviseRevenu.equals(d)) { avertissements.add("Devises différentes ou absentes : ratios exclus (aucune conversion inventée)."); return null; }
            BigDecimal mens = mensualite(c);
            BigDecimal reste = c.getMontantRestantDu();
            if (reste == null) {
                List<Echeance> e = echeances.computeIfAbsent(c.getId(), id -> echeanceRepository.findByContrat_IdOrderByNumeroEcheance(id));
                if (e.isEmpty()) { avertissements.add("Montant restant dû d'un crédit actif inconnu : ratios exclus."); return null; }
                reste = e.stream().map(x -> x.getMontantDu().subtract(x.getMontantPaye() == null ? BigDecimal.ZERO : x.getMontantPaye()).max(BigDecimal.ZERO))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            if (mens.signum() <= 0) { avertissements.add("Mensualité d'un crédit actif inconnue : ratios exclus."); return null; }
            charge += Math.min(reste.doubleValue(), mens.doubleValue() * 12);
            if (!totalCreditActifDeclare) expo += reste.doubleValue();
        }
        if (demande != null) {
            String d = normaliserDevise(demande.getDevise());
            if (!deviseRevenu.equals(d)) { avertissements.add("Devise de la demande différente du revenu : ratios exclus."); return null; }
            BigDecimal total = demande.getMontantTotalDemandeInteret();
            if (total == null || total.signum() <= 0) total = demande.getMontantTotalDu();
            if ((total == null || total.signum() <= 0) && demande.getMontantFinance() != null && demande.getNombreTotalEcheances() != null) {
                total = demande.getMontantFinance().multiply(BigDecimal.valueOf(1 + p.getInteretMensuelDemande() * demande.getNombreTotalEcheances()));
            }
            if (total == null || total.signum() <= 0) { avertissements.add("Montant de la demande inconnu : ratios exclus."); return null; }
            int n = demande.getNombreTotalEcheances() == null || demande.getNombreTotalEcheances() <= 0 ? 1 : demande.getNombreTotalEcheances();
            charge += Math.min(total.doubleValue(), total.doubleValue() / n * 12);
            expo += total.doubleValue();
            BigDecimal creditsActifs = demande.getTotalCreditActif();
            if (creditsActifs == null) {
                creditsActifs = BigDecimal.ZERO;
                if (demande.getCreditActifGarant() != null) creditsActifs = creditsActifs.add(demande.getCreditActifGarant());
                if (demande.getCreditActifFiance() != null) creditsActifs = creditsActifs.add(demande.getCreditActifFiance());
            }
            if (creditsActifs.signum() > 0) {
                if (!deviseRevenu.equals(d)) {
                    avertissements.add("Devise des crédits actifs différente de celle du revenu : exposition exclue.");
                    return null;
                }
                expo += creditsActifs.doubleValue();
            }
        }
        return new double[]{charge, expo};
    }

    private RevenuEvaluation evaluerRevenus(Emploi emploi, Contrat demande, List<String> avertissements) {
        BigDecimal total = emploi == null ? null : emploi.getRevenuAnnuelTotal();
        String devise = emploi == null ? null : normaliserDevise(emploi.getDevise());
        List<RevenuDemande> revenus = demande == null
                ? List.of() : revenuDemandeRepository.findByContrat_Id(demande.getId());
        if ((total == null || total.signum() <= 0) && !revenus.isEmpty()) {
            RevenuDemande premier = revenus.stream()
                    .filter(r -> r.getMontantAnnuel() != null && r.getMontantAnnuel().signum() > 0)
                    .filter(r -> normaliserDevise(r.getDevise()) != null)
                    .findFirst().orElse(null);
            if (premier != null) {
                total = BigDecimal.ZERO;
                devise = normaliserDevise(premier.getDevise());
            }
        }
        for (RevenuDemande revenu : revenus) {
            String deviseSource = normaliserDevise(revenu.getDevise());
            if (devise == null || deviseSource == null || !devise.equals(deviseSource)) {
                avertissements.add("Un revenu complémentaire est exclu : devise absente ou différente (aucune conversion inventée).");
                continue;
            }
            total = (total == null ? BigDecimal.ZERO : total).add(revenu.getMontantAnnuel());
        }
        if (total != null && emploi != null && emploi.getRevenuAnnuelTotal() == null && revenus.isEmpty()) {
            total = null;
        }
        return new RevenuEvaluation(total, devise);
    }

    private double pointsCouverture(double ratio) {
        if (ratio <= 100) return 0;
        if (ratio <= 120) return 8 * (ratio - 100) / 20;
        if (ratio <= 150) return 8 + 8 * (ratio - 120) / 30;
        if (ratio <= 200) return 16 + 8 * (ratio - 150) / 50;
        return 30;
    }

    private record RevenuEvaluation(BigDecimal montant, String devise) {}

    private BigDecimal mensualite(Contrat c) {
        if (c.getMontantEcheanceMensuelle() != null && c.getMontantEcheanceMensuelle().signum() > 0) return c.getMontantEcheanceMensuelle();
        if (c.getNombreTotalEcheances() != null && c.getNombreTotalEcheances() > 0) {
            BigDecimal base = c.getMontantTotalDu() != null ? c.getMontantTotalDu() : c.getMontantFinance();
            if (base != null && base.signum() > 0) return base.divide(BigDecimal.valueOf(c.getNombreTotalEcheances()), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /** Retard actuellement déclaré par la source (dernier mois observé, sinon champ du contrat). */
    private int retardCourantSource(Contrat c, List<HistoriquePaiementMensuel> mensuel, LocalDate ref) {
        int jours = 0;
        Optional<HistoriquePaiementMensuel> dernier = mensuel.stream().max(Comparator.comparing(HistoriquePaiementMensuel::getMois));
        if (dernier.isPresent() && ChronoUnit.MONTHS.between(dernier.get().getMois(), ref) <= 2) {
            Integer j = joursObserves(dernier.get());
            if (j != null) jours = j;
        }
        if (c.getNombreJoursRetard() != null && c.getNombreEcheancesImpayees() != null && c.getNombreEcheancesImpayees() > 0) {
            jours = Math.max(jours, c.getNombreJoursRetard());
        }
        return jours;
    }

    /** Co-titulaires (présents en base) d'un crédit actif de groupe qui sont « mauvais » ou en retard >= 30 j. */
    private int coObligesARisque(Client client, List<Contrat> actifs) {
        List<Long> ids = actifs.stream().filter(c -> c.getRoleClient() == RoleClient.CO_TITULAIRE || c.getRoleClient() == RoleClient.TITULAIRE)
                .map(Contrat::getId).toList();
        if (ids.isEmpty()) return 0;
        Set<Long> risque = new HashSet<>();
        for (ContratLien l : contratLienRepository.findByContrat_IdIn(ids)) {
            Client lie = l.getClientLie();
            if (lie == null || Objects.equals(lie.getId(), client.getId())) continue;
            boolean negatif = !evenementNegatifRepository.findByClient_IdAndActifTrue(lie.getId()).isEmpty();
            boolean retard = contratRepository.findByClient_Id(lie.getId()).stream()
                    .anyMatch(c -> c.getPhaseDemande() == PhaseDemande.ACTIF && c.getNombreJoursRetard() != null && c.getNombreJoursRetard() >= 30);
            if (negatif || retard) risque.add(lie.getId());
        }
        return risque.size();
    }

    /** Jours de retard d'une observation mensuelle ; null si non exploitable (pas d'invention de bucket). */
    private Integer joursObserves(HistoriquePaiementMensuel h) {
        if (h.getJoursDeRetard() != null) return h.getJoursDeRetard() < 0 ? null : h.getJoursDeRetard();
        BucketRetard b = BucketRetard.depuisStatut(h.getStatut());
        if (b == null) return null;
        return switch (b) {   // borne basse du bucket : on ne sur-déclare jamais un retard
            case A_JOUR -> 0; case JOURS_1_29 -> 1; case JOURS_30_59 -> 30; case JOURS_60_89 -> 60;
            case JOURS_90_119 -> 90; case JOURS_120_149 -> 120; case JOURS_150_179 -> 150; case JOURS_180_PLUS -> 180;
        };
    }

    private boolean statutNonObservable(String statut) {
        if (statut == null || statut.isBlank()) return true;
        String n = Normalizer.normalize(statut, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "");
        return n.isBlank() || Set.of("TROPRECENT", "NONDISPONIBLE", "ND", "NA", "NC", "NOUV", "NDIS").contains(n);
    }

    private int nombreEcheances(HistoriquePaiementMensuel h) {
        return h.getNombreEcheances() == null ? 1 : Math.max(1, h.getNombreEcheances());
    }

    private double qualite(int jours) {
        if (jours <= 0) return p.getQualitePayeATemps();
        if (jours < 30) return p.getQualiteRetardCourt();
        if (jours >= 180) return p.getQualiteImpaye();
        return p.getQualiteRetardLong() + (p.getQualiteImpaye() - p.getQualiteRetardLong()) * ((jours - 30) / 150.0);
    }

    private double poidsAnciennete(LocalDate obs, LocalDate ref) {
        long mois = ChronoUnit.MONTHS.between(obs, ref);
        return mois <= 12 ? p.getPoidsRecent() : mois <= 36 ? p.getPoidsMoyen() : p.getPoidsAncien();
    }

    private String normaliserDevise(String devise) {
        if (devise == null || devise.isBlank()) return null;
        String n = Normalizer.normalize(devise, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "");
        if (n.isBlank()) return null;
        return n.equals("MGA") || n.contains("ARIARY") ? "MGA" : n;
    }

    private double interpoler(double taux, double vert, double rouge, double max) {
        if (taux <= vert) return max;
        if (taux >= rouge) return 0;
        return max * (rouge - taux) / (rouge - vert);
    }

    private GrilleScore grilleScorePour(int score) {
        return grilleScoreRepository.findAll().stream()
                .filter(g -> score >= g.getScoreMin() && score <= g.getScoreMax()).findFirst()
                .orElseThrow(() -> new IllegalStateException("La grille de score ne couvre pas la valeur " + score));
    }
}