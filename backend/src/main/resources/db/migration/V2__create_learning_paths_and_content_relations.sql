ALTER TABLE IF EXISTS learning_instrument_profiles
    ADD COLUMN IF NOT EXISTS primary_profile BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS learning_paths (
    id VARCHAR(255) PRIMARY KEY,
    instrument_profile_id VARCHAR(255) NOT NULL,
    curriculum_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    derivation_version VARCHAR(255) NOT NULL,
    derivation_reason VARCHAR(3000) NOT NULL,
    status VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_learning_path_profile FOREIGN KEY (instrument_profile_id)
        REFERENCES learning_instrument_profiles(id),
    CONSTRAINT fk_learning_path_curriculum FOREIGN KEY (curriculum_id)
        REFERENCES learning_curricula(id)
);

CREATE INDEX IF NOT EXISTS idx_learning_path_profile_status
    ON learning_paths(instrument_profile_id, status);

CREATE INDEX IF NOT EXISTS idx_learning_path_curriculum
    ON learning_paths(curriculum_id);

CREATE TABLE IF NOT EXISTS learning_path_goals (
    learning_path_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    learning_goal_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (learning_path_id, position),
    CONSTRAINT fk_learning_path_goal_owner FOREIGN KEY (learning_path_id)
        REFERENCES learning_paths(id),
    CONSTRAINT fk_learning_path_goal_goal FOREIGN KEY (learning_goal_id)
        REFERENCES learning_goals(id)
);

CREATE TABLE IF NOT EXISTS learning_path_steps (
    learning_path_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    competency_id VARCHAR(255) NOT NULL,
    kind VARCHAR(64) NOT NULL,
    readiness VARCHAR(64) NOT NULL,
    rationale VARCHAR(1000) NOT NULL,
    PRIMARY KEY (learning_path_id, position),
    CONSTRAINT fk_learning_path_step_owner FOREIGN KEY (learning_path_id)
        REFERENCES learning_paths(id),
    CONSTRAINT fk_learning_path_step_competency FOREIGN KEY (competency_id)
        REFERENCES learning_competencies(id)
);

CREATE TABLE IF NOT EXISTS learning_content_relations (
    id VARCHAR(255) PRIMARY KEY,
    source_type VARCHAR(64) NOT NULL,
    source_id VARCHAR(255) NOT NULL,
    relation_type VARCHAR(64) NOT NULL,
    target_type VARCHAR(64) NOT NULL,
    target_id VARCHAR(255) NOT NULL,
    strength VARCHAR(64) NOT NULL,
    justification VARCHAR(2000) NOT NULL,
    conditions VARCHAR(2000),
    migrated_from VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_learning_content_relation UNIQUE
        (source_type, source_id, relation_type, target_type, target_id),
    CONSTRAINT ck_learning_content_relation_not_self CHECK
        (source_type <> target_type OR source_id <> target_id)
);

CREATE INDEX IF NOT EXISTS idx_learning_relation_source
    ON learning_content_relations(source_type, source_id);

CREATE INDEX IF NOT EXISTS idx_learning_relation_target
    ON learning_content_relations(target_type, target_id);
