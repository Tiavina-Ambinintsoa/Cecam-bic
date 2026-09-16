-- ============================================================================
-- BIC CECAM — Jeu de référence CRIF : les DEUX clients des rapports fournis
-- ============================================================================
--
-- Fichier à placer dans bic-backend/src/main/resources/data-crif.sql
-- puis à déclarer dans application.yml :
--     spring.sql.init.data-locations: classpath:data.sql, classpath:data-crif.sql
--
-- Ou à exécuter directement en psql. IDEMPOTENT : purge puis réinsère.
--
--  Client 1 — CHRISTIAN RADERA         (CreditReport_C00130366.pdf, 14/09/2026)
--             client avec historique -> « Client Trouvé », score calculé
--  Client 2 — JULIETTE RASOAMIARIMBOLANA (223-2521, 01/09/2026)
--             client sans historique -> « Client Introuvable, Client
--             Nouvellement Créé », score non calculé
--
-- Les deux couvrent les deux branches du rapport : c'est le jeu de
-- non-régression minimal du projet.
--
-- PRÉREQUIS : les colonnes ajoutées aux entités doivent exister. Avec
-- spring.jpa.hibernate.ddl-auto=update elles sont créées au démarrage.
-- Les séquences de codes CB doivent exister :
CREATE SEQUENCE IF NOT EXISTS seq_code_contrat_cb START WITH 700000001;
CREATE SEQUENCE IF NOT EXISTS seq_code_client_cb  START WITH 190001;


-- ============================================================================
-- 0. PURGE — y compris le doublon fantôme « CHRISTIAN RADERA » de data.sql
-- ============================================================================
DELETE FROM echeance    WHERE contrat_id IN (9701,9702,9703,9704,9705,9801,9956);
DELETE FROM garantie    WHERE contrat_id IN (9701,9702,9703,9704,9705,9801,9956);
DELETE FROM contrat     WHERE id         IN (9701,9702,9703,9704,9705,9801,9956);
DELETE FROM identifiant WHERE client_id  IN (9007,9008,9956);
DELETE FROM adresse     WHERE client_id  IN (9007,9008,9956);
DELETE FROM emploi      WHERE client_id  IN (9007,9008,9956);
DELETE FROM client      WHERE id         IN (9007,9008,9956);


-- ############################################################################
-- CLIENT 1 — CHRISTIAN RADERA — C00130366
-- ############################################################################
--
--   CRIF                       | ici  | phase            | rôle dans le test
--   ---------------------------|------|------------------|---------------------
--   300767570 (demande saisie) | 9705 | DEMANDE_EN_COURS | point d'entrée
--   L00730033                  | 9701 | DEMANDE_EN_COURS | historique
--   T00728181                  | 9702 | ACTIF            | historique
--   500562397                  | 9703 | FERME/FIN_A_TERME| historique
--   I00568335                  | 9704 | FERME/FIN_A_TERME| historique
--
--   -> GET /api/rapports/contrat/9705 reproduit le contexte CRIF :
--      4 contrats en base, Demandé 1 / Actif 1 / Fermé 2.

INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance,
                    ville_naissance, pays_naissance, genre, nationalite,
                    etat_civil, telephone, date_adhesion, date_derniere_modification)
VALUES (9007, 'C00130366', 'M.', '0004',
        'CHRISTIAN', NULL, 'RADERA', '1975-06-29',
        'ANJOMA', 'MADAGASCAR', 'HOMME', 'Malgache',
        NULL, '0386843401', '2023-06-02', '2026-03-24 00:00:00');

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete,
                     numero_rue, code_postal, ville, commune, region, pays,
                     actuelle, date_derniere_modification)
VALUES
 (9007, 9007, 'Individu - Adresse Principale', 'MORAFENO',
  NULL, NULL, NULL, NULL, NULL, NULL, true,  '2026-03-24 00:00:00'),
 (9008, 9007, 'Individu - Adresse Principale', 'MORAFENO FKT AMBATOMENA',
  NULL, NULL, NULL, NULL, NULL, NULL, false, '2023-07-31 00:00:00');

INSERT INTO identifiant (id, client_id, type_identifiant, numero)
VALUES (9007, 9007, 'CIN', '205091001976');

INSERT INTO emploi (id, client_id, statut_emploi, nom_employeur, profession,
                    date_embauche, revenu_annuel_total, devise)
VALUES (9007, 9007, NULL, NULL, 'Planteur de riz', NULL, 7200000, 'Ariary malgache');


