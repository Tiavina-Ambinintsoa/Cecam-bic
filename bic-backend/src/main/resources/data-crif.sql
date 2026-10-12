-- ============================================================================
-- DONNÉES CRIF OFFICIELLES REPRODUITES POUR LE BIC CECAM FIANARANTSOA
-- Clients : Berthine, Hervé, Marcel, Emmanuel, Germain, Maurice, Georgette, Christian, Juliette
-- ============================================================================

CREATE SEQUENCE IF NOT EXISTS seq_code_contrat_cb START WITH 700000001;
CREATE SEQUENCE IF NOT EXISTS seq_code_client_cb  START WITH 200001;

-- Nettoyage préventif
DELETE FROM echeance WHERE contrat_id IN (
    SELECT id FROM contrat WHERE client_id IN (
        SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
        OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
    ) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515
    OR code_contrat_cb IN ('F00728095','200647123','600508331','100471980','D00452133','S00441337',
                           'S00725748','500526717','300528701','M00515959',
                           '700735293','100655194','T00611864','D00562708',
                           '800727695','S00706971','J00719389',
                           'L00730033','T00728181','500562397','I00568335','300767570','600748775')
    OR id BETWEEN 9511 AND 9560 OR id BETWEEN 9701 AND 9815
);

DELETE FROM historique_paiement_mensuel WHERE contrat_id IN (
    SELECT id FROM contrat WHERE client_id IN (
        SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
        OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
    ) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515
    OR code_contrat_cb IN ('F00728095','200647123','600508331','100471980','D00452133','S00441337',
                           'S00725748','500526717','300528701','M00515959',
                           '700735293','100655194','T00611864','D00562708',
                           '800727695','S00706971','J00719389',
                           'L00730033','T00728181','500562397','I00568335','300767570','600748775')
    OR id BETWEEN 9511 AND 9560 OR id BETWEEN 9701 AND 9815
);

DELETE FROM garantie WHERE contrat_id IN (
    SELECT id FROM contrat WHERE client_id IN (
        SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
        OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
    ) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515
    OR code_contrat_cb IN ('F00728095','200647123','600508331','100471980','D00452133','S00441337',
                           'S00725748','500526717','300528701','M00515959',
                           '700735293','100655194','T00611864','D00562708',
                           '800727695','S00706971','J00719389',
                           'L00730033','T00728181','500562397','I00568335','300767570','600748775')
    OR id BETWEEN 9511 AND 9560 OR id BETWEEN 9701 AND 9815
);

DELETE FROM bien_leasing WHERE contrat_id IN (
    SELECT id FROM contrat WHERE client_id IN (
        SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
        OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
    ) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515
);

DELETE FROM demande_modification_contrat WHERE contrat_id IN (
    SELECT id FROM contrat WHERE client_id IN (
        SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
        OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
    ) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515
    OR code_contrat_cb IN ('F00728095','200647123','600508331','100471980','D00452133','S00441337',
                           'S00725748','500526717','300528701','M00515959',
                           '700735293','100655194','T00611864','D00562708',
                           '800727695','S00706971','J00719389',
                           'L00730033','T00728181','500562397','I00568335','300767570','600748775')
    OR id BETWEEN 9511 AND 9560 OR id BETWEEN 9701 AND 9815
);

DELETE FROM contrat_lien WHERE contrat_id IN (
    SELECT id FROM contrat WHERE client_id IN (
        SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
        OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
    ) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515
    OR code_contrat_cb IN ('F00728095','200647123','600508331','100471980','D00452133','S00441337',
                           'S00725748','500526717','300528701','M00515959',
                           '700735293','100655194','T00611864','D00562708',
                           '800727695','S00706971','J00719389',
                           'L00730033','T00728181','500562397','I00568335','300767570','600748775')
    OR id BETWEEN 9511 AND 9560 OR id BETWEEN 9701 AND 9815
) OR client_lie_id IN (
    SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
    OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
) OR client_lie_id BETWEEN 9007 AND 9015 OR client_lie_id BETWEEN 9501 AND 9515;

