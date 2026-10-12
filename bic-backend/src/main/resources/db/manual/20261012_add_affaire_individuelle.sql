CREATE TABLE IF NOT EXISTS affaire_individuelle (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL UNIQUE REFERENCES client(id) ON DELETE CASCADE,
    nom VARCHAR(255) NOT NULL,
    date_derniere_modification TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS adresse_affaire_individuelle (
    id BIGSERIAL PRIMARY KEY,
    affaire_individuelle_id BIGINT NOT NULL REFERENCES affaire_individuelle(id) ON DELETE CASCADE,
    type_adresse VARCHAR(255) NOT NULL DEFAULT 'Entreprise - Adresse Principale',
    adresse_complete VARCHAR(500) NOT NULL,
    numero_rue VARCHAR(255),
    code_postal VARCHAR(255),
    ville VARCHAR(255),
    commune VARCHAR(255),
    region VARCHAR(255),
    pays VARCHAR(255),
    actuelle BOOLEAN NOT NULL DEFAULT TRUE,
    date_derniere_modification TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_adresse_affaire_individuelle_parent
    ON adresse_affaire_individuelle (affaire_individuelle_id);
