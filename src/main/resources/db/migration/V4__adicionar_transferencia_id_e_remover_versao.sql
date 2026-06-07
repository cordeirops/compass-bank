ALTER TABLE contas DROP COLUMN versao;

ALTER TABLE transacoes ADD COLUMN transferencia_id UUID;

CREATE INDEX idx_transacoes_transferencia ON transacoes(transferencia_id);
