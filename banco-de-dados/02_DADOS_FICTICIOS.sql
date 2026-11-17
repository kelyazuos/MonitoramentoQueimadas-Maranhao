
/*
 UNIVERSIDADE FEDERAL DO MARANHÃO - UFMA
 Disciplina: Banco de Dados
 Projeto: Sistema de Monitoramento de Queimadas no Maranhão

 Objetivo:
 Inserir dados fictícios para demonstração do sistema.


*/

BEGIN;


-- 1. MUNICÍPIOS


INSERT INTO municipio (nome, estado)
VALUES
    ('São Luís', 'MA'),
    ('Imperatriz', 'MA'),
    ('Açailândia', 'MA');


-- 2. EQUIPES


INSERT INTO equipe (nome)
VALUES
    ('Equipe de Monitoramento Norte'),
    ('Equipe de Combate Sul');



-- 3. AGENTES
-- CPFs criados para teste

INSERT INTO agente (nome, cpf, id_equipe)
VALUES
    (
        'Bruno Costa',
        '00000000000',
        (
            SELECT id_equipe
            FROM equipe
            WHERE nome = 'Equipe de Monitoramento Norte'
        )
    ),
    (
        'Fabrícia Machado',
        '11111111111',
        (
            SELECT id_equipe
            FROM equipe
            WHERE nome = 'Equipe de Combate Sul'
        )
    ),
    (
        'Pedro Oliveira',
        '22222222222',
        (
            SELECT id_equipe
            FROM equipe
            WHERE nome = 'Equipe de Monitoramento Norte'
        )
    );


-- =============================================
-- 4. OCORRÊNCIAS
-- =============================================

-- Ocorrência urbana - São Luís

INSERT INTO ocorrencia (
    data,
    temperatura,
    umidade,
    descricao,
    situacao,
    id_municipio,
    id_agente,
    tipo_ocorrencia,
    tipo_vegetacao,
    bairro
)
VALUES (
           DATE '2026-11-15',
           34.50,
           48.00,
           'Queimada próxima a residências.',
           'em_atendimento',
           (
               SELECT id_municipio
               FROM municipio
               WHERE nome = 'São Luís'
           ),
           (
               SELECT id_agente
               FROM agente
               WHERE nome = 'Bruno Costa'
           ),
           'urbana',
           NULL,
           'Tirirical'
       );


-- Ocorrência rural - Imperatriz

INSERT INTO ocorrencia (
    data,
    temperatura,
    umidade,
    descricao,
    situacao,
    id_municipio,
    id_agente,
    tipo_ocorrencia,
    tipo_vegetacao,
    bairro
)
VALUES (
           DATE '2026-11-15',
           38.00,
           32.00,
           'Foco de queimada identificado em área rural.',
           'registrada',
           (
               SELECT id_municipio
               FROM municipio
               WHERE nome = 'Imperatriz'
           ),
           (
               SELECT id_agente
               FROM agente
               WHERE nome = 'Fabrícia Machado'
           ),
           'rural',
           NULL,
           'Localidade rural fictícia'
       );


-- Ocorrência rural - Açailândia

INSERT INTO ocorrencia (
    data,
    temperatura,
    umidade,
    descricao,
    situacao,
    id_municipio,
    id_agente,
    tipo_ocorrencia,
    tipo_vegetacao,
    bairro
)
VALUES (
           DATE '2026-11-14',
           36.00,
           40.00,
           'Ocorrência de queimada registrada para demonstração.',
           'encerrada',
           (
               SELECT id_municipio
               FROM municipio
               WHERE nome = 'Açailândia'
           ),
           (
               SELECT id_agente
               FROM agente
               WHERE nome = 'Pedro Oliveira'
           ),
           'rural',
           NULL,
           'Povoado fictício'
       );


-- 5. ATENDIMENTOS
-- Relacionamento N:N entre equipe e ocorrência

INSERT INTO atendimento (
    data,
    situacao,
    id_equipe,
    id_ocorrencia
)
VALUES
    (
        DATE '2026-11-15',
        'em_andamento',
        (
            SELECT id_equipe
            FROM equipe
            WHERE nome = 'Equipe de Monitoramento Norte'
        ),
        (
            SELECT id_ocorrencia
            FROM ocorrencia
            WHERE descricao = 'Queimada próxima a residências.'
        )
    ),
    (
        DATE '2026-11-15',
        'pendente',
        (
            SELECT id_equipe
            FROM equipe
            WHERE nome = 'Equipe de Combate Sul'
        ),
        (
            SELECT id_ocorrencia
            FROM ocorrencia
            WHERE descricao = 'Queimada próxima a residências.'
        )
    ),
    (
        DATE '2026-11-15',
        'pendente',
        (
            SELECT id_equipe
            FROM equipe
            WHERE nome = 'Equipe de Combate Sul'
        ),
        (
            SELECT id_ocorrencia
            FROM ocorrencia
            WHERE descricao = 'Foco de queimada identificado em área rural.'
        )
    );

COMMIT;


-- CONFERÊNCIA DOS REGISTROS

SELECT * FROM municipio;
SELECT * FROM equipe;
SELECT * FROM agente;
SELECT * FROM ocorrencia;
SELECT * FROM atendimento;