DELETE FROM contrat WHERE client_id IN (
    SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
    OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515
   OR code_contrat_cb IN ('F00728095','200647123','600508331','100471980','D00452133','S00441337',
                          'S00725748','500526717','300528701','M00515959',
                          '700735293','100655194','T00611864','D00562708',
                          '800727695','S00706971','J00719389',
                          'L00730033','T00728181','500562397','I00568335','300767570','600748775')
   OR id BETWEEN 9511 AND 9560 OR id BETWEEN 9701 AND 9815;

DELETE FROM adresse_affaire_individuelle WHERE affaire_individuelle_id IN (
    SELECT id FROM affaire_individuelle WHERE client_id IN (
        SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
        OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
    ) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515
);

DELETE FROM affaire_individuelle WHERE client_id IN (
    SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
    OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515;

DELETE FROM identifiant WHERE client_id IN (
    SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
    OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515
   OR numero IN ('220112006302','220251009756','220251001639','220111001265','111131000616','208051004455','218031004321','205091001976','208172002983');

DELETE FROM adresse WHERE client_id IN (
    SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
    OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515
   OR id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515;

DELETE FROM emploi WHERE client_id IN (
    SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
    OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
) OR client_id BETWEEN 9007 AND 9015 OR client_id BETWEEN 9501 AND 9515
   OR id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515;

DELETE FROM journal_audit WHERE (entite = 'CLIENT' AND (
    entite_id IN (
        SELECT id FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
        OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894')
    ) OR entite_id BETWEEN 9007 AND 9015 OR entite_id BETWEEN 9501 AND 9515
)) OR id BETWEEN 950001 AND 950020;

DELETE FROM client WHERE id BETWEEN 9007 AND 9015 OR id BETWEEN 9501 AND 9515
   OR code_client_cb IN ('500078959','C00094855','E00127416','D00170254','H00151494','700158713','N00172305','C00130366','L00180894');

-- ============================================================================
-- 1. CLIENT 9501 : BERTHINE RASOANANDRASANA (Rapport CRIF Officiel)
-- ============================================================================

INSERT INTO client (id, code_client_cb, code_client_etablissement, agence, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance,
                    genre, nationalite, etat_civil, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9501, '500078959', 'CE-050078959', '02000', 'Mme', '0004',
        'BERTHINE', NULL, 'RASOANANDRASANA', '1989-01-18',
        'AMBATOMAINTY', 'MADAGASCAR', 'FEMME', 'Malgache', 'Marié(e)', NULL,
        '2021-02-18', '2024-12-09 00:00:00', 'RAPPORT_CRIF');

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, numero_rue, code_postal, ville, commune, region, pays,
                     actuelle, date_derniere_modification)
VALUES
 (9510, 9501, 'Individu - Adresse Principale', 'MAHATSINJONY',  NULL, NULL, NULL, NULL, NULL, NULL, true,  '2024-12-09 00:00:00'),
 (9511, 9501, 'Individu - Adresse Principale', 'ALAKAMISY',      NULL, NULL, NULL, NULL, NULL, NULL, false, '2022-09-26 00:00:00'),
 (9512, 9501, 'Individu - Adresse Principale', 'AMPARIHILAVA',   NULL, NULL, NULL, NULL, NULL, NULL, false, '2022-02-08 00:00:00'),
 (9513, 9501, 'Individu - Adresse Principale', 'AMBATOMAINTY',   NULL, NULL, NULL, NULL, NULL, NULL, false, '2021-02-28 00:00:00');

INSERT INTO identifiant (id, client_id, type_identifiant, numero)
VALUES (9501, 9501, 'CIN', '220112006302');

INSERT INTO emploi (id, client_id, statut_emploi, nom_employeur, profession, date_embauche, revenu_annuel_total, devise)
VALUES (9501, 9501, 'Travailleur Autonome', NULL, 'Planteur de riz', NULL, 7000000, 'Ariary malgache');

-- 9511 : Demande saisie en cours (F00728095)
INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification, origine_donnee)
VALUES (9511, 'F00728095', '001', 9501, 'NOUVELLE_DEMANDE',
        'Prêt Personnel', 'TITULAIRE', '2026-02-16',
        3000000, 3000000, NULL, 5,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-02-16 00:00:00', 'RAPPORT_CRIF');

-- 9512 : Contrat 1 (200647123) - Fin à terme
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     date_dernier_reglement, montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement, mode_reglement,
                     phase_demande, motif_cloture, date_derniere_modification,
                     max_montant_impayes, max_nombre_echeances_impayees, date_max_nombre_echeances_impayees,
                     nombre_jours_retard, max_nombre_jours_retard, date_max_nombre_jours_retard, pire_statut, origine_donnee)
VALUES (9512, '200647123', '0202502736015005', '001',
        '001', 9501, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
        '2024-12-12', '2024-12-12', '2025-05-28', '2025-06-30',
        '2025-06-25', 650000, 789154, 119569,
        2, 'Ariary malgache', 'Mensuelle- 1 mois', 'Espèce',
        'FERME', 'FIN_A_TERME', '2025-06-30 00:00:00',
        0, 0, NULL,
        0, 0, '2025-06-30', 'No info', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
 (951201, 9512, 1, '2025-05-28', 452400, 452400, '2025-05-28', 'PAYE_A_TEMPS'),
 (951202, 9512, 2, '2025-06-30', 336754, 336754, '2025-06-25', 'PAYE_A_TEMPS');

INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9512, '2024-12-01', 0, 0, 0, 'OK'),
 (9512, '2025-01-01', 0, 0, 0, 'OK'),
 (9512, '2025-02-01', 0, 0, 0, 'OK'),
 (9512, '2025-03-01', 0, 0, 0, 'OK'),
 (9512, '2025-04-01', 0, 0, 0, 'OK'),
 (9512, '2025-05-01', 1, 0, 0, 'OK'),
 (9512, '2025-06-01', 1, 0, 0, 'OK');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES
 (95121, 9512, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAN7923 (42)', 'RAZAFIMANDIMBY MAURICE', '700158713', 800000),
 (95122, 9512, 'REELLE', 'Caution mutuelle', 'CAU7922 (1)', 'RAZAFIMANDIMBY MAURICE', '700158713', 1);

-- 9513 : Contrat 2 (600508331) - Fin à terme
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     date_dernier_reglement, montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement, mode_reglement,
                     phase_demande, motif_cloture, date_derniere_modification,
                     max_montant_impayes, max_nombre_echeances_impayees, date_max_nombre_echeances_impayees,
                     nombre_jours_retard, max_nombre_jours_retard, date_max_nombre_jours_retard, pire_statut, origine_donnee)
VALUES (9513, '600508331', '0202502736015004', '001',
        '001', 9501, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
        '2022-09-29', '2022-09-29', '2023-03-28', '2023-05-25',
        '2023-05-25', 1300000, 1628184, 202763,
        3, 'Ariary malgache', 'Mensuelle- 1 mois', 'Espèce',
        'FERME', 'FIN_A_TERME', '2023-05-31 00:00:00',
        0, 0, NULL,
        0, 0, '2023-05-31', 'No info', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
 (951301, 9513, 1, '2023-03-28', 574517, 574517, '2023-03-28', 'PAYE_A_TEMPS'),
 (951302, 9513, 2, '2023-04-28', 536167, 536167, '2023-04-28', 'PAYE_A_TEMPS'),
 (951303, 9513, 3, '2023-05-25', 517500, 517500, '2023-05-25', 'PAYE_A_TEMPS');

INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9513, '2022-09-01', 0, 0, 0, 'OK'),
 (9513, '2022-10-01', 0, 0, 0, 'OK'),
 (9513, '2022-11-01', 0, 0, 0, 'OK'),
 (9513, '2022-12-01', 0, 0, 0, 'OK'),
 (9513, '2023-01-01', 0, 0, 0, 'OK'),
 (9513, '2023-02-01', 0, 0, 0, 'OK'),
 (9513, '2023-03-01', 1, 0, 0, 'OK'),
 (9513, '2023-04-01', 1, 0, 0, 'OK'),
 (9513, '2023-05-01', 1, 0, 0, 'OK');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES
 (95131, 9513, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAM4008 (17)', 'BERTHINE RASOANANDRASANA', '500078959', 1600000);

-- 9514 : Contrat 3 (100471980) - Fin à terme
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     date_dernier_reglement, montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement, mode_reglement,
                     phase_demande, motif_cloture, date_derniere_modification,
                     max_montant_impayes, max_nombre_echeances_impayees, date_max_nombre_echeances_impayees,
                     nombre_jours_retard, max_nombre_jours_retard, date_max_nombre_jours_retard, pire_statut, origine_donnee)
VALUES (9514, '100471980', '0202502736015003', '001',
        '001', 9501, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
        '2022-02-18', '2022-02-18', '2022-06-30', '2022-07-21',
        '2022-07-21', 1200000, 1407900, 259282,
        2, 'Ariary malgache', 'Mensuelle- 1 mois', 'Espèce',
        'FERME', 'FIN_A_TERME', '2022-07-31 00:00:00',
        0, 0, NULL,
        0, 0, '2022-07-31', 'No info', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
 (951401, 9514, 1, '2022-06-30', 786200, 786200, '2022-06-29', 'PAYE_A_TEMPS'),
 (951402, 9514, 2, '2022-07-21', 621700, 621700, '2022-07-21', 'PAYE_A_TEMPS');

INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9514, '2022-02-01', 0, 0, 0, 'OK'),
 (9514, '2022-03-01', 0, 0, 0, 'OK'),
 (9514, '2022-04-01', 0, 0, 0, 'OK'),
 (9514, '2022-05-01', 0, 0, 0, 'OK'),
 (9514, '2022-06-01', 1, 0, 0, 'OK'),
 (9514, '2022-07-01', 1, 0, 0, 'OK');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES
 (95141, 9514, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAM3568 (18)', 'BERTHINE RASOANANDRASANA', '500078959', 2000000);

-- 9515 : Contrat 4 (D00452133) - Fin à terme
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     date_dernier_reglement, montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement, mode_reglement,
                     phase_demande, motif_cloture, date_derniere_modification,
                     max_montant_impayes, max_nombre_echeances_impayees, date_max_nombre_echeances_impayees,
                     nombre_jours_retard, max_nombre_jours_retard, date_max_nombre_jours_retard, pire_statut, origine_donnee)
VALUES (9515, 'D00452133', '0202502736015002', '001',
        '001', 9501, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
        '2021-07-29', '2021-07-29', '2022-01-31', '2022-01-31',
        '2022-01-31', 700000, 883167, 104372,
        3, 'Ariary malgache', 'Mensuelle- 1 mois', 'Espèce',
        'FERME', 'FIN_A_TERME', '2022-01-31 00:00:00',
        0, 0, NULL,
        0, 0, '2022-01-31', 'No info', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
 (951501, 9515, 1, '2022-01-31', 294389, 294389, '2022-01-31', 'PAYE_A_TEMPS'),
 (951502, 9515, 2, '2022-01-31', 294389, 294389, '2022-01-31', 'PAYE_A_TEMPS'),
 (951503, 9515, 3, '2022-01-31', 294389, 294389, '2022-01-31', 'PAYE_A_TEMPS');

INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9515, '2021-07-01', 0, 0, 0, 'OK'),
 (9515, '2021-08-01', 0, 0, 0, 'OK'),
 (9515, '2021-09-01', 0, 0, 0, 'OK'),
 (9515, '2021-10-01', 0, 0, 0, 'OK'),
 (9515, '2021-11-01', 0, 0, 0, 'OK'),
 (9515, '2021-12-01', 0, 0, 0, 'OK'),
 (9515, '2022-01-01', 1, 0, 0, 'OK');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES
 (95151, 9515, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAM3330 (20)', 'BERTHINE RASOANANDRASANA', '500078959', 2000000);

-- 9516 : Contrat 5 (S00441337) - Fin à terme
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     date_dernier_reglement, montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement, mode_reglement,
                     phase_demande, motif_cloture, date_derniere_modification,
                     max_montant_impayes, max_nombre_echeances_impayees, date_max_nombre_echeances_impayees,
                     nombre_jours_retard, max_nombre_jours_retard, date_max_nombre_jours_retard, pire_statut, origine_donnee)
VALUES (9516, 'S00441337', '0202502736015001', '001',
        '001', 9501, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
        '2021-02-18', '2021-02-18', '2021-06-30', '2021-06-17',
        '2021-06-17', 800000, 938600, 172855,
        2, 'Ariary malgache', 'Mensuelle- 1 mois', 'Espèce',
        'FERME', 'FIN_A_TERME', '2021-06-30 00:00:00',
        0, 0, NULL,
        0, 0, '2021-06-30', 'No info', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
 (951601, 9516, 1, '2021-06-30', 469300, 469300, '2021-06-17', 'PAYE_A_TEMPS'),
 (951602, 9516, 2, '2021-06-30', 469300, 469300, '2021-06-17', 'PAYE_A_TEMPS');

INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9516, '2021-02-01', 0, 0, 0, 'OK'),
 (9516, '2021-03-01', 0, 0, 0, 'OK'),
 (9516, '2021-04-01', 0, 0, 0, 'OK'),
 (9516, '2021-05-01', 0, 0, 0, 'OK'),
 (9516, '2021-06-01', 1, 0, 0, 'OK');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES
 (95161, 9516, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAM3038 (18)', 'BERTHINE RASOANANDRASANA', '500078959', 2000000);

-- ============================================================================
-- 2. CLIENT 9503 : HERVÉ RASAMBANIRINA (Rapport CRIF Officiel)
-- ============================================================================

INSERT INTO client (id, code_client_cb, code_client_etablissement, agence, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance,
                    genre, nationalite, etat_civil, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9503, 'C00094855', 'CE-000094855', '02000', 'M.', '0004',
        'HERVÉ', NULL, 'RASAMBANIRINA', '1988-07-21',
        NULL, NULL, 'FEMME', 'Malgache', NULL, NULL,
        '2018-11-22', '2025-06-10 00:00:00', 'RAPPORT_CRIF');

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, numero_rue, code_postal, ville, commune, region, pays,
                     actuelle, date_derniere_modification)
VALUES
 (9517, 9503, 'Individu - Adresse Principale', 'ALAKAMIISY-AMBOHIMAHA', NULL, NULL, NULL, NULL, NULL, NULL, true,  '2025-06-10 00:00:00'),
 (9518, 9503, 'Individu - Adresse Principale', 'SAFATA',                NULL, NULL, NULL, NULL, NULL, NULL, false, '2024-07-08 00:00:00'),
 (9519, 9503, 'Individu - Adresse Principale', 'TAMBOHOMANDRANO',       NULL, NULL, NULL, NULL, NULL, NULL, false, '2023-05-16 00:00:00'),
 (9520, 9503, 'Individu - Adresse Principale', 'LALANGINA',             NULL, NULL, NULL, NULL, NULL, NULL, false, '2018-11-22 00:00:00');

INSERT INTO identifiant (id, client_id, type_identifiant, numero)
VALUES (9503, 9503, 'CIN', '220251009756');

INSERT INTO affaire_individuelle (id, client_id, nom, date_derniere_modification)
VALUES (9503, 9503, 'CSGRMANIRYSAFATA', '2018-11-22 00:00:00');

INSERT INTO adresse_affaire_individuelle (id, affaire_individuelle_id, type_adresse, adresse_complete, actuelle, date_derniere_modification)
VALUES (9503, 9503, 'Entreprise - Adresse Principale', 'SAFATASAFATA', true, '2018-11-22 00:00:00');

-- 9531 : Demande saisie en cours (S00725748)
INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification, origine_donnee)
VALUES (9531, 'S00725748', '001', 9503, 'NOUVELLE_DEMANDE',
        'Prêt Personnel', 'TITULAIRE', '2026-04-13',
        6000000, 6000000, NULL, 5,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-04-13 00:00:00', 'RAPPORT_CRIF');

-- 9532 : Contrat historique 1 (500526717) - Fin à terme
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     date_dernier_reglement, montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement, mode_reglement,
                     phase_demande, motif_cloture, date_derniere_modification,
                     max_montant_impayes, max_nombre_echeances_impayees, date_max_nombre_echeances_impayees,
                     nombre_jours_retard, max_nombre_jours_retard, date_max_nombre_jours_retard, pire_statut, origine_donnee)
VALUES (9532, '500526717', '-', '002',
        '002', 9503, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'TITULAIRE',
        '2022-07-08', '2022-07-08', '2022-12-08', '2023-06-30',
        '2023-06-30', 2500000, 2500000, 500000,
        1, 'Ariary malgache', 'échéances chaque 5 mois', 'Espèce',
        'FERME', 'FIN_A_TERME', '2023-06-30 00:00:00',
        1340000, 1, '2023-05-31',
        0, 179, '2023-05-31', 'No info', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
 (953201, 9532, 1, '2022-12-08',   50000,   50000, '2023-02-10', 'EN_RETARD'),
 (953202, 9532, 2, '2022-12-08',  845000,  845000, '2023-03-15', 'EN_RETARD'),
 (953203, 9532, 3, '2022-12-08',  445000,  445000, '2023-06-30', 'EN_RETARD'),
 (953204, 9532, 4, '2023-06-30', 1160000, 1160000, '2023-06-30', 'PAYE_A_TEMPS');

INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9532, '2022-07-01', 0, 0, 0,   'NOUV'),
 (9532, '2022-08-01', 0, 0, 0,   'OK'),
 (9532, '2022-09-01', 0, 0, 0,   'OK'),
 (9532, '2022-10-01', 0, 0, 0,   'OK'),
 (9532, '2022-11-01', 0, 0, 0,   'OK'),
 (9532, '2022-12-01', 1, 1, 15,  '1-29'),
 (9532, '2023-01-01', 1, 1, 45,  '30+'),
 (9532, '2023-02-01', 1, 1, 75,  '60+'),
 (9532, '2023-03-01', 1, 1, 105, '90+'),
 (9532, '2023-04-01', 1, 1, 135, '120+'),
 (9532, '2023-05-01', 1, 1, 165, '150+'),
 (9532, '2023-06-01', 1, 0, 0,   'OK');

-- 9533 : Contrat historique 2 (300528701) - Fin à terme
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     date_dernier_reglement, montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement, mode_reglement,
                     phase_demande, motif_cloture, date_derniere_modification,
                     max_montant_impayes, max_nombre_echeances_impayees, date_max_nombre_echeances_impayees,
                     nombre_jours_retard, max_nombre_jours_retard, date_max_nombre_jours_retard, pire_statut, origine_donnee)
