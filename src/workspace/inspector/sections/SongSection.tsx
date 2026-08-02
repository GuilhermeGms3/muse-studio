import { Link } from "@tanstack/react-router";
import type { Song } from "@/shared/api/contracts";
import { Meter, Panel } from "@/shared/ui/workspace/Panel";

export function SongSection({ song }: { song?: Song }) {
  if (!song) return null;
  return (
    <Panel title="Música atual">
      <Link
        to="/repertorio/$songId"
        params={{ songId: song.id }}
        className="text-xs hover:text-signal focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
      >
        {song.title}
      </Link>
      <p className="text-2xs text-text-muted">{song.artist}</p>
      <div className="mt-2 space-y-1">
        {song.sections.slice(0, 4).map((section) => (
          <div key={section.id}>
            <div className="flex justify-between">
              <span className="text-2xs text-text-muted">{section.name}</span>
              <span className="num text-2xs">{section.progress}%</span>
            </div>
            <Meter value={section.progress} tone={section.progress > 85 ? "ok" : "info"} />
          </div>
        ))}
      </div>
    </Panel>
  );
}
