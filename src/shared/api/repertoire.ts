import { apiRequest, useApiQuery } from "./client";
import type {
  InstrumentId,
  PlanActivity,
  PracticeSong,
  Song,
  SongRecommendation,
} from "./contracts";

export const useSongs = (instrument?: InstrumentId) =>
  useApiQuery<Song[]>(
    ["songs", instrument ?? "all"],
    instrument ? `/songs?instrument=${instrument}` : "/songs",
  );

export const getSongRecommendations = (skill: string, instrument: InstrumentId) =>
  apiRequest<SongRecommendation[]>(
    `/recommendations/songs?skill=${encodeURIComponent(skill)}&instrument=${instrument}`,
  );

export const searchPracticeSong = (query: string, instrument?: InstrumentId) =>
  apiRequest<PracticeSong>(
    `/practice-songs/search?query=${encodeURIComponent(query)}${instrument ? `&instrument=${instrument}` : ""}`,
  );

export const saveSong = (song: Song) =>
  apiRequest<Song>(`/songs/${song.id}`, {
    method: "PUT",
    body: JSON.stringify(song),
  });

export const createSongPracticePlan = (songId: string, minutes: number) =>
  apiRequest<PlanActivity[]>(`/songs/${songId}/practice-plan?minutes=${minutes}`, {
    method: "POST",
  });
