
/*
 UNIVERSIDADE FEDERAL DO MARANHÃO - UFMA
 Disciplina: Banco de Dados
 3ª Avaliação

 Projeto: Sistema de Monitoramento de
 Queimadas no Maranhão

 Arquivo: 03_CONSULTAS_AVALIACAO.sql

 Objetivo:
 Demonstrar consultas SQL com agregação,
 HAVING, relacionamentos e função armazenada.
*/


/*
 QUESTÃO 1
 Utilizando uma função agregada, apresente
 a quantidade total de ocorrências
 cadastradas no sistema.
*/

SELECT
    COUNT(*) AS total_ocorrencias
FROM ocorrencia;


/*
 QUESTÃO 2
 Apresente o nome de cada município e
 a quantidade de ocorrências registradas.

 Utilize COUNT e GROUP BY.
*/

SELECT
    m.nome AS municipio,
    COUNT(o.id_ocorrencia) AS total_ocorrencias
FROM municipio m
         LEFT JOIN ocorrencia o
                   ON m.id_municipio = o.id_municipio
GROUP BY
    m.id_municipio,
    m.nome
ORDER BY
    total_ocorrencias DESC,
    m.nome;


/*
 QUESTÃO 3
 Utilizando HAVING, apresente os municípios
 que possuem pelo menos uma ocorrência
 cadastrada.
*/

SELECT
    m.nome AS municipio,
    COUNT(o.id_ocorrencia) AS total_ocorrencias
FROM municipio m
         JOIN ocorrencia o
              ON m.id_municipio = o.id_municipio
GROUP BY
    m.id_municipio,
    m.nome
HAVING
    COUNT(o.id_ocorrencia) >= 1
ORDER BY
    total_ocorrencias DESC,
    m.nome;


/*
 QUESTÃO 4
 Demonstre o relacionamento N:N.

 Liste as ocorrências atendidas por
 mais de uma equipe, utilizando HAVING.
*/

SELECT
    o.id_ocorrencia,
    m.nome AS municipio,
    COUNT(DISTINCT a.id_equipe) AS equipes_envolvidas
FROM ocorrencia o
         JOIN municipio m
              ON o.id_municipio = m.id_municipio
         JOIN atendimento a
              ON o.id_ocorrencia = a.id_ocorrencia
GROUP BY
    o.id_ocorrencia,
    m.nome
HAVING
    COUNT(DISTINCT a.id_equipe) > 1
ORDER BY
    o.id_ocorrencia;


/*
 QUESTÃO 5
 Utilize a função armazenada
 contar_ocorrencias_municipio() para
 consultar o total de ocorrências
 de cada município.

 A função é criada no arquivo 01.
*/

SELECT
    m.nome AS municipio,
    contar_ocorrencias_municipio(
            m.id_municipio
    ) AS total_ocorrencias
FROM municipio m
ORDER BY
    m.nome;


/*
 QUESTÃO 6
 Consulte os detalhes das ocorrências,
 apresentando os nomes do município
 e do agente responsável.
*/

SELECT
    o.id_ocorrencia,
    m.nome AS municipio,
    ag.nome AS agente_responsavel,
    o.data,
    o.tipo_ocorrencia,
    o.bairro AS bairro_localidade,
    o.temperatura,
    o.umidade,
    o.situacao,
    o.descricao
FROM ocorrencia o
         JOIN municipio m
              ON o.id_municipio = m.id_municipio
         JOIN agente ag
              ON o.id_agente = ag.id_agente
ORDER BY
    o.id_ocorrencia;


/*
 QUESTÃO 7
 Apresente a quantidade de ocorrências
 por tipo: rural e urbana.
*/

SELECT
    tipo_ocorrencia,
    COUNT(*) AS quantidade
FROM ocorrencia
GROUP BY
    tipo_ocorrencia
ORDER BY
    tipo_ocorrencia;
