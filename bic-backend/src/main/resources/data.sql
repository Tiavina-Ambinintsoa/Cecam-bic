INSERT INTO grille_score (intervalle, score_min, score_max, categorie_risque, couleur) VALUES
('A', 580, 850, 'Risque très faible', 'Vert sombre'),
('B', 500, 579, 'Risque faible', 'Vert clair'),
('C', 460, 499, 'Risque moyen', 'Jaune'),
('D', 400, 459, 'Risque élevé', 'Rouge verdâtre'),
('E', 300, 399, 'Risque très élevé', 'Rouge')
ON CONFLICT (intervalle) DO NOTHING;

INSERT INTO categorie_tiers (code, libelle, code_parent) VALUES
('0004', 'Ménages', NULL),
('0215', 'Institution de Micro-Finance (IMF)', NULL),
('0001', 'Administration publique', NULL),
('0011', 'Administration centrale', '0001'),
('0019', 'NCA (non classé ailleurs)', '0001'),
('0002', 'Institution financière', NULL),
('0211', 'Banque Centrale', '0002'),
('0212', 'Banques / Établissements de crédit', '0002'),
('0213', 'Établissements financiers', '0002'),
('0214', 'Institutions financières spécialisées', '0002'),
('0022', 'Bureau de change', '0002'),
('0231', 'Assurances', '0002'),
('0023', 'Sociétés financières non établissement de crédit', '0002'),
('0232', 'Autres sociétés financières', '0002'),
('0029', 'Autres institutions financières', '0002'),
('0003', 'Sociétés non financières', NULL),
('0321', 'Sociétés non financières privées franches', '0003'),
('0322', 'Sociétés non financières privées non franches', '0003'),
('3221', 'Grandes entreprises', '0003'),
('3222', 'Petites et moyennes entreprises (PME)', '0003'),
('3223', 'Très petites entreprises (TPE)', '0003'),
('0329', 'NCA (non classé ailleurs)', '0003')
ON CONFLICT (code) DO NOTHING;

INSERT INTO utilisateur (id, nom_utilisateur, mot_de_passe, role, actif) VALUES
(1, 'agent1', '$2b$10$MUvYPyZABeOBL1/EZwstXOV7mYuNfE/fDnfHB0Am1C8B9IoYsC6Ga', 'AGENT_CREDIT', true),
(2, 'admin1', '$2b$10$.1JeAl8BY/PxPtOGfNKPW.B/gRltqFF9sePuEFBrx4hyuV2q.kS3K', 'ADMIN', true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code, prenom, nom, date_naissance,
                     ville_naissance, pays_naissance, genre, nationalite, etat_civil, date_adhesion, date_derniere_modification)
VALUES (9001, 'L00190001', 'Mme', '0215', 'Hanta', 'Rakoto', '1985-04-12',
        'Fianarantsoa', 'Madagascar', 'FEMME', 'Malgache', 'Mariée', '2023-11-01', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, numero_rue, code_postal, ville, commune, region, pays, actuelle, date_derniere_modification)
