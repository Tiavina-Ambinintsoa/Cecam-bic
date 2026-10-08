// ClientService.java (remplace)
package mg.cecam.bic.client;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.client.dto.ClientEnregistrementResponse;
import mg.cecam.bic.client.dto.ClientRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final EmploiRepository emploiRepository;
    private final AffaireIndividuelleRepository affaireRepository;
    private final AuditService auditService;

    @Transactional
    public ClientEnregistrementResponse enregistrerOuRecuperer(ClientRequest request) {
        String cin = request.identifiants().stream()
                .filter(i -> "CIN".equalsIgnoreCase(i.typeIdentifiant()))
                .map(ClientRequest.IdentifiantRequest::numero)
                .findFirst()
                .orElse(null);

        if (cin != null) {
            List<Client> existants = clientRepository.findAllByIdentifiants_NumeroAndIdentifiants_TypeIdentifiant(cin, "CIN");
            if (!existants.isEmpty()) {
                Client existant = existants.get(0);
                auditService.enregistrer("CLIENT", existant.getId(), "CONSULTATION", "Nouvelle demande sur client existant, CIN " + cin);
                return new ClientEnregistrementResponse(existant, true);
            }
        }

        Client nouveau = creerClient(request);
        auditService.enregistrer("CLIENT", nouveau.getId(), "CREATION", "Code CB " + nouveau.getCodeClientCb());
        return new ClientEnregistrementResponse(nouveau, false);
    }

    private Client creerClient(ClientRequest request) {
        Client client = Client.builder()
                .codeClientCb(genererCodeClientCb())
                .codeClientEtablissement(vide(request.codeClientEtablissement()))
                .agence(vide(request.agence()))
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
                .etatCivil(request.etatCivil())
                .telephone(request.telephone())
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

        if (request.emploi() != null) {
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

        enregistrerAffaire(sauvegarde, request.affaireIndividuelle());
        return sauvegarde;
    }

    public Client rechercherParIdentifiant(String typeIdentifiant, String numero) {
        List<Client> trouves = clientRepository
                .findAllByIdentifiants_NumeroAndIdentifiants_TypeIdentifiant(numero, typeIdentifiant);
        if (trouves.size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Doublon détecté : " + trouves.size() + " clients portent le " + typeIdentifiant
                  + " " + numero + ". Corrigez la base avant de poursuivre.");
        }
        return trouves.isEmpty() ? null : trouves.get(0);
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
        client.setTelephone(request.telephone());
        if (vide(request.codeClientEtablissement()) != null) client.setCodeClientEtablissement(request.codeClientEtablissement().trim());
        if (vide(request.agence()) != null) client.setAgence(request.agence().trim());
        Client sauvegarde = clientRepository.save(client);
        enregistrerAffaire(sauvegarde, request.affaireIndividuelle());
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
                adresse.getTypeAdresse() + " : " + adresse.getAdresseComplete());
        return adresse;
    }

    /** Crée ou met à jour l'affaire individuelle du client ; sans nom d'affaire, rien n'est fait. */
    private void enregistrerAffaire(Client client, ClientRequest.AffaireRequest req) {
        if (req == null || vide(req.nomAffaire()) == null) return;
        AffaireIndividuelle affaire = affaireRepository.findByClient_Id(client.getId())
                .orElseGet(() -> AffaireIndividuelle.builder().client(client).build());
        affaire.setNomAffaire(req.nomAffaire().trim());
        affaire.setAdresseComplete(vide(req.adresseComplete()));
        affaire.setNumeroRue(vide(req.numeroRue()));
        affaire.setCodePostal(vide(req.codePostal()));
        affaire.setVille(vide(req.ville()));
        affaire.setCommune(vide(req.commune()));
        affaire.setRegion(vide(req.region()));
        affaire.setPays(vide(req.pays()));
        affaireRepository.save(affaire);
    }

    private String vide(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private String genererCodeClientCb() {
        return "L" + String.format("%08d", clientRepository.prochainCodeClientCb());
    }
}