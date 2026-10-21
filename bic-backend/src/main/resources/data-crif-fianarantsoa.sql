-- data-crif-fianarantsoa.sql
-- ============================================================================
-- CLIENTS RÉELS DES RAPPORTS CRIF FOURNIS (Fianarantsoa) — Perpétue, Clotilde, Léon Marcel, Marie Florence
-- Source : rapports CRIF du 25/02/2026, 03/12/2025, 13/10/2025, 25/02/2026.
-- DONNÉES PERSONNELLES CONFIDENTIELLES : ne pas versionner ce fichier (voir .gitignore).
-- Idempotent : DELETE puis INSERT. Plages utilisées : clients 9101-9104, contrats 9901-9960.
-- Choix de modélisation :
--  * un crédit de groupe partagé n'existe qu'une fois (code_contrat_cb unique) : il est porté par Perpétue
--    et relié aux co-titulaires via contrat_lien (Marie Florence -> client_lie_id = 9104) ;
--  * les retards CRIF (1-29, 30-59, 60-89 jours) sont stockés en statut texte, jours_de_retard reste NULL
--    (aucun nombre de jours inventé) ;
--  * les mois « Trop récent » / « NDIS » ne sont pas des observations de paiement ;
--  * date_adhesion = date du plus ancien contrat connu (la date d'adhésion CECAM réelle est inconnue) ;
--  * revenu annuel : seulement si fourni par le rapport (Clotilde 8 000 000 ; Léon 8 200 000), sinon NULL ;
--  * évènement négatif de Marie Florence (faillite/contentieux 31/05/2023) dans evenement_negatif.
-- ============================================================================

CREATE TABLE IF NOT EXISTS evenement_negatif (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES client(id),
    type VARCHAR(255) NOT NULL,
    description_evenement VARCHAR(255),
    date_evenement DATE,
    code_etablissement_crypte VARCHAR(40),
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- Nettoyage
DELETE FROM bien_leasing WHERE contrat_id IN (SELECT id FROM contrat WHERE code_contrat_cb IN ('K00717258','A00693573','R00676443','G00654931','A00504452','M00483592','H00478392','H00453432','700711720','D00722765','M00674656','A00588086','H00589520','000579649','000527079','500442533','B00435140','500407838','L00715017','600637342','I00630115','H00567810','J00544279','G00558294','A00552639','A00552242','500546531','E00515354','O00492229','J00490687','I00504340','R00504854','B00497397','G00472912','S00444944','A00447628','400701753','I00697290','O00637033','200588948','400507174','K00708906','000730188') OR client_id IN (9101,9102,9103,9104) OR id BETWEEN 9901 AND 9943);
DELETE FROM demande_modification_contrat WHERE contrat_id IN (SELECT id FROM contrat WHERE code_contrat_cb IN ('K00717258','A00693573','R00676443','G00654931','A00504452','M00483592','H00478392','H00453432','700711720','D00722765','M00674656','A00588086','H00589520','000579649','000527079','500442533','B00435140','500407838','L00715017','600637342','I00630115','H00567810','J00544279','G00558294','A00552639','A00552242','500546531','E00515354','O00492229','J00490687','I00504340','R00504854','B00497397','G00472912','S00444944','A00447628','400701753','I00697290','O00637033','200588948','400507174','K00708906','000730188') OR client_id IN (9101,9102,9103,9104) OR id BETWEEN 9901 AND 9943);
DELETE FROM revenu_demande WHERE contrat_id IN (SELECT id FROM contrat WHERE code_contrat_cb IN ('K00717258','A00693573','R00676443','G00654931','A00504452','M00483592','H00478392','H00453432','700711720','D00722765','M00674656','A00588086','H00589520','000579649','000527079','500442533','B00435140','500407838','L00715017','600637342','I00630115','H00567810','J00544279','G00558294','A00552639','A00552242','500546531','E00515354','O00492229','J00490687','I00504340','R00504854','B00497397','G00472912','S00444944','A00447628','400701753','I00697290','O00637033','200588948','400507174','K00708906','000730188') OR client_id IN (9101,9102,9103,9104) OR id BETWEEN 9901 AND 9943);
DELETE FROM historique_paiement_mensuel WHERE contrat_id IN (SELECT id FROM contrat WHERE code_contrat_cb IN ('K00717258','A00693573','R00676443','G00654931','A00504452','M00483592','H00478392','H00453432','700711720','D00722765','M00674656','A00588086','H00589520','000579649','000527079','500442533','B00435140','500407838','L00715017','600637342','I00630115','H00567810','J00544279','G00558294','A00552639','A00552242','500546531','E00515354','O00492229','J00490687','I00504340','R00504854','B00497397','G00472912','S00444944','A00447628','400701753','I00697290','O00637033','200588948','400507174','K00708906','000730188') OR client_id IN (9101,9102,9103,9104) OR id BETWEEN 9901 AND 9943);
DELETE FROM garantie WHERE contrat_id IN (SELECT id FROM contrat WHERE code_contrat_cb IN ('K00717258','A00693573','R00676443','G00654931','A00504452','M00483592','H00478392','H00453432','700711720','D00722765','M00674656','A00588086','H00589520','000579649','000527079','500442533','B00435140','500407838','L00715017','600637342','I00630115','H00567810','J00544279','G00558294','A00552639','A00552242','500546531','E00515354','O00492229','J00490687','I00504340','R00504854','B00497397','G00472912','S00444944','A00447628','400701753','I00697290','O00637033','200588948','400507174','K00708906','000730188') OR client_id IN (9101,9102,9103,9104) OR id BETWEEN 9901 AND 9943);
DELETE FROM echeance WHERE contrat_id IN (SELECT id FROM contrat WHERE code_contrat_cb IN ('K00717258','A00693573','R00676443','G00654931','A00504452','M00483592','H00478392','H00453432','700711720','D00722765','M00674656','A00588086','H00589520','000579649','000527079','500442533','B00435140','500407838','L00715017','600637342','I00630115','H00567810','J00544279','G00558294','A00552639','A00552242','500546531','E00515354','O00492229','J00490687','I00504340','R00504854','B00497397','G00472912','S00444944','A00447628','400701753','I00697290','O00637033','200588948','400507174','K00708906','000730188') OR client_id IN (9101,9102,9103,9104) OR id BETWEEN 9901 AND 9943);
DELETE FROM contrat_lien WHERE contrat_id IN (SELECT id FROM contrat WHERE code_contrat_cb IN ('K00717258','A00693573','R00676443','G00654931','A00504452','M00483592','H00478392','H00453432','700711720','D00722765','M00674656','A00588086','H00589520','000579649','000527079','500442533','B00435140','500407838','L00715017','600637342','I00630115','H00567810','J00544279','G00558294','A00552639','A00552242','500546531','E00515354','O00492229','J00490687','I00504340','R00504854','B00497397','G00472912','S00444944','A00447628','400701753','I00697290','O00637033','200588948','400507174','K00708906','000730188') OR client_id IN (9101,9102,9103,9104) OR id BETWEEN 9901 AND 9943) OR client_lie_id IN (9101,9102,9103,9104);
DELETE FROM evenement_negatif WHERE client_id IN (9101,9102,9103,9104);
DELETE FROM dernier_contrat WHERE contrat_id IN (SELECT id FROM contrat WHERE code_contrat_cb IN ('K00717258','A00693573','R00676443','G00654931','A00504452','M00483592','H00478392','H00453432','700711720','D00722765','M00674656','A00588086','H00589520','000579649','000527079','500442533','B00435140','500407838','L00715017','600637342','I00630115','H00567810','J00544279','G00558294','A00552639','A00552242','500546531','E00515354','O00492229','J00490687','I00504340','R00504854','B00497397','G00472912','S00444944','A00447628','400701753','I00697290','O00637033','200588948','400507174','K00708906','000730188') OR client_id IN (9101,9102,9103,9104) OR id BETWEEN 9901 AND 9943);
DELETE FROM contrat WHERE code_contrat_cb IN ('K00717258','A00693573','R00676443','G00654931','A00504452','M00483592','H00478392','H00453432','700711720','D00722765','M00674656','A00588086','H00589520','000579649','000527079','500442533','B00435140','500407838','L00715017','600637342','I00630115','H00567810','J00544279','G00558294','A00552639','A00552242','500546531','E00515354','O00492229','J00490687','I00504340','R00504854','B00497397','G00472912','S00444944','A00447628','400701753','I00697290','O00637033','200588948','400507174','K00708906','000730188') OR client_id IN (9101,9102,9103,9104) OR id BETWEEN 9901 AND 9943;
DELETE FROM adresse WHERE client_id IN (9101,9102,9103,9104);
DELETE FROM identifiant WHERE client_id IN (9101,9102,9103,9104);
DELETE FROM emploi WHERE client_id IN (9101,9102,9103,9104);
DELETE FROM client WHERE id IN (9101,9102,9103,9104) OR code_client_cb IN ('B00060035','K00054802','R00073573','400030668');

-- ---------------- CLIENT 9101 : PERPETUE RASOARIMALALA
INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code, prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance, genre, nationalite, etat_civil, agence, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9101, 'B00060035', 'Mme', '0004', 'PERPETUE', NULL, 'RASOARIMALALA', '1972-03-25', 'MAEVATANANA', 'MADAGASCAR', 'FEMME', 'Malgache', 'Marié(e)', 'FIANARANTSOA', '0327590821', '2021-08-11', '2026-02-25 00:00:00', 'RAPPORT_CRIF');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99201, 9101, 'Individu - Adresse Principale', 'AMBODIHADY', true, '2026-02-25 00:00:00');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99202, 9101, 'Individu - Adresse Principale', 'FIANARANTSOA', false, '2025-11-04 00:00:00');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99203, 9101, 'Individu - Adresse Principale', 'TSARAMANDROSO', false, '2024-11-30 00:00:00');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99204, 9101, 'Individu - Adresse Principale', 'LOT IVF 47 AMBODIHADY', false, '2014-11-05 00:00:00');
INSERT INTO identifiant (id, client_id, type_identifiant, numero) VALUES (99301, 9101, 'CIN', '201012003063');
INSERT INTO emploi (id, client_id, statut_emploi, nom_employeur, profession, date_embauche, revenu_annuel_total, devise) VALUES (99301, 9101, NULL, NULL, 'Planteur de riz', NULL, NULL, NULL);

