-- Remove only untouched personal-state fixtures created by the legacy initializer.
-- Catalog content is intentionally preserved.

CREATE TEMPORARY TABLE demo_music_projects_to_remove AS
SELECT id FROM music_projects p WHERE (
  p.id = 'umbra' AND p.name = 'Umbra' AND p.musical_key = 'Em' AND p.bpm = 132
  AND p.status = 'arranging' AND p.lyrics = 'Sombras longas na sala vazia...'
  AND (SELECT COUNT(*) FROM music_project_ideas i WHERE i.music_project_id = p.id) = 2
  AND (SELECT COUNT(*) FROM music_project_ideas i WHERE i.music_project_id = p.id AND i.ideas IN ('Trocar o refrão para 6/8', 'Guitarra limpa com delay')) = 2
  AND (SELECT COUNT(*) FROM music_project_references r WHERE r.music_project_id = p.id) = 1
  AND (SELECT COUNT(*) FROM music_project_references r WHERE r.music_project_id = p.id AND r.references = 'Russian Circles — Harper Lewis') = 1
  AND (SELECT COUNT(*) FROM music_project_riffs r WHERE r.music_project_id = p.id) = 1
  AND (SELECT COUNT(*) FROM music_project_riffs r WHERE r.music_project_id = p.id AND r.riff_id = 'r1' AND r.name = 'Riff principal') = 1
  AND (SELECT COUNT(*) FROM music_project_versions v WHERE v.music_project_id = p.id) = 2
  AND (SELECT COUNT(*) FROM music_project_versions v WHERE v.music_project_id = p.id AND ((v.version_id = 'v1' AND v.label = 'v0.1 — esqueleto' AND v.created_on = '02/07') OR (v.version_id = 'v2' AND v.label = 'v0.2 — riff e ponte' AND v.created_on = '14/07'))) = 2
) OR (
  p.id = 'noturno' AND p.name = 'Noturno' AND p.musical_key = 'Dm' AND p.bpm = 72
  AND p.status = 'sketch' AND COALESCE(p.lyrics, '') = ''
  AND (SELECT COUNT(*) FROM music_project_ideas i WHERE i.music_project_id = p.id) = 2
  AND (SELECT COUNT(*) FROM music_project_ideas i WHERE i.music_project_id = p.id AND i.ideas IN ('Melodia sobre pedal de D', 'Testar rearmonização')) = 2
  AND (SELECT COUNT(*) FROM music_project_references r WHERE r.music_project_id = p.id) = 2
  AND (SELECT COUNT(*) FROM music_project_references r WHERE r.music_project_id = p.id AND r.references IN ('Satie', 'Nils Frahm')) = 2
  AND (SELECT COUNT(*) FROM music_project_riffs r WHERE r.music_project_id = p.id) = 1
  AND (SELECT COUNT(*) FROM music_project_riffs r WHERE r.music_project_id = p.id AND r.riff_id = 'r1' AND r.name = 'Tema de teclado' AND r.tab = 'Dm — Bb — F — C') = 1
  AND (SELECT COUNT(*) FROM music_project_versions v WHERE v.music_project_id = p.id) = 1
  AND (SELECT COUNT(*) FROM music_project_versions v WHERE v.music_project_id = p.id AND v.version_id = 'v1' AND v.label = 'v0.1 — ideia inicial' AND v.created_on = '09/07') = 1
);

DELETE FROM music_project_ideas WHERE music_project_id IN (SELECT id FROM demo_music_projects_to_remove);
DELETE FROM music_project_references WHERE music_project_id IN (SELECT id FROM demo_music_projects_to_remove);
DELETE FROM music_project_riffs WHERE music_project_id IN (SELECT id FROM demo_music_projects_to_remove);
DELETE FROM music_project_versions WHERE music_project_id IN (SELECT id FROM demo_music_projects_to_remove);
DELETE FROM music_projects WHERE id IN (SELECT id FROM demo_music_projects_to_remove);
DROP TABLE demo_music_projects_to_remove;

