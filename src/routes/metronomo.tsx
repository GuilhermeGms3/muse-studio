import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Panel, Row } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { useWorkspace } from "@/lib/workspace-store";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/metronomo")({
  head: () => ({
    meta: [
      { title: "Metrônomo — Music OS" },
      { name: "description", content: "BPM, compassos, subdivisões, presets e histórico de tempos de estudo." },
      { property: "og:title", content: "Metrônomo — Music OS" },
      { property: "og:description", content: "Metrônomo integrado à sessão de prática." },
    ],
  }),
  component: MetronomePage,
});

const presets = [
  { name: "Aquecimento", bpm: 60, beats: 4, subdivision: 1 },
  { name: "Alternate lento", bpm: 90, beats: 4, subdivision: 2 },
  { name: "Alternate limite", bpm: 118, beats: 4, subdivision: 4 },
  { name: "Legato", bpm: 104, beats: 4, subdivision: 3 },
  { name: "Levada 6/8", bpm: 76, beats: 6, subdivision: 2 },
  { name: "Galope", bpm: 152, beats: 4, subdivision: 4 },
];

function MetronomePage() {
  const { metronome, setMetronome } = useWorkspace();
  const [history, setHistory] = useState<number[]>([90, 104, 112, 118]);

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Prática", "Metrônomo"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-auto bg-border lg:grid-cols-[1fr_280px]">
        <Panel title="Transporte">
          <div className="num text-7xl leading-none tracking-tighter">{metronome.bpm}</div>
          <span className="label-tech">BPM</span>
          <input
            type="range"
            min={30}
            max={260}
            value={metronome.bpm}
            onChange={(e) => setMetronome({ bpm: Number(e.target.value) })}
            className="mt-3 w-full accent-[var(--color-signal)]"
          />
          <div className="mt-3 flex gap-px">
            {[-8, -4, -1, 1, 4, 8].map((d) => (
              <button
                key={d}
                onClick={() => setMetronome({ bpm: Math.max(30, Math.min(260, metronome.bpm + d)) })}
                className="num flex-1 border border-border bg-surface py-1 text-2xs hover:bg-surface-2"
              >
                {d > 0 ? `+${d}` : d}
              </button>
            ))}
          </div>
          <button
            onClick={() => {
              setMetronome({ playing: !metronome.playing });
              if (!metronome.playing) setHistory((h) => [metronome.bpm, ...h].slice(0, 10));
            }}
            className={cn(
              "mt-3 w-full border border-border py-2 text-xs",
              metronome.playing ? "bg-signal text-signal-foreground" : "bg-surface hover:bg-surface-2",
            )}
          >
            {metronome.playing ? "Parar" : "Tocar"}
          </button>

          <div className="mt-4 grid grid-cols-2 gap-3">
            <div>
              <span className="label-tech">Compasso</span>
              <div className="flex gap-px">
                {[2, 3, 4, 5, 6, 7].map((b) => (
                  <button
                    key={b}
                    onClick={() => setMetronome({ beats: b })}
                    className={cn(
                      "num flex-1 border border-border py-1 text-2xs",
                      metronome.beats === b ? "bg-surface-2" : "text-muted-foreground",
                    )}
                  >
                    {b}
                  </button>
                ))}
              </div>
            </div>
            <div>
              <span className="label-tech">Subdivisão</span>
              <div className="flex gap-px">
                {[1, 2, 3, 4].map((s) => (
                  <button
                    key={s}
                    onClick={() => setMetronome({ subdivision: s })}
                    className={cn(
                      "num flex-1 border border-border py-1 text-2xs",
                      metronome.subdivision === s ? "bg-surface-2" : "text-muted-foreground",
                    )}
                  >
                    {s}x
                  </button>
                ))}
              </div>
            </div>
          </div>
        </Panel>

        <div className="flex min-h-0 flex-col gap-px bg-border">
          <Panel title="Presets">
            {presets.map((p) => (
              <button
                key={p.name}
                onClick={() => setMetronome({ bpm: p.bpm, beats: p.beats, subdivision: p.subdivision })}
                className="flex w-full items-center justify-between border-b border-border/60 py-1 text-xs hover:text-signal"
              >
                <span>{p.name}</span>
                <span className="num text-2xs text-muted-foreground">
                  {p.bpm} · {p.beats}/{p.subdivision}x
                </span>
              </button>
            ))}
          </Panel>
          <Panel title="Histórico" className="flex-1">
            {history.map((h, i) => (
              <Row key={`${h}-${i}`} label={`Uso ${i + 1}`} value={`${h} BPM`} />
            ))}
          </Panel>
        </div>
      </div>
    </div>
  );
}
