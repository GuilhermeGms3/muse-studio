CREATE TABLE IF NOT EXISTS practice_recordings (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE,
    context_type VARCHAR(255),
    context_id VARCHAR(255),
    file_name VARCHAR(255),
    original_name VARCHAR(255),
    mime_type VARCHAR(255),
    duration_millis BIGINT NOT NULL,
    target_bpm INTEGER,
    measured_bpm INTEGER,
    timing_offset_millis INTEGER,
    rhythm_stability INTEGER,
    target_note VARCHAR(255),
    pitch_offset_cents INTEGER,
    bend_stability INTEGER
);

CREATE TABLE IF NOT EXISTS exercise_attempts (
    id UUID PRIMARY KEY,
    exercise_id VARCHAR(255),
    practiced_at TIMESTAMP WITH TIME ZONE,
    bpm INTEGER,
    accuracy INTEGER,
    duration_seconds BIGINT,
    repetitions INTEGER,
    perceived_difficulty INTEGER,
    passed BOOLEAN
);

CREATE TABLE mission_experiences (
    id UUID PRIMARY KEY,
    mission_id VARCHAR(255) NOT NULL,
    instrument_profile_id VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL,
    current_activity_kind VARCHAR(30) NOT NULL,
    current_activity_id VARCHAR(255) NOT NULL,
    last_recording_id UUID,
    assessment_attempt_id UUID,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    paused_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_mission_experience_profile UNIQUE (mission_id, instrument_profile_id),
    CONSTRAINT fk_mission_experience_mission FOREIGN KEY (mission_id) REFERENCES learning_missions(id),
    CONSTRAINT fk_mission_experience_profile FOREIGN KEY (instrument_profile_id) REFERENCES learning_instrument_profiles(id),
    CONSTRAINT fk_mission_experience_recording FOREIGN KEY (last_recording_id) REFERENCES practice_recordings(id),
    CONSTRAINT fk_mission_experience_assessment FOREIGN KEY (assessment_attempt_id) REFERENCES assessment_attempts(id),
    CONSTRAINT ck_mission_experience_status CHECK (status IN ('IN_PROGRESS', 'PAUSED', 'COMPLETED')),
    CONSTRAINT ck_mission_experience_activity CHECK (current_activity_kind IN ('ORIENTATION', 'LESSON', 'EXERCISE', 'APPLICATION', 'REFLECTION'))
);

CREATE INDEX idx_mission_experience_profile_updated
    ON mission_experiences(instrument_profile_id, updated_at);

ALTER TABLE exercise_attempts ADD COLUMN mission_experience_id UUID;
ALTER TABLE exercise_attempts ADD CONSTRAINT fk_exercise_attempt_experience
    FOREIGN KEY (mission_experience_id) REFERENCES mission_experiences(id);
CREATE INDEX idx_exercise_attempt_experience ON exercise_attempts(mission_experience_id);
