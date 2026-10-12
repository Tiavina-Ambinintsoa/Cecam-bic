package mg.cecam.bic.contrat.dto;

import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.StatutDemandeModification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ModificationContratResponse(
        Long id,
        Long contratId,
        String codeContratCb,
        String clientNomComplet,
        PhaseDemande phaseActuelle,
        MotifCloture motifClotureActuel,
        String codeContratEtablissementActuel,
        String codeDemandeEtablissementActuel,
        BigDecimal montantEcheanceActuel,
        String deviseActuelle,
        String codeContratEtablissement,
        String codeDemandeEtablissement,
        PhaseDemande phaseDemande,
        MotifCloture motifCloture,
        BigDecimal montantEcheanceMensuelle,
        String devise,
        String justification,
        StatutDemandeModification statut,
        String soumisPar,
        LocalDateTime dateSoumission,
        String traitePar,
        LocalDateTime dateTraitement,
        String commentaireAdmin
) {}
