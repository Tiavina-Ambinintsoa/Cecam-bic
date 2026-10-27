package mg.cecam.bic.client;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.client.dto.ClientEnregistrementResponse;
import mg.cecam.bic.client.dto.ClientRequest;
import mg.cecam.bic.contrat.ContratRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion du cycle de vie des clients (sociétaires).
 * <p>Responsabilités : création, recherche par identifiant, mise à jour,
 * suppression et détection des doublons par CIN normalisé.</p>
 */
@Service
@RequiredArgsConstructor
public class ClientService {

    public static final String DETAIL_NOUVELLE_DEMANDE_CLIENT_EXISTANT =
            "Nouvelle demande sur client existant";

    private final ClientRepository clientRepository;
    private final ContratRepository contratRepository;
    private final EmploiRepository emploiRepository;
    private final AuditService auditService;

    @Transactional
    public ClientEnregistrementResponse enregistrerOuRecuperer(ClientRequest request) {
        String cin = request.identifiants().stream()
                .filter(i -> "CIN".equalsIgnoreCase(i.typeIdentifiant()))
                .map(ClientRequest.IdentifiantRequest::numero)
                .map(ClientService::normaliserCin)
                .filter(numero -> !numero.isBlank())
                .findFirst()
                .orElse(null);

        if (cin != null) {
            List<Client> existants = clientRepository.findAllByCinNormalise(cin);
            if (existants.size() > 1) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Plusieurs clients correspondent au CIN fourni. Corrigez les doublons avant de poursuivre.");
            }
            if (!existants.isEmpty()) {
                Client existant = existants.get(0);
                if (request.affaireIndividuelle() != null) {
                    appliquerAffaireIndividuelle(existant, request.affaireIndividuelle());
                    clientRepository.save(existant);
                    auditService.enregistrer("CLIENT", existant.getId(), "MODIFICATION_AFFAIRE_INDIVIDUELLE",
                            "Affaire individuelle mise à jour depuis une nouvelle demande");
                }
                auditService.enregistrer("CLIENT", existant.getId(), "CONSULTATION",
                        DETAIL_NOUVELLE_DEMANDE_CLIENT_EXISTANT);
                return new ClientEnregistrementResponse(existant, true);
            }
        }