VALUES (9001, 9001, 'Individu - Adresse principale', 'Lot II M 45 Tsianolondroa', '45', '301', 'Fianarantsoa', 'Fianarantsoa I', 'Haute Matsiatra', 'Madagascar', true, now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO identifiant (id, client_id, type_identifiant, numero) VALUES (9001, 9001, 'CIN', '301021985001') ON CONFLICT (id) DO NOTHING;

INSERT INTO contrat (id, code_contrat_cb, client_id, mode_rattachement, type_contrat, role_client,
                      date_demande, montant_finance, montant_echeance_mensuelle, nombre_total_echeances,
                      devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9001, '600900001', 9001, 'NOUVELLE_DEMANDE', 'Prêt personnel', 'TITULAIRE',
        '2024-01-15', 1200000, 100000, 12, 'Ariary malgache', 'Mensuelle', 'FERME', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
(90011, 9001, 1, '2024-02-15', 100000, 100000, '2024-02-14', 'PAYE_A_TEMPS'),
(90012, 9001, 2, '2024-03-15', 100000, 100000, '2024-03-15', 'PAYE_A_TEMPS'),
(90013, 9001, 3, '2024-04-15', 100000, 100000, '2024-04-20', 'EN_RETARD'),
(90014, 9001, 4, '2024-05-15', 100000, 100000, '2024-05-15', 'PAYE_A_TEMPS'),
(90015, 9001, 5, '2024-06-15', 100000, 100000, '2024-06-13', 'PAYE_A_TEMPS'),
(90016, 9001, 6, '2024-07-15', 100000, NULL, NULL, 'IMPAYE'),
(90017, 9001, 7, '2024-08-15', 100000, 100000, '2024-08-16', 'EN_RETARD'),
(90018, 9001, 8, '2024-09-15', 100000, 100000, '2024-09-15', 'PAYE_A_TEMPS'),
(90019, 9001, 9, '2024-10-15', 100000, 100000, '2024-10-15', 'PAYE_A_TEMPS'),
(90020, 9001, 10, '2024-11-15', 100000, 100000, '2024-11-14', 'PAYE_A_TEMPS'),
(90021, 9001, 11, '2024-12-15', 100000, 100000, '2024-12-15', 'PAYE_A_TEMPS'),
(90022, 9001, 12, '2025-01-15', 100000, 100000, '2025-01-15', 'PAYE_A_TEMPS')
ON CONFLICT (id) DO NOTHING;

INSERT INTO contrat (id, code_contrat_cb, client_id, mode_rattachement, type_contrat, role_client,
                      date_demande, montant_finance, montant_echeance_mensuelle, nombre_total_echeances,
                      devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9002, '600900002', 9001, 'NOUVELLE_DEMANDE', 'Prêt personnel', 'TITULAIRE',
        '2026-03-01', 800000, 100000, 8, 'Ariary malgache', 'Mensuelle', 'ACTIF', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
(90031, 9002, 1, '2026-04-01', 100000, 100000, '2026-04-01', 'PAYE_A_TEMPS'),
(90032, 9002, 2, '2026-05-01', 100000, 100000, '2026-05-03', 'EN_RETARD'),
(90033, 9002, 3, '2026-06-01', 100000, 100000, '2026-06-01', 'PAYE_A_TEMPS'),
(90034, 9002, 4, '2026-07-01', 100000, 100000, '2026-07-01', 'PAYE_A_TEMPS'),
(90035, 9002, 5, '2026-08-01', 100000, 100000, '2026-08-02', 'EN_RETARD'),
(90036, 9002, 6, '2026-09-01', 100000, NULL, NULL, 'A_VENIR'),
(90037, 9002, 7, '2026-10-01', 100000, NULL, NULL, 'A_VENIR'),
(90038, 9002, 8, '2026-11-01', 100000, NULL, NULL, 'A_VENIR')
ON CONFLICT (id) DO NOTHING;

INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code, prenom, nom, date_naissance,
                     ville_naissance, pays_naissance, genre, nationalite, etat_civil, date_adhesion, date_derniere_modification)
VALUES (9003, 'L00190003', 'Mme', '0004', 'Voahangy', 'Rasoanirina', '1990-03-22',
        'Fianarantsoa', 'Madagascar', 'FEMME', 'Malgache', 'Célibataire', '2022-03-01', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, numero_rue, code_postal, ville, commune, region, pays, actuelle, date_derniere_modification)
VALUES (9003, 9003, 'Individu - Adresse principale', 'Lot IVG 12 Bis Andrainjato', '12 Bis', '301', 'Fianarantsoa', 'Fianarantsoa I', 'Haute Matsiatra', 'Madagascar', true, now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO identifiant (id, client_id, type_identifiant, numero) VALUES (9003, 9003, 'CIN', '190031990004') ON CONFLICT (id) DO NOTHING;

INSERT INTO contrat (id, code_contrat_cb, client_id, mode_rattachement, type_contrat, role_client,
                      date_demande, montant_finance, montant_echeance_mensuelle, nombre_total_echeances,
                      devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9101, '600910001', 9003, 'NOUVELLE_DEMANDE', 'Prêt personnel', 'TITULAIRE',
        '2022-06-01', 2000000, 90000, 24, 'Ariary malgache', 'Mensuelle', 'FERME', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
(910101, 9101, 1, '2022-07-01', 90000, 90000, '2022-07-01', 'PAYE_A_TEMPS'),
(910102, 9101, 2, '2022-08-01', 90000, 90000, '2022-08-01', 'PAYE_A_TEMPS'),
(910103, 9101, 3, '2022-09-01', 90000, 90000, '2022-09-01', 'PAYE_A_TEMPS'),
(910104, 9101, 4, '2022-10-01', 90000, 90000, '2022-10-01', 'PAYE_A_TEMPS'),
(910105, 9101, 5, '2022-11-01', 90000, 90000, '2022-11-01', 'PAYE_A_TEMPS'),
(910106, 9101, 6, '2022-12-01', 90000, 90000, '2022-12-01', 'PAYE_A_TEMPS'),
(910107, 9101, 7, '2023-01-01', 90000, 90000, '2023-01-01', 'PAYE_A_TEMPS'),
(910108, 9101, 8, '2023-02-01', 90000, 90000, '2023-02-01', 'PAYE_A_TEMPS'),
(910109, 9101, 9, '2023-03-01', 90000, 90000, '2023-03-01', 'PAYE_A_TEMPS'),
(910110, 9101, 10, '2023-04-01', 90000, 90000, '2023-04-01', 'PAYE_A_TEMPS'),
(910111, 9101, 11, '2023-05-01', 90000, 90000, '2023-05-01', 'PAYE_A_TEMPS'),
(910112, 9101, 12, '2023-06-01', 90000, 90000, '2023-06-01', 'PAYE_A_TEMPS'),
(910113, 9101, 13, '2023-07-01', 90000, 90000, '2023-07-01', 'PAYE_A_TEMPS'),
(910114, 9101, 14, '2023-08-01', 90000, 90000, '2023-08-01', 'PAYE_A_TEMPS'),
(910115, 9101, 15, '2023-09-01', 90000, 90000, '2023-09-01', 'PAYE_A_TEMPS'),
(910116, 9101, 16, '2023-10-01', 90000, 90000, '2023-10-01', 'PAYE_A_TEMPS'),
(910117, 9101, 17, '2023-11-01', 90000, 90000, '2023-11-01', 'PAYE_A_TEMPS'),
(910118, 9101, 18, '2023-12-01', 90000, 90000, '2023-12-01', 'PAYE_A_TEMPS'),
(910119, 9101, 19, '2024-01-01', 90000, 90000, '2024-01-01', 'PAYE_A_TEMPS'),
(910120, 9101, 20, '2024-02-01', 90000, 90000, '2024-02-01', 'PAYE_A_TEMPS'),
(910121, 9101, 21, '2024-03-01', 90000, 90000, '2024-03-01', 'PAYE_A_TEMPS'),
(910122, 9101, 22, '2024-04-01', 90000, 90000, '2024-04-01', 'PAYE_A_TEMPS'),
(910123, 9101, 23, '2024-05-01', 90000, 90000, '2024-05-01', 'PAYE_A_TEMPS'),
(910124, 9101, 24, '2024-06-01', 90000, 90000, '2024-06-01', 'PAYE_A_TEMPS')
ON CONFLICT (id) DO NOTHING;

INSERT INTO contrat (id, code_contrat_cb, client_id, mode_rattachement, type_contrat, role_client,
                      date_demande, montant_finance, montant_echeance_mensuelle, nombre_total_echeances,
                      devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9102, '600910002', 9003, 'NOUVELLE_DEMANDE', 'Prêt personnel', 'TITULAIRE',
        '2025-01-10', 500000, 83333, 6, 'Ariary malgache', 'Mensuelle', 'FERME', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
(910201, 9102, 1, '2025-02-10', 83333, 83333, '2025-02-10', 'PAYE_A_TEMPS'),
(910202, 9102, 2, '2025-03-10', 83333, 83333, '2025-03-10', 'PAYE_A_TEMPS'),
(910203, 9102, 3, '2025-04-10', 83333, 83333, '2025-04-10', 'PAYE_A_TEMPS'),
(910204, 9102, 4, '2025-05-10', 83333, 83333, '2025-05-10', 'PAYE_A_TEMPS'),
(910205, 9102, 5, '2025-06-10', 83333, 83333, '2025-06-10', 'PAYE_A_TEMPS'),
(910206, 9102, 6, '2025-07-10', 83333, 83333, '2025-07-10', 'PAYE_A_TEMPS')
ON CONFLICT (id) DO NOTHING;

INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code, prenom, nom, date_naissance,
                     ville_naissance, pays_naissance, genre, nationalite, etat_civil, date_adhesion, date_derniere_modification)
VALUES (9004, 'L00190004', 'Mr', '0004', 'Fenohasina', 'Andrianarimanana', '1988-11-05',
        'Fianarantsoa', 'Madagascar', 'HOMME', 'Malgache', 'Marié', '2022-11-01', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, numero_rue, code_postal, ville, commune, region, pays, actuelle, date_derniere_modification)
VALUES (9004, 9004, 'Individu - Adresse principale', 'Lot 67 Ter Ambalapaiso', '67 Ter', '301', 'Fianarantsoa', 'Fianarantsoa I', 'Haute Matsiatra', 'Madagascar', true, now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO identifiant (id, client_id, type_identifiant, numero) VALUES (9004, 9004, 'CIN', '188111988002') ON CONFLICT (id) DO NOTHING;

INSERT INTO contrat (id, code_contrat_cb, client_id, mode_rattachement, type_contrat, role_client,
                      date_demande, montant_finance, montant_echeance_mensuelle, nombre_total_echeances,
                      devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9201, '600920001', 9004, 'NOUVELLE_DEMANDE', 'Prêt personnel', 'TITULAIRE',
        '2023-02-01', 1500000, 83333, 18, 'Ariary malgache', 'Mensuelle', 'FERME', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
(920101, 9201, 1, '2023-03-01', 83333, 83333, '2023-03-01', 'PAYE_A_TEMPS'),
(920102, 9201, 2, '2023-04-01', 83333, 83333, '2023-04-01', 'PAYE_A_TEMPS'),
(920103, 9201, 3, '2023-05-01', 83333, 83333, '2023-05-01', 'PAYE_A_TEMPS'),
(920104, 9201, 4, '2023-06-01', 83333, 83333, '2023-06-07', 'EN_RETARD'),
(920105, 9201, 5, '2023-07-01', 83333, 83333, '2023-07-01', 'PAYE_A_TEMPS'),
(920106, 9201, 6, '2023-08-01', 83333, 83333, '2023-08-01', 'PAYE_A_TEMPS'),
(920107, 9201, 7, '2023-09-01', 83333, 83333, '2023-09-01', 'PAYE_A_TEMPS'),
(920108, 9201, 8, '2023-10-01', 83333, 83333, '2023-10-07', 'EN_RETARD'),
(920109, 9201, 9, '2023-11-01', 83333, 83333, '2023-11-01', 'PAYE_A_TEMPS'),
(920110, 9201, 10, '2023-12-01', 83333, 83333, '2023-12-01', 'PAYE_A_TEMPS'),
(920111, 9201, 11, '2024-01-01', 83333, 83333, '2024-01-01', 'PAYE_A_TEMPS'),
(920112, 9201, 12, '2024-02-01', 83333, 83333, '2024-02-07', 'EN_RETARD'),
(920113, 9201, 13, '2024-03-01', 83333, 83333, '2024-03-01', 'PAYE_A_TEMPS'),
(920114, 9201, 14, '2024-04-01', 83333, 83333, '2024-04-01', 'PAYE_A_TEMPS'),
(920115, 9201, 15, '2024-05-01', 83333, 83333, '2024-05-01', 'PAYE_A_TEMPS'),
(920116, 9201, 16, '2024-06-01', 83333, 83333, '2024-06-07', 'EN_RETARD'),
(920117, 9201, 17, '2024-07-01', 83333, 83333, '2024-07-01', 'PAYE_A_TEMPS'),
(920118, 9201, 18, '2024-08-01', 83333, 83333, '2024-08-01', 'PAYE_A_TEMPS')
ON CONFLICT (id) DO NOTHING;

INSERT INTO contrat (id, code_contrat_cb, client_id, mode_rattachement, type_contrat, role_client,
                      date_demande, montant_finance, montant_echeance_mensuelle, nombre_total_echeances,
                      devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9202, '600920002', 9004, 'NOUVELLE_DEMANDE', 'Prêt personnel', 'TITULAIRE',
        '2026-06-15', 600000, 100000, 6, 'Ariary malgache', 'Mensuelle', 'ACTIF', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
(920201, 9202, 1, '2026-07-15', 100000, 100000, '2026-07-15', 'PAYE_A_TEMPS'),
(920202, 9202, 2, '2026-08-15', 100000, 100000, '2026-08-20', 'EN_RETARD'),
(920203, 9202, 3, '2026-09-15', 100000, NULL, NULL, 'A_VENIR'),
(920204, 9202, 4, '2026-10-15', 100000, NULL, NULL, 'A_VENIR'),
(920205, 9202, 5, '2026-11-15', 100000, NULL, NULL, 'A_VENIR'),
(920206, 9202, 6, '2026-12-15', 100000, NULL, NULL, 'A_VENIR')
ON CONFLICT (id) DO NOTHING;

INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code, prenom, nom, date_naissance,
                     ville_naissance, pays_naissance, genre, nationalite, etat_civil, date_adhesion, date_derniere_modification)
VALUES (9005, 'L00190005', 'Mr', '0004', 'Tovoniaina', 'Razafindrakoto', '1995-07-19',
        'Fianarantsoa', 'Madagascar', 'HOMME', 'Malgache', 'Célibataire', '2023-09-01', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, numero_rue, code_postal, ville, commune, region, pays, actuelle, date_derniere_modification)
VALUES (9005, 9005, 'Individu - Adresse principale', 'Lot 23 Ankidona', '23', '301', 'Fianarantsoa', 'Fianarantsoa I', 'Haute Matsiatra', 'Madagascar', true, now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO identifiant (id, client_id, type_identifiant, numero) VALUES (9005, 9005, 'CIN', '195071995003') ON CONFLICT (id) DO NOTHING;

INSERT INTO contrat (id, code_contrat_cb, client_id, mode_rattachement, type_contrat, role_client,
                      date_demande, montant_finance, montant_echeance_mensuelle, nombre_total_echeances,
                      devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9301, '600930001', 9005, 'NOUVELLE_DEMANDE', 'Prêt personnel', 'TITULAIRE',
        '2024-01-01', 1000000, 83333, 12, 'Ariary malgache', 'Mensuelle', 'FERME', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
(930101, 9301, 1, '2024-02-01', 83333, 83333, '2024-02-01', 'PAYE_A_TEMPS'),
(930102, 9301, 2, '2024-03-01', 83333, 83333, '2024-03-01', 'PAYE_A_TEMPS'),
(930103, 9301, 3, '2024-04-01', 83333, 83333, '2024-04-09', 'EN_RETARD'),
(930104, 9301, 4, '2024-05-01', 83333, 83333, '2024-05-01', 'PAYE_A_TEMPS'),
(930105, 9301, 5, '2024-06-01', 83333, 83333, '2024-06-13', 'EN_RETARD'),
(930106, 9301, 6, '2024-07-01', 83333, NULL, NULL, 'IMPAYE'),
(930107, 9301, 7, '2024-08-01', 83333, NULL, NULL, 'IMPAYE'),
(930108, 9301, 8, '2024-09-01', 83333, 83333, '2024-09-16', 'EN_RETARD'),
(930109, 9301, 9, '2024-10-01', 83333, NULL, NULL, 'IMPAYE'),
(930110, 9301, 10, '2024-11-01', 83333, NULL, NULL, 'IMPAYE'),
(930111, 9301, 11, '2024-12-01', 83333, NULL, NULL, 'IMPAYE'),
(930112, 9301, 12, '2025-01-01', 83333, NULL, NULL, 'IMPAYE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO contrat (id, code_contrat_cb, client_id, mode_rattachement, type_contrat, role_client,
                      date_demande, montant_finance, montant_echeance_mensuelle, nombre_total_echeances,
                      devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9302, '600930002', 9005, 'NOUVELLE_DEMANDE', 'Prêt personnel', 'TITULAIRE',
        '2026-02-01', 400000, 50000, 8, 'Ariary malgache', 'Mensuelle', 'ACTIF', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
(930201, 9302, 1, '2026-03-01', 50000, 50000, '2026-03-01', 'PAYE_A_TEMPS'),
(930202, 9302, 2, '2026-04-01', 50000, 50000, '2026-04-11', 'EN_RETARD'),
(930203, 9302, 3, '2026-05-01', 50000, NULL, NULL, 'IMPAYE'),
(930204, 9302, 4, '2026-06-01', 50000, NULL, NULL, 'IMPAYE'),
(930205, 9302, 5, '2026-07-01', 50000, NULL, NULL, 'IMPAYE'),
(930206, 9302, 6, '2026-08-01', 50000, NULL, NULL, 'IMPAYE'),
(930207, 9302, 7, '2026-09-01', 50000, NULL, NULL, 'IMPAYE'),
(930208, 9302, 8, '2026-10-01', 50000, NULL, NULL, 'A_VENIR')
ON CONFLICT (id) DO NOTHING;

INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code, prenom, nom, date_naissance,
                     ville_naissance, pays_naissance, genre, nationalite, etat_civil, date_adhesion, date_derniere_modification)
VALUES (9006, 'L00190006', 'Mr', '0004', 'Nirina', 'Rakotomanana', '1980-09-02',
        'Fianarantsoa', 'Madagascar', 'HOMME', 'Malgache', 'Marié', '2019-09-01', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, numero_rue, code_postal, ville, commune, region, pays, actuelle, date_derniere_modification)
VALUES (9006, 9006, 'Individu - Adresse principale', 'Lot 34 Tsianolondroa', '34', '301', 'Fianarantsoa', 'Fianarantsoa I', 'Haute Matsiatra', 'Madagascar', true, now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO identifiant (id, client_id, type_identifiant, numero) VALUES (9006, 9006, 'CIN', '180091980005') ON CONFLICT (id) DO NOTHING;

INSERT INTO contrat (id, code_contrat_cb, client_id, mode_rattachement, type_contrat, role_client,
                      date_demande, montant_finance, montant_echeance_mensuelle, nombre_total_echeances,
                      devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9401, '600940001', 9006, 'NOUVELLE_DEMANDE', 'Prêt personnel', 'TITULAIRE',
        '2020-01-01', 3000000, 62500, 36, 'Ariary malgache', 'Mensuelle', 'FERME', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
(940101, 9401, 1, '2020-02-01', 62500, 62500, '2020-02-01', 'PAYE_A_TEMPS'),
(940102, 9401, 2, '2020-03-01', 62500, 62500, '2020-03-01', 'PAYE_A_TEMPS'),
(940103, 9401, 3, '2020-04-01', 62500, 62500, '2020-04-01', 'PAYE_A_TEMPS'),
(940104, 9401, 4, '2020-05-01', 62500, 62500, '2020-05-01', 'PAYE_A_TEMPS'),
(940105, 9401, 5, '2020-06-01', 62500, 62500, '2020-06-10', 'EN_RETARD'),
(940106, 9401, 6, '2020-07-01', 62500, 62500, '2020-07-01', 'PAYE_A_TEMPS'),
(940107, 9401, 7, '2020-08-01', 62500, 62500, '2020-08-01', 'PAYE_A_TEMPS'),
(940108, 9401, 8, '2020-09-01', 62500, 62500, '2020-09-01', 'PAYE_A_TEMPS'),
(940109, 9401, 9, '2020-10-01', 62500, 62500, '2020-10-01', 'PAYE_A_TEMPS'),
(940110, 9401, 10, '2020-11-01', 62500, 62500, '2020-11-01', 'PAYE_A_TEMPS'),
(940111, 9401, 11, '2020-12-01', 62500, 62500, '2020-12-01', 'PAYE_A_TEMPS'),
(940112, 9401, 12, '2021-01-01', 62500, 62500, '2021-01-01', 'PAYE_A_TEMPS'),
(940113, 9401, 13, '2021-02-01', 62500, 62500, '2021-02-01', 'PAYE_A_TEMPS'),
(940114, 9401, 14, '2021-03-01', 62500, 62500, '2021-03-01', 'PAYE_A_TEMPS'),
(940115, 9401, 15, '2021-04-01', 62500, 62500, '2021-04-01', 'PAYE_A_TEMPS'),
(940116, 9401, 16, '2021-05-01', 62500, 62500, '2021-05-01', 'PAYE_A_TEMPS'),
(940117, 9401, 17, '2021-06-01', 62500, 62500, '2021-06-01', 'PAYE_A_TEMPS'),
(940118, 9401, 18, '2021-07-01', 62500, 62500, '2021-07-01', 'PAYE_A_TEMPS'),
(940119, 9401, 19, '2021-08-01', 62500, 62500, '2021-08-01', 'PAYE_A_TEMPS'),
(940120, 9401, 20, '2021-09-01', 62500, 62500, '2021-09-01', 'PAYE_A_TEMPS'),
(940121, 9401, 21, '2021-10-01', 62500, 62500, '2021-10-01', 'PAYE_A_TEMPS'),
(940122, 9401, 22, '2021-11-01', 62500, 62500, '2021-11-01', 'PAYE_A_TEMPS'),
(940123, 9401, 23, '2021-12-01', 62500, 62500, '2021-12-01', 'PAYE_A_TEMPS'),
(940124, 9401, 24, '2022-01-01', 62500, 62500, '2022-01-01', 'PAYE_A_TEMPS'),
(940125, 9401, 25, '2022-02-01', 62500, 62500, '2022-02-01', 'PAYE_A_TEMPS'),
(940126, 9401, 26, '2022-03-01', 62500, 62500, '2022-03-01', 'PAYE_A_TEMPS'),
(940127, 9401, 27, '2022-04-01', 62500, 62500, '2022-04-01', 'PAYE_A_TEMPS'),
(940128, 9401, 28, '2022-05-01', 62500, 62500, '2022-05-01', 'PAYE_A_TEMPS'),
(940129, 9401, 29, '2022-06-01', 62500, 62500, '2022-06-01', 'PAYE_A_TEMPS'),
(940130, 9401, 30, '2022-07-01', 62500, 62500, '2022-07-01', 'PAYE_A_TEMPS'),
(940131, 9401, 31, '2022-08-01', 62500, 62500, '2022-08-01', 'PAYE_A_TEMPS'),
(940132, 9401, 32, '2022-09-01', 62500, 62500, '2022-09-01', 'PAYE_A_TEMPS'),
(940133, 9401, 33, '2022-10-01', 62500, 62500, '2022-10-01', 'PAYE_A_TEMPS'),
(940134, 9401, 34, '2022-11-01', 62500, 62500, '2022-11-01', 'PAYE_A_TEMPS'),
(940135, 9401, 35, '2022-12-01', 62500, 62500, '2022-12-01', 'PAYE_A_TEMPS'),
(940136, 9401, 36, '2023-01-01', 62500, 62500, '2023-01-01', 'PAYE_A_TEMPS')
ON CONFLICT (id) DO NOTHING;

INSERT INTO contrat (id, code_contrat_cb, client_id, mode_rattachement, type_contrat, role_client,
                      date_demande, montant_finance, montant_echeance_mensuelle, nombre_total_echeances,
                      devise, periodicite_paiement, phase_demande, date_derniere_modification)
VALUES (9402, '600940002', 9006, 'NOUVELLE_DEMANDE', 'Prêt personnel', 'TITULAIRE',
        '2026-05-01', 500000, 100000, 5, 'Ariary malgache', 'Mensuelle', 'ACTIF', now())
ON CONFLICT (id) DO NOTHING;

INSERT INTO echeance (id, contrat_id, numero_echeance, date_echeance, montant_du, montant_paye, date_paiement, statut) VALUES
(940201, 9402, 1, '2026-06-01', 100000, 100000, '2026-06-01', 'PAYE_A_TEMPS'),
(940202, 9402, 2, '2026-07-01', 100000, 100000, '2026-07-03', 'EN_RETARD'),
(940203, 9402, 3, '2026-08-01', 100000, 100000, '2026-08-01', 'PAYE_A_TEMPS'),
(940204, 9402, 4, '2026-09-01', 100000, NULL, NULL, 'A_VENIR'),
(940205, 9402, 5, '2026-10-01', 100000, NULL, NULL, 'A_VENIR')
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('client', 'id'), (SELECT MAX(id) FROM client));
SELECT setval(pg_get_serial_sequence('adresse', 'id'), (SELECT MAX(id) FROM adresse));
SELECT setval(pg_get_serial_sequence('identifiant', 'id'), (SELECT MAX(id) FROM identifiant));
SELECT setval(pg_get_serial_sequence('contrat', 'id'), (SELECT MAX(id) FROM contrat));
SELECT setval(pg_get_serial_sequence('echeance', 'id'), (SELECT MAX(id) FROM echeance));
SELECT setval(pg_get_serial_sequence('utilisateur', 'id'), (SELECT MAX(id) FROM utilisateur));