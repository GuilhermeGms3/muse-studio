ALTER TABLE learning_evidence
    ADD CONSTRAINT fk_evidence_profile
    FOREIGN KEY (instrument_profile_id) REFERENCES learning_instrument_profiles(id);

ALTER TABLE learning_evidence
    ADD CONSTRAINT fk_evidence_competency
    FOREIGN KEY (competency_id) REFERENCES learning_competencies(id);

ALTER TABLE learning_evidence
    ADD CONSTRAINT fk_evidence_superseded
    FOREIGN KEY (supersedes_evidence_id) REFERENCES learning_evidence(id);

ALTER TABLE learning_evidence
    ADD CONSTRAINT ck_evidence_challenge_level
    CHECK (challenge_level BETWEEN 1 AND 5);

ALTER TABLE learning_mastery
    ADD CONSTRAINT fk_mastery_competency
    FOREIGN KEY (competency_id) REFERENCES learning_competencies(id);

ALTER TABLE mastery_supporting_evidence
    ADD CONSTRAINT fk_mastery_supporting_evidence
    FOREIGN KEY (evidence_id) REFERENCES learning_evidence(id);

ALTER TABLE mastery_limiting_evidence
    ADD CONSTRAINT fk_mastery_limiting_evidence
    FOREIGN KEY (evidence_id) REFERENCES learning_evidence(id);

CREATE INDEX IF NOT EXISTS idx_evidence_profile_competency_validity
    ON learning_evidence(instrument_profile_id, competency_id, state, valid_until);

CREATE INDEX IF NOT EXISTS idx_evidence_supersedes
    ON learning_evidence(supersedes_evidence_id);
