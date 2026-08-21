-- Relation identifiers are deterministic hashes of their endpoints. V9 kept
-- the legacy row identifiers while changing endpoints, so the initializer
-- could see a different id for the same unique relation. These rows are a
-- derived catalog index and are rebuilt from canonical content on startup.
DELETE FROM learning_content_relations
WHERE (source_type = 'EXERCISE' AND source_id IN (
    'guitar-chromatic-1234',
    'guitar-string-crossing-triplets',
    'guitar-whole-step-bend',
    'guitar-controlled-vibrato',
    'acoustic-fingerstyle-pima',
    'keys-sight-reading'
)) OR (target_type = 'EXERCISE' AND target_id IN (
    'guitar-chromatic-1234',
    'guitar-string-crossing-triplets',
    'guitar-whole-step-bend',
    'guitar-controlled-vibrato',
    'acoustic-fingerstyle-pima',
    'keys-sight-reading'
));
