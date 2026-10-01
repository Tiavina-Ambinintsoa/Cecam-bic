package mg.cecam.bic.client;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.client.dto.ImportLigneDTO;
import mg.cecam.bic.client.dto.ImportResultatDTO;
import mg.cecam.bic.common.enums.Genre;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.ContratRepository;
import mg.cecam.bic.common.enums.ModeRattachement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final ClientRepository clientRepository;
    private final ContratRepository contratRepository;
    private final AuditService auditService;

    public ImportResultatDTO importer(MultipartFile fichier) {
        List<String> erreurs = new ArrayList<>();
        int lues = 0, importees = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fichier.getInputStream(), StandardCharsets.UTF_8))) {
            reader.readLine();
            String ligne;
            int numeroLigne = 1;
            while ((ligne = reader.readLine()) != null) {
                numeroLigne++;
                if (ligne.isBlank()) continue;
                lues++;
                try {
                    importerLigne(parserLigne(ligne));
                    importees++;
                } catch (Exception e) {
                    erreurs.add("Ligne " + numeroLigne + " : " + e.getMessage());
                }
            }
        } catch (Exception e) {
            erreurs.add("Erreur de lecture du fichier : " + e.getMessage());
        }

        auditService.enregistrer("IMPORT", 0L, "IMPORT_CSV",
                importees + " client(s) importé(s), " + erreurs.size() + " erreur(s)");

        return new ImportResultatDTO(lues, importees, erreurs.size(), erreurs);
    }

    private ImportLigneDTO parserLigne(String ligne) {
        List<String> champs = new ArrayList<>();
        StringBuilder courant = new StringBuilder();
        boolean dansGuillemets = false;
        for (char c : ligne.toCharArray()) {
            if (c == '"') dansGuillemets = !dansGuillemets;
            else if (c == ',' && !dansGuillemets) { champs.add(courant.toString()); courant = new StringBuilder(); }
            else courant.append(c);
        }
        champs.add(courant.toString());

        if (champs.size() < 23) throw new IllegalArgumentException("Nombre de colonnes insuffisant (23 attendues, " + champs.size() + " trouvées)");

        return new ImportLigneDTO(
                champs.get(0).trim(), champs.get(1).trim(), vide(champs.get(2)), champs.get(3).trim(), vide(champs.get(4)),
                champs.get(5).trim(), champs.get(6).trim(), champs.get(7).trim(), champs.get(8).trim(),
                vide(champs.get(9)), vide(champs.get(10)), vide(champs.get(11)), champs.get(12).replace("\"", "").trim(),
                champs.get(13).trim(), champs.get(14).trim(), champs.get(15).trim(), champs.get(16).trim(),
                champs.get(17).trim(), vide(champs.get(18)), vide(champs.get(19)),
                champs.get(20).trim(), vide(champs.get(21)), champs.get(22).trim()
        );
    }

    private String vide(String s) { return s == null || s.isBlank() ? null : s.trim(); }

    @Transactional
    public void importerLigne(ImportLigneDTO l) {
        if (l.cin() == null || l.cin().isBlank()) throw new IllegalArgumentException("CIN manquant");

        Client client = clientRepository.findAllByIdentifiants_NumeroAndIdentifiants_TypeIdentifiant(l.cin(), "CIN")
                .stream().findFirst()
                .orElseGet(() -> creerClient(l));

        Contrat contrat = Contrat.builder()
                .client(client)
                .codeContratCb(l.codeContratCb())
                .typeContrat(l.typeContrat())
                .roleClient(RoleClient.valueOf(l.roleClient().toUpperCase()))
                .modeRattachement(ModeRattachement.NOUVELLE_DEMANDE)
                .phaseDemande(PhaseDemande.valueOf(l.phaseDemande().toUpperCase()))
                .dateDemande(LocalDate.parse(l.dateDemande()))
                .dateDebutContrat(l.dateDebutContrat() != null ? LocalDate.parse(l.dateDebutContrat()) : null)
                .dateFinContrat(l.dateFinContrat() != null ? LocalDate.parse(l.dateFinContrat()) : null)
                .montantFinance(new BigDecimal(l.montantFinance()))
                .montantTotalDu(new BigDecimal(l.montantFinance()))
                .montantEcheanceMensuelle(l.montantEcheanceMensuelle() != null ? new BigDecimal(l.montantEcheanceMensuelle()) : null)
                .nombreTotalEcheances(1)
                .devise(l.devise())
                .build();
        contratRepository.save(contrat);
    }

    private Client creerClient(ImportLigneDTO l) {
        Client client = Client.builder()
                .codeClientCb("L" + String.format("%08d", (long) (Math.random() * 100_000_000)))
                .titre(l.titre()).prenom(l.prenom()).deuxiemePrenom(l.deuxiemePrenom()).nom(l.nom())
                .dateNaissance(LocalDate.parse(l.dateNaissance()))
                .villeNaissance(l.villeNaissance()).paysNaissance(l.paysNaissance())
                .genre(Genre.valueOf(l.genre().toUpperCase()))
                .nationalite(l.nationalite()).categorieTiersCode(l.categorieTiersCode())
                .telephone(l.telephone())
                .build();
        Client sauvegarde = clientRepository.save(client);

        Adresse adresse = Adresse.builder()
                .client(sauvegarde).typeAdresse("Individu - Adresse principale")
                .adresseComplete(l.adresseComplete() != null ? l.adresseComplete() : "-")
                .actuelle(true).build();
        Identifiant identifiant = Identifiant.builder().client(sauvegarde).typeIdentifiant("CIN").numero(l.cin()).build();
        sauvegarde.setAdresses(List.of(adresse));
        sauvegarde.setIdentifiants(List.of(identifiant));
        return clientRepository.save(sauvegarde);
    }
}