-- --- 9701 — L00730033 — demande en cours, jamais décaissée ------------------
-- Co-titulaire CRIF : GEORGETTE RAVAORISOA (N00172305). Non reproductible :
-- le modèle n'a pas de table de liaison contrat <-> clients.
INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9701, 'L00730033', '001', 9007, 'NOUVELLE_DEMANDE',
        'Prêt Personnel', 'TITULAIRE', '2026-03-16',
        2500000, 2500000, NULL, 2,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-03-16 00:00:00');


-- --- 9702 — T00728181 — actif, aucun incident -------------------------------
-- VARIANTE A (par défaut) : capital pur, 6 x 250 000.
-- La variante fidèle aux intérêts déclarés par CRIF est en fin de fichier.
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement,
                     phase_demande, date_derniere_modification)
VALUES (9702, 'T00728181', '0202802034015001', '001',
        9007, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
        '2026-03-16', '2026-03-27', '2026-04-27', '2026-09-27',
        1500000, 1500000, 250000, 6, 'Ariary malgache', 'MENSUELLE',
        'ACTIF', '2026-05-31 00:00:00');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance,
                      montant_du, montant_paye, date_paiement, statut) VALUES
 (970201, 9702, 1, '2026-04-27', 250000, 250000, '2026-04-27', 'PAYE_A_TEMPS'),
 (970202, 9702, 2, '2026-05-27', 250000, 250000, '2026-05-27', 'PAYE_A_TEMPS'),
 (970203, 9702, 3, '2026-06-27', 250000, 250000, '2026-06-25', 'PAYE_A_TEMPS'),
 (970204, 9702, 4, '2026-07-27', 250000, 250000, '2026-07-27', 'PAYE_A_TEMPS'),
 (970205, 9702, 5, '2026-08-27', 250000, 250000, '2026-08-27', 'PAYE_A_TEMPS'),
 (970206, 9702, 6, '2026-09-27', 250000, NULL,   NULL,         'A_VENIR');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie,
                      nom_garant, code_client_cb_garant, montant_couvert,
                      date_debut_validite, date_fin_validite)
VALUES (9702, 9702, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel',
        'NAM11327 (5)', 'CHRISTIAN RADERA', 'C00130366', 2600000, NULL, NULL);


-- --- 9703 — 500562397 — soldé au terme --------------------------------------
-- CRIF déclare une échéance de 214 334 sur 5 périodes, soit 1 071 670 pour
-- 1 089 000 financés. Le reliquat de 17 330 est porté par la dernière échéance,
-- conformément à la correction apportée à genererEcheances().
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement,
                     phase_demande, motif_cloture, date_derniere_modification)
VALUES (9703, '500562397', '0202802034012001', '001',
        9007, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE',
        '2023-06-02', '2023-06-02', '2023-07-28', '2023-11-23',
        1089000, 1089000, 214334, 5, 'Ariary malgache', 'MENSUELLE',
        'FERME', 'FIN_A_TERME', '2023-11-30 00:00:00');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance,
                      montant_du, montant_paye, date_paiement, statut) VALUES
 (970301, 9703, 1, '2023-07-28', 214334, 214334, '2023-07-28', 'PAYE_A_TEMPS'),
 (970302, 9703, 2, '2023-08-28', 214334, 214334, '2023-08-28', 'PAYE_A_TEMPS'),
 (970303, 9703, 3, '2023-09-28', 214334, 214334, '2023-09-28', 'PAYE_A_TEMPS'),
 (970304, 9703, 4, '2023-10-28', 214334, 214334, '2023-10-28', 'PAYE_A_TEMPS'),
 (970305, 9703, 5, '2023-11-28', 231664, 231664, '2023-11-23', 'PAYE_A_TEMPS');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie,
                      nom_garant, code_client_cb_garant, montant_couvert,
                      date_debut_validite, date_fin_validite)
VALUES (9703, 9703, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel',
        'NAN4535 (60)', 'CHRISTIAN RADERA', 'C00130366', 1089000, NULL, NULL);


-- --- 9704 — I00568335 — soldé au terme --------------------------------------
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement,
                     phase_demande, motif_cloture, date_derniere_modification)
