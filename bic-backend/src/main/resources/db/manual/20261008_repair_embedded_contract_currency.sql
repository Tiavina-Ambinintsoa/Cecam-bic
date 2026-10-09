-- Repair legacy imports where the entire source label was stored in the currency column.
-- The currency is replaced only when the malformed value ends with the explicit source label.
UPDATE contrat
SET devise = 'Ariary malgache'
WHERE devise ~* 'Devise[[:space:]]+Ariary[[:space:]]+malgache[[:space:]]*$';
