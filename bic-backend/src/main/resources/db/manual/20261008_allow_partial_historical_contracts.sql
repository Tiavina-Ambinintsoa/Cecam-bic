-- Apply once to an existing BIC database before importing historical bureau records.
-- New-demand DTO validation still requires amounts and installment counts.
ALTER TABLE contrat ALTER COLUMN date_demande DROP NOT NULL;
ALTER TABLE contrat ALTER COLUMN montant_finance DROP NOT NULL;
ALTER TABLE contrat ALTER COLUMN nombre_total_echeances DROP NOT NULL;