VALUES (9533, '300528701', '-', '002',
        '002', 9503, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'TITULAIRE',
        '2021-10-27', '2021-10-27', '2022-04-27', '2022-05-25',
        '2022-05-25', 2100000, 2100000, 350000,
        1, 'Ariary malgache', 'Semestriel-6 mois', 'Espèce',
        'FERME', 'FIN_A_TERME', '2022-05-31 00:00:00',
        2100000, 1, '2022-04-30',
        0, 28, '2022-04-30', 'No info', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
 (953301, 9533, 1, '2022-04-27', 2100000, 2100000, '2022-05-25', 'EN_RETARD');

INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9533, '2021-10-01', 0, 0, 0,  'NOUV'),
 (9533, '2021-11-01', 0, 0, 0,  'NOUV'),
 (9533, '2021-12-01', 0, 0, 0,  'NOUV'),
 (9533, '2022-01-01', 0, 0, 0,  'NOUV'),
 (9533, '2022-02-01', 0, 0, 0,  'NOUV'),
 (9533, '2022-03-01', 0, 0, 0,  'NOUV'),
 (9533, '2022-04-01', 1, 1, 28, '1-29'),
 (9533, '2022-05-01', 1, 0, 0,  'OK');

-- 9534 : Contrat historique 3 (M00515959) - Fin à terme
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     date_dernier_reglement, montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement, mode_reglement,
                     phase_demande, motif_cloture, date_derniere_modification,
                     max_montant_impayes, max_nombre_echeances_impayees, date_max_nombre_echeances_impayees,
                     nombre_jours_retard, max_nombre_jours_retard, date_max_nombre_jours_retard, pire_statut,
                     code_restructuration, origine_donnee)