-- ---------------- CLIENT 9102 : CLOTILDE SAHOLINIRINA
INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code, prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance, genre, nationalite, etat_civil, agence, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9102, 'K00054802', 'Mme', '0004', 'CLOTILDE', NULL, 'SAHOLINIRINA', '1981-07-16', 'FIANARANTSOA', 'MADAGASCAR', 'FEMME', 'Malgache', NULL, 'FIANARANTSOA', '034.41.135.93', '2019-02-15', '2025-04-08 00:00:00', 'RAPPORT_CRIF');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99205, 9102, 'Individu - Adresse Principale', 'ANKOFAFA', true, '2025-04-08 00:00:00');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99206, 9102, 'Individu - Adresse Principale', 'ANTANIFOTSY ANKOFAFA', false, '2024-08-31 00:00:00');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99207, 9102, 'Individu - Adresse Principale', 'ANTARANDOLO', false, '2023-08-29 00:00:00');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99208, 9102, 'Individu - Adresse Principale', 'LOT 204 AE/3602 ANTSENA ANKOFAFA AMBONY FIANARANTSOA I, 1101004, MG', false, '2016-09-20 00:00:00');
INSERT INTO identifiant (id, client_id, type_identifiant, numero) VALUES (99302, 9102, 'CIN', '201032019112');
INSERT INTO emploi (id, client_id, statut_emploi, nom_employeur, profession, date_embauche, revenu_annuel_total, devise) VALUES (99302, 9102, NULL, NULL, 'Planteur de riz', NULL, 8000000, 'Ariary malgache');

-- ---------------- CLIENT 9103 : LÉON MARCEL RAKOTONIAINA
INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code, prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance, genre, nationalite, etat_civil, agence, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9103, 'R00073573', 'M.', '0004', 'LÉON MARCEL', NULL, 'RAKOTONIAINA', '1978-08-26', 'TANAMBAO', 'MADAGASCAR', 'HOMME', 'Malgache', 'Marié(e)', 'FIANARANTSOA', NULL, '2021-05-22', '2024-10-09 00:00:00', 'RAPPORT_CRIF');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99209, 9103, 'Individu - Adresse Principale', 'AMBOHINAMBOARINA', true, '2024-10-09 00:00:00');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99210, 9103, 'Individu - Adresse Principale', 'FIANARANTSOA', false, '2022-10-24 00:00:00');
INSERT INTO identifiant (id, client_id, type_identifiant, numero) VALUES (99303, 9103, 'CIN', '208231000814');
INSERT INTO emploi (id, client_id, statut_emploi, nom_employeur, profession, date_embauche, revenu_annuel_total, devise) VALUES (99303, 9103, 'Autre', NULL, 'Planteur de riz', NULL, 8200000, 'Ariary malgache');

-- ---------------- CLIENT 9104 : MARIE FLORENCE RAVAOARISOA
INSERT INTO client (id, code_client_cb, titre, categorie_tiers_code, prenom, deuxieme_prenom, nom, date_naissance, ville_naissance, pays_naissance, genre, nationalite, etat_civil, agence, telephone, date_adhesion, date_derniere_modification, origine_donnee)
VALUES (9104, '400030668', 'Mme', '0004', 'MARIE FLORENCE', NULL, 'RAVAOARISOA', '1978-06-22', 'MTE FIANARANTSOA', 'MADAGASCAR', 'FEMME', 'Malgache', 'Marié(e)', 'FIANARANTSOA', '261345193157', '2021-08-11', '2025-11-04 00:00:00', 'RAPPORT_CRIF');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99211, 9104, 'Individu - Adresse Principale', 'FIANARANTSOA', true, '2025-11-04 00:00:00');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99212, 9104, 'Individu - Adresse Principale', 'TSARAMANDROSO', false, '2024-11-30 00:00:00');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99213, 9104, 'Individu - Adresse Principale', 'LOT FI004/3704 AMBODIHARANA FIANARANTSOA I', false, '2024-11-30 00:00:00');
INSERT INTO adresse (id, client_id, type_adresse, adresse_complete, actuelle, date_derniere_modification) VALUES (99214, 9104, 'Individu - Adresse Principale', 'LOT FI AMBODIHARANA HAUTE VILLE FIANARANTSOA', false, '2023-12-05 00:00:00');
INSERT INTO identifiant (id, client_id, type_identifiant, numero) VALUES (99304, 9104, 'CIN', '201012007486');
INSERT INTO emploi (id, client_id, statut_emploi, nom_employeur, profession, date_embauche, revenu_annuel_total, devise) VALUES (99304, 9104, NULL, NULL, 'Détaillant en magasin de produits locaux agricoles et de volaille', NULL, NULL, NULL);

