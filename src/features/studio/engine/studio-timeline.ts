import type { StudioProject } from "@/shared/api/contracts";

export function studioDuration(project: StudioProject) {
  return Math.max(
    30,
    ...project.clips.map((clip) => clip.startSeconds + clip.durationSeconds),
    ...project.regions.map((region) => region.endSeconds),
  );
}

export function selectedLoop(project: StudioProject) {
  if (!project.loopEnabled || !project.selectedRegionId) return undefined;
  return project.regions.find((region) => region.id === project.selectedRegionId);
}

export function audibleTrackIds(project: StudioProject) {
  const solo = project.tracks.filter((track) => track.solo);
  return new Set(
    (solo.length ? solo : project.tracks.filter((track) => !track.muted)).map((item) => item.id),
  );
}
