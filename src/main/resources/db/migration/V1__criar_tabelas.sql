CREATE TABLE tecnico (
    id                 BIGSERIAL PRIMARY KEY,
    nome               VARCHAR(100) NOT NULL,
    email              VARCHAR(254) UNIQUE,
    codigo_id_discord  VARCHAR(100) UNIQUE,
    status             VARCHAR(20) NOT NULL
);

CREATE TABLE demanda (
    id                     BIGSERIAL PRIMARY KEY,
    titulo                 VARCHAR(200) NOT NULL,
    descricao              VARCHAR(350) NOT NULL,
    data_vencimento        DATE NOT NULL,
    tecnico_id             BIGINT REFERENCES tecnico(id),
    observacao             VARCHAR(1000),
    status                 VARCHAR(20) NOT NULL,
    alerta_prazo_enviado   BOOLEAN NOT NULL DEFAULT FALSE,
    alerta_atraso_enviado  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE usuario (
    id       BIGSERIAL PRIMARY KEY,
    usuario  VARCHAR(50) NOT NULL UNIQUE,
    senha    VARCHAR(150) NOT NULL,
    status   VARCHAR(20) NOT NULL
);
