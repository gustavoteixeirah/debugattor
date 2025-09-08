CREATE TABLE artifacts
(
    id        UUID PRIMARY KEY                  DEFAULT gen_random_uuid(),
    step_id   UUID                     NOT NULL REFERENCES steps (id) ON DELETE CASCADE,
    content   TEXT                     NOT NULL,
    type      VARCHAR(20)              NOT NULL,
    logged_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_artifact_types CHECK (type IN ('IMAGE', 'LOG', 'JSON_DATA'))
);