INSERT INTO evenement_negatif (client_id, type, description_evenement, date_evenement, code_etablissement_crypte, actif) VALUES (9104, 'Société en faillite (dépôt de bilan)', 'CONTENTIEUX', '2023-05-31', '002', true);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, date_prochaine_echeance, montant_prochaine_echeance, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, nombre_echeances_restantes, montant_restant_du, nombre_echeances_impayees, montant_impayes, max_montant_impayes, max_nombre_echeances_impayees, devise, periodicite_paiement, pire_statut, phase_demande, date_derniere_modification, origine_donnee) VALUES (9901, 'K00717258', '0204201665032004', '001', '001', 9101, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'CO_TITULAIRE', '2025-11-07', '2026-02-26', '2025-11-27', '2026-01-28', '2026-02-26', 1282500, 3600000, 1107486, 4, 1, 1282500, 1, 159521, 159521, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'ACTIF', '2026-01-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9901, '2025-11-01', 1, 0, NULL, 'Payé à temps'),
 (9901, '2025-12-01', 1, 0, NULL, 'Payé à temps'),
 (9901, '2026-01-01', 1, 1, NULL, '1-29 jours de retard');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99001, 9901, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAN5879 (50)', 'THERESE RAVAORISOA', 'J00079207', 1);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99101, 9901, 'J00079207', 'THERESE RAVAORISOA', 'TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99102, 9901, 'G00084801', 'SOLOMAMPIONONA JULIE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99103, 9901, 'J00155225', 'HARANTANIRINA LYDIA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99104, 9901, 'A00080573', 'NAMBINIARISOA ONJANIAINA MARIE ZATIANAH', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99105, 9901, 'E00079281', 'RAZAFIARISOARIMALALA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99106, 9901, 'P00106546', 'ARLETTE RANDRIAMAMPIONONA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99107, 9901, '400030668', 'MARIE FLORENCE RAVAOARISOA', 'CO_TITULAIRE', 9104);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99108, 9901, 'J00159262', 'SAHOLINIRINA ELISA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99109, 9901, 'O00116708', 'MBOLATIANA ANJARASOA ANJARASOA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99110, 9901, '800056641', 'CECILE MARCELLE RASOAZANANY', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99111, 9901, 'A00155949', 'MARTINE RASOAMALALA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99112, 9901, '100162332', 'RASOAMIAFARA ANNIE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99113, 9901, 'E00030852', 'RAVAOARISOA FANJANIRINA FRANCIA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99114, 9901, 'L00172855', 'VOLOLOMBOAHIRANA RAHARINAIVO', 'CO_TITULAIRE', NULL);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9902, 'A00693573', '0204201665032003', '001', '001', 9101, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'CO_TITULAIRE', '2025-07-18', '2025-11-30', '2025-07-24', '2025-11-03', 2900000, 893514, 5, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2025-11-30 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9902, '2025-07-01', 1, 0, NULL, 'Payé à temps'),
 (9902, '2025-08-01', 1, 0, NULL, 'Payé à temps'),
 (9902, '2025-09-01', 1, 0, NULL, 'Payé à temps'),
 (9902, '2025-10-01', 1, 0, NULL, 'Payé à temps'),
 (9902, '2025-11-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99002, 9902, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAN5490 (56)', 'THERESE RAVAORISOA', 'J00079207', 1);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99115, 9902, 'J00079207', 'THERESE RAVAORISOA', 'TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99116, 9902, 'G00084801', 'SOLOMAMPIONONA JULIE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99117, 9902, 'L00168515', 'RAZAFY FANOMEZANTSOA EMMANUELINE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99118, 9902, '400030668', 'MARIE FLORENCE RAVAOARISOA', 'CO_TITULAIRE', 9104);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99119, 9902, '100162332', 'RASOAMIAFARA ANNIE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99120, 9902, 'E00079281', 'RAZAFIARISOARIMALALA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99121, 9902, 'O00116708', 'MBOLATIANA ANJARASOA ANJARASOA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99122, 9902, 'S00057064', 'JULIA LUCETTE SAHOBINIRINA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99123, 9902, 'J00155225', 'HARANTANIRINA LYDIA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99124, 9902, 'J00159262', 'SAHOLINIRINA ELISA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99125, 9902, 'A00080573', 'NAMBINIARISOA ONJANIAINA MARIE ZATIANAH', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99126, 9902, '800056641', 'CECILE MARCELLE RASOAZANANY', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99127, 9902, 'F00159497', 'RASOANANDRASANA AVISOANIRINA MARIE NATACHA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99128, 9902, 'A00155949', 'MARTINE RASOAMALALA', 'CO_TITULAIRE', NULL);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9903, 'R00676443', '0204201665032002', '001', '001', 9101, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'CO_TITULAIRE', '2025-03-07', '2025-07-31', '2025-03-27', '2025-07-07', 3440000, 997863, 5, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_ANTICIPEE', '2025-07-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9903, '2025-03-01', 1, 0, NULL, 'Payé à temps'),
 (9903, '2025-04-01', 1, 0, NULL, 'Payé à temps'),
 (9903, '2025-05-01', 1, 0, NULL, 'Payé à temps'),
 (9903, '2025-06-01', 1, 0, NULL, 'Payé à temps'),
 (9903, '2025-07-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99003, 9903, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAN4882 (67)', 'VIVIANE RASOANANTENAINA', '000151166', 1);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99129, 9903, '000151166', 'VIVIANE RASOANANTENAINA', 'TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99130, 9903, 'J00155225', 'HARANTANIRINA LYDIA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99131, 9903, 'N00081555', 'RAHARIMAMPITEBITERA REGINE ABDONNE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99132, 9903, 'E00079281', 'RAZAFIARISOARIMALALA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99133, 9903, 'A00080573', 'NAMBINIARISOA ONJANIAINA MARIE ZATIANAH', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99134, 9903, '400030668', 'MARIE FLORENCE RAVAOARISOA', 'CO_TITULAIRE', 9104);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99135, 9903, 'C00068306', 'GEORGINE RASOAMALALA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99136, 9903, '600134501', 'RAZAFINDRAMANANA MAMINIAINA JULIETTE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99137, 9903, 'K00151055', 'RAZANAJAZA BRIGITTE OMEGA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99138, 9903, 'G00084801', 'SOLOMAMPIONONA JULIE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99139, 9903, 'A00155949', 'MARTINE RASOAMALALA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99140, 9903, 'J00079207', 'THERESE RAVAORISOA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99141, 9903, 'I00081678', 'RAHARINAMBININA LALATIANA MURNEL', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99142, 9903, '100162332', 'RASOAMIAFARA ANNIE', 'CO_TITULAIRE', NULL);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9904, 'G00654931', '0204201665032001', '001', '001', 9101, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'CO_TITULAIRE', '2024-11-21', '2025-03-31', '2024-11-28', '2025-03-03', 2700000, 825201, 5, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2025-03-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9904, '2024-11-01', 1, 0, NULL, 'Payé à temps'),
 (9904, '2024-12-01', 1, 0, NULL, 'Payé à temps'),
 (9904, '2025-01-01', 1, 0, NULL, 'Payé à temps'),
 (9904, '2025-02-01', 1, 0, NULL, 'Payé à temps'),
 (9904, '2025-03-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99004, 9904, 'REELLE', 'Caution mutuelle', 'CAU4332 (5)', 'VIVIANE RASOANANTENAINA', '000151166', 1);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99143, 9904, '000151166', 'VIVIANE RASOANANTENAINA', 'TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99144, 9904, 'E00079281', 'RAZAFIARISOARIMALALA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99145, 9904, 'K00151055', 'RAZANAJAZA BRIGITTE OMEGA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99146, 9904, '600134501', 'RAZAFINDRAMANANA MAMINIAINA JULIETTE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99147, 9904, 'C00068306', 'GEORGINE RASOAMALALA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99148, 9904, 'A00080573', 'NAMBINIARISOA ONJANIAINA MARIE ZATIANAH', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99149, 9904, 'A00155949', 'MARTINE RASOAMALALA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99150, 9904, '300156721', 'FARANIAINA PATRICIA JET LAI RAZANAKOTO', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99151, 9904, '400030668', 'MARIE FLORENCE RAVAOARISOA', 'CO_TITULAIRE', 9104);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99152, 9904, 'J00079207', 'THERESE RAVAORISOA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99153, 9904, 'I00081678', 'RAHARINAMBININA LALATIANA MURNEL', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99154, 9904, 'N00081555', 'RAHARIMAMPITEBITERA REGINE ABDONNE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99155, 9904, '800056641', 'CECILE MARCELLE RASOAZANANY', 'CO_TITULAIRE', NULL);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, max_montant_impayes, max_nombre_echeances_impayees, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9905, 'A00504452', '0202703251032004', '001', '001', 9101, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'CO_TITULAIRE', '2022-07-26', '2022-12-16', '2022-08-24', '2022-10-31', 4150000, 1254907, 4, 621016, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2022-12-16 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9905, '2022-07-01', 1, 0, NULL, 'Payé à temps'),
 (9905, '2022-08-01', 1, 0, NULL, 'Payé à temps'),
 (9905, '2022-09-01', 1, 0, NULL, 'Payé à temps'),
 (9905, '2022-10-01', 1, 1, NULL, '1-29 jours de retard'),
 (9905, '2022-11-01', 1, 1, NULL, '1-29 jours de retard'),
 (9905, '2022-12-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99005, 9905, 'REELLE', 'Caution mutuelle', 'CAU5558 (5)', 'RASOANJANAHARY AUBINE VALERIE', 'E00028148', 1);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99156, 9905, 'E00028148', 'RASOANJANAHARY AUBINE VALERIE', 'TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99157, 9905, 'E00079281', 'RAZAFIARISOARIMALALA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99158, 9905, 'I00081678', 'RAHARINAMBININA LALATIANA MURNEL', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99159, 9905, 'G00064149', 'RANOROMALALA VOLAMANANA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99160, 9905, '400030668', 'MARIE FLORENCE RAVAOARISOA', 'CO_TITULAIRE', 9104);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99161, 9905, 'A00080573', 'NAMBINIARISOA ONJANIAINA MARIE ZATIANAH', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99162, 9905, 'H00081436', 'RASOANJANAHARY NICOLE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99163, 9905, '400031758', 'RANDRIANARIMANANA,SANDRA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99164, 9905, 'C00068306', 'GEORGINE RASOAMALALA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99165, 9905, 'G00081438', 'FANAMBINANTSOA LOVATIANA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99166, 9905, 'N00081555', 'RAHARIMAMPITEBITERA REGINE ABDONNE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99167, 9905, 'J00079207', 'THERESE RAVAORISOA', 'CO_TITULAIRE', NULL);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9906, 'M00483592', '0202703251032003', '001', '001', 9101, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'CO_TITULAIRE', '2022-04-06', '2022-07-22', '2022-04-27', '2022-07-22', 3000000, 750000, 4, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_ANTICIPEE', '2022-07-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9906, '2022-04-01', 1, 0, NULL, 'Payé à temps'),
 (9906, '2022-05-01', 1, 0, NULL, 'Payé à temps'),
 (9906, '2022-06-01', 1, 0, NULL, 'Payé à temps'),
 (9906, '2022-07-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99006, 9906, 'REELLE', 'Caution mutuelle', 'CAU5289 (4)', 'RASOANJANAHARY AUBINE VALERIE', 'E00028148', 1);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99168, 9906, 'E00028148', 'RASOANJANAHARY AUBINE VALERIE', 'TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99169, 9906, 'E00079281', 'RAZAFIARISOARIMALALA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99170, 9906, '400031758', 'RANDRIANARIMANANA,SANDRA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99171, 9906, 'G00064149', 'RANOROMALALA VOLAMANANA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99172, 9906, '400030668', 'MARIE FLORENCE RAVAOARISOA', 'CO_TITULAIRE', 9104);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99173, 9906, 'A00080573', 'NAMBINIARISOA ONJANIAINA MARIE ZATIANAH', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99174, 9906, 'N00081555', 'RAHARIMAMPITEBITERA REGINE ABDONNE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99175, 9906, 'C00068306', 'GEORGINE RASOAMALALA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99176, 9906, 'I00081678', 'RAHARINAMBININA LALATIANA MURNEL', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99177, 9906, 'J00079207', 'THERESE RAVAORISOA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99178, 9906, 'G00081438', 'FANAMBINANTSOA LOVATIANA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99179, 9906, 'H00081436', 'RASOANJANAHARY NICOLE', 'CO_TITULAIRE', NULL);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9907, 'H00478392', '0202703251032002', '001', '001', 9101, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'CO_TITULAIRE', '2021-12-09', '2022-03-30', '2022-02-28', '2022-03-30', 2650000, 816486, 4, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_ANTICIPEE', '2022-03-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9907, '2021-12-01', 1, 0, NULL, 'Payé à temps'),
 (9907, '2022-01-01', 1, 0, NULL, 'Payé à temps'),
 (9907, '2022-02-01', 1, 0, NULL, 'Payé à temps'),
 (9907, '2022-03-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99007, 9907, 'REELLE', 'Caution mutuelle', 'CAU5086 (6)', 'RASOANJANAHARY AUBINE VALERIE', 'E00028148', 1);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99180, 9907, 'E00028148', 'RASOANJANAHARY AUBINE VALERIE', 'TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99181, 9907, 'E00079281', 'RAZAFIARISOARIMALALA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99182, 9907, '400031758', 'RANDRIANARIMANANA,SANDRA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99183, 9907, 'G00064149', 'RANOROMALALA VOLAMANANA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99184, 9907, '400030668', 'MARIE FLORENCE RAVAOARISOA', 'CO_TITULAIRE', 9104);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99185, 9907, 'A00080573', 'NAMBINIARISOA ONJANIAINA MARIE ZATIANAH', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99186, 9907, 'N00081555', 'RAHARIMAMPITEBITERA REGINE ABDONNE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99187, 9907, 'C00068306', 'GEORGINE RASOAMALALA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99188, 9907, 'I00081678', 'RAHARINAMBININA LALATIANA MURNEL', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99189, 9907, 'J00079207', 'THERESE RAVAORISOA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99190, 9907, 'G00081438', 'FANAMBINANTSOA LOVATIANA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99191, 9907, 'H00081436', 'RASOANJANAHARY NICOLE', 'CO_TITULAIRE', NULL);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9908, 'H00453432', '0202703251032001', '001', '001', 9101, 'NOUVELLE_DEMANDE', 'Crédit au Groupement', 'CO_TITULAIRE', '2021-08-11', '2021-11-24', '2021-10-27', '2021-11-24', 2250000, 687668, 5, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2021-11-30 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9908, '2021-08-01', 1, 0, NULL, 'Payé à temps'),
 (9908, '2021-09-01', 1, 0, NULL, 'Payé à temps'),
 (9908, '2021-10-01', 1, 0, NULL, 'Payé à temps'),
 (9908, '2021-11-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99008, 9908, 'REELLE', 'Caution mutuelle', 'CAU4815 (5)', 'RASOANJANAHARY AUBINE VALERIE', 'E00028148', 1);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99192, 9908, 'E00028148', 'RASOANJANAHARY AUBINE VALERIE', 'TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99193, 9908, 'G00081438', 'FANAMBINANTSOA LOVATIANA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99194, 9908, 'I00081678', 'RAHARINAMBININA LALATIANA MURNEL', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99195, 9908, 'H00081436', 'RASOANJANAHARY NICOLE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99196, 9908, 'G00064149', 'RANOROMALALA VOLAMANANA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99197, 9908, '400031758', 'RANDRIANARIMANANA,SANDRA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99198, 9908, '400030668', 'MARIE FLORENCE RAVAOARISOA', 'CO_TITULAIRE', 9104);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99199, 9908, 'A00080573', 'NAMBINIARISOA ONJANIAINA MARIE ZATIANAH', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99200, 9908, 'C00068306', 'GEORGINE RASOAMALALA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99201, 9908, 'N00081555', 'RAHARIMAMPITEBITERA REGINE ABDONNE', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99202, 9908, 'E00079281', 'RAZAFIARISOARIMALALA PRISCA', 'CO_TITULAIRE', NULL);
INSERT INTO contrat_lien (id, contrat_id, code_client_cb, nom_client, role_client, client_lie_id) VALUES (99203, 9908, 'J00079207', 'THERESE RAVAORISOA', 'CO_TITULAIRE', NULL);

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_demande, montant_finance, montant_total_du, nombre_total_echeances, devise, phase_demande, date_derniere_modification, origine_donnee) VALUES (9909, '700711720', '001', '001', 9101, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE', '2025-11-04', 3600000, 3600000, 4, 'Ariary malgache', 'DEMANDE_EN_COURS', '2025-11-04 00:00:00', 'RAPPORT_CRIF');

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_demande, montant_finance, montant_total_du, nombre_total_echeances, devise, phase_demande, date_derniere_modification, origine_donnee) VALUES (9910, 'D00722765', '001', '001', 9101, 'NOUVELLE_DEMANDE', 'Crédit à la Consommation', 'TITULAIRE', '2026-02-25', 400000, 400000, 4, 'Ariary malgache', 'DEMANDE_EN_COURS', '2026-02-25 00:00:00', 'RAPPORT_CRIF');

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, date_prochaine_echeance, montant_prochaine_echeance, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, nombre_echeances_restantes, montant_restant_du, nombre_echeances_impayees, montant_impayes, max_montant_impayes, max_nombre_echeances_impayees, devise, periodicite_paiement, pire_statut, phase_demande, date_derniere_modification, origine_donnee) VALUES (9911, 'M00674656', '0202701573030004', '001', '001', 9102, 'NOUVELLE_DEMANDE', 'Crédits immobiliers pour l''habitat', 'TITULAIRE', '2025-04-11', '2027-03-25', '2025-05-25', '2025-10-25', '2025-11-25', 257928, 4000000, 234569, 23, 17, 3698622, 0, 0, 0, 0, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'ACTIF', '2025-10-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9911, '2025-04-01', 1, 0, NULL, 'Payé à temps'),
 (9911, '2025-05-01', 1, 0, NULL, 'Payé à temps'),
 (9911, '2025-06-01', 1, 0, NULL, 'Payé à temps'),
 (9911, '2025-07-01', 1, 0, NULL, 'Payé à temps'),
 (9911, '2025-08-01', 1, 0, NULL, 'Payé à temps'),
 (9911, '2025-09-01', 1, 0, NULL, 'Payé à temps'),
 (9911, '2025-10-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99009, 9911, 'REELLE', 'Caution mutuelle', 'CAU14295 (2)', 'MARIE OLIRAVIKINIAINA', '300027488', 1);
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99010, 9911, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM14278 (2)', 'CLOTILDE SAHOLINIRINA', 'K00054802', 6810000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, date_prochaine_echeance, montant_prochaine_echeance, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, nombre_echeances_restantes, montant_restant_du, nombre_echeances_impayees, montant_impayes, max_montant_impayes, max_nombre_echeances_impayees, devise, periodicite_paiement, pire_statut, phase_demande, date_derniere_modification, origine_donnee) VALUES (9912, 'A00588086', '0202701573014002', '001', '001', 9102, 'NOUVELLE_DEMANDE', 'Location-vente', 'TITULAIRE', '2023-12-14', '2026-11-25', '2024-01-25', '2025-10-25', '2025-11-25', 36283, 1050000, 41389, 36, 13, 399899, 0, 0, 43999, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'ACTIF', '2025-10-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9912, '2023-12-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2024-01-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2024-02-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2024-03-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2024-04-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2024-05-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2024-06-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2024-07-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2024-08-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2024-10-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2024-11-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2024-12-01', 1, 1, NULL, '1-29 jours de retard'),
 (9912, '2025-01-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2025-02-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2025-03-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2025-04-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2025-05-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2025-06-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2025-07-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2025-08-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2025-09-01', 1, 0, NULL, 'Payé à temps'),
 (9912, '2025-10-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99011, 9912, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM8693 (4)', 'CLOTILDE SAHOLINIRINA', 'K00054802', 700000);
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99012, 9912, 'REELLE', 'Caution mutuelle', 'CAU8694 (3)', 'MARIE OLIRAVIKINIAINA', '300027488', 1);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, max_montant_impayes, max_nombre_echeances_impayees, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9913, 'H00589520', '0202701573030003', '001', '001', 9102, 'NOUVELLE_DEMANDE', 'Crédits immobiliers pour l''habitat', 'TITULAIRE', '2023-10-06', '2025-03-31', '2023-11-25', '2025-03-28', 4500000, 249144, 23, 252167, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2025-03-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9913, '2023-10-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2023-11-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2023-12-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2024-01-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2024-02-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2024-03-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2024-04-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2024-05-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2024-06-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2024-07-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2024-08-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2024-10-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2024-11-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2024-12-01', 1, 1, NULL, '1-29 jours de retard'),
 (9913, '2025-01-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2025-02-01', 1, 0, NULL, 'Payé à temps'),
 (9913, '2025-03-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99013, 9913, 'REELLE', 'Caution mutuelle', 'CAU8058 (5)', 'MARIE OLIRAVIKINIAINA', '300027488', 1);
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99014, 9913, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM8040 (5)', 'CLOTILDE SAHOLINIRINA', 'K00054802', 6110000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9914, '000579649', '0202701573031007', '001', '001', 9102, 'NOUVELLE_DEMANDE', 'Crédits de trésorerie pour l''exploitation', 'TITULAIRE', '2023-08-31', '2023-11-30', '2023-09-25', '2023-11-30', 500000, 108719, 5, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2023-11-30 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9914, '2023-08-01', 1, 0, NULL, 'Payé à temps'),
 (9914, '2023-09-01', 1, 0, NULL, 'Payé à temps'),
 (9914, '2023-10-01', 1, 0, NULL, 'Payé à temps'),
 (9914, '2023-11-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99015, 9914, 'REELLE', 'Caution mutuelle', 'CAU7662 (3)', 'MARIE OLIRAVIKINIAINA', '300027488', 1);
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99016, 9914, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM7660 (6)', 'CLOTILDE SAHOLINIRINA', 'K00054802', 900000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9915, '000527079', '0202701573031006', '001', '001', 9102, 'NOUVELLE_DEMANDE', 'Crédits de trésorerie pour l''exploitation', 'TITULAIRE', '2022-10-27', '2023-09-25', '2022-11-27', '2023-09-25', 1200000, 125485, 11, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2023-09-30 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9915, '2022-10-01', 1, 0, NULL, 'Payé à temps'),
 (9915, '2022-11-01', 1, 0, NULL, 'Payé à temps'),
 (9915, '2022-12-01', 1, 0, NULL, 'Payé à temps'),
 (9915, '2023-01-01', 1, 0, NULL, 'Payé à temps'),
 (9915, '2023-02-01', 1, 0, NULL, 'Payé à temps'),
 (9915, '2023-03-01', 1, 0, NULL, 'Payé à temps'),
 (9915, '2023-04-01', 1, 0, NULL, 'Payé à temps'),
 (9915, '2023-05-01', 1, 0, NULL, 'Payé à temps'),
 (9915, '2023-06-01', 1, 0, NULL, 'Payé à temps'),
 (9915, '2023-07-01', 1, 0, NULL, 'Payé à temps'),
 (9915, '2023-08-01', 1, 0, NULL, 'Payé à temps'),
 (9915, '2023-09-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99017, 9915, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM5733 (10)', 'CLOTILDE SAHOLINIRINA', 'K00054802', 1720000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9916, '500442533', '0202701573030002', '001', '001', 9102, 'NOUVELLE_DEMANDE', 'Crédits immobiliers pour l''habitat', 'TITULAIRE', '2021-02-05', '2023-01-31', '2021-03-31', '2023-01-31', 3200000, 179776, 23, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_ANTICIPEE', '2023-01-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9916, '2021-02-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2021-03-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2021-04-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2021-05-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2021-06-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2021-07-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2021-08-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2021-09-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2021-10-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2021-11-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2021-12-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-01-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-02-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-03-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-04-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-05-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-06-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-07-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-08-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-09-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-10-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-11-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2022-12-01', 1, 0, NULL, 'Payé à temps'),
 (9916, '2023-01-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99018, 9916, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM4462 (12)', 'CLOTILDE SAHOLINIRINA', 'K00054802', 3950000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9917, 'B00435140', '0202701573031005', '001', '001', 9102, 'NOUVELLE_DEMANDE', 'Crédits de trésorerie pour l''exploitation', 'TITULAIRE', '2020-09-24', '2021-08-31', '2020-11-30', '2021-08-31', 1000000, 104446, 10, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_ANTICIPEE', '2021-08-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9917, '2020-12-01', 1, 0, NULL, 'Payé à temps'),
 (9917, '2021-01-01', 1, 0, NULL, 'Payé à temps'),
 (9917, '2021-02-01', 1, 0, NULL, 'Payé à temps'),
 (9917, '2021-03-01', 1, 0, NULL, 'Payé à temps'),
 (9917, '2021-04-01', 1, 0, NULL, 'Payé à temps'),
 (9917, '2021-05-01', 1, 0, NULL, 'Payé à temps'),
 (9917, '2021-06-01', 1, 0, NULL, 'Payé à temps'),
 (9917, '2021-07-01', 1, 0, NULL, 'Payé à temps'),
 (9917, '2021-08-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99019, 9917, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAN4253 (69)', 'CLOTILDE SAHOLINIRINA', 'K00054802', 2000000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9918, '500407838', '0202701573030001', '001', '001', 9102, 'NOUVELLE_DEMANDE', 'Crédits immobiliers pour l''habitat', 'TITULAIRE', '2019-02-15', '2021-01-29', '2019-03-31', '2021-01-29', 4000000, 231685, 23, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2021-01-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9918, '2020-12-01', 1, 0, NULL, 'Payé à temps'),
 (9918, '2021-01-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99020, 9918, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAN2582 (59)', 'CLOTILDE SAHOLINIRINA', 'K00054802', 7700000);

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_demande, montant_finance, montant_total_du, nombre_total_echeances, devise, phase_demande, date_derniere_modification, origine_donnee) VALUES (9919, 'L00715017', '001', '001', 9102, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE', '2025-12-03', 4000000, 4000000, 12, 'Ariary malgache', 'DEMANDE_EN_COURS', '2025-12-03 00:00:00', 'RAPPORT_CRIF');

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9920, '600637342', '0202301728015004', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE', '2024-10-12', '2025-08-31', '2025-05-26', '2025-08-28', 2200000, 276468, 4, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_ANTICIPEE', '2025-08-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9920, '2024-10-01', 1, 0, NULL, 'Payé à temps'),
 (9920, '2024-11-01', 1, 0, NULL, 'Payé à temps'),
 (9920, '2024-12-01', 1, 0, NULL, 'Payé à temps'),
 (9920, '2025-01-01', 1, 0, NULL, 'Payé à temps'),
 (9920, '2025-02-01', 1, 0, NULL, 'Payé à temps'),
 (9920, '2025-03-01', 1, 0, NULL, 'Payé à temps'),
 (9920, '2025-04-01', 1, 0, NULL, 'Payé à temps'),
 (9920, '2025-05-01', 1, 0, NULL, 'Payé à temps'),
 (9920, '2025-06-01', 1, 0, NULL, 'Payé à temps'),
 (9920, '2025-07-01', 1, 0, NULL, 'Payé à temps'),
 (9920, '2025-08-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99021, 9920, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAN7813 (32)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 3900000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9921, 'I00630115', '0202301728012013', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2024-06-05', '2024-10-31', '2024-12-21', '2024-10-31', 936000, 169412, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2024-10-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9921, '2024-06-01', 1, 0, NULL, 'Payé à temps'),
 (9921, '2024-07-01', 1, 0, NULL, 'Payé à temps'),
 (9921, '2024-08-01', 1, 0, NULL, 'Payé à temps'),
 (9921, '2024-10-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99022, 9921, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAN7047 (53)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 936000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9922, 'H00567810', '0202301728015003', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE', '2023-08-26', '2024-06-30', '2024-03-25', '2024-06-29', 2000000, 260789, 4, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_ANTICIPEE', '2024-06-30 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9922, '2023-08-01', 1, 0, NULL, 'Payé à temps'),
 (9922, '2023-09-01', 1, 0, NULL, 'Payé à temps'),
 (9922, '2023-10-01', 1, 0, NULL, 'Payé à temps'),
 (9922, '2023-11-01', 1, 0, NULL, 'Payé à temps'),
 (9922, '2023-12-01', 1, 0, NULL, 'Payé à temps'),
 (9922, '2024-01-01', 1, 0, NULL, 'Payé à temps'),
 (9922, '2024-02-01', 1, 0, NULL, 'Payé à temps'),
 (9922, '2024-03-01', 1, 0, NULL, 'Payé à temps'),
 (9922, '2024-04-01', 1, 0, NULL, 'Payé à temps'),
 (9922, '2024-05-01', 1, 0, NULL, 'Payé à temps'),
 (9922, '2024-06-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99023, 9922, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAN5582 (46)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 2900000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9923, 'J00544279', '0202301728012008', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2023-04-29', '2023-10-11', '2023-10-27', '2023-10-11', 1440000, 243693, 2, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2023-10-11 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9923, '2023-04-01', 1, 0, NULL, 'Payé à temps'),
 (9923, '2023-05-01', 1, 0, NULL, 'Payé à temps'),
 (9923, '2023-06-01', 1, 0, NULL, 'Payé à temps'),
 (9923, '2023-07-01', 1, 0, NULL, 'Payé à temps'),
 (9923, '2023-08-01', 1, 0, NULL, 'Payé à temps'),
 (9923, '2023-09-01', 1, 0, NULL, 'Payé à temps'),
 (9923, '2023-10-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99024, 9923, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM4849 (21)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 1440000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9924, 'G00558294', '0202301728012012', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2023-05-27', '2023-10-26', '2023-10-27', '2023-10-26', 312000, 70598, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2023-10-26 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9924, '2023-05-01', 1, 0, NULL, 'Payé à temps'),
 (9924, '2023-06-01', 1, 0, NULL, 'Payé à temps'),
 (9924, '2023-07-01', 1, 0, NULL, 'Payé à temps'),
 (9924, '2023-08-01', 1, 0, NULL, 'Payé à temps'),
 (9924, '2023-09-01', 1, 0, NULL, 'Payé à temps'),
 (9924, '2023-10-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99025, 9924, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM5085 (14)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 312000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9925, 'A00552639', '0202301728012011', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2023-05-27', '2023-10-19', '2023-11-27', '2023-10-19', 576000, 108435, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2023-10-19 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9925, '2023-05-01', 1, 0, NULL, 'Payé à temps'),
 (9925, '2023-06-01', 1, 0, NULL, 'Payé à temps'),
 (9925, '2023-07-01', 1, 0, NULL, 'Payé à temps'),
 (9925, '2023-08-01', 1, 0, NULL, 'Payé à temps'),
 (9925, '2023-09-01', 1, 0, NULL, 'Payé à temps'),
 (9925, '2023-10-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99026, 9925, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM5084 (19)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 576000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9926, 'A00552242', '0202301728012010', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2023-05-17', '2023-10-27', '2023-11-27', '2023-10-27', 960000, 177311, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2023-10-27 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9926, '2023-05-01', 1, 0, NULL, 'Payé à temps'),
 (9926, '2023-06-01', 1, 0, NULL, 'Payé à temps'),
 (9926, '2023-07-01', 1, 0, NULL, 'Payé à temps'),
 (9926, '2023-08-01', 1, 0, NULL, 'Payé à temps'),
 (9926, '2023-09-01', 1, 0, NULL, 'Payé à temps'),
 (9926, '2023-10-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99027, 9926, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM4987 (12)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 960000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9927, '500546531', '0202301728012009', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2023-04-29', '2023-10-19', '2023-10-27', '2023-10-19', 468000, 91737, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2023-10-19 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9927, '2023-04-01', 1, 0, NULL, 'Payé à temps'),
 (9927, '2023-05-01', 1, 0, NULL, 'Payé à temps'),
 (9927, '2023-06-01', 1, 0, NULL, 'Payé à temps'),
 (9927, '2023-07-01', 1, 0, NULL, 'Payé à temps'),
 (9927, '2023-08-01', 1, 0, NULL, 'Payé à temps'),
 (9927, '2023-09-01', 1, 0, NULL, 'Payé à temps'),
 (9927, '2023-10-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99028, 9927, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM4850 (15)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 468000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9928, 'E00515354', '0202301728015002', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE', '2022-10-27', '2023-07-28', '2023-05-27', '2023-07-28', 1500000, 212021, 3, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_ANTICIPEE', '2023-07-28 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9928, '2022-10-01', 1, 0, NULL, 'Payé à temps'),
 (9928, '2022-11-01', 1, 0, NULL, 'Payé à temps'),
 (9928, '2022-12-01', 1, 0, NULL, 'Payé à temps'),
 (9928, '2023-01-01', 1, 0, NULL, 'Payé à temps'),
 (9928, '2023-02-01', 1, 0, NULL, 'Payé à temps'),
 (9928, '2023-03-01', 1, 0, NULL, 'Payé à temps'),
 (9928, '2023-04-01', 1, 0, NULL, 'Payé à temps'),
 (9928, '2023-05-01', 1, 0, NULL, 'Payé à temps'),
 (9928, '2023-06-01', 1, 0, NULL, 'Payé à temps'),
 (9928, '2023-07-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99029, 9928, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM4349 (11)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 2900000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9929, 'O00492229', '0202301728012005', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2022-05-28', '2022-10-11', '2022-11-26', '2022-10-11', 1344000, 255515, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2022-10-11 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9929, '2022-05-01', 1, 0, NULL, 'Payé à temps'),
 (9929, '2022-06-01', 1, 0, NULL, 'Payé à temps'),
 (9929, '2022-07-01', 1, 0, NULL, 'Payé à temps'),
 (9929, '2022-08-01', 1, 0, NULL, 'Payé à temps'),
 (9929, '2022-09-01', 1, 0, NULL, 'Payé à temps'),
 (9929, '2022-10-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99030, 9929, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM3968 (16)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 1344000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9930, 'J00490687', '0202301728012003', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2022-05-14', '2022-10-07', '2022-10-22', '2022-10-07', 1152000, 204996, 2, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2022-10-07 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9930, '2022-05-01', 1, 0, NULL, 'Payé à temps'),
 (9930, '2022-06-01', 1, 0, NULL, 'Payé à temps'),
 (9930, '2022-07-01', 1, 0, NULL, 'Payé à temps'),
 (9930, '2022-08-01', 1, 0, NULL, 'Payé à temps'),
 (9930, '2022-09-01', 1, 0, NULL, 'Payé à temps'),
 (9930, '2022-10-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99031, 9930, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM3893 (18)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 1152000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9931, 'I00504340', '0202301728012007', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2022-06-18', '2022-09-22', '2022-11-26', '2022-09-22', 288000, 61891, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2022-09-22 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9931, '2022-06-01', 1, 0, NULL, 'Payé à temps'),
 (9931, '2022-07-01', 1, 0, NULL, 'Payé à temps'),
 (9931, '2022-08-01', 1, 0, NULL, 'Payé à temps'),
 (9931, '2022-09-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99032, 9931, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM4062 (16)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 288000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9932, 'R00504854', '0202301728012006', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2022-06-18', '2022-09-30', '2022-11-26', '2022-09-30', 1152000, 247562, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2022-09-30 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9932, '2022-06-01', 1, 0, NULL, 'Payé à temps'),
 (9932, '2022-07-01', 1, 0, NULL, 'Payé à temps'),
 (9932, '2022-08-01', 1, 0, NULL, 'Payé à temps'),
 (9932, '2022-09-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99033, 9932, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM4061 (16)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 1152000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9933, 'B00497397', '0202301728012004', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2022-05-14', '2022-09-22', '2022-09-24', '2022-09-22', 528000, 135158, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2022-09-22 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9933, '2022-05-01', 1, 0, NULL, 'Payé à temps'),
 (9933, '2022-06-01', 1, 0, NULL, 'Payé à temps'),
 (9933, '2022-07-01', 1, 0, NULL, 'Payé à temps'),
 (9933, '2022-08-01', 1, 0, NULL, 'Payé à temps'),
 (9933, '2022-09-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99034, 9933, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM3894 (16)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 528000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9934, 'G00472912', '0202301728015001', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE', '2021-11-11', '2022-08-20', '2022-06-30', '2022-08-20', 1200000, 162733, 3, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2022-08-20 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9934, '2021-11-01', 1, 0, NULL, 'Payé à temps'),
 (9934, '2021-12-01', 1, 0, NULL, 'Payé à temps'),
 (9934, '2022-01-01', 1, 0, NULL, 'Payé à temps'),
 (9934, '2022-02-01', 1, 0, NULL, 'Payé à temps'),
 (9934, '2022-03-01', 1, 0, NULL, 'Payé à temps'),
 (9934, '2022-04-01', 1, 0, NULL, 'Payé à temps'),
 (9934, '2022-05-01', 1, 0, NULL, 'Payé à temps'),
 (9934, '2022-06-01', 1, 0, NULL, 'Payé à temps'),
 (9934, '2022-07-01', 1, 0, NULL, 'Payé à temps'),
 (9934, '2022-08-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99035, 9934, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM3462 (14)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 2000000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9935, 'S00444944', '0202301728012001', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2021-05-22', '2021-11-06', '2021-10-31', '2021-11-06', 960000, 176850, 2, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2021-11-06 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9935, '2021-05-01', 1, 0, NULL, 'Payé à temps'),
 (9935, '2021-06-01', 1, 0, NULL, 'Payé à temps'),
 (9935, '2021-07-01', 1, 0, NULL, 'Payé à temps'),
 (9935, '2021-08-01', 1, 0, NULL, 'Payé à temps'),
 (9935, '2021-09-01', 1, 0, NULL, 'Payé à temps'),
 (9935, '2021-10-01', 1, 0, NULL, 'Payé à temps'),
 (9935, '2021-11-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99036, 9935, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM3181 (21)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 960000);

INSERT INTO contrat (id, code_contrat_cb, code_contrat_etablissement, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9936, 'A00447628', '0202301728012002', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Préfinancement de collecte de produits agricoles/Avances', 'TITULAIRE', '2021-05-29', '2021-11-06', '2021-11-30', '2021-11-06', 320000, 60266, 1, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2021-11-06 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9936, '2021-05-01', 1, 0, NULL, 'Payé à temps'),
 (9936, '2021-06-01', 1, 0, NULL, 'Payé à temps'),
 (9936, '2021-07-01', 1, 0, NULL, 'Payé à temps'),
 (9936, '2021-08-01', 1, 0, NULL, 'Payé à temps'),
 (9936, '2021-09-01', 1, 0, NULL, 'Payé à temps'),
 (9936, '2021-10-01', 1, 0, NULL, 'Payé à temps'),
 (9936, '2021-11-01', 1, 0, NULL, 'Payé à temps');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99037, 9936, 'REELLE', 'Nantissement sur Outillage, Matériel, d’Equipement Professionnel', 'NAM3205 (24)', 'LÉON MARCEL RAKOTONIAINA', 'R00073573', 320000);

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_demande, montant_finance, montant_total_du, nombre_total_echeances, devise, phase_demande, date_derniere_modification, origine_donnee) VALUES (9937, '400701753', '001', '001', 9103, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE', '2025-10-13', 2000000, 2000000, 3, 'Ariary malgache', 'DEMANDE_EN_COURS', '2025-10-13 00:00:00', 'RAPPORT_CRIF');

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, date_premiere_echeance, date_dernier_reglement, date_prochaine_echeance, montant_prochaine_echeance, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, nombre_echeances_restantes, montant_restant_du, devise, periodicite_paiement, pire_statut, phase_demande, date_derniere_modification, origine_donnee) VALUES (9938, 'I00697290', '001', '003', 9104, 'NOUVELLE_DEMANDE', 'Prêts subordonnés', 'TITULAIRE', '2025-07-03', '2026-07-15', '2025-08-23', '2025-12-23', '2026-01-15', 55826, 667526, 55844, 12, 7, 389390, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'ACTIF', '2025-12-31 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9938, '2025-07-01', NULL, NULL, NULL, 'Trop récent'),
 (9938, '2025-08-01', NULL, NULL, NULL, 'Trop récent'),
 (9938, '2025-09-01', NULL, NULL, NULL, 'Trop récent'),
 (9938, '2025-10-01', NULL, NULL, NULL, 'Trop récent'),
 (9938, '2025-11-01', NULL, NULL, NULL, 'Trop récent'),
 (9938, '2025-12-01', NULL, NULL, NULL, 'Trop récent');
INSERT INTO garantie (id, contrat_id, nature, type_garantie, code_etablissement_garantie, nom_garant, code_client_cb_garant, montant_couvert) VALUES (99038, 9938, 'REELLE', 'Cautions reçues par l''emprunteur', NULL, 'RASOARIMANANA LUCIE BRIGITTE', NULL, 667526);

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9939, 'O00637033', '001', '003', 9104, 'NOUVELLE_DEMANDE', 'Prêts subordonnés', 'TITULAIRE', '2024-09-19', '2025-07-03', 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2025-07-03 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9939, '2024-09-01', NULL, NULL, NULL, 'Trop récent'),
 (9939, '2024-10-01', NULL, NULL, NULL, 'Trop récent'),
 (9939, '2024-11-01', NULL, NULL, NULL, 'Trop récent'),
 (9939, '2024-12-01', NULL, NULL, NULL, 'Trop récent'),
 (9939, '2025-01-01', NULL, NULL, NULL, 'Trop récent'),
 (9939, '2025-02-01', NULL, NULL, NULL, 'Trop récent'),
 (9939, '2025-03-01', NULL, NULL, NULL, 'Trop récent'),
 (9939, '2025-04-01', NULL, NULL, NULL, 'Trop récent'),
 (9939, '2025-05-01', NULL, NULL, NULL, 'Trop récent'),
 (9939, '2025-06-01', NULL, NULL, NULL, 'Trop récent'),
 (9939, '2025-07-01', NULL, NULL, NULL, 'Trop récent');

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9940, '200588948', '001', '003', 9104, 'NOUVELLE_DEMANDE', 'Prêts subordonnés', 'TITULAIRE', '2023-12-06', '2024-09-20', 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2024-09-20 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9940, '2023-12-01', NULL, NULL, NULL, 'Trop récent'),
 (9940, '2024-01-01', NULL, NULL, NULL, 'Trop récent'),
 (9940, '2024-02-01', NULL, NULL, NULL, 'Trop récent'),
 (9940, '2024-03-01', NULL, NULL, NULL, 'Trop récent'),
 (9940, '2024-04-01', NULL, NULL, NULL, 'Trop récent'),
 (9940, '2024-05-01', NULL, NULL, NULL, 'Trop récent'),
 (9940, '2024-06-01', NULL, NULL, NULL, 'Trop récent'),
 (9940, '2024-07-01', NULL, NULL, NULL, 'Trop récent'),
 (9940, '2024-08-01', NULL, NULL, NULL, 'Trop récent'),
 (9940, '2024-09-01', NULL, NULL, NULL, 'Trop récent');

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_debut_contrat, date_fin_contrat, montant_finance, montant_echeance_mensuelle, nombre_total_echeances, max_montant_impayes, max_nombre_echeances_impayees, devise, periodicite_paiement, pire_statut, phase_demande, motif_cloture, date_derniere_modification, origine_donnee) VALUES (9941, '400507174', '001', '001', 9104, 'NOUVELLE_DEMANDE', 'Crédits d''équipement', 'TITULAIRE', '2022-05-04', '2023-09-30', 651120, 66497, 12, 201504, 5, 'Ariary malgache', 'Mensuelle- 1 mois', 'No info', 'FERME', 'FIN_A_TERME', '2023-09-30 00:00:00', 'RAPPORT_CRIF');
INSERT INTO historique_paiement_mensuel (contrat_id, mois, nombre_echeances, nombre_echeances_impayees, jours_de_retard, statut) VALUES
 (9941, '2022-05-01', 1, 0, NULL, 'Payé à temps'),
 (9941, '2022-06-01', 1, 0, NULL, 'Payé à temps'),
 (9941, '2022-07-01', 1, 0, NULL, 'Payé à temps'),
 (9941, '2022-08-01', 1, 1, NULL, '1-29 jours de retard'),
 (9941, '2022-09-01', 1, 1, NULL, '1-29 jours de retard'),
 (9941, '2022-10-01', 1, 1, NULL, '1-29 jours de retard'),
 (9941, '2022-11-01', 1, 1, NULL, '1-29 jours de retard'),
 (9941, '2022-12-01', 1, 1, NULL, '1-29 jours de retard'),
 (9941, '2023-01-01', 1, 1, NULL, '1-29 jours de retard'),
 (9941, '2023-02-01', 2, 2, NULL, '30-59 jours de retard'),
 (9941, '2023-03-01', 2, 2, NULL, '30-59 jours de retard'),
 (9941, '2023-04-01', 3, 3, NULL, '60-89 jours de retard'),
 (9941, '2023-06-01', 5, 5, NULL, '60-89 jours de retard'),
 (9941, '2023-07-01', 5, 5, NULL, '60-89 jours de retard'),
 (9941, '2023-08-01', 5, 5, NULL, '60-89 jours de retard'),
 (9941, '2023-09-01', 1, 0, NULL, 'Payé à temps');

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_demande, montant_finance, montant_total_du, nombre_total_echeances, devise, phase_demande, date_derniere_modification, origine_donnee) VALUES (9942, 'K00708906', '001', '001', 9104, 'NOUVELLE_DEMANDE', 'Prêt Personnel', 'TITULAIRE', '2025-11-04', 3600000, 3600000, 4, 'Ariary malgache', 'DEMANDE_EN_COURS', '2025-11-04 00:00:00', 'RAPPORT_CRIF');

