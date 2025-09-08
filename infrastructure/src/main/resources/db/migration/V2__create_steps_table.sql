CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE steps
(
    id           UUID PRIMARY KEY                  DEFAULT gen_random_uuid(),
    execution_id UUID                     NOT NULL REFERENCES executions (id) ON DELETE CASCADE,
    name         VARCHAR(255)             NOT NULL,
    status       VARCHAR(20)              NOT NULL,
    registered_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at  TIMESTAMP WITH TIME ZONE NULL,
    CONSTRAINT ck_steps_status CHECK (status IN ('RUNNING', 'COMPLETED', 'FAILED'))
);
