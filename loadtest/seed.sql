-- Seeds the urls table with 3,000,000 rows so lookups can no longer fit
-- entirely in Postgres's cache. Bypasses the app/JPA for speed: this is a
-- bulk load for benchmarking, not something the app would do at runtime.
--
-- Short codes use an "sd" prefix and are zero-padded to a fixed width so
-- loadtest/redirect-seeded.js can compute valid codes by index alone,
-- without querying the database or the API first.
INSERT INTO urls (full_url, short_code, created_at)
SELECT
    'https://example.com/seed/' || i,
    'sd' || lpad(i::text, 9, '0'),
    now()
FROM generate_series(1, 3000000) AS i
ON CONFLICT (short_code) DO NOTHING;

-- Sanity check: row count and one sample row
SELECT count(*) AS total_rows FROM urls;
SELECT * FROM urls WHERE short_code = 'sd000000001';
