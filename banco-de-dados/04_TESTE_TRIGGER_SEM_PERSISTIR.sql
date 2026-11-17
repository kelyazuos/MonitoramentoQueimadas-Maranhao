
/*
 UNIVERSIDADE FEDERAL DO MARANHÃO - UFMA
 Disciplina: Banco de Dados
 3ª Avaliação

 Projeto: Sistema de Monitoramento de
 Queimadas no Maranhão

 Arquivo: 04_TESTE_TRIGGER_SEM_PERSISTIR.sql

 OBJETIVO:
 Demonstrar o funcionamento da trigger
 trg_atendimento_concluido.

 A trigger atualiza automaticamente a
 situação da ocorrência para 'controlada'
 quando todos os atendimentos associados
 estiverem concluídos.

 IMPORTANTE:
 O teste utiliza BEGIN e ROLLBACK.
 Nenhuma alteração será mantida no banco.

*/

BEGIN;



-- ETAPA 1
-- CONSULTAR A SITUAÇÃO ORIGINAL


SELECT
    o.id_ocorrencia,
    m.nome AS municipio,
    o.descricao,
    o.situacao AS situacao_ocorrencia,
    e.nome AS equipe,
    a.situacao AS situacao_atendimento
FROM ocorrencia o
         JOIN municipio m
              ON m.id_municipio = o.id_municipio
         JOIN atendimento a
              ON a.id_ocorrencia = o.id_ocorrencia
         JOIN equipe e
              ON e.id_equipe = a.id_equipe
WHERE o.descricao =
      'Queimada próxima a residências.'
ORDER BY e.nome;


-- ETAPA 2
-- CONCLUIR APENAS O PRIMEIRO ATENDIMENTO


UPDATE atendimento a
SET situacao = 'concluido'
    FROM ocorrencia o, equipe e
WHERE a.id_ocorrencia = o.id_ocorrencia
  AND a.id_equipe = e.id_equipe
  AND o.descricao =
    'Queimada próxima a residências.'
  AND e.nome = 'Equipe de Monitoramento Norte';


SELECT
    o.id_ocorrencia,
    o.situacao AS situacao_apos_primeira_equipe
FROM ocorrencia o
WHERE o.descricao =
      'Queimada próxima a residências.';



-- ETAPA 3
-- CONCLUIR O SEGUNDO ATENDIMENTO


UPDATE atendimento a
SET situacao = 'concluido'
    FROM ocorrencia o, equipe e
WHERE a.id_ocorrencia = o.id_ocorrencia
  AND a.id_equipe = e.id_equipe
  AND o.descricao =
    'Queimada próxima a residências.'
  AND e.nome = 'Equipe de Combate Sul';


-- ETAPA 4
-- COMPROVAR A EXECUÇÃO DA TRIGGER


-- Agora todos os atendimentos estão
-- concluídos, então a situação da
-- ocorrência deve ser 'controlada'.

SELECT
    o.id_ocorrencia,
    m.nome AS municipio,
    o.situacao AS situacao_atualizada,
    COUNT(a.id_atendimento) AS total_atendimentos,
    COUNT(a.id_atendimento) FILTER (
        WHERE a.situacao = 'concluido'
    ) AS atendimentos_concluidos
FROM ocorrencia o
         JOIN municipio m
              ON m.id_municipio = o.id_municipio
         JOIN atendimento a
              ON a.id_ocorrencia = o.id_ocorrencia
WHERE o.descricao =
      'Queimada próxima a residências.'
GROUP BY
    o.id_ocorrencia,
    m.nome,
    o.situacao;



-- ETAPA 5
-- DESFAZER AS ALTERAÇÕES DO TESTE


ROLLBACK;



-- ETAPA 6
-- CONFERIR QUE OS DADOS FORAM RESTAURADOS


SELECT
    o.id_ocorrencia,
    o.situacao AS situacao_original
FROM ocorrencia o
WHERE o.descricao =
      'Queimada próxima a residências.';
