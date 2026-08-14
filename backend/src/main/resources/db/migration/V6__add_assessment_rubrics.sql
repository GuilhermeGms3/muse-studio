CREATE TABLE assessment_rubric_levels (
    assessment_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    criterion_key VARCHAR(255) NOT NULL,
    band VARCHAR(255) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    CONSTRAINT pk_assessment_rubric_levels PRIMARY KEY (assessment_id, position),
    CONSTRAINT fk_assessment_rubric_levels_assessment
        FOREIGN KEY (assessment_id) REFERENCES learning_assessments(id) ON DELETE CASCADE
);

CREATE INDEX idx_assessment_rubric_criterion
    ON assessment_rubric_levels(assessment_id, criterion_key);
