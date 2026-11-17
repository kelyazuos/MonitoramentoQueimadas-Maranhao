
-- UFMA | Banco de Dados - P3
-- Sistema de Monitoramento de Queimadas no Maranhão
-- Modelo físico PostgreSQL
-- KELY SOUZA DA SILVA

BEGIN;

-- 1. MUNICÍPIO
CREATE TABLE municipio (
                           id_municipio integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           nome varchar(100) NOT NULL
                               CHECK (btrim(nome) <> ''),
                           estado char(2) NOT NULL DEFAULT 'MA'
                               CHECK (estado = 'MA')
);

CREATE UNIQUE INDEX uq_municipio_nome_ma
    ON municipio (lower(btrim(nome)), estado);

-- 2. EQUIPE
CREATE TABLE equipe (
                        id_equipe integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        nome varchar(100) NOT NULL UNIQUE
                            CHECK (btrim(nome) <> '')
);

-- 3. AGENTE
CREATE TABLE agente (
                        id_agente integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        nome varchar(100) NOT NULL
                            CHECK (btrim(nome) <> ''),
                        cpf varchar(11) NOT NULL UNIQUE,
                        id_equipe integer NOT NULL,

                        CONSTRAINT chk_agente_cpf_formato
                            CHECK (cpf ~ '^[0-9]{11}$'),

    CONSTRAINT fk_agente_equipe
        FOREIGN KEY (id_equipe)
        REFERENCES equipe(id_equipe)
);

-- 4. OCORRÊNCIA
CREATE TABLE ocorrencia (
                            id_ocorrencia integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            data date NOT NULL,
                            temperatura numeric(5,2) NOT NULL,
                            umidade numeric(5,2) NOT NULL,
                            descricao text NOT NULL
                                CHECK (btrim(descricao) <> ''),
                            situacao varchar(30) NOT NULL DEFAULT 'registrada',
                            id_municipio integer NOT NULL,
                            id_agente integer NOT NULL,
                            tipo_ocorrencia varchar(10) NOT NULL,
                            tipo_vegetacao varchar(100),
                            bairro varchar(100) NOT NULL,

                            CONSTRAINT chk_umidade
                                CHECK (umidade BETWEEN 0 AND 100),

                            CONSTRAINT chk_situacao_ocorrencia
                                CHECK (
                                    situacao IN (
                                                 'registrada',
                                                 'em_atendimento',
                                                 'controlada',
                                                 'encerrada'
                                        )
                                    ),

                            CONSTRAINT chk_tipo_ocorrencia
                                CHECK (
                                    tipo_ocorrencia IN ('rural', 'urbana')
                                        AND btrim(bairro) <> ''
                                    ),

                            CONSTRAINT fk_ocorrencia_municipio
                                FOREIGN KEY (id_municipio)
                                    REFERENCES municipio(id_municipio),

                            CONSTRAINT fk_ocorrencia_agente
                                FOREIGN KEY (id_agente)
                                    REFERENCES agente(id_agente)
);

-- 5. ATENDIMENTO
-- Relacionamento N:N entre equipe e ocorrência
CREATE TABLE atendimento (
                             id_atendimento integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             data date NOT NULL,
                             situacao varchar(30) NOT NULL DEFAULT 'pendente',
                             id_equipe integer NOT NULL,
                             id_ocorrencia integer NOT NULL,

                             CONSTRAINT chk_situacao_atendimento
                                 CHECK (
                                     situacao IN (
                                                  'pendente',
                                                  'em_andamento',
                                                  'concluido'
                                         )
                                     ),

                             CONSTRAINT fk_atendimento_equipe
                                 FOREIGN KEY (id_equipe)
                                     REFERENCES equipe(id_equipe),

                             CONSTRAINT fk_atendimento_ocorrencia
                                 FOREIGN KEY (id_ocorrencia)
                                     REFERENCES ocorrencia(id_ocorrencia)
);

-- FUNÇÃO 1
-- Conta as ocorrências de um município
CREATE OR REPLACE FUNCTION contar_ocorrencias_municipio(
    p_id_municipio integer
)
RETURNS bigint
LANGUAGE plpgsql
AS $$
DECLARE
v_total bigint;
BEGIN
SELECT COUNT(*)
INTO v_total
FROM ocorrencia
WHERE id_municipio = p_id_municipio;

RETURN v_total;
END;
$$;

-- FUNÇÃO 2
-- Atualiza a situação da ocorrência
-- quando todos os atendimentos forem concluídos
CREATE OR REPLACE FUNCTION atualizar_situacao_ocorrencia()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.situacao = 'concluido' THEN

        IF NOT EXISTS (
            SELECT 1
            FROM atendimento
            WHERE id_ocorrencia = NEW.id_ocorrencia
              AND situacao <> 'concluido'
        ) THEN

UPDATE ocorrencia
SET situacao = 'controlada'
WHERE id_ocorrencia = NEW.id_ocorrencia
  AND situacao <> 'encerrada';

END IF;
END IF;

RETURN NEW;
END;
$$;

-- TRIGGER
CREATE TRIGGER trg_atendimento_concluido
    AFTER UPDATE OF situacao ON atendimento
    FOR EACH ROW
    WHEN (NEW.situacao = 'concluido')
    EXECUTE FUNCTION atualizar_situacao_ocorrencia();

COMMIT;