VALUES (9534, 'M00515959', '-', '002',
        '002', 9503, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'TITULAIRE',
        '2020-10-01', '2020-10-01', '2021-01-29', '2021-08-19',
        '2021-08-19', 3300000, 3300000, 825000,
        1, 'Ariary malgache', 'Quadrimestriel - 4 mois', 'Espèce',
        'FERME', 'FIN_A_TERME', '2021-08-31 00:00:00',
        3300000, 1, '2021-07-31',
        0, 202, '2021-07-31', 'No info',
        'Aucune restructuration au cours de la période de référence', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
 (953401, 9534, 1, '2021-01-29',  999600,  999600, '2021-03-10', 'EN_RETARD'),
 (953402, 9534, 2, '2021-01-29', 1978400, 1978400, '2021-07-15', 'EN_RETARD'),
 (953403, 9534, 3, '2021-01-29',  322000,  322000, '2021-08-19', 'EN_RETARD');

INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9534, '2020-10-01', 0, 0, 0,   'NDIS'),
 (9534, '2020-11-01', 0, 0, 0,   'NDIS'),
 (9534, '2020-12-01', 0, 0, 0,   'NDIS'),
 (9534, '2021-01-01', 0, 0, 0,   'NDIS'),
 (9534, '2021-02-01', 0, 0, 0,   'NDIS'),
 (9534, '2021-03-01', 0, 0, 0,   'NDIS'),
 (9534, '2021-04-01', 1, 1, 105, '90+'),
 (9534, '2021-05-01', 1, 1, 135, '120+'),
 (9534, '2021-06-01', 1, 1, 165, '150+'),
 (9534, '2021-07-01', 1, 1, 202, '180+'),
 (9534, '2021-08-01', 1, 0, 0,   'OK');

