import { apiRequest, useApiQuery } from "./client";
import type { Exercise, ExerciseAttempt, InstrumentId } from "./contracts";

export const useExercises = (instrument: InstrumentId) =>
  useApiQuery<Exercise[]>(["exercises", instrument], `/exercises?instrument=${instrument}`);

export const useExercise = (id: string) =>
  useApiQuery<Exercise>(["exercise", id], `/exercises/${encodeURIComponent(id)}`);

export const useExerciseHistory = (id: string) =>
  useApiQuery<ExerciseAttempt[]>(
    ["exercise-attempts", id],
    `/exercises/${encodeURIComponent(id)}/attempts`,
  );

export const recordExerciseAttempt = (
  id: string,
  attempt: {
    bpm: number;
    accuracy: number;
    durationSeconds: number;
    repetitions: number;
    perceivedDifficulty: number;
    missionExperienceId?: string;
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
