-- Connect to the production database and create the wallet schema
\connect production;

-- Connect to the qualification database and create the wallet schema
\connect qualification;
CREATE OR REPLACE FUNCTION insert_crypto_transaction(
    p_date_utc TIMESTAMP,
    p_pair TEXT,
    p_side TEXT,
    p_price NUMERIC,
    p_executed_amount NUMERIC,
    p_executed_currency TEXT,
    p_amount_amount NUMERIC,
    p_amount_currency TEXT,
    p_fee_amount NUMERIC,
    p_fee_currency TEXT
)
    RETURNS VOID AS
$$
BEGIN
    -- Check if a transaction with the same date_utc, pair, and side already exists
    IF NOT EXISTS (
        SELECT 1
        FROM crypto_transaction
        WHERE date_utc = p_date_utc
          AND pair = p_pair
          AND side = p_side
          AND price = p_price
          AND executed_amount = p_executed_amount
          AND executed_currency = p_executed_currency
          AND amount_amount = p_amount_amount
          AND amount_currency = p_amount_currency
          AND fee_amount = p_fee_amount
          AND fee_currency = p_fee_currency
    ) THEN
        -- Insert the new transaction if no matching record is found
        INSERT INTO crypto_transaction (date_utc, pair, side, price, executed_amount, executed_currency,
                                        amount_amount, amount_currency, fee_amount, fee_currency)
        VALUES (p_date_utc, p_pair, p_side, p_price, p_executed_amount, p_executed_currency,
                p_amount_amount, p_amount_currency, p_fee_amount, p_fee_currency);
    END IF;
END;
$$
    LANGUAGE plpgsql;