        Client nouveau = creerClient(request);
        auditService.enregistrer("CLIENT", nouveau.getId(), "CREATION", "Client créé depuis le formulaire");
        return new ClientEnregistrementResponse(nouveau, false);
    }

    private Client creerClient(ClientRequest request) {
        Client client = Client.builder()
                .codeClientCb(genererCodeClientCb())
                .titre(request.titre())
                .categorieTiersCode(request.categorieTiersCode())
                .prenom(request.prenom())
                .deuxiemePrenom(request.deuxiemePrenom())
                .nom(request.nom())
                .dateNaissance(request.dateNaissance())
                .villeNaissance(request.villeNaissance())
                .paysNaissance(request.paysNaissance())
                .genre(request.genre())
                .nationalite(request.nationalite())
                .codeClientEtablissement(request.codeClientEtablissement())
                .etatCivil(request.etatCivil())
                .telephone(request.telephone())
                .dateAdhesion(request.dateAdhesion() != null ? request.dateAdhesion() : LocalDate.now())
                .build();

        List<Adresse> adresses = request.adresses().stream()
                .map(a -> Adresse.builder()
                        .client(client).typeAdresse(a.typeAdresse()).adresseComplete(a.adresseComplete())
                        .numeroRue(a.numeroRue()).codePostal(a.codePostal()).ville(a.ville())
                        .commune(a.commune()).region(a.region()).pays(a.pays())
                        .build())
                .collect(Collectors.toList());
        client.setAdresses(adresses);

        List<Identifiant> identifiants = request.identifiants().stream()
                .map(i -> Identifiant.builder().client(client).typeIdentifiant(i.typeIdentifiant()).numero(i.numero()).build())
                .collect(Collectors.toList());
        client.setIdentifiants(identifiants);

        Client sauvegarde = clientRepository.save(client);

        if (request.emploi() != null && emploiRenseigne(request.emploi())) {
            Emploi emploi = Emploi.builder()
                    .client(sauvegarde)
                    .statutEmploi(request.emploi().statutEmploi())
                    .nomEmployeur(request.emploi().nomEmployeur())
                    .profession(request.emploi().profession())
                    .dateEmbauche(request.emploi().dateEmbauche())
                    .revenuAnnuelTotal(request.emploi().revenuAnnuelTotal())
                    .devise(request.emploi().devise())
                    .build();
            emploiRepository.save(emploi);
        }

        appliquerAffaireIndividuelle(sauvegarde, request.affaireIndividuelle());

        return sauvegarde;
    }

    public Client rechercherParIdentifiant(String typeIdentifiant, String numero) {
        String cinNormalise = normaliserCin(numero);
        if ("CIN".equalsIgnoreCase(typeIdentifiant) && cinNormalise.isBlank()) return null;
        List<Client> trouves = "CIN".equalsIgnoreCase(typeIdentifiant)
                ? clientRepository.findAllByCinNormalise(cinNormalise)
                : clientRepository.findAllByIdentifiants_NumeroAndIdentifiants_TypeIdentifiant(
                        numero == null ? null : numero.trim(), typeIdentifiant);
        if (trouves.size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Doublon détecté : " + trouves.size() + " clients portent le " + typeIdentifiant
                  + " " + numero + ". Corrigez la base avant de poursuivre.");
        }
        return trouves.isEmpty() ? null : trouves.get(0);
    }

    static String normaliserCin(String numero) {
        return numero == null ? "" : numero.replaceAll("[^0-9]", "");
    }

    @Transactional
    public Client mettreAJour(Long id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable"));
        client.setTitre(request.titre());
        client.setPrenom(request.prenom());
        client.setDeuxiemePrenom(request.deuxiemePrenom());
        client.setNom(request.nom());
        client.setDateNaissance(request.dateNaissance());
        client.setVilleNaissance(request.villeNaissance());
        client.setPaysNaissance(request.paysNaissance());
        client.setGenre(request.genre());
        client.setNationalite(request.nationalite());
        client.setEtatCivil(request.etatCivil());
        if (request.dateAdhesion() != null) {
            client.setDateAdhesion(request.dateAdhesion());
        }
        client.setCodeClientEtablissement(request.codeClientEtablissement());
        client.setTelephone(request.telephone());
        appliquerAffaireIndividuelle(client, request.affaireIndividuelle());
        Client sauvegarde = clientRepository.save(client);
        auditService.enregistrer("CLIENT", id, "MODIFICATION", "Informations personnelles modifiées");
        return sauvegarde;
    }

    @Transactional
    public Adresse ajouterAdresse(Long clientId, ClientRequest.AdresseRequest req) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable"));
 
        client.getAdresses().stream()
                .filter(a -> a.getTypeAdresse() != null
                          && a.getTypeAdresse().equalsIgnoreCase(req.typeAdresse()))
                .filter(a -> Boolean.TRUE.equals(a.getActuelle()))
                .forEach(a -> a.setActuelle(false));
 
        Adresse adresse = Adresse.builder()
                .client(client).typeAdresse(req.typeAdresse()).adresseComplete(req.adresseComplete())
                .numeroRue(req.numeroRue()).codePostal(req.codePostal()).ville(req.ville())
                .commune(req.commune()).region(req.region()).pays(req.pays())
                .actuelle(true)
                .build();
 
        client.getAdresses().add(adresse);
        clientRepository.save(client);
        auditService.enregistrer("CLIENT", clientId, "AJOUT_ADRESSE",
                "Adresse " + adresse.getTypeAdresse() + " ajoutée");
        return adresse;
    }

    private String genererCodeClientCb() {
        String code;
        do {
            code = "L" + String.format("%08d", clientRepository.prochainCodeClientCb());
        } while (clientRepository.findByCodeClientCb(code).isPresent());
        return code;
    }

    private void appliquerAffaireIndividuelle(Client client, ClientRequest.AffaireIndividuelleRequest request) {
        if (request == null || request.nom() == null || request.nom().isBlank()) return;

        AffaireIndividuelle existante = client.getAffaireIndividuelle();
        AffaireIndividuelle affaire = existante == null
                ? AffaireIndividuelle.builder().client(client).build()
                : existante;
        if (existante == null) client.setAffaireIndividuelle(affaire);
        affaire.setNom(request.nom().trim());
        affaire.setDateDerniereModification(java.time.LocalDateTime.now());

        if (request.adresses() == null || request.adresses().isEmpty()) return;
        List<AdresseAffaireIndividuelle> adresses = request.adresses().stream()
                .filter(a -> a != null && a.adresseComplete() != null && !a.adresseComplete().isBlank())
                .map(a -> AdresseAffaireIndividuelle.builder()
                        .affaireIndividuelle(affaire)
                        .adresseComplete(a.adresseComplete().trim())
                        .numeroRue(a.numeroRue()).codePostal(a.codePostal()).ville(a.ville())
                        .commune(a.commune()).region(a.region()).pays(a.pays())
                        .build())
                .toList();
        affaire.getAdresses().clear();
        affaire.getAdresses().addAll(adresses);
    }

    private boolean emploiRenseigne(ClientRequest.EmploiRequest emploi) {
        Emploi valeur = Emploi.builder()
                .statutEmploi(emploi.statutEmploi())
                .nomEmployeur(emploi.nomEmployeur())
                .profession(emploi.profession())
                .dateEmbauche(emploi.dateEmbauche())
                .revenuAnnuelTotal(emploi.revenuAnnuelTotal())
                .devise(emploi.devise())
                .build();
        return valeur.estRenseigne();
    }

    /**
     * Supprime un client et toutes ses données liées (contrats, emploi, etc.).
     * <p>Ne supprime que si le client n'a aucun contrat actif.</p>
     */
    @Transactional
    public void supprimer(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable"));
        boolean aContratActif = contratRepository.findPourClient(clientId).stream()
                .anyMatch(c -> c.getPhaseDemande() == mg.cecam.bic.common.enums.PhaseDemande.ACTIF);
        if (aContratActif) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Impossible de supprimer un client ayant un contrat actif.");
        }
        emploiRepository.findByClient_Id(clientId).ifPresent(emploiRepository::delete);
        auditService.enregistrer("CLIENT", clientId, "SUPPRESSION", "Client supprimé par un administrateur");
        clientRepository.delete(client);
    }
}