VALUES (9704, 'I00568335', '0202802034012002', '001',
        9007, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE',
        '2023-06-02', '2023-06-02', '2023-07-28', '2023-11-20',
        264000, 264000, 51827, 5, 'Ariary malgache', 'MENSUELLE',
        'FERME', 'FIN_A_TERME', '2023-11-30 00:00:00');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance,
                      montant_du, montant_paye, date_paiement, statut) VALUES
 (970401, 9704, 1, '2023-07-28', 51827, 51827, '2023-07-28', 'PAYE_A_TEMPS'),
 (970402, 9704, 2, '2023-08-28', 51827, 51827, '2023-08-28', 'PAYE_A_TEMPS'),
 (970403, 9704, 3, '2023-09-28', 51827, 51827, '2023-09-28', 'PAYE_A_TEMPS'),
 (970404, 9704, 4, '2023-10-28', 51827, 51827, '2023-10-28', 'PAYE_A_TEMPS'),
 (970405, 9704, 5, '2023-11-28', 56692, 56692, '2023-11-20', 'PAYE_A_TEMPS');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie,
                      nom_garant, code_client_cb_garant, montant_couvert,
                      date_debut_validite, date_fin_validite)
VALUES (9704, 9704, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel',
        'NAN4536 (55)', 'CHRISTIAN RADERA', 'C00130366', 264000, NULL, NULL);


-- --- 9705 — 300767570 — LA DEMANDE SAISIE du rapport CRIF --------------------
INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9705, '300767570', '001', 9007, 'DEMANDE_EXISTANTE',
        'Prêt Personnel', 'TITULAIRE', '2026-09-14',
        1500000, 1500000, NULL, 3,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-09-14 00:00:00');


-- ############################################################################
-- CLIENT 2 — JULIETTE RASOAMIARIMBOLANA — L00180894
-- ############################################################################
--
-- Rapport CRIF du 01/09/2026 : cliente créée le jour même, aucun contrat en
-- base, score non calculé. C'est la branche « client neuf » du rapport.
--
-- Points de contrôle que ce client valide :
--   - statut  : « Client Introuvable, Client Nouvellement Créé »
--   - score   : non calculé, message « Ce client a été nouvellement créé... »
--   - chiffres: Nombre Total de Contrat 0, Établissements Déclarants 0,
--               Restant dû « - », Impayés « - », Exposition « - »,
--               Réciprocité « - », MAIS Demandes 0 et Garantie Signature 0
--   - répartition : quatre lignes à zéro
--
-- C'est ce client qui prouve que « Montant Total Demandes » ne compte PAS la
-- demande saisie : CRIF affiche 0 alors que la demande vaut 1 500 000.
--
--   -> GET /api/rapports/contrat/9801

INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance,
                    ville_naissance, pays_naissance, genre, nationalite,
                    etat_civil, telephone, date_adhesion, date_derniere_modification)
VALUES (9008, 'L00180894', NULL, '0004',
        'JULIETTE', NULL, 'RASOAMIARIMBOLANA', '1976-10-01',
        NULL, NULL, 'FEMME', 'Malgache',
        NULL, NULL, '2026-09-01', '2026-09-01 00:00:00');

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete,
                     numero_rue, code_postal, ville, commune, region, pays,
                     actuelle, date_derniere_modification)
VALUES (9009, 9008, 'Individu - Adresse Principale', 'AMBATOLAHY VOHIPOSA',
        NULL, NULL, NULL, NULL, NULL, NULL, true, '2026-09-01 00:00:00');

INSERT INTO identifiant (id, client_id, type_identifiant, numero)
VALUES (9008, 9008, 'CIN', '208172002983');

-- Pas d'emploi déclaré : CRIF n'affiche aucune section Emploi pour elle.
-- Le score reste non calculable, donc l'absence de revenu n'a pas d'incidence.

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9801, '600748775', '001', 9008, 'NOUVELLE_DEMANDE',
        'Prêt Personnel', 'TITULAIRE', '2026-09-01',
        1500000, 1500000, NULL, 3,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-09-01 00:00:00');


-- ============================================================================
-- SÉQUENCES — impérativement en dernier, sinon le prochain client créé depuis
-- l'application entre en collision d'id avec Christian Radera.
-- ============================================================================
SELECT setval(pg_get_serial_sequence('client',     'id'), (SELECT MAX(id) FROM client));
SELECT setval(pg_get_serial_sequence('adresse',    'id'), (SELECT MAX(id) FROM adresse));
SELECT setval(pg_get_serial_sequence('identifiant','id'), (SELECT MAX(id) FROM identifiant));
SELECT setval(pg_get_serial_sequence('emploi',     'id'), (SELECT MAX(id) FROM emploi));
SELECT setval(pg_get_serial_sequence('contrat',    'id'), (SELECT MAX(id) FROM contrat));
SELECT setval(pg_get_serial_sequence('echeance',   'id'), (SELECT MAX(id) FROM echeance));
SELECT setval(pg_get_serial_sequence('garantie',   'id'), (SELECT MAX(id) FROM garantie));


