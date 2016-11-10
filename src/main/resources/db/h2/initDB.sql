DROP TABLE IF EXISTS short_urls;
DROP TABLE IF EXISTS accounts;
DROP SEQUENCE IF EXISTS global_seq;

CREATE SEQUENCE global_seq START WITH 100;

CREATE TABLE accounts (
  id       INTEGER DEFAULT global_seq.nextval PRIMARY KEY,
  name     VARCHAR UNIQUE NOT NULL,
  password VARCHAR        NOT NULL
);

CREATE TABLE short_urls (
  id               INTEGER DEFAULT global_seq.nextval PRIMARY KEY,
  short_url        VARCHAR NOT NULL,
  target_url       VARCHAR NOT NULL,
  redirect_type    VARCHAR NOT NULL,
  account_id       INTEGER NOT NULL,
  redirect_counter INTEGER NOT NULL,
  FOREIGN KEY (account_id) REFERENCES accounts (id),
  CONSTRAINT unique_account_target_url UNIQUE (target_url, account_id)
);
