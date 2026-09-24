package mg.cecam.bic.score;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.score")
@Getter @Setter
public class ScoreProperties {

    private int scoreBase = 300;
    private int scoreMax = 850;

    private double maxPaiement = 200;
    private double maxEndettement = 120;
    private double maxExposition = 100;
    private double maxAnciennete = 60;
    private double maxNouveaux = 40;
    private double maxMixite = 30;

    private int maturiteMois = 36;
    private double plancherConfiance = 0.50;

    private double poidsRecent = 1.00;   
    private double poidsMoyen = 0.60;    
    private double poidsAncien = 0.30;   

    private double qualitePayeATemps = 1.00;
    private double qualiteRetardCourt = 0.50;

    private double qualiteRetardLong = 0.15;   
    private double qualiteImpaye = 0.00;       

    private double endettementSeuilVert = 0.33;
    private double endettementSeuilRouge = 0.70;

    private double malusSurendettementCoef = 60;
    private double malusSurendettementMax = 90;

    private double expositionSeuilVert = 0.25;
    private double expositionSeuilRouge = 0.75;

    private int ancienneteMoisPlein = 84;

    private double malusParContratRecent = 8;
    private double malusParDemandeEnAttente = 12;
    private int fenetreContratRecentMois = 12;
    private int seuilDemandeEnAttenteJours = 60;

    private double mixiteRatioDeuxTypes = 0.60;
    private double mixiteRatioUnType = 0.20;

    private double plafondSecondaireBase = 0.70;
    private double plafondSecondairePart = 0.30;

    private double neutreSansRevenu = 0.60;

    public double maxSecondaire() {
        return maxEndettement + maxExposition + maxAnciennete + maxNouveaux + maxMixite;
    }
}