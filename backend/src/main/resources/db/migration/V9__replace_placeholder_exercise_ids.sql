-- Preserve historical attempts and links while replacing opaque seed identifiers
-- that leaked into student-facing URLs.
-- These guards also complete the representative pre-Flyway legacy shape where
-- the exercises table may have existed with only its primary key.
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS bpm_step INTEGER;
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS current_bpm INTEGER NOT NULL DEFAULT 0;
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS difficulty INTEGER;
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS min_bpm INTEGER;
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS minutes INTEGER NOT NULL DEFAULT 0;
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS pass_accuracy INTEGER;
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS pass_repetitions INTEGER;
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS target_bpm INTEGER NOT NULL DEFAULT 0;
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS activity_type VARCHAR(255);
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS description VARCHAR(255);
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS name VARCHAR(255);
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS practice_song_query VARCHAR(255);
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS reading_note VARCHAR(255);
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS reading_title VARCHAR(255);
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS reading_url VARCHAR(255);
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS skill_id VARCHAR(255);
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS technique VARCHAR(255);
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS video_query VARCHAR(255);
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS instrument ENUM ('ACOUSTIC','DRUMS','GUITAR','KEYS');
ALTER TABLE exercises ADD COLUMN IF NOT EXISTS stage ENUM ('ADVANCED','BEGINNER','BEGINNER_ADVANCED','EARLY_INTERMEDIATE','FIRST_STEPS','INTERMEDIATE','UPPER_INTERMEDIATE');

INSERT INTO exercises (
    id, bpm_step, cognitive_demand, contextual_demand, coordination_demand,
    current_bpm, difficulty, endurance_demand, expressive_demand,
    harmonic_melodic_demand, memory_demand, min_bpm, minutes, pass_accuracy,
    pass_repetitions, perceptual_demand, performance_demand, reading_demand,
    rhythmic_demand, speed_demand, target_bpm, technical_demand,
    practice_conditions, success_criteria, activity_type, description, name,
    observable_objective, practice_song_query, reading_note, reading_title,
    reading_url, skill_id, technique, video_query, instrument, stage
)
SELECT
    CASE id
        WHEN 'ex1' THEN 'guitar-chromatic-1234'
        WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
        WHEN 'ex5' THEN 'guitar-whole-step-bend'
        WHEN 'ex6' THEN 'guitar-controlled-vibrato'
        WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
        WHEN 'ex17' THEN 'keys-sight-reading'
    END,
    bpm_step, cognitive_demand, contextual_demand, coordination_demand,
    current_bpm, difficulty, endurance_demand, expressive_demand,
    harmonic_melodic_demand, memory_demand, min_bpm, minutes, pass_accuracy,
    pass_repetitions, perceptual_demand, performance_demand, reading_demand,
    rhythmic_demand, speed_demand, target_bpm, technical_demand,
    practice_conditions, success_criteria, activity_type, description, name,
    observable_objective, practice_song_query, reading_note, reading_title,
    reading_url, skill_id, technique, video_query, instrument, stage
FROM exercises old_exercise
WHERE id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17')
  AND NOT EXISTS (
      SELECT 1 FROM exercises current_exercise
      WHERE current_exercise.id = CASE old_exercise.id
          WHEN 'ex1' THEN 'guitar-chromatic-1234'
          WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
          WHEN 'ex5' THEN 'guitar-whole-step-bend'
          WHEN 'ex6' THEN 'guitar-controlled-vibrato'
          WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
          WHEN 'ex17' THEN 'keys-sight-reading'
      END
  );

UPDATE exercise_instructions SET exercise_id = CASE exercise_id
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE exercise_id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE exercise_variations SET exercise_id = CASE exercise_id
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE exercise_id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE exercise_competencies SET exercise_id = CASE exercise_id
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE exercise_id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE mission_exercises SET exercise_id = CASE exercise_id
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE exercise_id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE exercise_attempts SET exercise_id = CASE exercise_id
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE exercise_id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE studio_projects SET exercise_id = CASE exercise_id
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE exercise_id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE skill_exercises SET exercises = CASE exercises
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE exercises IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE practice_session_activity_ids SET activity_ids = CASE activity_ids
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE activity_ids IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE session_activity_results SET activity_id = CASE activity_id
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE activity_id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE practice_recordings SET context_id = CASE context_id
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE context_id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE learning_evidence SET source_id = CASE source_id
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE source_type = 'EXERCISE'
  AND source_id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE learning_content_relations SET source_id = CASE source_id
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE source_type = 'EXERCISE'
  AND source_id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

UPDATE learning_content_relations SET target_id = CASE target_id
    WHEN 'ex1' THEN 'guitar-chromatic-1234'
    WHEN 'ex2' THEN 'guitar-string-crossing-triplets'
    WHEN 'ex5' THEN 'guitar-whole-step-bend'
    WHEN 'ex6' THEN 'guitar-controlled-vibrato'
    WHEN 'ex15' THEN 'acoustic-fingerstyle-pima'
    WHEN 'ex17' THEN 'keys-sight-reading'
END WHERE target_type = 'EXERCISE'
  AND target_id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');

DELETE FROM exercises WHERE id IN ('ex1', 'ex2', 'ex5', 'ex6', 'ex15', 'ex17');
