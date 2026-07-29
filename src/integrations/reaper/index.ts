/**
 * Reaper integration — ARCHITECTURE ONLY.
 *
 * Nothing here talks to Reaper yet. The interface below is the contract the
 * workspace will use once the bridge (ReaScript / OSC / Web Remote) exists,
 * so no UI code needs to change when it is implemented.
 */

export interface ReaperMarker {
  id: string;
  position: number;
  name: string;
}

export interface ReaperTake {
  id: string;
  name: string;
  trackName: string;
  start: number;
  length: number;
  rating?: number;
}

export interface ReaperProject {
  id: string;
  name: string;
  path: string;
  bpm: number;
  timeSignature: string;
  markers: ReaperMarker[];
  takes: ReaperTake[];
}

export interface ReaperBridge {
  isAvailable(): Promise<boolean>;
  listProjects(): Promise<ReaperProject[]>;
  openProject(id: string): Promise<void>;
  listTakes(projectId: string): Promise<ReaperTake[]>;
  importTempo(projectId: string): Promise<number>;
  importMarkers(projectId: string): Promise<ReaperMarker[]>;
  /** Link a recorded take to a practice session or a song section. */
  linkTakeToStudy(takeId: string, target: { kind: "session" | "song" | "project"; id: string }): Promise<void>;
}

export const reaperBridge: ReaperBridge = {
  async isAvailable() {
    return false;
  },
  async listProjects() {
    return [];
  },
  async openProject() {
    throw new Error("Reaper bridge not implemented yet");
  },
  async listTakes() {
    return [];
  },
  async importTempo() {
    throw new Error("Reaper bridge not implemented yet");
  },
  async importMarkers() {
    return [];
  },
  async linkTakeToStudy() {
    throw new Error("Reaper bridge not implemented yet");
  },
};
