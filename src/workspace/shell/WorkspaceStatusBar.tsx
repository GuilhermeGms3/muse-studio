import type { Instrument } from "@/shared/api/contracts";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { useBuildIdentity } from "@/shared/api/system";

export function WorkspaceStatusBar({ instruments }: { instruments: Instrument[] }) {
  const { instrument, metronome } = useWorkspace();
  const build = useBuildIdentity().data;
  return (
    <footer className="flex h-7 shrink-0 items-center gap-3 overflow-hidden border-t border-border bg-background-rail px-2 text-2xs text-text-muted">
      <span className="num hidden shrink-0 sm:inline">
        {metronome.bpm} BPM · {metronome.beats}/4
      </span>
      <span className="hidden truncate md:inline">
        {instruments.find((item) => item.id === instrument)?.name}
      </span>
      {build && (
        <span className="num hidden shrink-0 opacity-70 lg:inline" title={build.buildId}>
          v{build.version} · {build.gitSha === "unknown" ? build.buildId : build.gitSha.slice(0, 8)}
        </span>
      )}
      <span className="num ml-auto hidden shrink-0 opacity-70 xl:inline">
        Ctrl K busca · Ctrl B sidebar · Ctrl J dock · Ctrl \ inspetor · Ctrl M metrônomo
      </span>
    </footer>
  );
}
