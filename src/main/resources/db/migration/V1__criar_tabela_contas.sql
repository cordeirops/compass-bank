CREATE TABLE contas (
    id            UUID          NOT NULL PRIMARY KEY,
    nome          VARCHAR(255)  NOT NULL,
    saldo         NUMERIC(19,4) NOT NULL,
    versao        BIGINT        NOT NULL DEFAULT 0,
    criada_em     TIMESTAMP     NOT NULL DEFAULT NOW(),
    atualizada_em TIMESTAMP     NOT NULL DEFAULT NOW()
);
