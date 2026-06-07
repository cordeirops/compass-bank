INSERT INTO contas (id, nome, saldo, versao, criada_em, atualizada_em) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'Ana Silva',        5000.0000, 0, NOW(), NOW()),
    ('a0000000-0000-0000-0000-000000000002', 'Bruno Santos',     3000.0000, 0, NOW(), NOW()),
    ('a0000000-0000-0000-0000-000000000003', 'Carlos Oliveira',  1500.0000, 0, NOW(), NOW()),
    ('a0000000-0000-0000-0000-000000000004', 'Daniela Costa',    2500.0000, 0, NOW(), NOW()),
    ('a0000000-0000-0000-0000-000000000005', 'Eduardo Pereira',  4000.0000, 0, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;
