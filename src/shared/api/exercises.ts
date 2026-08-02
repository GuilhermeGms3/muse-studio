import { apiRequest, useApiQuery } from "./client";
import type { Exercise, ExerciseAttempt, InstrumentId } from "./contracts";

export const useExercises = (instrument: InstrumentId) =>
  useApiQuery<Exercise[]>(["exercises", instrument], `/exercises?instrument=${instrument}`);

export const recordExerciseAttempt = (
  id: string,
  attempt: {
    bpm: number;
    accuracy: number;
    durationSeconds: number;
    repetitions: number;
    perceivedDifficulty: number;
  },
) =>
  apiRequest<ExerciseAttempt>(`/exercises/${id}/attempts`, {
    method: "POST",
    body: JSON.stringify(attempt),
  });

export const saveExercise = (exercise: Exercise) =>
  apiRequest<Exercise>(`/exercises/${exercise.id}`, {
    method: "PUT",
    body: JSON.stringify(exercise),
  });
