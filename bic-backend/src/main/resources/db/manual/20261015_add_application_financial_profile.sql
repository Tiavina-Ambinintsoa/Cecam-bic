ALTER TABLE contrat
    ADD COLUMN IF NOT EXISTS valeur_patrimoine NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS marge_patrimoine NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS ratio_couverture_patrimoine NUMERIC(10, 2),
    ADD COLUMN IF NOT EXISTS client_garant_id BIGINT REFERENCES client(id),
    ADD COLUMN IF NOT EXISTS client_fiance_id BIGINT REFERENCES client(id),
    ADD COLUMN IF NOT EXISTS depot_demandeur NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS depot_garant NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS depot_fiance NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS total_depot NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS credit_actif_demandeur NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS credit_actif_garant NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS credit_actif_fiance NUMERIC(19, 2),
    ADD COLUMN IF NOT EXISTS total_credit_actif NUMERIC(19, 2);

CREATE TABLE IF NOT EXISTS revenu_demande (
    id BIGSERIAL PRIMARY KEY,
    contrat_id BIGINT NOT NULL REFERENCES contrat(id) ON DELETE CASCADE,
    type_revenu VARCHAR(40) NOT NULL,
    description VARCHAR(255) NOT NULL,
    montant_annuel NUMERIC(19, 2) NOT NULL,
    devise VARCHAR(40) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_revenu_demande_contrat ON revenu_demande (contrat_id);