-- ============================================================================
-- 3. CLIENT 9502 : MARCEL OLIVIER RANDRIANARISON
-- ============================================================================

INSERT INTO client (id, code_client_cb, code_client_etablissement, agence, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance,
                    genre, nationalite, etat_civil, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9502, 'E00127416', 'CE-000127416', '02000', NULL, '0004',
        'MARCEL OLIVIER', NULL, 'RANDRIANARISON', '1976-12-10',
        'ALAKAMISY AMBOHIMAHA', 'MADAGASCAR', 'HOMME', 'Malgache', NULL, NULL,
        '2023-08-24', '2025-02-18 00:00:00', 'RAPPORT_CRIF');

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, numero_rue, code_postal, ville, commune, region, pays,
                     actuelle, date_derniere_modification)
VALUES
 (9521, 9502, 'Individu - Adresse Principale', 'ALAKAMISY AMBOHIMAHA', NULL, NULL, NULL, NULL, NULL, NULL, true,  '2025-02-18 00:00:00'),
 (9522, 9502, 'Individu - Adresse Principale', 'IHATRAKA',             NULL, NULL, NULL, NULL, NULL, NULL, false, '2024-08-31 00:00:00'),
 (9523, 9502, 'Individu - Adresse Principale', 'VATOFOTSY',            NULL, NULL, NULL, NULL, NULL, NULL, false, '2023-07-08 00:00:00');

INSERT INTO identifiant (id, client_id, type_identifiant, numero)
VALUES (9502, 9502, 'CIN', '220251001639');

INSERT INTO emploi (id, client_id, statut_emploi, nom_employeur, profession, date_embauche, revenu_annuel_total, devise)
VALUES (9502, 9502, NULL, NULL, 'Planteur de riz', NULL, NULL, NULL);

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification, origine_donnee)
VALUES (9521, '700735293', '001', 9502, 'NOUVELLE_DEMANDE',
        'Prêt Personnel', 'TITULAIRE', '2026-02-18',
        1500000, 1500000, NULL, 2,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-02-18 00:00:00', 'RAPPORT_CRIF');

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement, mode_reglement,
                     phase_demande, date_derniere_modification, origine_donnee)
