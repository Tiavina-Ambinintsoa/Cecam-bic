CREATE TABLE IF NOT EXISTS demande_modification_contrat (
    id BIGSERIAL PRIMARY KEY,
    contrat_id BIGINT NOT NULL REFERENCES contrat(id),
    phase_initiale VARCHAR(30) NOT NULL,
    motif_cloture_initial VARCHAR(30),
    code_contrat_etablissement_initial VARCHAR(40),
    code_demande_etablissement_initial VARCHAR(40),
    montant_echeance_initial NUMERIC(19, 2),
    devise_initiale VARCHAR(40),
    code_contrat_etablissement VARCHAR(40),
    code_demande_etablissement VARCHAR(40),
    phase_demandee VARCHAR(30) NOT NULL,
    motif_cloture VARCHAR(30),
    montant_echeance_mensuelle NUMERIC(19, 2),
    devise VARCHAR(40) NOT NULL,
    justification VARCHAR(500) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    soumis_par VARCHAR(100) NOT NULL,
    date_soumission TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    traite_par VARCHAR(100),
    date_traitement TIMESTAMP,
    commentaire_admin VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_demande_modification_statut_date
    ON demande_modification_contrat (statut, date_soumission);

CREATE UNIQUE INDEX IF NOT EXISTS uq_demande_modification_contrat_en_attente
    ON demande_modification_contrat (contrat_id)
    WHERE statut = 'EN_ATTENTE';
