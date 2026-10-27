-- =============================================================================
-- CECAM BIC - Script d'initialisation propre (sans historique client de test)
-- Ce fichier contient uniquement les séquences, référentiels et comptes initiaux.
-- =============================================================================

-- Séquences pour la génération automatique de codes uniques
CREATE SEQUENCE IF NOT EXISTS seq_code_contrat_cb START WITH 700000001;
CREATE SEQUENCE IF NOT EXISTS seq_code_client_cb  START WITH 200001;

-- Defaults SQL
ALTER TABLE client ALTER COLUMN origine_donnee SET DEFAULT 'NON_RENSEIGNEE';
ALTER TABLE contrat ALTER COLUMN origine_donnee SET DEFAULT 'NON_RENSEIGNEE';

-- Référentiel des classes de score (Grille officielle CECAM BIC)
INSERT INTO grille_score (intervalle, score_min, score_max, categorie_risque, couleur) VALUES
('A', 530, 850, 'Risque très faible', 'Pistache'),
('B', 500, 529, 'Risque faible', 'Jaune moutarde'),
('C', 460, 499, 'Risque moyen', 'Jaune Or'),
('D', 400, 459, 'Risque élevé', 'Orange citrouille'),
('E', 300, 399, 'Risque très élevé', 'Rouge Vif')
ON CONFLICT (intervalle) DO UPDATE SET
    score_min = EXCLUDED.score_min,
    score_max = EXCLUDED.score_max,
    categorie_risque = EXCLUDED.categorie_risque,
    couleur = EXCLUDED.couleur;

-- Nomenclature des catégories de tiers (BCEAO / CRIF Madagascar)
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

-- Comptes utilisateurs par défaut pour le démarrage
-- admin1 / admin1 (Rôle ADMIN)
-- agent1 / agent1 (Rôle AGENT_CREDIT)
INSERT INTO utilisateur (id, nom_utilisateur, mot_de_passe, role, actif) VALUES
(1, 'agent1', '$2b$10$MUvYPyZABeOBL1/EZwstXOV7mYuNfE/fDnfHB0Am1C8B9IoYsC6Ga', 'AGENT_CREDIT', true),
(2, 'admin1', '$2b$10$.1JeAl8BY/PxPtOGfNKPW.B/gRltqFF9sePuEFBrx4hyuV2q.kS3K', 'ADMIN', true)
ON CONFLICT (id) DO NOTHING;

-- Réinitialisation de la séquence utilisateur
SELECT setval(pg_get_serial_sequence('utilisateur', 'id'), (SELECT COALESCE(MAX(id), 1) FROM utilisateur));
