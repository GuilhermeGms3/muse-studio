import { useEffect, useState } from "react";
import { ExternalLink, Play, Replace } from "lucide-react";
import type { SongRecommendation } from "@/shared/api/contracts";
import { Panel } from "@/shared/ui/workspace/Panel";
import { cn } from "@/shared/utils/cn";

export function PracticeMediaPanel({
  title,
  items,
}: {
  title: string;
  items: SongRecommendation[];
}) {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const selected = items[selectedIndex] ?? items[0];
  const origin =
    typeof window === "undefined" ? "" : `&origin=${encodeURIComponent(window.location.origin)}`;

  useEffect(() => setSelectedIndex(0), [items]);

  return (
    <Panel title={title} bodyClassName="p-0">
      {selected?.videoId ? (
        <iframe
          key={selected.videoId}
          title={`${title}: ${selected.title}`}
          src={`https://www.youtube.com/embed/${selected.videoId}?playsinline=1&rel=0${origin}`}
          className="aspect-video w-full border-0"
          allow="accelerometer; autoplay; encrypted-media; picture-in-picture"
          referrerPolicy="strict-origin-when-cross-origin"
          allowFullScreen
        />
      ) : selected ? (
        <a
          href={selected.youtubeUrl}
          target="_blank"
          rel="noreferrer"
          className="flex aspect-video items-center justify-center gap-2 bg-background-rail text-xs hover:text-signal"
        >
          Abrir busca no YouTube <ExternalLink className="size-3" aria-hidden="true" />
        </a>
      ) : (
        <div className="flex aspect-video items-center justify-center bg-background-rail text-xs text-text-muted">
          Nenhum vídeo encontrado.
        </div>
      )}

      {items.length > 1 && (
        <div className="border-t border-border">
          <div className="flex h-8 items-center gap-1 px-2 text-2xs text-text-muted">
            <Replace className="size-3" aria-hidden="true" />
            Trocar
          </div>
          <div className="max-h-36 overflow-y-auto border-t border-border">
            {items.map((item, index) => (
              <button
                key={`${item.videoId}-${item.title}`}
                type="button"
                onClick={() => setSelectedIndex(index)}
                className={cn(
                  "flex w-full items-center gap-2 border-b border-border/60 px-2 py-1.5 text-left text-2xs last:border-0 hover:bg-surface-hover",
                  index === selectedIndex && "bg-surface-card text-text-primary",
                )}
              >
                <Play className="size-3 shrink-0 text-signal" aria-hidden="true" />
                <span className="min-w-0 flex-1 truncate">{item.title}</span>
                <span className="max-w-24 truncate text-text-muted">{item.channel}</span>
              </button>
            ))}
          </div>
        </div>
      )}
    </Panel>
  );
}
