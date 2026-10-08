-- ============================================================================
-- Clients de test reproduits depuis les rapports CRIF (Berthine, Marcel, Hervé,
-- Rakotonirina, Ramaliarisoa) + Maurice (garant) + Georgette (co-titulaire de
-- Christian). Maurice et Georgette ont des CIN, adresses et dates inventés.
-- Rejoué à chaque démarrage : DELETE puis INSERT. Plage utilisée : clients 9501-9507.
--
-- Écarts assumés avec CRIF :
--  * le « Nombre Totale Échéances » reprend celui de CRIF, sauf sur 9515/9516 (échéances
--    fractionnées) et sur 9522, 9532, 9534, où il vaut le nombre d'échéances réellement
--    créées, car les paiements partiels de CRIF y sont modélisés par plusieurs échéances ;
--  * le contrat actif de Marcel (9522) est daté par rapport à CURRENT_DATE pour rester
--    « actif » quel que soit le jour où vous lancez l'application ;
--  * le « mode de règlement » (Espèce) n'est pas stocké ;
--  * les mois NDIS (non déclarés) ne sont pas reproduits.
-- ============================================================================

DELETE FROM echeance WHERE contrat_id IN (SELECT c.id FROM contrat c JOIN client cl ON cl.id = c.client_id
  WHERE cl.code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305'));
DELETE FROM garantie WHERE contrat_id IN (SELECT c.id FROM contrat c JOIN client cl ON cl.id = c.client_id
  WHERE cl.code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305'));
DELETE FROM bien_leasing WHERE contrat_id IN (SELECT c.id FROM contrat c JOIN client cl ON cl.id = c.client_id
  WHERE cl.code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305'));
DELETE FROM historique_paiement_mensuel WHERE contrat_id IN (SELECT c.id FROM contrat c JOIN client cl ON cl.id = c.client_id
  WHERE cl.code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305'));
DELETE FROM contrat_lien WHERE contrat_id IN (SELECT c.id FROM contrat c JOIN client cl ON cl.id = c.client_id
  WHERE cl.code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305'));
DELETE FROM contrat_lien WHERE client_lie_id IN (SELECT id FROM client
  WHERE code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305'));
DELETE FROM contrat WHERE client_id IN (SELECT id FROM client
  WHERE code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305'));
DELETE FROM affaire_individuelle WHERE client_id IN (SELECT id FROM client
  WHERE code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305'));
DELETE FROM identifiant WHERE client_id IN (SELECT id FROM client
  WHERE code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305'));
DELETE FROM adresse WHERE client_id IN (SELECT id FROM client
  WHERE code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305'));
DELETE FROM emploi WHERE client_id IN (SELECT id FROM client
  WHERE code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305'));
DELETE FROM client WHERE code_client_cb IN ('500078959','E00127416','C00094855','D00170254','H00151494','700158713','N00172305');

DELETE FROM echeance     WHERE contrat_id IN (SELECT id FROM contrat WHERE client_id BETWEEN 9501 AND 9507);
DELETE FROM garantie     WHERE contrat_id IN (SELECT id FROM contrat WHERE client_id BETWEEN 9501 AND 9507);
DELETE FROM bien_leasing WHERE contrat_id IN (SELECT id FROM contrat WHERE client_id BETWEEN 9501 AND 9507);
DELETE FROM contrat_lien WHERE contrat_id IN (SELECT id FROM contrat WHERE client_id BETWEEN 9501 AND 9507)
                            OR contrat_id = 9701;
DELETE FROM contrat      WHERE client_id BETWEEN 9501 AND 9507;
DELETE FROM affaire_individuelle WHERE client_id BETWEEN 9501 AND 9507;
DELETE FROM journal_audit WHERE id BETWEEN 950001 AND 950010;
DELETE FROM identifiant  WHERE client_id BETWEEN 9501 AND 9507;
DELETE FROM adresse      WHERE client_id BETWEEN 9501 AND 9507;
DELETE FROM emploi       WHERE client_id BETWEEN 9501 AND 9507;
DELETE FROM client       WHERE id BETWEEN 9501 AND 9507;

-- ----------------------------------------------------------------------------
-- Clients
-- ----------------------------------------------------------------------------
INSERT INTO client (id, code_client_cb, code_client_etablissement, agence, titre, categorie_tiers_code,
                    prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance,
                    genre, nationalite, etat_civil, telephone, date_adhesion, date_derniere_modification)
VALUES
 (9501, '500078959', 'CE-050078959', '02000', 'Mme', '0004', 'BERTHINE', NULL, 'RASOANANDRASANA', '1989-01-18',
  'AMBATOMAINTY', 'MADAGASCAR', 'FEMME', 'Malgache', 'Marié(e)', NULL, '2021-02-18', '2024-12-09 00:00:00'),
 (9502, 'E00127416', 'CE-000127416', '02000', NULL, '0004', 'MARCEL OLIVIER', NULL, 'RANDRIANARISON', '1976-12-10',
  'ALAKAMISY AMBOHIMAHA', 'MADAGASCAR', 'HOMME', 'Malgache', NULL, NULL, '2023-08-24', '2025-02-18 00:00:00'),
 (9503, 'C00094855', 'CE-000094855', '02000', 'M.', '0004', 'HERVÉ', NULL, 'RASAMBANIRINA', '1988-07-21',
  NULL, NULL, 'FEMME', 'Malgache', NULL, NULL, '2018-11-22', '2025-06-10 00:00:00'),
 (9504, 'D00170254', 'CE-000170254', '02000', NULL, '0004', 'RAKOTONIRINA', NULL, 'EMMANUEL', '1974-09-07',
  'MAHATSINJONY', 'MADAGASCAR', 'HOMME', 'Malgache', NULL, NULL, '2025-12-01', '2025-12-01 00:00:00'),
 (9505, 'H00151494', 'CE-000151494', '02000', NULL, '0004', 'GERMAIN', NULL, 'RAMALIARISON', '1972-01-01',
  'AMBATOLAHY II', 'MADAGASCAR', 'HOMME', 'Malgache', NULL, NULL, '2024-11-30', '2024-11-30 00:00:00'),
 (9506, '700158713', 'CE-700158713', '02000', 'M.', '0004', 'MAURICE', NULL, 'RAZAFIMANDIMBY', '1968-05-12',
  NULL, 'MADAGASCAR', 'HOMME', 'Malgache', NULL, NULL, '2020-03-10', '2025-05-28 00:00:00'),
 (9507, 'N00172305', 'CE-000172305', '02000', 'Mme', '0004', 'GEORGETTE', NULL, 'RAVAORISOA', '1970-03-15',
  NULL, 'MADAGASCAR', 'FEMME', 'Malgache', NULL, NULL, '2023-01-10', '2026-03-16 00:00:00');

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, numero_rue, code_postal, ville, commune, region, pays,
                     actuelle, date_derniere_modification)
VALUES
 (9510, 9501, 'Individu - Adresse Principale', 'MAHATSINJONY',            NULL, NULL, NULL, NULL, NULL, NULL, true,  '2024-12-09 00:00:00'),
 (9511, 9501, 'Individu - Adresse Principale', 'ALAKAMISY',               NULL, NULL, NULL, NULL, NULL, NULL, false, '2022-09-26 00:00:00'),
 (9512, 9501, 'Individu - Adresse Principale', 'AMPARIHILAVA',            NULL, NULL, NULL, NULL, NULL, NULL, false, '2022-02-08 00:00:00'),
 (9513, 9501, 'Individu - Adresse Principale', 'AMBATOMAINTY',            NULL, NULL, NULL, NULL, NULL, NULL, false, '2021-02-28 00:00:00'),
 (9514, 9502, 'Individu - Adresse Principale', 'ALAKAMISY AMBOHIMAHA',    NULL, NULL, NULL, NULL, NULL, NULL, true,  '2025-02-18 00:00:00'),
 (9515, 9502, 'Individu - Adresse Principale', 'IHATRAKA',                NULL, NULL, NULL, NULL, NULL, NULL, false, '2024-08-31 00:00:00'),
 (9516, 9502, 'Individu - Adresse Principale', 'VATOFOTSY',               NULL, NULL, NULL, NULL, NULL, NULL, false, '2023-07-08 00:00:00'),
 (9517, 9503, 'Individu - Adresse Principale', 'ALAKAMIISY-AMBOHIMAHA',   NULL, NULL, NULL, NULL, NULL, NULL, true,  '2025-06-10 00:00:00'),
 (9518, 9503, 'Individu - Adresse Principale', 'SAFATA',                  NULL, NULL, NULL, NULL, NULL, NULL, false, '2024-07-08 00:00:00'),
 (9519, 9503, 'Individu - Adresse Principale', 'TAMBOHOMANDRANO',         NULL, NULL, NULL, NULL, NULL, NULL, false, '2023-05-16 00:00:00'),
 (9520, 9503, 'Individu - Adresse Principale', 'LALANGINA',               NULL, NULL, NULL, NULL, NULL, NULL, false, '2018-11-22 00:00:00'),
 (9521, 9504, 'Individu - Adresse Principale', '220',                     NULL, NULL, NULL, NULL, NULL, NULL, true,  '2025-12-01 00:00:00'),
 (9522, 9504, 'Individu - Adresse Principale', 'AMBATOLAHIHAMBANA',      NULL, NULL, NULL, NULL, NULL, NULL, false, '2025-10-31 00:00:00'),
 (9523, 9505, 'Individu - Adresse Principale', 'CAMP ROBIN',              NULL, NULL, NULL, NULL, NULL, NULL, true,  '2024-11-30 00:00:00'),
 (9524, 9506, 'Individu - Adresse Principale', 'AMBOHIMANGAKELY',         NULL, NULL, NULL, NULL, NULL, NULL, true,  '2025-05-28 00:00:00'),
 (9525, 9507, 'Individu - Adresse Principale', 'AMBOHIJATOVO',            NULL, NULL, NULL, NULL, NULL, NULL, true,  '2026-03-16 00:00:00');

INSERT INTO identifiant (id, client_id, type_identifiant, numero) VALUES
 (9501, 9501, 'CIN', '220112006302'),
 (9502, 9502, 'CIN', '220251001639'),
 (9503, 9503, 'CIN', '220251009756'),
 (9504, 9504, 'CIN', '220111001265'),
 (9505, 9505, 'CIN', '111131000616'),
 (9506, 9506, 'CIN', '208051004455'),
 (9507, 9507, 'CIN', '218031004321');

INSERT INTO emploi (id, client_id, statut_emploi, nom_employeur, profession, date_embauche, revenu_annuel_total, devise) VALUES
 (9501, 9501, 'Travailleur Autonome', NULL, 'Planteur de riz', NULL, 7000000, 'Ariary malgache'),
 (9502, 9502, NULL, NULL, 'Planteur de riz', NULL, NULL, NULL),
 (9504, 9504, NULL, NULL, 'Planteur de riz', NULL, NULL, NULL),
 (9505, 9505, NULL, NULL, 'Planteur de riz', NULL, NULL, NULL);

-- Hervé n'a pas d'emploi déclaré, mais une affaire individuelle (section « AFFAIRE INDIVIDUELLE »).
INSERT INTO affaire_individuelle (id, client_id, nom_affaire, adresse_complete, numero_rue, code_postal, ville, commune, region, pays,
                                  date_derniere_modification)
VALUES (9503, 9503, 'CSGRMANIRYSAFATA', 'SAFATASAFATA', NULL, NULL, NULL, NULL, NULL, NULL, '2018-11-22 00:00:00');

-- ----------------------------------------------------------------------------
-- Contrats
-- ----------------------------------------------------------------------------
INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, client_id,
                     mode_rattachement, type_contrat, role_client,
                     date_demande, date_debut_contrat, date_premiere_echeance, date_fin_contrat,
                     montant_finance, montant_total_du, montant_echeance_mensuelle, nombre_total_echeances,
                     devise, periodicite_paiement, phase_demande, motif_cloture, date_derniere_modification)
VALUES
 -- ===== Berthine : demande saisie F00728095 puis 5 prêts soldés =====
 (9511, 'F00728095', NULL, '001', 9501, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  CURRENT_DATE, NULL, NULL, NULL, 3000000, 3000000, NULL, 5,
  'Ariary malgache', NULL, 'DEMANDE_EN_COURS', NULL, now()),
 (9512, '200647123', '0202502736015005', '001', 9501, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  '2024-12-12', '2024-12-12', '2025-05-28', '2025-06-30', 650000, 789154, 119569, 2,
  'Ariary malgache', 'MENSUELLE', 'FERME', 'FIN_A_TERME', '2025-06-30 00:00:00'),
 (9513, '600508331', '0202502736015004', '001', 9501, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  '2022-09-29', '2022-09-29', '2023-03-28', '2023-05-25', 1300000, 1628184, 202763, 3,
  'Ariary malgache', 'MENSUELLE', 'FERME', 'FIN_A_TERME', '2023-05-31 00:00:00'),
 (9514, '100471980', '0202502736015003', '001', 9501, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  '2022-02-18', '2022-02-18', '2022-06-30', '2022-07-21', 1200000, 1407900, 259282, 2,
  'Ariary malgache', 'MENSUELLE', 'FERME', 'FIN_A_TERME', '2022-07-31 00:00:00'),
 (9515, 'D00452133', '0202502736015002', '001', 9501, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  '2021-07-29', '2021-07-29', '2022-01-31', '2022-01-31', 700000, 883167, 104372, 3,
  'Ariary malgache', 'MENSUELLE', 'FERME', 'FIN_A_TERME', '2022-01-31 00:00:00'),
 (9516, 'S00441337', '0202502736015001', '001', 9501, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  '2021-02-18', '2021-02-18', '2021-06-30', '2021-06-17', 800000, 938600, 172855, 2,
  'Ariary malgache', 'MENSUELLE', 'FERME', 'FIN_A_TERME', '2021-06-30 00:00:00'),

 -- ===== Marcel : demande saisie 700735293, un prêt actif (dates relatives) et 2 soldés =====
 (9521, '700735293', NULL, '001', 9502, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  CURRENT_DATE, NULL, NULL, NULL, 1500000, 1500000, NULL, 2,
  'Ariary malgache', NULL, 'DEMANDE_EN_COURS', NULL, now()),
 (9522, '100655194', '0202502978015003', '001', 9502, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  (CURRENT_DATE + 20 - INTERVAL '12 months')::date, (CURRENT_DATE + 20 - INTERVAL '12 months')::date,
  (CURRENT_DATE + 20 - INTERVAL '6 months')::date, (CURRENT_DATE + 20)::date,
  2000000, 2666283, 216244, 4,
  'Ariary malgache', 'MENSUELLE', 'ACTIF', NULL, now()),
 (9523, 'T00611864', '0202502978015002', '001', 9502, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  '2024-04-09', '2024-04-09', '2024-11-28', '2025-02-28', 2000000, 2693816, 228871, 3,
  'Ariary malgache', 'MENSUELLE', 'FERME', 'FIN_A_TERME', '2025-02-28 00:00:00'),
 (9524, 'D00562708', '0202502978015001', '001', 9502, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  '2023-08-24', '2023-08-24', '2024-04-28', '2024-03-27', 1000000, 1326083, 121667, 2,
  'Ariary malgache', 'MENSUELLE', 'FERME', 'FIN_A_TERME', '2024-03-27 00:00:00'),

 -- ===== Hervé : demande saisie S00725748, 3 « Crédit au Groupement » d'un autre établissement (002) =====
 (9531, 'S00725748', NULL, '001', 9503, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  CURRENT_DATE, NULL, NULL, NULL, 6000000, 6000000, NULL, 5,
  'Ariary malgache', NULL, 'DEMANDE_EN_COURS', NULL, now()),
 (9532, '500526717', NULL, '002', 9503, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'TITULAIRE',
  '2022-07-08', '2022-07-08', '2022-12-08', '2023-06-30', 2500000, 2500000, 500000, 4,
  'Ariary malgache', 'échéances chaque 5 mois', 'FERME', 'FIN_A_TERME', '2023-06-30 00:00:00'),
 (9533, '300528701', NULL, '002', 9503, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'TITULAIRE',
  '2021-10-27', '2021-10-27', '2022-04-27', '2022-05-25', 2100000, 2100000, 350000, 1,
  'Ariary malgache', 'Semestriel-6 mois', 'FERME', 'FIN_A_TERME', '2022-05-31 00:00:00'),
 (9534, 'M00515959', NULL, '002', 9503, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'TITULAIRE',
  '2020-10-01', '2020-10-01', '2021-01-29', '2021-08-19', 3300000, 3300000, 825000, 3,
  'Ariary malgache', 'Quadrimestriel - 4 mois', 'FERME', 'FIN_A_TERME', '2021-08-31 00:00:00'),

 -- ===== Rakotonirina : demande saisie 800727695, une seule demande antérieure restée en cours =====
 (9541, '800727695', NULL, '001', 9504, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  CURRENT_DATE, NULL, NULL, NULL, 1000000, 1000000, NULL, 2,
  'Ariary malgache', NULL, 'DEMANDE_EN_COURS', NULL, now()),
 (9542, 'S00706971', NULL, '002', 9504, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  '2025-12-01', NULL, NULL, NULL, 1000000, 1000000, NULL, 12,
  'Ariary malgache', NULL, 'DEMANDE_EN_COURS', NULL, '2025-12-01 00:00:00'),

 -- ===== Ramaliarisoa : demande saisie J00719389, aucun historique =====
 (9551, 'J00719389', NULL, '001', 9505, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE',
  CURRENT_DATE, NULL, NULL, NULL, 1000000, 1000000, NULL, 2,
  'Ariary malgache', NULL, 'DEMANDE_EN_COURS', NULL, now());

-- ----------------------------------------------------------------------------
-- Échéances (le montant d'une échéance reproduit les variations d'encours des rapports CRIF)
-- ----------------------------------------------------------------------------
INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut)
VALUES
 -- Berthine 9512 : encours 789 154 puis 336 754
 (951201, 9512, 1, '2025-05-28', 452400, 452400, '2025-05-28', 'PAYE_A_TEMPS'),
 (951202, 9512, 2, '2025-06-30', 336754, 336754, '2025-06-25', 'PAYE_A_TEMPS'),
 -- Berthine 9513 : 1 628 184 -> 1 053 667 -> 517 500
 (951301, 9513, 1, '2023-03-28', 574517, 574517, '2023-03-28', 'PAYE_A_TEMPS'),
 (951302, 9513, 2, '2023-04-28', 536167, 536167, '2023-04-28', 'PAYE_A_TEMPS'),
 (951303, 9513, 3, '2023-05-25', 517500, 517500, '2023-05-25', 'PAYE_A_TEMPS'),
 -- Berthine 9514 : 1 407 900 -> 621 700
 (951401, 9514, 1, '2022-06-30', 786200, 786200, '2022-06-29', 'PAYE_A_TEMPS'),
 (951402, 9514, 2, '2022-07-21', 621700, 621700, '2022-07-21', 'PAYE_A_TEMPS'),
 -- Berthine 9515 : 883 167 (3 × 294 389)
 (951501, 9515, 1, '2022-01-31', 294389, 294389, '2022-01-31', 'PAYE_A_TEMPS'),
 (951502, 9515, 2, '2022-01-31', 294389, 294389, '2022-01-31', 'PAYE_A_TEMPS'),
 (951503, 9515, 3, '2022-01-31', 294389, 294389, '2022-01-31', 'PAYE_A_TEMPS'),
 -- Berthine 9516 : 938 600 (2 × 469 300), réglé avant l'échéance
 (951601, 9516, 1, '2021-06-30', 469300, 469300, '2021-06-17', 'PAYE_A_TEMPS'),
 (951602, 9516, 2, '2021-06-30', 469300, 469300, '2021-06-17', 'PAYE_A_TEMPS'),

 -- Marcel 9522 (actif, dates relatives) : 2 666 283 -> 1 622 925 -> 412 925 -> 122 925
 (952201, 9522, 1, (CURRENT_DATE + 20 - INTERVAL '6 months')::date, 1043358, 1043358, (CURRENT_DATE + 20 - INTERVAL '6 months')::date, 'PAYE_A_TEMPS'),
 (952202, 9522, 2, (CURRENT_DATE + 20 - INTERVAL '5 months')::date, 1210000, 1210000, (CURRENT_DATE + 20 - INTERVAL '5 months')::date, 'PAYE_A_TEMPS'),
 (952203, 9522, 3, (CURRENT_DATE + 20 - INTERVAL '4 months')::date,  290000,  290000, (CURRENT_DATE + 20 - INTERVAL '4 months')::date, 'PAYE_A_TEMPS'),
 (952204, 9522, 4, (CURRENT_DATE + 20)::date,                        122925, NULL,   NULL,                                           'A_VENIR'),
 -- Marcel 9523 : 2 693 816 -> 1 547 816 -> 251 453
 (952301, 9523, 1, '2024-10-15', 1146000, 1146000, '2024-10-15', 'PAYE_A_TEMPS'),
 (952302, 9523, 2, '2025-01-15', 1296363, 1296363, '2025-01-15', 'PAYE_A_TEMPS'),
 (952303, 9523, 3, '2025-02-11',  251453,  251453, '2025-02-11', 'PAYE_A_TEMPS'),
 -- Marcel 9524 : 1 326 083 (663 042 + 663 041)
 (952401, 9524, 1, '2024-03-27', 663042, 663042, '2024-03-27', 'PAYE_A_TEMPS'),
 (952402, 9524, 2, '2024-03-27', 663041, 663041, '2024-03-27', 'PAYE_A_TEMPS'),

 -- Hervé 9532 : toutes dues le 08/12/2022 ; les paiements partiels reproduisent 1 340 000 -> 1 290 000 -> 445 000
 (953201, 9532, 1, '2022-12-08',   50000,   50000, '2023-02-10', 'EN_RETARD'),
 (953202, 9532, 2, '2022-12-08',  845000,  845000, '2023-03-15', 'EN_RETARD'),
 (953203, 9532, 3, '2022-12-08',  445000,  445000, '2023-06-30', 'EN_RETARD'),
 (953204, 9532, 4, '2023-06-30', 1160000, 1160000, '2023-06-30', 'PAYE_A_TEMPS'),
 -- Hervé 9533 : une échéance de 2 100 000 réglée avec 28 jours de retard
 (953301, 9533, 1, '2022-04-27', 2100000, 2100000, '2022-05-25', 'EN_RETARD'),
 -- Hervé 9534 : échéance du 29/01/2021 soldée en trois fois (retard jusqu'à plus de 180 jours)
 (953401, 9534, 1, '2021-01-29',  999600,  999600, '2021-03-10', 'EN_RETARD'),
 (953402, 9534, 2, '2021-01-29', 1978400, 1978400, '2021-07-15', 'EN_RETARD'),
 (953403, 9534, 3, '2021-01-29',  322000,  322000, '2021-08-19', 'EN_RETARD');

-- ----------------------------------------------------------------------------
-- Garanties
-- ----------------------------------------------------------------------------
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie,
                      nom_garant, code_client_cb_garant, montant_couvert, date_debut_validite, date_fin_validite)
VALUES
 (9512,  9512, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAN7923 (42)',
  'RAZAFIMANDIMBY MAURICE', '700158713', 800000, NULL, NULL),
 (95121, 9512, 'REELLE', 'Caution mutuelle', 'CAU7922 (1)',
  'RAZAFIMANDIMBY MAURICE', '700158713', 1, NULL, NULL),
 (9513, 9513, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAM4008 (17)',
  'BERTHINE RASOANANDRASANA', '500078959', 1600000, NULL, NULL),
 (9514, 9514, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAM3568 (18)',
  'BERTHINE RASOANANDRASANA', '500078959', 2000000, NULL, NULL),
 (9515, 9515, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAM3330 (20)',
  'BERTHINE RASOANANDRASANA', '500078959', 2000000, NULL, NULL),
 (9516, 9516, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAM3038 (18)',
  'BERTHINE RASOANANDRASANA', '500078959', 2000000, NULL, NULL),
 (9522, 9522, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAN8125 (35)',
  'MARCEL OLIVIER RANDRIANARISON', 'E00127416', 3800000, NULL, NULL),
 (9523, 9523, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAN6118 (49)',
  'MARCEL OLIVIER RANDRIANARISON', 'E00127416', 3300000, NULL, NULL),
 (9524, 9524, 'REELLE', 'Nantissement sur Outillage, Matériel, d''Equipement Professionnel', 'NAN4989 (61)',
  'MARCEL OLIVIER RANDRIANARISON', 'E00127416', 1700000, NULL, NULL);

-- ----------------------------------------------------------------------------
-- Clients liés (multi-emprunteurs) : Maurice garantit un prêt de Berthine ;
-- Georgette est co-titulaire de la demande L00730033 de Christian (contrat 9701, data-crif.sql)
-- ----------------------------------------------------------------------------
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client) VALUES
 (9501, 9512, '700158713', 'RAZAFIMANDIMBY MAURICE', 'GARANT'),
 (9502, 9701, 'N00172305', 'GEORGETTE RAVAORISOA', 'CO_TITULAIRE');

-- ----------------------------------------------------------------------------
-- Historique des consultations affiché dans les rapports
-- ----------------------------------------------------------------------------
INSERT INTO journal_audit (id, date_evenement, utilisateur, entite, entite_id, action, detail) VALUES
 (950001, '2026-01-19 09:00:00', 'agent1', 'CLIENT', 9502, 'CONSULTATION', 'Données de test CRIF'),
 (950002, '2026-02-19 09:00:00', 'agent1', 'CLIENT', 9502, 'CONSULTATION', 'Données de test CRIF'),
 (950003, '2026-03-19 09:00:00', 'agent1', 'CLIENT', 9502, 'CONSULTATION', 'Données de test CRIF'),
 (950004, '2025-12-01 09:00:00', 'agent1', 'CLIENT', 9504, 'CREATION',     'Données de test CRIF');

SELECT setval(pg_get_serial_sequence('client',               'id'), (SELECT MAX(id) FROM client));
SELECT setval(pg_get_serial_sequence('adresse',              'id'), (SELECT MAX(id) FROM adresse));
SELECT setval(pg_get_serial_sequence('identifiant',          'id'), (SELECT MAX(id) FROM identifiant));
SELECT setval(pg_get_serial_sequence('emploi',               'id'), (SELECT MAX(id) FROM emploi));
SELECT setval(pg_get_serial_sequence('contrat',              'id'), (SELECT MAX(id) FROM contrat));
SELECT setval(pg_get_serial_sequence('echeance',             'id'), (SELECT MAX(id) FROM echeance));
SELECT setval(pg_get_serial_sequence('garantie',             'id'), (SELECT MAX(id) FROM garantie));
SELECT setval(pg_get_serial_sequence('contrat_lien',         'id'), (SELECT COALESCE(MAX(id), 1) FROM contrat_lien));
SELECT setval(pg_get_serial_sequence('affaire_individuelle', 'id'), (SELECT COALESCE(MAX(id), 1) FROM affaire_individuelle));
SELECT setval(pg_get_serial_sequence('journal_audit',        'id'), (SELECT MAX(id) FROM journal_audit));