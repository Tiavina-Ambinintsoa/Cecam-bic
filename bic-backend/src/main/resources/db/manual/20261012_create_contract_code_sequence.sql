-- Required by ContratRepository.prochainCodeContratCb() for new demand codes.
CREATE SEQUENCE IF NOT EXISTS seq_code_contrat_cb AS BIGINT MINVALUE 1;

DO $$
DECLARE
    max_code numeric;
    sequence_last_value bigint;
    sequence_is_called boolean;
    prochain_code numeric;
BEGIN
    SELECT COALESCE(MAX(code_contrat_cb::numeric), 0)
      INTO max_code
      FROM contrat
     WHERE code_contrat_cb ~ '^[0-9]+$';

    SELECT last_value, is_called
      INTO sequence_last_value, sequence_is_called
      FROM seq_code_contrat_cb;

    prochain_code := GREATEST(
        max_code,
        CASE WHEN sequence_is_called THEN sequence_last_value::numeric
             ELSE sequence_last_value::numeric - 1
        END
    ) + 1;

    IF prochain_code > 9223372036854775807 THEN
        RAISE EXCEPTION 'Impossible de générer un code contrat : la séquence bigint est épuisée.';
    END IF;

    PERFORM setval('seq_code_contrat_cb', prochain_code::bigint, false);
END $$;