CREATE TEMPORARY TABLE demo_journal_entries_to_remove AS
SELECT id FROM journal_entries j WHERE COALESCE(j.notes, '') = '' AND (
  (j.duration_seconds = 8040 AND j.instrument = 'GUITAR' AND j.difficulties = 'Bends desafinando.' AND j.improvements = 'Cromático subiu para 110 BPM.'
    AND (SELECT COUNT(*) FROM journal_entry_worked w WHERE w.journal_entry_id = j.id) = 3
    AND (SELECT COUNT(*) FROM journal_entry_worked w WHERE w.journal_entry_id = j.id AND w.worked IN ('Pentatônica', 'Alternate Picking', 'Sweet Child O'' Mine')) = 3)
  OR (j.duration_seconds = 6300 AND j.instrument = 'KEYS' AND j.difficulties = 'Figuras pontuadas lentas.' AND j.improvements = 'Seção A sem parar.'
    AND (SELECT COUNT(*) FROM journal_entry_worked w WHERE w.journal_entry_id = j.id) = 3
    AND (SELECT COUNT(*) FROM journal_entry_worked w WHERE w.journal_entry_id = j.id AND w.worked IN ('Leitura', 'Campo Harmônico', 'Gymnopédie')) = 3)
  OR (j.duration_seconds = 6480 AND j.instrument = 'GUITAR' AND j.difficulties = 'Ruído de cordas soltas.' AND j.improvements = 'Fraseado mais musical.'
    AND (SELECT COUNT(*) FROM journal_entry_worked w WHERE w.journal_entry_id = j.id) = 2
    AND (SELECT COUNT(*) FROM journal_entry_worked w WHERE w.journal_entry_id = j.id AND w.worked IN ('Legato', 'Improvisação')) = 2)
  OR (j.duration_seconds = 3120 AND j.instrument = 'ACOUSTIC' AND j.difficulties = 'Polegar perde constância.' AND j.improvements = 'Intro estável.'
    AND (SELECT COUNT(*) FROM journal_entry_worked w WHERE w.journal_entry_id = j.id) = 2
    AND (SELECT COUNT(*) FROM journal_entry_worked w WHERE w.journal_entry_id = j.id AND w.worked IN ('Blackbird', 'Levadas')) = 2)
  OR (j.duration_seconds = 9000 AND j.instrument = 'GUITAR' AND j.difficulties = 'Sweep embolado.' AND j.improvements = 'Duas versões gravadas.'
    AND (SELECT COUNT(*) FROM journal_entry_worked w WHERE w.journal_entry_id = j.id) = 2
    AND (SELECT COUNT(*) FROM journal_entry_worked w WHERE w.journal_entry_id = j.id AND w.worked IN ('Arpejos', 'Projeto Umbra')) = 2)
);

DELETE FROM journal_entry_worked WHERE journal_entry_id IN (SELECT id FROM demo_journal_entries_to_remove);
DELETE FROM journal_entries WHERE id IN (SELECT id FROM demo_journal_entries_to_remove);
DROP TABLE demo_journal_entries_to_remove;

DELETE FROM plan_activities WHERE done = FALSE AND version = 0 AND (
  (id LIKE '%-gtr-1' AND title = 'Alternate Picking' AND kind = 'technique' AND target = '122 BPM limpo')
  OR (id LIKE '%-gtr-2' AND title = 'Sweet Child O'' Mine' AND kind = 'repertoire' AND target = 'Solo B a 92 BPM')
  OR (id LIKE '%-gtr-3' AND title = 'Campo Harmônico' AND kind = 'theory' AND target = 'Graus em C, G e D')
  OR (id LIKE '%-gtr-4' AND title = 'Revisão' AND kind = 'warmup' AND target = 'Bends e vibrato')
  OR (id LIKE '%-vla-1' AND title = 'Fingerstyle' AND kind = 'technique' AND target = 'Polegar constante')
  OR (id LIKE '%-vla-2' AND title = 'Blackbird' AND kind = 'repertoire' AND target = 'Bridge')
  OR (id LIKE '%-key-1' AND title = 'Leitura' AND kind = 'technique' AND target = 'Sem interromper')
  OR (id LIKE '%-key-2' AND title = 'Gymnopédie No.1' AND kind = 'repertoire' AND target = 'Dinâmica e pedal')
);

CREATE TEMPORARY TABLE demo_preferences_to_remove AS
SELECT id FROM user_preferences p WHERE p.id = 'default' AND p.level = 'intermediate' AND p.session_minutes = 60
  AND COALESCE(p.onboarding_completed, FALSE) = FALSE AND COALESCE(p.rhythm_baseline, 0) = 0
  AND COALESCE(p.ear_baseline, 0) = 0 AND COALESCE(p.technique_baseline, 0) = 0
  AND COALESCE(p.primary_instrument, 'GUITAR') = 'GUITAR'
  AND (SELECT COUNT(*) FROM user_preferences_favorite_songs s WHERE s.user_preferences_id = p.id) = 0
  AND (SELECT COUNT(*) FROM user_preferences_favorite_genres g WHERE g.user_preferences_id = p.id) = 4
  AND (SELECT COUNT(*) FROM user_preferences_favorite_genres g WHERE g.user_preferences_id = p.id AND g.favorite_genres IN ('Rock', 'Blues', 'Post-rock', 'Instrumental')) = 4
  AND (SELECT COUNT(*) FROM user_preferences_favorite_artists a WHERE a.user_preferences_id = p.id) = 4
  AND (SELECT COUNT(*) FROM user_preferences_favorite_artists a WHERE a.user_preferences_id = p.id AND a.favorite_artists IN ('Guns N'' Roses', 'Jimi Hendrix', 'The Beatles', 'Russian Circles')) = 4;

DELETE FROM user_preferences_favorite_artists WHERE user_preferences_id IN (SELECT id FROM demo_preferences_to_remove);
DELETE FROM user_preferences_favorite_genres WHERE user_preferences_id IN (SELECT id FROM demo_preferences_to_remove);
DELETE FROM user_preferences_favorite_songs WHERE user_preferences_id IN (SELECT id FROM demo_preferences_to_remove);
DELETE FROM user_preferences WHERE id IN (SELECT id FROM demo_preferences_to_remove);
DROP TABLE demo_preferences_to_remove;
