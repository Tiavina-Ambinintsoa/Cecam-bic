// mg/cecam/bic/score/ScoreProperties.java  — NOUVEAU
package mg.cecam.bic.score;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Toutes les constantes du moteur de score, externalisées.
 *
 * Elles étaient codées en dur, ce qui rendait impossible toute recalibration
 * sans recompiler — et impossible à justifier en soutenance. Elles sont
 * désormais dans application.yml sous le préfixe app.score, avec les valeurs
 * par défaut ci-dessous.
 *
 * Contrainte à respecter en cas de modification :
 *   paiement + endettement + exposition + anciennete + nouveaux + mixite
 *   doit valoir exactement (scoreMax - scoreBase), soit 550.
 */
@Component
@ConfigurationProperties(prefix = "app.score")
@Getter @Setter
public class ScoreProperties {

    private int scoreBase = 300;
    private int scoreMax = 850;

    // --- Poids maximaux des six axes (somme = 550) ---
    private double maxPaiement = 200;
    private double maxEndettement = 120;
    private double maxExposition = 100;
    private double maxAnciennete = 60;
    private double maxNouveaux = 40;
    private double maxMixite = 30;

    // --- Axe comportement de paiement ---
    /** Nombre d'échéances échues à partir duquel le dossier est jugé mature. */
    private int maturiteEcheances = 36;
    /** Part du score de paiement accessible même sur un dossier très jeune. */
    private double plancherConfiance = 0.50;
    /** Pondération d'ancienneté appliquée AUX BONUS COMME AUX MALUS. */
    private double poidsRecent = 1.00;   // <= 12 mois
    private double poidsMoyen = 0.60;    // <= 36 mois
    private double poidsAncien = 0.30;   // au-delà
    /** Qualité attribuée à chaque issue d'échéance, entre 0 et 1. */
    private double qualitePayeATemps = 1.00;
    private double qualiteRetardCourt = 0.50;   // payée avec <= 30 j de retard
    private double qualiteRetardLong = 0.15;    // payée avec > 30 j de retard
    private double qualiteImpaye = 0.00;        // échue et toujours non soldée

    // --- Axe endettement (charge mensuelle / revenu mensuel) ---
    private double endettementSeuilVert = 0.33;
    private double endettementSeuilRouge = 0.70;
    /** Malus appliqué au-delà du seuil rouge : (taux - seuilRouge) x coefficient. */
    private double malusSurendettementCoef = 60;
    private double malusSurendettementMax = 90;

    // --- Axe exposition (engagement total / revenu annuel) ---
    private double expositionSeuilVert = 0.25;
    private double expositionSeuilRouge = 0.75;

    // --- Axe ancienneté ---
    private int ancienneteMoisPlein = 84;

    // --- Axe nouveaux crédits ---
    private double malusParContratRecent = 8;
    private double malusParDemandeEnAttente = 12;
    private int fenetreContratRecentMois = 12;
    private int seuilDemandeEnAttenteJours = 60;

    // --- Axe mixité ---
    private double mixiteRatioDeuxTypes = 0.60;
    private double mixiteRatioUnType = 0.20;

    /**
     * Plafonnement des axes secondaires par la qualité de paiement.
     * Ancienne valeur : 0,40 + 0,60 x ratio — trop agressive, elle annulait
     * toute variation des cinq autres axes (cf. audit §1).
     */
    private double plafondSecondaireBase = 0.70;
    private double plafondSecondairePart = 0.30;

    /** Valeur neutre des axes financiers quand le revenu n'est pas déclaré. */
    private double neutreSansRevenu = 0.60;

    public double maxSecondaire() {
        return maxEndettement + maxExposition + maxAnciennete + maxNouveaux + maxMixite;
    }
}