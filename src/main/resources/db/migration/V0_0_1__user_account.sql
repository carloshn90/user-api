
CREATE SEQUENCE IF NOT EXISTS user_schema.user_account_seq
    INCREMENT 1
    START 1;

CREATE TABLE IF NOT EXISTS user_schema.user_account
(
    id          NUMERIC DEFAULT nextval('user_schema.user_account_seq') CONSTRAINT user_account_pk PRIMARY KEY   NOT NULL,
    name        VARCHAR(30)                                                                                      NOT NULL,
    surname     VARCHAR(70)                                                                                              ,
    username    VARCHAR(30)                                                                                      NOT NULL,
    email       VARCHAR(40)                                             CONSTRAINT uniq_email      UNIQUE        NOT NULL,
    password    VARCHAR(100)                                                                                     NOT NULL
);