VALUES (9522, '100655194', '0202502978015003', '001',
        '001', 9502, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
        '2025-02-18', '2025-02-18', '2025-08-18', '2026-02-18',
        2000000, 2666283, 216244, 4,
        'Ariary malgache', 'MENSUELLE', 'Espèce',
        'ACTIF', '2025-08-18 00:00:00', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
 (952201, 9522, 1, '2025-08-18', 1043358, 1043358, '2025-08-18', 'PAYE_A_TEMPS'),
 (952202, 9522, 2, '2025-09-18', 1210000, 1210000, '2025-09-18', 'PAYE_A_TEMPS'),
 (952203, 9522, 3, '2025-10-18',  290000,  290000, '2025-10-18', 'PAYE_A_TEMPS'),
 (952204, 9522, 4, '2026-02-18',  122925,    NULL,         NULL, 'A_VENIR');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES
 (95221, 9522, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAN8125 (35)', 'MARCEL OLIVIER RANDRIANARISON', 'E00127416', 3800000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     date_dernier_reglement, montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement, mode_reglement,
                     phase_demande, motif_cloture, date_derniere_modification, origine_donnee)
VALUES (9523, 'T00611864', '0202502978015002', '001',
        '001', 9502, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
        '2024-04-09', '2024-04-09', '2024-11-28', '2025-02-28',
        '2025-02-11', 2000000, 2693816, 228871, 3,
        'Ariary malgache', 'MENSUELLE', 'Espèce',
        'FERME', 'FIN_A_TERME', '2025-02-28 00:00:00', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
 (952301, 9523, 1, '2024-10-15', 1146000, 1146000, '2024-10-15', 'PAYE_A_TEMPS'),
 (952302, 9523, 2, '2025-01-15', 1296363, 1296363, '2025-01-15', 'PAYE_A_TEMPS'),
 (952303, 9523, 3, '2025-02-11',  251453,  251453, '2025-02-11', 'PAYE_A_TEMPS');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES
 (95231, 9523, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAN6118 (49)', 'MARCEL OLIVIER RANDRIANARISON', 'E00127416', 3300000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     date_dernier_reglement, montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement, mode_reglement,
                     phase_demande, motif_cloture, date_derniere_modification, origine_donnee)
VALUES (9524, 'D00562708', '0202502978015001', '001',
        '001', 9502, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
        '2023-08-24', '2023-08-24', '2024-04-28', '2024-03-27',
        '2024-03-27', 1000000, 1326083, 121667, 2,
        'Ariary malgache', 'MENSUELLE', 'Espèce',
        'FERME', 'FIN_A_TERME', '2024-03-27 00:00:00', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
 (952401, 9524, 1, '2024-03-27', 663042, 663042, '2024-03-27', 'PAYE_A_TEMPS'),
 (952402, 9524, 2, '2024-03-27', 663041, 663041, '2024-03-27', 'PAYE_A_TEMPS');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES
 (95241, 9524, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAN4989 (61)', 'MARCEL OLIVIER RANDRIANARISON', 'E00127416', 1700000);

-- ============================================================================
-- 4. CLIENT 9504 : RAKOTONIRINA EMMANUEL
-- ============================================================================

INSERT INTO client (id, code_client_cb, code_client_etablissement, agence, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance,
                    genre, nationalite, etat_civil, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9504, 'D00170254', 'CE-000170254', '02000', NULL, '0004',
        'RAKOTONIRINA', NULL, 'EMMANUEL', '1974-09-07',
        'MAHATSINJONY', 'MADAGASCAR', 'HOMME', 'Malgache', NULL, NULL,
        '2025-12-01', '2025-12-01 00:00:00', 'RAPPORT_CRIF');

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, numero_rue, code_postal, ville, commune, region, pays,
                     actuelle, date_derniere_modification)
VALUES
 (9526, 9504, 'Individu - Adresse Principale', '220',                NULL, NULL, NULL, NULL, NULL, NULL, true,  '2025-12-01 00:00:00'),
 (9527, 9504, 'Individu - Adresse Principale', 'AMBATOLAHIHAMBANA', NULL, NULL, NULL, NULL, NULL, NULL, false, '2025-10-31 00:00:00');

INSERT INTO identifiant (id, client_id, type_identifiant, numero)
VALUES (9504, 9504, 'CIN', '220111001265');

INSERT INTO emploi (id, client_id, statut_emploi, nom_employeur, profession, date_embauche, revenu_annuel_total, devise)
VALUES (9504, 9504, NULL, NULL, 'Planteur de riz', NULL, NULL, NULL);

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification, origine_donnee)
VALUES
 (9541, '800727695', '001', 9504, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  '2026-02-18', 1000000, 1000000, NULL, 2, 'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-02-18 00:00:00', 'RAPPORT_CRIF'),
 (9542, 'S00706971', '002', 9504, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  '2025-12-01', 1000000, 1000000, NULL, 12, 'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2025-12-01 00:00:00', 'RAPPORT_CRIF');

-- ============================================================================
-- 5. CLIENT 9505 : GERMAIN RAMALIARISON
-- ============================================================================

INSERT INTO client (id, code_client_cb, code_client_etablissement, agence, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance,
                    genre, nationalite, etat_civil, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9505, 'H00151494', 'CE-000151494', '02000', NULL, '0004',
        'GERMAIN', NULL, 'RAMALIARISON', '1972-01-01',
        'AMBATOLAHY II', 'MADAGASCAR', 'HOMME', 'Malgache', NULL, NULL,
        '2024-11-30', '2024-11-30 00:00:00', 'RAPPORT_CRIF');

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification)
VALUES (9528, 9505, 'Individu - Adresse Principale', 'CAMP ROBIN', true, '2024-11-30 00:00:00');

INSERT INTO identifiant (id, client_id, type_identifiant, numero)
VALUES (9505, 9505, 'CIN', '111131000616');

INSERT INTO emploi (id, client_id, statut_emploi, nom_employeur, profession, date_embauche, revenu_annuel_total, devise)
VALUES (9505, 9505, NULL, NULL, 'Planteur de riz', NULL, NULL, NULL);

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification, origine_donnee)
VALUES (9551, 'J00719389', '001', 9505, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
        '2026-02-18', 1000000, 1000000, NULL, 2, 'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-02-18 00:00:00', 'RAPPORT_CRIF');

-- ============================================================================
-- 6. CLIENT 9506 : MAURICE RAZAFIMANDIMBY (Garant)
-- ============================================================================

INSERT INTO client (id, code_client_cb, code_client_etablissement, agence, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance,
                    genre, nationalite, etat_civil, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9506, '700158713', 'CE-700158713', '02000', 'M.', '0004',
        'MAURICE', NULL, 'RAZAFIMANDIMBY', '1968-05-12',
        NULL, 'MADAGASCAR', 'HOMME', 'Malgache', NULL, NULL,
        '2020-03-10', '2025-05-28 00:00:00', 'RAPPORT_CRIF');

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification)
VALUES (9529, 9506, 'Individu - Adresse Principale', 'AMBOHIMANGAKELY', true, '2025-05-28 00:00:00');

