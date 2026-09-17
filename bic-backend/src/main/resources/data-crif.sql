CREATE SEQUENCE IF NOT EXISTS seq_code_contrat_cb START WITH 700000001;
CREATE SEQUENCE IF NOT EXISTS seq_code_client_cb  START WITH 190001;

DELETE FROM echeance    WHERE contrat_id IN (9701,9702,9703,9704,9705,9801,9956);
DELETE FROM garantie    WHERE contrat_id IN (9701,9702,9703,9704,9705,9801,9956);
DELETE FROM contrat     WHERE id         IN (9701,9702,9703,9704,9705,9801,9956);
DELETE FROM identifiant WHERE client_id  IN (9007,9008,9956);
DELETE FROM adresse     WHERE client_id  IN (9007,9008,9956);
DELETE FROM emploi      WHERE client_id  IN (9007,9008,9956);
DELETE FROM client      WHERE id         IN (9007,9008,9956);

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

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9701, 'L00730033', '001', 9007, 'NOUVELLE_DEMANDE',
        'Prêt Personnel', 'TITULAIRE', '2026-03-16',
        2500000, 2500000, NULL, 2,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-03-16 00:00:00');

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

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9705, '300767570', '001', 9007, 'DEMANDE_EXISTANTE',
        'Prêt Personnel', 'TITULAIRE', '2026-09-14',
        1500000, 1500000, NULL, 3,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-09-14 00:00:00');

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

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9801, '600748775', '001', 9008, 'NOUVELLE_DEMANDE',
        'Prêt Personnel', 'TITULAIRE', '2026-09-01',
        1500000, 1500000, NULL, 3,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-09-01 00:00:00');

SELECT setval(pg_get_serial_sequence('client',     'id'), (SELECT MAX(id) FROM client));
SELECT setval(pg_get_serial_sequence('adresse',    'id'), (SELECT MAX(id) FROM adresse));
SELECT setval(pg_get_serial_sequence('identifiant','id'), (SELECT MAX(id) FROM identifiant));
SELECT setval(pg_get_serial_sequence('emploi',     'id'), (SELECT MAX(id) FROM emploi));
SELECT setval(pg_get_serial_sequence('contrat',    'id'), (SELECT MAX(id) FROM contrat));
SELECT setval(pg_get_serial_sequence('echeance',   'id'), (SELECT MAX(id) FROM echeance));
SELECT setval(pg_get_serial_sequence('garantie',   'id'), (SELECT MAX(id) FROM garantie));