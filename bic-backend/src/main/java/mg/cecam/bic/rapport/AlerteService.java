package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.ContratRepository;
import mg.cecam.bic.contrat.Echeance;
import mg.cecam.bic.contrat.EcheanceRepository;
import mg.cecam.bic.rapport.dto.AlerteDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AlerteService {

    private final ContratRepository contratRepository;
    private final EcheanceRepository echeanceRepository;

    /** Alertes sur l'ensemble des clients, sans filtre. */
    @Transactional(readOnly = true)
    public List<AlerteDTO> lister() {
        return lister(null, null, null, null, null, null);
    }

    /**
     * Alertes filtrées — page « Sommaire des alertes ».
     * Dates au format aaaa-mm-jj. La période porte sur la date de début de l'incident,
     * c'est-à-dire la plus ancienne échéance restée sans paiement.
     */
    @Transactional(readOnly = true)
    public List<AlerteDTO> lister(String dateDebut, String dateFin, String nom,
                                  String codeClientCb, String codeContratCb, String agence) {
        LocalDate reference = LocalDate.now();
        LocalDate debut = parserDate(dateDebut, "début");
        LocalDate fin = parserDate(dateFin, "fin");
        String nomRecherche = normaliser(nom);

        return contratRepository.findAll().stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF)
                .filter(c -> egal(codeContratCb, c.getCodeContratCb()))
                .filter(c -> egal(codeClientCb, c.getClient().getCodeClientCb()))
                .filter(c -> egal(agence, c.getClient().getAgence()))
                .filter(c -> nomRecherche == null
                        || Objects.requireNonNull(normaliser(nomComplet(c.getClient()))).contains(nomRecherche))
                .map(c -> toAlerte(c, reference, debut, fin))
                .filter(Objects::nonNull)
                .toList();
    }

    /** Alertes d'un seul client — affichées dans son rapport de solvabilité. */
    @Transactional(readOnly = true)
    public List<AlerteDTO> listerParClient(Long clientId, LocalDate reference) {
        return contratRepository.findByClient_Id(clientId).stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF)
                .map(c -> toAlerte(c, reference, null, null))
                .filter(Objects::nonNull)
                .toList();
    }

    /** null si le contrat n'a ni impayé ni retard, ou si l'incident est hors de la période demandée. */
    private AlerteDTO toAlerte(Contrat c, LocalDate reference, LocalDate debut, LocalDate fin) {
        List<Echeance> echeances = echeanceRepository.findByContrat_IdOrderByNumeroEcheance(c.getId());
        long impayees = 0;
        long enRetard = 0;
        LocalDate debutIncident = null;

        for (Echeance e : echeances) {
            StatutEcheance s = e.statutEffectif(reference);
            if (s == StatutEcheance.IMPAYE) impayees++;
            else if (s == StatutEcheance.EN_RETARD && !e.estPayee()) enRetard++;

            if (e.estEnSouffrance(reference)
                    && (debutIncident == null || e.getDateEcheance().isBefore(debutIncident))) {
                debutIncident = e.getDateEcheance();
            }
        }
        if (impayees == 0 && enRetard == 0) return null;

        if (debutIncident != null) {
            if (debut != null && debutIncident.isBefore(debut)) return null;
            if (fin != null && debutIncident.isAfter(fin)) return null;
        }

        Client client = c.getClient();
        return new AlerteDTO(client.getId(), client.getCodeClientCb(), nomComplet(client),
                c.getId(), c.getCodeContratCb(), impayees, enRetard);
    }

    private String nomComplet(Client client) {
        String prenom = client.getPrenom() != null ? client.getPrenom() : "";
        String nom = client.getNom() != null ? client.getNom() : "";
        return (prenom + " " + nom).trim();
    }

    /** Filtre vide = pas de filtre ; sinon égalité exacte, sans tenir compte de la casse. */
    private boolean egal(String recherche, String valeur) {
        if (recherche == null || recherche.isBlank()) return true;
        return valeur != null && recherche.trim().equalsIgnoreCase(valeur.trim());
    }

    /** Minuscules sans accents, pour que « Hervé » trouve « HERVE ». */
    private String normaliser(String s) {
        if (s == null || s.isBlank()) return null;
        return Normalizer.normalize(s.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private LocalDate parserDate(String valeur, String libelle) {
        if (valeur == null || valeur.isBlank()) return null;
        try {
            return LocalDate.parse(valeur.trim());
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Date de " + libelle + " invalide (format attendu : aaaa-mm-jj).");
        }
    }
}