INSERT INTO identifiant (id, client_id, type_identifiant, numero)
VALUES (9506, 9506, 'CIN', '208051004455');

-- Lien garant sur contrat 9512 de Berthine
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id)
VALUES (9501, 9512, '700158713', 'RAZAFIMANDIMBY MAURICE', 'GARANT', 9506);

-- ============================================================================
-- 7. CLIENT 9507 : GEORGETTE RAVAORISOA (Co-titulaire)
-- ============================================================================

INSERT INTO client (id, code_client_cb, code_client_etablissement, agence, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance,
                    genre, nationalite, etat_civil, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9507, 'N00172305', 'CE-000172305', '02000', 'Mme', '0004',
        'GEORGETTE', NULL, 'RAVAORISOA', '1970-03-15',
        NULL, 'MADAGASCAR', 'FEMME', 'Malgache', NULL, NULL,
        '2023-01-10', '2026-03-16 00:00:00', 'RAPPORT_CRIF');

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification)
VALUES (9530, 9507, 'Individu - Adresse Principale', 'AMBOHIJATOVO', true, '2026-03-16 00:00:00');

INSERT INTO identifiant (id, client_id, type_identifiant, numero)
VALUES (9507, 9507, 'CIN', '218031004321');

-- ============================================================================
-- 8. CLIENT 9007 : CHRISTIAN RADERA
-- ============================================================================

INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance,
                    ville_naissance, pays_naissance, genre, nationalite,
                    etat_civil, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9007, 'C00130366', 'M.', '0004',
        'CHRISTIAN', NULL, 'RADERA', '1975-06-29',
        'ANJOMA', 'MADAGASCAR', 'HOMME', 'Malgache',
        NULL, '0386843401', '2023-06-02', '2026-03-24 00:00:00', 'RAPPORT_CRIF');

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
                     devise, periodicite_paiement, phase_demande, date_derniere_modification, origine_donnee)
VALUES (9701, 'L00730033', '001', 9007, 'NOUVELLE_DEMANDE',
        'Prêt Personnel', 'TITULAIRE', '2026-03-16',
        2500000, 2500000, NULL, 2,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-03-16 00:00:00', 'RAPPORT_CRIF');

-- Co-titulaire Georgette sur 9701
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id)
VALUES (9502, 9701, 'N00172305', 'GEORGETTE RAVAORISOA', 'CO_TITULAIRE', 9507);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement,
                     phase_demande, date_derniere_modification, origine_donnee)
VALUES (9702, 'T00728181', '0202802034015001', '001',
        9007, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
        '2026-03-16', '2026-03-27', '2026-04-27', '2026-09-27',
        1500000, 1500000, 250000, 6, 'Ariary malgache', 'MENSUELLE',
        'ACTIF', '2026-05-31 00:00:00', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance,
                      montant_du, montant_paye, date_paiement, statut) VALUES
 (970201, 9702, 1, '2026-04-27', 250000, 250000, '2026-04-27', 'PAYE_A_TEMPS'),
 (970202, 9702, 2, '2026-05-27', 250000, 250000, '2026-05-27', 'PAYE_A_TEMPS'),
 (970203, 9702, 3, '2026-06-27', 250000, 250000, '2026-06-25', 'PAYE_A_TEMPS'),
 (970204, 9702, 4, '2026-07-27', 250000, 250000, '2026-07-27', 'PAYE_A_TEMPS'),
 (970205, 9702, 5, '2026-08-27', 250000, 250000, '2026-08-27', 'PAYE_A_TEMPS'),
 (970206, 9702, 6, '2026-09-27', 250000, NULL,   NULL,         'A_VENIR');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie,
                      nom_garant, code_client_cb_garant, montant_couvert)
VALUES (9702, 9702, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel',
        'NAM11327 (5)', 'CHRISTIAN RADERA', 'C00130366', 2600000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement,
                     phase_demande, motif_cloture, date_derniere_modification, origine_donnee)
VALUES (9703, '500562397', '0202802034012001', '001',
        9007, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE',
        '2023-06-02', '2023-06-02', '2023-07-28', '2023-11-23',
        1089000, 1089000, 214334, 5, 'Ariary malgache', 'MENSUELLE',
        'FERME', 'FIN_A_TERME', '2023-11-30 00:00:00', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance,
                      montant_du, montant_paye, date_paiement, statut) VALUES
 (970301, 9703, 1, '2023-07-28', 214334, 214334, '2023-07-28', 'PAYE_A_TEMPS'),
 (970302, 9703, 2, '2023-08-28', 214334, 214334, '2023-08-28', 'PAYE_A_TEMPS'),
 (970303, 9703, 3, '2023-09-28', 214334, 214334, '2023-09-28', 'PAYE_A_TEMPS'),
 (970304, 9703, 4, '2023-10-28', 214334, 214334, '2023-10-28', 'PAYE_A_TEMPS'),
 (970305, 9703, 5, '2023-11-28', 231664, 231664, '2023-11-23', 'PAYE_A_TEMPS');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie,
                      nom_garant, code_client_cb_garant, montant_couvert)
VALUES (9703, 9703, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel',
        'NAN4535 (60)', 'CHRISTIAN RADERA', 'C00130366', 1089000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant,
                     client_id, mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     montant_finance, montant_total_du, montant_echeance_mensuelle,
                     nombre_total_echeances, devise, periodicite_paiement,
                     phase_demande, motif_cloture, date_derniere_modification, origine_donnee)
