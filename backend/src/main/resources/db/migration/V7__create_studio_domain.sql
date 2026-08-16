CREATE TABLE IF NOT EXISTS studio_projects (
    id UUID PRIMARY KEY,
    owner_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    instrument VARCHAR(32) NOT NULL,
    source_kind VARCHAR(32) NOT NULL,
    source_id VARCHAR(255),
    mission_id VARCHAR(255),
    mission_experience_id UUID,
    exercise_id VARCHAR(255),
    practice_session_id UUID,
    song_id VARCHAR(255),
    music_project_id VARCHAR(255),
    bpm INTEGER NOT NULL,
    time_signature_numerator INTEGER NOT NULL,
    time_signature_denominator INTEGER NOT NULL,
    count_in_bars INTEGER NOT NULL,
    loop_enabled BOOLEAN NOT NULL,
    selected_region_id UUID,
    engine_mode VARCHAR(32) NOT NULL,
    external_project_id VARCHAR(255),
    external_project_path VARCHAR(2000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_studio_bpm CHECK (bpm BETWEEN 30 AND 300),
    CONSTRAINT ck_studio_count_in CHECK (count_in_bars BETWEEN 0 AND 8)
);

CREATE INDEX IF NOT EXISTS idx_studio_owner_updated ON studio_projects(owner_id, updated_at);
CREATE INDEX IF NOT EXISTS idx_studio_source ON studio_projects(owner_id, source_kind, source_id);

CREATE TABLE IF NOT EXISTS studio_tracks (
    project_id UUID NOT NULL,
    position_index INTEGER NOT NULL,
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    muted BOOLEAN NOT NULL,
    solo BOOLEAN NOT NULL,
    volume DOUBLE PRECISION NOT NULL,
    pan DOUBLE PRECISION NOT NULL,
    external_track_id VARCHAR(255),
    PRIMARY KEY (project_id, position_index),
    CONSTRAINT uk_studio_track_id UNIQUE (project_id, id),
    CONSTRAINT fk_studio_track_project FOREIGN KEY (project_id) REFERENCES studio_projects(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS studio_clips (
    project_id UUID NOT NULL,
    position_index INTEGER NOT NULL,
    id UUID NOT NULL,
    track_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    source_kind VARCHAR(32) NOT NULL,
    recording_id UUID,
    source_reference VARCHAR(2000),
    start_seconds DOUBLE PRECISION NOT NULL,
    offset_seconds DOUBLE PRECISION NOT NULL,
    duration_seconds DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (project_id, position_index),
    CONSTRAINT uk_studio_clip_id UNIQUE (project_id, id),
    CONSTRAINT fk_studio_clip_project FOREIGN KEY (project_id) REFERENCES studio_projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_studio_clip_track FOREIGN KEY (project_id, track_id)
        REFERENCES studio_tracks(project_id, id),
    CONSTRAINT fk_studio_clip_recording FOREIGN KEY (recording_id) REFERENCES practice_recordings(id)
);

CREATE TABLE IF NOT EXISTS studio_regions (
    project_id UUID NOT NULL,
    position_index INTEGER NOT NULL,
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    start_seconds DOUBLE PRECISION NOT NULL,
    end_seconds DOUBLE PRECISION NOT NULL,
    origin VARCHAR(32) NOT NULL,
    PRIMARY KEY (project_id, position_index),
    CONSTRAINT uk_studio_region_id UNIQUE (project_id, id),
    CONSTRAINT fk_studio_region_project FOREIGN KEY (project_id) REFERENCES studio_projects(id) ON DELETE CASCADE,
    CONSTRAINT ck_studio_region_time CHECK (start_seconds >= 0 AND end_seconds > start_seconds)
);

CREATE TABLE IF NOT EXISTS studio_markers (
    project_id UUID NOT NULL,
    position_index INTEGER NOT NULL,
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    position_seconds DOUBLE PRECISION NOT NULL,
    origin VARCHAR(32) NOT NULL,
    PRIMARY KEY (project_id, position_index),
    CONSTRAINT uk_studio_marker_id UNIQUE (project_id, id),
    CONSTRAINT fk_studio_marker_project FOREIGN KEY (project_id) REFERENCES studio_projects(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS studio_takes (
    project_id UUID NOT NULL,
    position_index INTEGER NOT NULL,
    id UUID NOT NULL,
    track_id UUID NOT NULL,
    recording_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    preferred BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    external_take_id VARCHAR(255),
    PRIMARY KEY (project_id, position_index),
    CONSTRAINT uk_studio_take_id UNIQUE (project_id, id),
    CONSTRAINT fk_studio_take_project FOREIGN KEY (project_id) REFERENCES studio_projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_studio_take_track FOREIGN KEY (project_id, track_id)
        REFERENCES studio_tracks(project_id, id),
    CONSTRAINT fk_studio_take_recording FOREIGN KEY (recording_id) REFERENCES practice_recordings(id)
);

CREATE TABLE IF NOT EXISTS reaper_integration_settings (
    id VARCHAR(32) PRIMARY KEY,
    executable_path VARCHAR(2000) NOT NULL,
    workspace_path VARCHAR(2000) NOT NULL,
    enabled BOOLEAN NOT NULL,
    last_status VARCHAR(32),
    last_message VARCHAR(1000),
    checked_at TIMESTAMP WITH TIME ZONE
);
