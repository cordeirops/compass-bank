CREATE TABLE transacoes (
    id               UUID          NOT NULL PRIMARY KEY,
    conta_origem_id  UUID,
    conta_destino_id UUID,
    valor            NUMERIC(19,4) NOT NULL,
    tipo             VARCHAR(10)   NOT NULL,
    descricao        VARCHAR(500),
    criada_em        TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_transacoes_origem
        FOREIGN KEY (conta_origem_id) REFERENCES contas(id),
    CONSTRAINT fk_transacoes_destino
        FOREIGN KEY (conta_destino_id) REFERENCES contas(id)
);

CREATE INDEX idx_transacoes_origem  ON transacoes(conta_origem_id);
CREATE INDEX idx_transacoes_destino ON transacoes(conta_destino_id);
CREATE INDEX idx_transacoes_criada  ON transacoes(criada_em DESC);
