CREATE TABLE IF NOT EXISTS learning_curricula (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    version_label VARCHAR(255) NOT NULL,
    purpose VARCHAR(3000) NOT NULL,
    audience VARCHAR(2000) NOT NULL,
    instrument VARCHAR(32),
    starting_stage VARCHAR(64) NOT NULL,
    target_stage VARCHAR(64) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS curriculum_outcomes (
    curriculum_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    outcome VARCHAR(1000) NOT NULL,
    PRIMARY KEY (curriculum_id, position),
    CONSTRAINT fk_curriculum_outcome FOREIGN KEY (curriculum_id) REFERENCES learning_curricula(id)
);

CREATE TABLE IF NOT EXISTS curriculum_competencies (
    curriculum_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    competency_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (curriculum_id, position),
    CONSTRAINT fk_curriculum_competency_owner FOREIGN KEY (curriculum_id) REFERENCES learning_curricula(id)
);

CREATE TABLE IF NOT EXISTS learning_competencies (
    id VARCHAR(255) PRIMARY KEY,
    friendly_title VARCHAR(255) NOT NULL,
    technical_name VARCHAR(255) NOT NULL,
    domain VARCHAR(255) NOT NULL,
    observable_action VARCHAR(255) NOT NULL,
    description VARCHAR(3000) NOT NULL,
    observation_conditions VARCHAR(2000),
    kind VARCHAR(64) NOT NULL,
    track VARCHAR(64) NOT NULL,
    stage VARCHAR(64) NOT NULL,
    evidence_policy_key VARCHAR(255),
    retention_window_days INTEGER,
    legacy_skill_id VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_competency_legacy_skill
    ON learning_competencies(legacy_skill_id);

CREATE TABLE IF NOT EXISTS competency_instruments (
    competency_id VARCHAR(255) NOT NULL,
    instrument VARCHAR(32) NOT NULL,
    CONSTRAINT fk_competency_instrument FOREIGN KEY (competency_id) REFERENCES learning_competencies(id)
);

CREATE TABLE IF NOT EXISTS competency_prerequisites (
    competency_id VARCHAR(255) NOT NULL,
    prerequisite_competency_id VARCHAR(255) NOT NULL,
    prerequisite_type VARCHAR(64) NOT NULL,
    CONSTRAINT fk_competency_prerequisite_owner FOREIGN KEY (competency_id) REFERENCES learning_competencies(id)
);

CREATE TABLE IF NOT EXISTS competency_criteria (
    competency_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    criterion_key VARCHAR(255) NOT NULL,
    PRIMARY KEY (competency_id, position),
    CONSTRAINT fk_competency_criterion FOREIGN KEY (competency_id) REFERENCES learning_competencies(id)
);

CREATE TABLE IF NOT EXISTS learning_lessons (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    technical_name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    summary VARCHAR(2000) NOT NULL,
    content VARCHAR(10000) NOT NULL,
    estimated_minutes INTEGER NOT NULL,
    stage VARCHAR(64) NOT NULL,
    format VARCHAR(64) NOT NULL,
    legacy_library_content_id VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_lesson_legacy_content
    ON learning_lessons(legacy_library_content_id);

CREATE TABLE IF NOT EXISTS lesson_competencies (
    lesson_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    competency_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (lesson_id, position),
    CONSTRAINT fk_lesson_competency_owner FOREIGN KEY (lesson_id) REFERENCES learning_lessons(id)
);

CREATE TABLE IF NOT EXISTS lesson_objectives (
    lesson_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    objective VARCHAR(1000) NOT NULL,
    PRIMARY KEY (lesson_id, position),
    CONSTRAINT fk_lesson_objective FOREIGN KEY (lesson_id) REFERENCES learning_lessons(id)
);

CREATE TABLE IF NOT EXISTS lesson_examples (
    lesson_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    example VARCHAR(2000) NOT NULL,
    PRIMARY KEY (lesson_id, position),
    CONSTRAINT fk_lesson_example FOREIGN KEY (lesson_id) REFERENCES learning_lessons(id)
);

ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS observable_objective VARCHAR(255);
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS practice_conditions VARCHAR(2000);
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS success_criteria VARCHAR(2000);
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS technical_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS rhythmic_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS coordination_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS speed_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS endurance_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS cognitive_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS memory_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS reading_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS perceptual_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS harmonic_melodic_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS expressive_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS contextual_demand INTEGER NOT NULL DEFAULT 0;
ALTER TABLE IF EXISTS exercises ADD COLUMN IF NOT EXISTS performance_demand INTEGER NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS exercise_competencies (
    exercise_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    competency_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (exercise_id, position)
);

CREATE TABLE IF NOT EXISTS learning_assessments (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    type VARCHAR(64) NOT NULL,
    protocol_version VARCHAR(255) NOT NULL,
    instructions VARCHAR(5000) NOT NULL,
    conditions VARCHAR(2000) NOT NULL,
    allowed_support VARCHAR(2000) NOT NULL,
    inconclusive_rule VARCHAR(2000) NOT NULL,
    estimated_minutes INTEGER NOT NULL,
    maximum_attempts INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    technical_demand INTEGER NOT NULL DEFAULT 0,
    rhythmic_demand INTEGER NOT NULL DEFAULT 0,
    coordination_demand INTEGER NOT NULL DEFAULT 0,
    speed_demand INTEGER NOT NULL DEFAULT 0,
    endurance_demand INTEGER NOT NULL DEFAULT 0,
    cognitive_demand INTEGER NOT NULL DEFAULT 0,
    memory_demand INTEGER NOT NULL DEFAULT 0,
    reading_demand INTEGER NOT NULL DEFAULT 0,
    perceptual_demand INTEGER NOT NULL DEFAULT 0,
    harmonic_melodic_demand INTEGER NOT NULL DEFAULT 0,
    expressive_demand INTEGER NOT NULL DEFAULT 0,
    contextual_demand INTEGER NOT NULL DEFAULT 0,
    performance_demand INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS assessment_competencies (
    assessment_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    competency_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (assessment_id, position),
    CONSTRAINT fk_assessment_competency_owner FOREIGN KEY (assessment_id) REFERENCES learning_assessments(id)
);

CREATE TABLE IF NOT EXISTS assessment_criteria (
    assessment_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    criterion_key VARCHAR(255) NOT NULL,
    PRIMARY KEY (assessment_id, position),
    CONSTRAINT fk_assessment_criterion FOREIGN KEY (assessment_id) REFERENCES learning_assessments(id)
);

CREATE TABLE IF NOT EXISTS learning_missions (
    id VARCHAR(255) PRIMARY KEY,
    curriculum_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    observable_objective VARCHAR(2000) NOT NULL,
    context VARCHAR(2000) NOT NULL,
    motivation VARCHAR(2000) NOT NULL,
    estimated_minutes INTEGER NOT NULL,
    instrument VARCHAR(32),
    stage VARCHAR(64) NOT NULL,
    status VARCHAR(64) NOT NULL,
    completion_criteria VARCHAR(2000) NOT NULL,
    expected_evidence VARCHAR(2000) NOT NULL,
    musical_application VARCHAR(2000),
    easier_mission_id VARCHAR(255),
    technical_demand INTEGER NOT NULL DEFAULT 0,
    rhythmic_demand INTEGER NOT NULL DEFAULT 0,
    coordination_demand INTEGER NOT NULL DEFAULT 0,
    speed_demand INTEGER NOT NULL DEFAULT 0,
    endurance_demand INTEGER NOT NULL DEFAULT 0,
    cognitive_demand INTEGER NOT NULL DEFAULT 0,
    memory_demand INTEGER NOT NULL DEFAULT 0,
    reading_demand INTEGER NOT NULL DEFAULT 0,
    perceptual_demand INTEGER NOT NULL DEFAULT 0,
    harmonic_melodic_demand INTEGER NOT NULL DEFAULT 0,
    expressive_demand INTEGER NOT NULL DEFAULT 0,
    contextual_demand INTEGER NOT NULL DEFAULT 0,
    performance_demand INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_mission_curriculum FOREIGN KEY (curriculum_id) REFERENCES learning_curricula(id)
);

CREATE TABLE IF NOT EXISTS mission_competencies (
    mission_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    competency_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (mission_id, position),
    CONSTRAINT fk_mission_competency_owner FOREIGN KEY (mission_id) REFERENCES learning_missions(id)
);

CREATE TABLE IF NOT EXISTS mission_lessons (
    mission_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    lesson_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (mission_id, position),
    CONSTRAINT fk_mission_lesson_owner FOREIGN KEY (mission_id) REFERENCES learning_missions(id)
);

CREATE TABLE IF NOT EXISTS mission_exercises (
    mission_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    exercise_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (mission_id, position),
    CONSTRAINT fk_mission_exercise_owner FOREIGN KEY (mission_id) REFERENCES learning_missions(id)
);

CREATE TABLE IF NOT EXISTS mission_assessments (
    mission_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    assessment_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (mission_id, position),
    CONSTRAINT fk_mission_assessment_owner FOREIGN KEY (mission_id) REFERENCES learning_missions(id)
);

CREATE TABLE IF NOT EXISTS learning_instrument_profiles (
    id VARCHAR(255) PRIMARY KEY,
    owner_id VARCHAR(255) NOT NULL,
    instrument VARCHAR(32) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    current_stage VARCHAR(64) NOT NULL,
    curriculum_id VARCHAR(255),
    legacy_preferences_id VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_instrument_profile_owner_instrument UNIQUE (owner_id, instrument)
);

CREATE TABLE IF NOT EXISTS learning_goals (
    id VARCHAR(255) PRIMARY KEY,
    instrument_profile_id VARCHAR(255) NOT NULL,
    curriculum_id VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    desired_outcome VARCHAR(3000) NOT NULL,
    musical_context VARCHAR(2000) NOT NULL,
    type VARCHAR(64) NOT NULL,
    status VARCHAR(64) NOT NULL,
    priority INTEGER NOT NULL,
    target_date DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_learning_goal_profile FOREIGN KEY (instrument_profile_id) REFERENCES learning_instrument_profiles(id)
);

CREATE TABLE IF NOT EXISTS learning_goal_competencies (
    learning_goal_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    competency_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (learning_goal_id, position),
    CONSTRAINT fk_learning_goal_competency_owner FOREIGN KEY (learning_goal_id) REFERENCES learning_goals(id)
);

CREATE TABLE IF NOT EXISTS learning_goal_repertoire (
    learning_goal_id VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    repertoire_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (learning_goal_id, position),
    CONSTRAINT fk_learning_goal_repertoire_owner FOREIGN KEY (learning_goal_id) REFERENCES learning_goals(id)
);

CREATE TABLE IF NOT EXISTS learning_evidence (
    id VARCHAR(255) PRIMARY KEY,
    instrument_profile_id VARCHAR(255) NOT NULL,
    competency_id VARCHAR(255) NOT NULL,
    criterion_key VARCHAR(255) NOT NULL,
    type VARCHAR(64) NOT NULL,
    state VARCHAR(64) NOT NULL,
    functional_weight VARCHAR(64) NOT NULL,
    reliability VARCHAR(64) NOT NULL,
    result VARCHAR(64) NOT NULL,
    source_type VARCHAR(64) NOT NULL,
    source_id VARCHAR(255) NOT NULL,
    independence_key VARCHAR(255) NOT NULL,
    challenge_level INTEGER NOT NULL,
    observation VARCHAR(4000) NOT NULL,
    conditions VARCHAR(3000) NOT NULL,
    protocol_version VARCHAR(255),
    analyzer_version VARCHAR(255),
    artifact_reference VARCHAR(255),
    supersedes_evidence_id VARCHAR(255),
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    captured_at TIMESTAMP WITH TIME ZONE NOT NULL,
    valid_until TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_evidence_profile_competency_occurred
    ON learning_evidence(instrument_profile_id, competency_id, occurred_at);

CREATE TABLE IF NOT EXISTS learning_mastery (
    id VARCHAR(255) PRIMARY KEY,
    instrument_profile_id VARCHAR(255) NOT NULL,
    competency_id VARCHAR(255) NOT NULL,
    state VARCHAR(64) NOT NULL,
    evidence_policy_version VARCHAR(255) NOT NULL,
    mandatory_criteria_covered BOOLEAN NOT NULL DEFAULT FALSE,
    independent_evidence_count INTEGER NOT NULL DEFAULT 0,
    application_observed BOOLEAN NOT NULL DEFAULT FALSE,
    transfer_observed BOOLEAN NOT NULL DEFAULT FALSE,
    retention_observed BOOLEAN NOT NULL DEFAULT FALSE,
    unresolved_conflict BOOLEAN NOT NULL DEFAULT FALSE,
    rationale VARCHAR(3000) NOT NULL,
    assessed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_evidence_at TIMESTAMP WITH TIME ZONE,
    next_review_at TIMESTAMP WITH TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_mastery_profile_competency UNIQUE (instrument_profile_id, competency_id),
    CONSTRAINT fk_mastery_profile FOREIGN KEY (instrument_profile_id) REFERENCES learning_instrument_profiles(id)
);

CREATE TABLE IF NOT EXISTS mastery_supporting_evidence (
    mastery_id VARCHAR(255) NOT NULL,
    evidence_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_mastery_supporting_owner FOREIGN KEY (mastery_id) REFERENCES learning_mastery(id)
);

CREATE TABLE IF NOT EXISTS mastery_limiting_evidence (
    mastery_id VARCHAR(255) NOT NULL,
    evidence_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_mastery_limiting_owner FOREIGN KEY (mastery_id) REFERENCES learning_mastery(id)
);
