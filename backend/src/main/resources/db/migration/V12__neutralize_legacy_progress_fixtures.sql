-- Neutralize only exact legacy progress fixtures. Definitions and user-modified progress are preserved.

UPDATE skills SET state = 'AVAILABLE', hours = 0, accuracy = 0
WHERE id = 'rhythm' AND state = 'MASTERED' AND hours = 18 AND accuracy = 91
  AND current_bpm IS NULL AND last_practiced_at IS NULL;
UPDATE skills SET state = 'LOCKED', hours = 0, accuracy = 0, current_bpm = NULL
WHERE id = 'alternate-picking' AND state = 'PRACTICING' AND hours = 12.5 AND accuracy = 76
  AND current_bpm = 118 AND last_practiced_at IS NULL;
UPDATE skills SET state = 'LOCKED', hours = 0, accuracy = 0, current_bpm = NULL
WHERE id = 'bends' AND state = 'PRACTICING' AND hours = 7.5 AND accuracy = 72
  AND current_bpm = 72 AND last_practiced_at IS NULL;
UPDATE skills SET state = 'LOCKED', hours = 0, accuracy = 0, current_bpm = NULL
WHERE id = 'vibrato' AND state = 'AVAILABLE' AND hours = 3 AND accuracy = 55
  AND current_bpm = 78 AND last_practiced_at IS NULL;
UPDATE skills SET state = 'LOCKED', hours = 0, accuracy = 0
WHERE id = 'harmonic-field' AND state = 'LEARNING' AND hours = 6 AND accuracy = 64
  AND current_bpm IS NULL AND last_practiced_at IS NULL;
UPDATE skills SET hours = 0, accuracy = 0
WHERE id = 'modes' AND state = 'LOCKED' AND hours = 1 AND accuracy = 20
  AND current_bpm IS NULL AND last_practiced_at IS NULL;

CREATE TEMPORARY TABLE demo_song_progress_to_remove AS
SELECT id FROM songs WHERE
  (id = 'sweet-child' AND status = 'learning' AND progress = 55 AND notes = 'Solo B ainda inconsistente; conferir a afinação dos bends.')
  OR (id = 'little-wing' AND status = 'backlog' AND progress = 10 AND notes = 'Estudar tríades móveis antes do arranjo.')
  OR (id = 'blackbird' AND status = 'learning' AND progress = 48 AND notes = 'Trabalhar independência do polegar.')
  OR (id = 'gymnopedie' AND status = 'learning' AND progress = 70 AND notes = 'Refinar dinâmica e pedal.')
  OR (id = 'seven-nation-army-drums' AND status = 'learning' AND progress = 15 AND notes = 'Manter caixa em 2 e 4 e voltar ao groove depois das viradas.')
  OR (id = 'back-in-black-drums' AND status = 'backlog' AND progress = 0 AND notes = 'Ouvir os espaços e não preencher demais.')
  OR (id = 'billie-jean-drums' AND status = 'backlog' AND progress = 0 AND notes = 'O desafio é repetir o mesmo groove sem perder a precisão.');

UPDATE song_sections SET progress = 0
WHERE song_id IN (SELECT id FROM demo_song_progress_to_remove);
UPDATE songs SET status = 'backlog', progress = 0, notes = NULL
WHERE id IN (SELECT id FROM demo_song_progress_to_remove);
DROP TABLE demo_song_progress_to_remove;
