CREATE TABLE IF NOT EXISTS assessment_attempts (
    id UUID PRIMARY KEY,
    assessment_id VARCHAR(255) NOT NULL,
    instrument_profile_id VARCHAR(255) NOT NULL,
    observer_type VARCHAR(32) NOT NULL,
    challenge_level INTEGER NOT NULL,
    artifact_reference VARCHAR(255),
    note VARCHAR(2000),
    completed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_assessment_attempt_assessment FOREIGN KEY (assessment_id)
        REFERENCES learning_assessments(id),
    CONSTRAINT fk_assessment_attempt_profile FOREIGN KEY (instrument_profile_id)
        REFERENCES learning_instrument_profiles(id),
    CONSTRAINT ck_assessment_attempt_challenge CHECK (challenge_level BETWEEN 1 AND 5)
);

CREATE TABLE IF NOT EXISTS assessment_attempt_evidence (
    attempt_id UUID NOT NULL,
    position INTEGER NOT NULL,
    evidence_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (attempt_id, position),
    CONSTRAINT fk_assessment_attempt_evidence_owner FOREIGN KEY (attempt_id)
        REFERENCES assessment_attempts(id),
    CONSTRAINT fk_assessment_attempt_evidence_item FOREIGN KEY (evidence_id)
        REFERENCES learning_evidence(id)
);

CREATE INDEX IF NOT EXISTS idx_assessment_attempt_profile_date
    ON assessment_attempts(instrument_profile_id, completed_at);

CREATE INDEX IF NOT EXISTS idx_assessment_attempt_assessment
    ON assessment_attempts(assessment_id);
