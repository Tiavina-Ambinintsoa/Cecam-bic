-- Les enregistrements existants restent volontairement NON_RENSEIGNEE :
-- leur source ne peut pas être déduite de façon fiable.
ALTER TABLE client
    ADD COLUMN IF NOT EXISTS origine_donnee varchar(30) DEFAULT 'NON_RENSEIGNEE';

UPDATE client
SET origine_donnee = 'NON_RENSEIGNEE'
WHERE origine_donnee IS NULL;

ALTER TABLE client
    ALTER COLUMN origine_donnee SET DEFAULT 'NON_RENSEIGNEE',
    ALTER COLUMN origine_donnee SET NOT NULL;

ALTER TABLE contrat
    ADD COLUMN IF NOT EXISTS origine_donnee varchar(30) DEFAULT 'NON_RENSEIGNEE';

UPDATE contrat
SET origine_donnee = 'NON_RENSEIGNEE'
WHERE origine_donnee IS NULL;

ALTER TABLE contrat
    ALTER COLUMN origine_donnee SET DEFAULT 'NON_RENSEIGNEE',
    ALTER COLUMN origine_donnee SET NOT NULL;