INSERT INTO contrat (id, code_contrat_cb, code_etablissement_declarant, code_etablissement_crypte, client_id, mode_rattachement, type_contrat, role_client, date_demande, montant_finance, montant_total_du, nombre_total_echeances, devise, phase_demande, date_derniere_modification, origine_donnee) VALUES (9943, '000730188', '001', '001', 9104, 'NOUVELLE_DEMANDE', 'Crédit à la Consommation', 'TITULAIRE', '2026-02-25', 250000, 250000, 4, 'Ariary malgache', 'DEMANDE_EN_COURS', '2026-02-25 00:00:00', 'RAPPORT_CRIF');

-- Séquences
SELECT setval(pg_get_serial_sequence('client','id'), GREATEST(10000, (SELECT MAX(id) FROM client)));
SELECT setval(pg_get_serial_sequence('adresse','id'), (SELECT MAX(id) FROM adresse));
SELECT setval(pg_get_serial_sequence('identifiant','id'), (SELECT MAX(id) FROM identifiant));
SELECT setval(pg_get_serial_sequence('emploi','id'), (SELECT MAX(id) FROM emploi));
SELECT setval(pg_get_serial_sequence('contrat','id'), GREATEST(10000, (SELECT MAX(id) FROM contrat)));
SELECT setval(pg_get_serial_sequence('garantie','id'), (SELECT MAX(id) FROM garantie));
SELECT setval(pg_get_serial_sequence('contrat_lien','id'), (SELECT COALESCE(MAX(id),1) FROM contrat_lien));
SELECT setval(pg_get_serial_sequence('historique_paiement_mensuel','id'), (SELECT COALESCE(MAX(id),1) FROM historique_paiement_mensuel));
SELECT setval(pg_get_serial_sequence('evenement_negatif','id'), (SELECT COALESCE(MAX(id),1) FROM evenement_negatif));