VALUES (9704, 'I00568335', '0202802034012002', '001',
        9007, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE',
        '2023-06-02', '2023-06-02', '2023-07-28', '2023-11-20',
        264000, 264000, 51827, 5, 'Ariary malgache', 'MENSUELLE',
        'FERME', 'FIN_A_TERME', '2023-11-30 00:00:00', 'RAPPORT_CRIF');

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance,
                      montant_du, montant_paye, date_paiement, statut) VALUES
 (970401, 9704, 1, '2023-07-28', 51827, 51827, '2023-07-28', 'PAYE_A_TEMPS'),
 (970402, 9704, 2, '2023-08-28', 51827, 51827, '2023-08-28', 'PAYE_A_TEMPS'),
 (970403, 9704, 3, '2023-09-28', 51827, 51827, '2023-09-28', 'PAYE_A_TEMPS'),
 (970404, 9704, 4, '2023-10-28', 51827, 51827, '2023-10-28', 'PAYE_A_TEMPS'),
 (970405, 9704, 5, '2023-11-28', 56692, 56692, '2023-11-20', 'PAYE_A_TEMPS');

INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie,
                      nom_garant, code_client_cb_garant, montant_couvert)
VALUES (9704, 9704, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel',
        'NAN4536 (55)', 'CHRISTIAN RADERA', 'C00130366', 264000);

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification, origine_donnee)
VALUES (9705, '300767570', '001', 9007, 'DEMANDE_EXISTANTE',
        'Prêt Personnel', 'TITULAIRE', '2026-09-14',
        1500000, 1500000, NULL, 3,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-09-14 00:00:00', 'RAPPORT_CRIF');

-- ============================================================================
-- 9. CLIENT 9008 : JULIETTE RASOAMIARIMBOLANA
-- ============================================================================

INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance,
                    ville_naissance, pays_naissance, genre, nationalite,
                    etat_civil, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9008, 'L00180894', NULL, '0004',
        'JULIETTE', NULL, 'RASOAMIARIMBOLANA', '1976-10-01',
        NULL, NULL, 'FEMME', 'Malgache',
        NULL, NULL, '2026-09-01', '2026-09-01 00:00:00', 'RAPPORT_CRIF');

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification)
VALUES (9009, 9008, 'Individu - Adresse Principale', 'AMBATOLAHY VOHIPOSA', true, '2026-09-01 00:00:00');

INSERT INTO identifiant (id, client_id, type_identifiant, numero)
VALUES (9008, 9008, 'CIN', '208172002983');

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, montant_finance, montant_total_du,
                     montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, date_derniere_modification, origine_donnee)
VALUES (9801, '600748775', '001', 9008, 'NOUVELLE_DEMANDE',
        'Prêt Personnel', 'TITULAIRE', '2026-09-01',
        1500000, 1500000, NULL, 3,
        'Ariary malgache', NULL, 'DEMANDE_EN_COURS', '2026-09-01 00:00:00', 'RAPPORT_CRIF');

-- ============================================================================
-- 10. JOURNAL AUDIT / CONSULTATIONS CRIF
-- ============================================================================

INSERT INTO journal_audit (id, date_evenement, utilisateur, entite, entite_id, action, detail) VALUES
 (950001, '2026-01-19 09:00:00', 'agent1', 'CLIENT', 9502, 'CONSULTATION', 'Données CRIF Marcel'),
 (950002, '2026-02-19 09:00:00', 'agent1', 'CLIENT', 9502, 'CONSULTATION', 'Données CRIF Marcel'),
 (950003, '2026-03-19 09:00:00', 'agent1', 'CLIENT', 9502, 'CONSULTATION', 'Données CRIF Marcel'),
 (950004, '2025-12-01 09:00:00', 'agent1', 'CLIENT', 9504, 'CREATION',     'Données CRIF Emmanuel'),
 (950005, '2026-02-16 08:30:00', 'agent1', 'CLIENT', 9501, 'CREATION',     'Demande F00728095 saisie'),
 (950006, '2026-04-13 10:15:00', 'agent1', 'CLIENT', 9503, 'CREATION',     'Demande S00725748 saisie');

-- ============================================================================
-- 11. SYNCHRONISATION DES SÉQUENCES POSTGRESQL
-- ============================================================================

SELECT setval(pg_get_serial_sequence('client',               'id'), (SELECT MAX(id) FROM client));
SELECT setval(pg_get_serial_sequence('adresse',              'id'), (SELECT MAX(id) FROM adresse));
SELECT setval(pg_get_serial_sequence('identifiant',          'id'), (SELECT MAX(id) FROM identifiant));
SELECT setval(pg_get_serial_sequence('emploi',               'id'), (SELECT MAX(id) FROM emploi));
SELECT setval(pg_get_serial_sequence('contrat',              'id'), (SELECT MAX(id) FROM contrat));
SELECT setval(pg_get_serial_sequence('echeance',             'id'), (SELECT MAX(id) FROM echeance));
SELECT setval(pg_get_serial_sequence('garantie',             'id'), (SELECT MAX(id) FROM garantie));
SELECT setval(pg_get_serial_sequence('contrat_lien',         'id'), (SELECT COALESCE(MAX(id), 1) FROM contrat_lien));
SELECT setval(pg_get_serial_sequence('affaire_individuelle', 'id'), (SELECT COALESCE(MAX(id), 1) FROM affaire_individuelle));
SELECT setval(pg_get_serial_sequence('adresse_affaire_individuelle', 'id'), (SELECT COALESCE(MAX(id), 1) FROM adresse_affaire_individuelle));
SELECT setval(pg_get_serial_sequence('journal_audit',        'id'), (SELECT MAX(id) FROM journal_audit));
SELECT setval(pg_get_serial_sequence('historique_paiement_mensuel', 'id'), (SELECT COALESCE(MAX(id), 1) FROM historique_paiement_mensuel));
SELECT setval('seq_code_client_cb', GREATEST(200000, COALESCE((SELECT MAX(SUBSTRING(code_client_cb FROM '[0-9]+')::bigint) FROM client), 0)));
SELECT setval('seq_code_contrat_cb', GREATEST(700000000, COALESCE((SELECT MAX(code_contrat_cb::bigint) FROM contrat WHERE code_contrat_cb ~ '^[0-9]+$'), 0)));