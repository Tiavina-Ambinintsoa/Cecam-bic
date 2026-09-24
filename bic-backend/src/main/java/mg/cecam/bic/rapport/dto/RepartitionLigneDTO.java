package mg.cecam.bic.rapport.dto;

public record RepartitionLigneDTO(String categorie, long demande, long refuse, long abandonne, long actif, long ferme) {}