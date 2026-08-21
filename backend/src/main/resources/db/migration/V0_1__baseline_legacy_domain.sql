CREATE TABLE IF NOT EXISTS ear_training_attempts (
        correct boolean,
        difficulty integer,
        response_millis integer,
        practiced_at timestamp(6) with time zone,
        id uuid not null,
        answer varchar(255),
        module varchar(255),
        prompt varchar(255),
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS exercise_instructions (
        exercise_id varchar(255) not null,
        instructions varchar(255)
    );

CREATE TABLE IF NOT EXISTS exercise_variations (
        bpm_offset integer,
        duration_minutes integer,
        instructions varchar(2000),
        exercise_id varchar(255) not null,
        name varchar(255)
    );

CREATE TABLE IF NOT EXISTS exercises (
        bpm_step integer,
        cognitive_demand integer,
        contextual_demand integer,
        coordination_demand integer,
        current_bpm integer not null,
        difficulty integer,
        endurance_demand integer,
        expressive_demand integer,
        harmonic_melodic_demand integer,
        memory_demand integer,
        min_bpm integer,
        minutes integer not null,
        pass_accuracy integer,
        pass_repetitions integer,
        perceptual_demand integer,
        performance_demand integer,
        reading_demand integer,
        rhythmic_demand integer,
        speed_demand integer,
        target_bpm integer not null,
        technical_demand integer,
        practice_conditions varchar(2000),
        success_criteria varchar(2000),
        activity_type varchar(255),
        description varchar(255),
        id varchar(255) not null,
        name varchar(255),
        observable_objective varchar(255),
        practice_song_query varchar(255),
        reading_note varchar(255),
        reading_title varchar(255),
        reading_url varchar(255),
        skill_id varchar(255),
        technique varchar(255),
        video_query varchar(255),
        instrument enum ('ACOUSTIC','DRUMS','GUITAR','KEYS'),
        stage enum ('ADVANCED','BEGINNER','BEGINNER_ADVANCED','EARLY_INTERMEDIATE','FIRST_STEPS','INTERMEDIATE','UPPER_INTERMEDIATE'),
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS instrument_focus (
        focus varchar(255),
        instrument_id enum ('ACOUSTIC','DRUMS','GUITAR','KEYS') not null
    );

CREATE TABLE IF NOT EXISTS instruments (
        name varchar(255),
        short_name varchar(255),
        id enum ('ACOUSTIC','DRUMS','GUITAR','KEYS') not null,
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS journal_entries (
        duration_seconds bigint not null,
        practiced_at timestamp(6) with time zone,
        id uuid not null,
        difficulties varchar(255),
        improvements varchar(255),
        notes varchar(255),
        instrument enum ('ACOUSTIC','DRUMS','GUITAR','KEYS'),
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS journal_entry_worked (
        journal_entry_id uuid not null,
        worked varchar(255)
    );

CREATE TABLE IF NOT EXISTS library_contents (
        estimated_minutes integer,
        diagram_data varchar(2000),
        tablature varchar(3000),
        category varchar(255),
        diagram_type varchar(255),
        friendly_title varchar(255),
        id varchar(255) not null,
        level varchar(255),
        skill_id varchar(255),
        summary varchar(255),
        technical_name varchar(255),
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS library_content_body (
        body varchar(255),
        library_content_id varchar(255) not null
    );

CREATE TABLE IF NOT EXISTS library_content_common_mistakes (
        common_mistakes varchar(255),
        library_content_id varchar(255) not null
    );

CREATE TABLE IF NOT EXISTS library_content_examples (
        examples varchar(255),
        library_content_id varchar(255) not null
    );

CREATE TABLE IF NOT EXISTS library_content_objectives (
        library_content_id varchar(255) not null,
        objectives varchar(255)
    );

CREATE TABLE IF NOT EXISTS library_content_related (
        library_content_id varchar(255) not null,
        related varchar(255)
    );

CREATE TABLE IF NOT EXISTS library_content_steps (
        musical_example varchar(1000),
        notation varchar(1000),
        tablature varchar(2000),
        explanation varchar(3000),
        audio_notes varchar(255),
        library_content_id varchar(255) not null,
        title varchar(255)
    );

CREATE TABLE IF NOT EXISTS music_projects (
        bpm integer not null,
        id varchar(255) not null,
        lyrics varchar(255),
        musical_key varchar(255),
        name varchar(255),
        status varchar(255),
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS music_project_ideas (
        ideas varchar(255),
        music_project_id varchar(255) not null
    );

CREATE TABLE IF NOT EXISTS music_project_references (
        music_project_id varchar(255) not null,
        references varchar(255)
    );

CREATE TABLE IF NOT EXISTS music_project_riffs (
        music_project_id varchar(255) not null,
        name varchar(255),
        riff_id varchar(255),
        tab varchar(255)
    );

CREATE TABLE IF NOT EXISTS music_project_versions (
        created_on varchar(255),
        label varchar(255),
        music_project_id varchar(255) not null,
        version_id varchar(255)
    );

CREATE TABLE IF NOT EXISTS plan_activities (
        done boolean not null,
        minutes integer not null,
        position integer not null,
        scheduled_for date,
        version bigint not null,
        id varchar(255) not null,
        kind varchar(255),
        skill_id varchar(255),
        target varchar(255),
        title varchar(255),
        instrument enum ('ACOUSTIC','DRUMS','GUITAR','KEYS'),
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS practice_sessions (
        current_activity_index integer not null,
        elapsed_seconds bigint not null,
        finished_at timestamp(6) with time zone,
        started_at timestamp(6) with time zone,
        version bigint not null,
        id uuid not null,
        notes varchar(255),
        instrument enum ('ACOUSTIC','DRUMS','GUITAR','KEYS'),
        status enum ('ACTIVE','FINISHED','PAUSED'),
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS practice_session_activity_ids (
        practice_session_id uuid not null,
        activity_ids varchar(255)
    );

CREATE TABLE IF NOT EXISTS session_activity_results (
        accuracy integer not null,
        bpm integer not null,
        suggested_bpm integer not null,
        timing_offset_millis integer not null,
        completed_at timestamp(6) with time zone,
        duration_seconds bigint not null,
        id uuid not null,
        session_id uuid,
        activity_id varchar(255),
        feedback varchar(255),
        skill_id varchar(255),
        title varchar(255),
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS skill_contents (
        contents varchar(255),
        skill_id varchar(255) not null
    );

CREATE TABLE IF NOT EXISTS skill_exercises (
        exercises varchar(255),
        skill_id varchar(255) not null
    );

CREATE TABLE IF NOT EXISTS skill_instruments (
        skill_id varchar(255) not null,
        instruments enum ('ACOUSTIC','DRUMS','GUITAR','KEYS')
    );

CREATE TABLE IF NOT EXISTS skill_next_skills (
        next_skills varchar(255),
        skill_id varchar(255) not null
    );

CREATE TABLE IF NOT EXISTS skill_prerequisites (
        prerequisites varchar(255),
        skill_id varchar(255) not null
    );

CREATE TABLE IF NOT EXISTS skill_songs (
        skill_id varchar(255) not null,
        songs varchar(255)
    );

CREATE TABLE IF NOT EXISTS skills (
        accuracy integer not null,
        current_bpm integer,
        exercise_completions integer,
        hours float(53) not null,
        practice_days integer,
        retention integer,
        review_count integer,
        review_interval_days integer,
        self_rating integer,
        songs_completed integer,
        target_bpm integer,
        last_practiced_at timestamp(6) with time zone,
        next_review_at timestamp(6) with time zone,
        version bigint not null,
        description varchar(3000),
        domain varchar(255),
        friendly_title varchar(255),
        id varchar(255) not null,
        technical_name varchar(255),
        kind enum ('ABILITY','KNOWLEDGE'),
        stage enum ('ADVANCED','BEGINNER','BEGINNER_ADVANCED','EARLY_INTERMEDIATE','FIRST_STEPS','INTERMEDIATE','UPPER_INTERMEDIATE'),
        state enum ('AVAILABLE','CONSISTENT','EXPERT','LEARNING','LOCKED','MASTERED','NATURAL','PRACTICING'),
        track enum ('CREATION','EAR','HARMONY','IMPROVISATION','PERFORMANCE','READING','REPERTOIRE','RHYTHM','TECHNIQUE'),
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS song_scales (
        scales varchar(255),
        song_id varchar(255) not null
    );

CREATE TABLE IF NOT EXISTS song_sections (
        bpm integer,
        end_seconds integer,
        progress integer,
        start_seconds integer,
        name varchar(255),
        note varchar(255),
        section_id varchar(255),
        skill_ids varchar(255),
        song_id varchar(255) not null,
        tablature varchar(255),
        tone_preset varchar(255)
    );

CREATE TABLE IF NOT EXISTS song_techniques (
        song_id varchar(255) not null,
        techniques varchar(255)
    );

CREATE TABLE IF NOT EXISTS songs (
        bpm integer not null,
        difficulty integer not null,
        progress integer not null,
        artist varchar(255),
        id varchar(255) not null,
        musical_key varchar(255),
        notes varchar(255),
        status varchar(255),
        title varchar(255),
        tuning varchar(255),
        instrument enum ('ACOUSTIC','DRUMS','GUITAR','KEYS'),
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS user_preferences (
        ear_baseline integer,
        onboarding_completed boolean,
        rhythm_baseline integer,
        session_minutes integer,
        technique_baseline integer,
        id varchar(255) not null,
        level varchar(255),
        primary_instrument enum ('ACOUSTIC','DRUMS','GUITAR','KEYS'),
        primary key (id)
    );

CREATE TABLE IF NOT EXISTS user_preferences_favorite_artists (
        favorite_artists varchar(255),
        user_preferences_id varchar(255) not null
    );

CREATE TABLE IF NOT EXISTS user_preferences_favorite_genres (
        favorite_genres varchar(255),
        user_preferences_id varchar(255) not null
    );

CREATE TABLE IF NOT EXISTS user_preferences_favorite_songs (
        favorite_songs varchar(255),
        user_preferences_id varchar(255) not null
    );

ALTER TABLE IF EXISTS exercise_instructions 
       ADD CONSTRAINT IF NOT EXISTS FKgriv3l4u9gvq0hmo65gyd2kdi 
       foreign key (exercise_id) 
       references exercises;

ALTER TABLE IF EXISTS exercise_variations 
       ADD CONSTRAINT IF NOT EXISTS FKlsoqcoe07rm6g7uplw07qsdrn 
       foreign key (exercise_id) 
       references exercises;

ALTER TABLE IF EXISTS instrument_focus 
       ADD CONSTRAINT IF NOT EXISTS FKs6n5wksoko6pooevejcoojmd9 
       foreign key (instrument_id) 
       references instruments;

ALTER TABLE IF EXISTS journal_entry_worked 
       ADD CONSTRAINT IF NOT EXISTS FKb7gsqjxq8sqconu2ebe57gunq 
       foreign key (journal_entry_id) 
       references journal_entries;

ALTER TABLE IF EXISTS library_content_body 
       ADD CONSTRAINT IF NOT EXISTS FKtmugsmlcfu5t9lahjnqafncut 
       foreign key (library_content_id) 
       references library_contents;

ALTER TABLE IF EXISTS library_content_common_mistakes 
       ADD CONSTRAINT IF NOT EXISTS FK9y7b576q1l419fi2klacw0ch0 
       foreign key (library_content_id) 
       references library_contents;

ALTER TABLE IF EXISTS library_content_examples 
       ADD CONSTRAINT IF NOT EXISTS FKjrxiexnr75uru7oqbdjkqwsmd 
       foreign key (library_content_id) 
       references library_contents;

ALTER TABLE IF EXISTS library_content_objectives 
       ADD CONSTRAINT IF NOT EXISTS FKd1kog1ry23il9qswuok08hxuc 
       foreign key (library_content_id) 
       references library_contents;

ALTER TABLE IF EXISTS library_content_related 
       ADD CONSTRAINT IF NOT EXISTS FKiyibsj4qw2m85468dgg1e2t1j 
       foreign key (library_content_id) 
       references library_contents;

ALTER TABLE IF EXISTS library_content_steps 
       ADD CONSTRAINT IF NOT EXISTS FKgpc4dnv0eng3v7jqr19pk655j 
       foreign key (library_content_id) 
       references library_contents;

ALTER TABLE IF EXISTS music_project_ideas 
       ADD CONSTRAINT IF NOT EXISTS FKbl5rcthiqo3r2jrm8hetxl9po 
       foreign key (music_project_id) 
       references music_projects;

ALTER TABLE IF EXISTS music_project_references 
       ADD CONSTRAINT IF NOT EXISTS FKgkxbuu4a9i1tdwg8dm5cfaq93 
       foreign key (music_project_id) 
       references music_projects;

ALTER TABLE IF EXISTS music_project_riffs 
       ADD CONSTRAINT IF NOT EXISTS FKea1a0hjxrpsveboe61bsw03jb 
       foreign key (music_project_id) 
       references music_projects;

ALTER TABLE IF EXISTS music_project_versions 
       ADD CONSTRAINT IF NOT EXISTS FK6vr72cnye9sdiqr2us0urt30x 
       foreign key (music_project_id) 
       references music_projects;

ALTER TABLE IF EXISTS practice_session_activity_ids 
       ADD CONSTRAINT IF NOT EXISTS FKrpccvtwfvnxr1coibxi3ddsas 
       foreign key (practice_session_id) 
       references practice_sessions;

ALTER TABLE IF EXISTS skill_contents 
       ADD CONSTRAINT IF NOT EXISTS FKbywpmcsv12bfr0hau01bn37vw 
       foreign key (skill_id) 
       references skills;

ALTER TABLE IF EXISTS skill_exercises 
       ADD CONSTRAINT IF NOT EXISTS FK520wafhiq0f4j927bvyyml247 
       foreign key (skill_id) 
       references skills;

ALTER TABLE IF EXISTS skill_instruments 
       ADD CONSTRAINT IF NOT EXISTS FK4wc8aceg1p1097ivuumugddg8 
       foreign key (skill_id) 
       references skills;

ALTER TABLE IF EXISTS skill_next_skills 
       ADD CONSTRAINT IF NOT EXISTS FKk5lmp73p3txb5whtuctoxltb6 
       foreign key (skill_id) 
       references skills;

ALTER TABLE IF EXISTS skill_prerequisites 
       ADD CONSTRAINT IF NOT EXISTS FKsctoe1hxbwctdctdv9ihe4cjn 
       foreign key (skill_id) 
       references skills;

ALTER TABLE IF EXISTS skill_songs 
       ADD CONSTRAINT IF NOT EXISTS FKsd8p1jwc166u90sx8r3jwa9up 
       foreign key (skill_id) 
       references skills;

ALTER TABLE IF EXISTS song_scales 
       ADD CONSTRAINT IF NOT EXISTS FKr10x5srk21go2qnm7k2r67f4 
       foreign key (song_id) 
       references songs;

ALTER TABLE IF EXISTS song_sections 
       ADD CONSTRAINT IF NOT EXISTS FKl8sa1rk5wwaj5568nlb6am3f1 
       foreign key (song_id) 
       references songs;

ALTER TABLE IF EXISTS song_techniques 
       ADD CONSTRAINT IF NOT EXISTS FK36btwa7kv82uh57jfp3e0t660 
       foreign key (song_id) 
       references songs;

ALTER TABLE IF EXISTS user_preferences_favorite_artists 
       ADD CONSTRAINT IF NOT EXISTS FK1aduktd2px8k5wb0a6lu1dhgs 
       foreign key (user_preferences_id) 
       references user_preferences;

ALTER TABLE IF EXISTS user_preferences_favorite_genres 
       ADD CONSTRAINT IF NOT EXISTS FKf1x02xjso1w109xc4gqdsbjtc 
       foreign key (user_preferences_id) 
       references user_preferences;

ALTER TABLE IF EXISTS user_preferences_favorite_songs 
       ADD CONSTRAINT IF NOT EXISTS FKk22srk2nm8i38tkdx4ebrcouu 
       foreign key (user_preferences_id) 
       references user_preferences;
