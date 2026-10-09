-- Adds an optional foreign key so each co-borrower/guarantor links to its own client profile.
ALTER TABLE contrat_lien ADD COLUMN IF NOT EXISTS client_lie_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_schema = current_schema()
          AND table_name = 'contrat_lien'
          AND constraint_name = 'fk_contrat_lien_client_lie'
    ) THEN
        ALTER TABLE contrat_lien
            ADD CONSTRAINT fk_contrat_lien_client_lie FOREIGN KEY (client_lie_id) REFERENCES client(id);
    END IF;
END $$;

UPDATE contrat_lien l
SET client_lie_id = c.id
FROM client c
WHERE l.client_lie_id IS NULL
  AND l.code_client_cb = c.code_client_cb;
