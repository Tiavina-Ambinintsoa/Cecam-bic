-- Compatibilite des bases ou la table a ete creee avant l'ajout du nom de l'affaire.
-- La migration est idempotente et ne modifie ni ne deduit les donnees existantes.
ALTER TABLE IF EXISTS affaire_individuelle
    ADD COLUMN IF NOT EXISTS nom VARCHAR(255);