-- ============================================================================
-- RÉSULTATS ATTENDUS avec le code corrigé et la calibration par défaut
-- ============================================================================
--
-- === JULIETTE (contrat 9801), au 15/09/2026 ===
--   Statut                    : Client Introuvable, Client Nouvellement Créé   [= CRIF]
--   Score                     : non calculé                                     [= CRIF]
--   Nombre Total de Contrat   : 0                                               [= CRIF]
--   Établissements Déclarants : 0                                               [= CRIF]
--   Restant dû / Impayés / Exposition / Réciprocité : « - »                     [= CRIF]
--   Montant Total Demandes    : 0                                               [= CRIF]
--   Total Garantie Signature  : 0                                               [= CRIF]
--   Répartition               : 4 lignes à zéro                                 [= CRIF]
--   -> reproduction ligne à ligne du modèle CRIF.
--
-- === CHRISTIAN RADERA (contrat 9705), au 15/09/2026 ===
--   Statut                    : Client Trouvé                                   [= CRIF]
--   Nombre Total de Contrat   : 4                                               [= CRIF]
--   Répartition               : Demandé 1 / Actif 1 / Fermé 2                    [= CRIF]
--   Établissements Déclarants : 1                                               [= CRIF]
--   Montant Total Demandes    : 2 500 000                                        [= CRIF]
--   Montant Total Impayés     : 0                                                [= CRIF]
--   Garantie par Signature    : 0  (les 3 nantissements sont des garanties réelles) [= CRIF]
--   Montant Total Restant dû  : 250 000     — CRIF 1 717 875 (intérêts, variante B)
--   Exposition Potentielle    : 250 000     — idem
--
--   Score attendu : 499  /  intervalle C  /  « Risque moyen »
--   CRIF          : 480  /  intervalle C  /  « Risque Moyen »
--   -> MÊME CATÉGORIE DE RISQUE, 19 points d'écart.
--
--   Décomposition (visible dans le rapport) :
--     Paiement            141,67   15 échéances échues, qualité 100 %
--     Endettement           0,00   charge 750 000 / revenu 600 000 = 1,25
--     Exposition           31,94   engagement 4 250 000 / revenu annuel = 0,59
--     Ancienneté           27,86   39 mois sur 84
--     Nouveaux crédits     12,00   2 contrats récents, 1 demande dormante
--     Mixité               18,00   2 types de contrat
--     Malus surendettement -33,00  (1,25 - 0,70) x 60
--     Total = 300 + 141,67 + 89,80 - 33,00 = 498,5 -> 499
--
--
-- ============================================================================
-- OPTIONNEL — VARIANTE B : intérêts déclarés par CRIF sur T00728181
-- ============================================================================
-- CRIF déclare pour ce contrat un restant dû de 1 717 875 sur 1 500 000
-- financés, réparti sur 2 échéances dont la prochaine à 914 500. Le schéma
-- corrigé sait désormais porter cette distinction via montant_total_du.
--
-- Lecture cohérente des données CRIF : les lignes Mars/Avr/Mai sont des
-- DÉCLARATIONS MENSUELLES d'établissement, pas des échéances — la première
-- échéance est au 28/06/2026. Les dates sont décalées pour que l'état
-- « 2 échéances restantes, 0 impayé » soit vrai à la date du test.
--
-- Score attendu dans cette variante : nettement plus bas (E), car seules 10
-- échéances sont observables et la charge mensuelle passe à 858 938 pour un
-- revenu mensuel de 600 000. Ce n'est pas une régression : c'est le modèle
-- qui réagit correctement à un taux d'endettement de 226 %. Utilisez cette
-- variante pour démontrer la sensibilité du score à la donnée d'entrée.
--
-- UPDATE contrat SET montant_total_du = 1717875,
--                    montant_echeance_mensuelle = 858938,
--                    nombre_total_echeances = 2,
--                    date_premiere_echeance = '2026-09-28',
--                    date_fin_contrat = '2026-10-28'
--  WHERE id = 9702;
-- DELETE FROM echeance WHERE contrat_id = 9702;
-- INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance,
--                       montant_du, montant_paye, date_paiement, statut) VALUES
--  (970211, 9702, 1, '2026-09-28', 914500, NULL, NULL, 'A_VENIR'),
--  (970212, 9702, 2, '2026-10-28', 803375, NULL, NULL, 'A_VENIR');
-- SELECT setval(pg_get_serial_sequence('echeance','id'), (SELECT MAX(id) FROM echeance));