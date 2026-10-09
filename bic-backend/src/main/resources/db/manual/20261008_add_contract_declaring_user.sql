-- Stores the authenticated user who records a newly created or manually entered contract.
-- Existing records remain NULL because their source user is not reliably available.
ALTER TABLE contrat ADD COLUMN IF NOT EXISTS utilisateur_declarant VARCHAR(100);
