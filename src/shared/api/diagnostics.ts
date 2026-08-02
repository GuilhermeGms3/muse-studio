import { apiRequest } from "./client";
import type { DiagnosticResult, InstrumentId, UserPreferences } from "./contracts";

export const savePreferences = (preferences: UserPreferences) =>
  apiRequest<UserPreferences>("/preferences", {
    method: "PUT",
    body: JSON.stringify(preferences),
  });

export const completeDiagnostic = (diagnostic: {
  instrument: InstrumentId;
  level: string;
  sessionMinutes: number;
  favoriteGenres: string[];
  favoriteArtists: string[];
  favoriteSongs: string[];
  rhythmScore: number;
  earScore: number;
  techniqueScore: number;
}) =>
  apiRequest<DiagnosticResult>("/diagnostic", {
    method: "POST",
    body: JSON.stringify(diagnostic),
  });
