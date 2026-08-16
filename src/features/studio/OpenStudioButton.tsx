import { useNavigate } from "@tanstack/react-router";
import { AudioLines } from "lucide-react";
import { useState } from "react";
import { createStudioProject, type InstrumentId, type StudioSourceKind } from "@/lib/music-api";

export function OpenStudioButton({
  instrument,
  sourceKind,
  sourceId,
  bpm,
  missionId,
  missionExperienceId,
  exerciseId,
  practiceSessionId,
  songId,
  musicProjectId,
}: {
  instrument: InstrumentId;
  sourceKind: StudioSourceKind;
  sourceId?: string;
  bpm?: number;
  missionId?: string;
  missionExperienceId?: string;
  exerciseId?: string;
  practiceSessionId?: string;
  songId?: string;
  musicProjectId?: string;
}) {
  const navigate = useNavigate();
  const [busy, setBusy] = useState(false);
  return (
    <button
      disabled={busy}
      onClick={async () => {
        setBusy(true);
        try {
          const project = await createStudioProject({
            instrument,
            sourceKind,
            sourceId,
            bpm,
            missionId,
            missionExperienceId,
            exerciseId,
            practiceSessionId,
            songId,
            musicProjectId,
          });
          await navigate({
            to: "/studio/$studioProjectId",
            params: { studioProjectId: project.id },
          });
        } finally {
          setBusy(false);
        }
      }}
      className="inline-flex h-8 items-center gap-2 border border-signal px-3 text-xs text-signal hover:bg-signal/10 disabled:opacity-40"
    >
      <AudioLines className="size-4" />
      {busy ? "Preparando Studio..." : "Abrir no Studio"}
    </button>
  );
}
