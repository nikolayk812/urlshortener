DROP TABLE IF EXISTS url_stats;
DROP TABLE IF EXISTS account_short_urls;
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
  short_url        VARCHAR UNIQUE NOT NULL,
  target_url       VARCHAR NOT NULL,
  redirect_type    VARCHAR NOT NULL,
  CONSTRAINT unique_target_url_redirect_type UNIQUE (target_url, redirect_type)
);

CREATE TABLE account_short_urls (
    account_id INTEGER NOT NULL,
    short_url_id INTEGER NOT NULL,
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (short_url_id) REFERENCES short_urls (id)
);

CREATE TABLE url_stats (
  id INTEGER DEFAULT global_seq.nextval PRIMARY KEY,
  url_id INTEGER NOT NULL,
  hit_counter INTEGER NOT NULL,
  FOREIGN KEY (url_id) REFERENCES short_urls (id)
);