ALTER TABLE customers
    DROP CONSTRAINT IF EXISTS ukrfbvkrffamfql7cjmen8v976v;

ALTER TABLE customers
    ADD CONSTRAINT uk_customers_email UNIQUE (email);