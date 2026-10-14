package mg.cecam.bic.score;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.score")
@Getter @Setter
public class ScoreProperties {

    // Échelle : score = base + echellePoints * (points obtenus / points disponibles)
    private int scoreBase = 300;
    private int scoreMax = 850;
    /** Points utiles au-dessus de la base : score effectif 300..700 (A >= 530 = 57,5 % de l'échelle). */
    private double echellePoints = 400;

    // Poids relatifs des blocs
    private double poidsPaiement = 290;
    private double poidsArrieresEnCours = 60;
    private double poidsEndettement = 35;
    private double poidsExposition = 25;
    private double poidsBudget = 30;
    private double poidsGarantie = 30;
    private double poidsAnciennete = 40;
    private double poidsNouveaux = 30;
    private double poidsMixite = 10;

    // Comportement de paiement
    private int observationsPleineConfiance = 12;   // nb d'échéances observées (36 derniers mois) pour confiance = 1
    private int fenetreConfianceMois = 36;
    private double plancherConfiance = 0.50;
    private double poidsRecent = 1.00;              // observation <= 12 mois
    private double poidsMoyen = 0.60;               // 13..36 mois
    private double poidsAncien = 0.30;              // > 36 mois
    private double qualitePayeATemps = 1.00;
    private double qualiteRetardCourt = 0.55;       // 1..29 jours
    private double qualiteRetardLong = 0.15;        // 30 jours, décroît linéairement jusqu'à 0 à 180 jours
    private double qualiteImpaye = 0.00;

    // Arriérés en cours (part des points conservée)
    private double ratioArrieres1a29 = 0.25;
    private double ratioArrieres30a59 = 0.20;       // appliqué à partir de 30 jours (puis 0 dès 60)

    // Capacité de remboursement (revenu annuel)
    private double endettementSeuilVert = 0.30;
    private double endettementSeuilRouge = 0.80;
    private double expositionSeuilVert = 0.15;
    private double expositionSeuilRouge = 0.60;
    private double interetMensuelDemande = 0.03;    // identique à ContratService (+3 %/mois)

    // Ancienneté / nouveaux crédits / mixité
    private int ancienneteMoisPlein = 84;
    private double malusParContratRecent = 6;
    private double malusContratsRecentsMax = 18;
    private double malusParDemandeEnAttente = 10;
    private int fenetreContratRecentMois = 12;
    private int seuilDemandeEnAttenteJours = 60;
    private double mixiteRatioDeuxTypes = 0.60;
    private double mixiteRatioUnType = 0.20;

    // Plafonds de décision (le score ne peut pas dépasser ces valeurs)
    private int plafondSansCapacite = 529;          // ni revenu, ni budget : jamais « très faible »
    private int plafondSansHistorique = 499;        // aucun remboursement observé : jamais mieux que « moyen »
    private int plafondBandeTresEleve = 399;        // client « mauvais »
    private int plafondCharge12MoisSup100 = 459;    // charge sur 12 mois > revenu annuel
    private int plafondCharge12MoisSup80 = 499;
    private double expositionPlafondRatio = 1.50;
    private int plafondExpositionExcessive = 459;
    private int plafondArrieres30 = 499;
    private int plafondArrieres60 = 459;
    private int plafondArrieres90 = 399;
    private int plafondBudgetCritique = 399;        // recettes <= dépenses
    private int plafondBudgetFaible = 459;          // reste <= 25 % des dépenses
    private int plafondGarantieInsuffisante = 459;  // couverture <= 100 %
    private int plafondGarantieJuste = 499;         // couverture <= 120 %

    // Risque de groupe (co-titulaires d'un crédit au groupement)
    private double malusParCoObligeARisque = 20;
    private double malusCoObligesMax = 60;
}