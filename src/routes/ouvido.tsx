import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Panel, Row, Meter } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/ouvido")({
  head: () => ({
    meta: [
      { title: "Treino de Ouvido — Music OS" },
      { name: "description", content: "Reconhecimento de intervalos, acordes, escalas, ritmos, progressões e melodias." },
      { property: "og:title", content: "Treino de Ouvido — Music OS" },
      { property: "og:description", content: "Rotina diária de ear training com registro de precisão." },
    ],
  }),
  component: EarPage,
});

const drills = [
  { id: "intervals", name: "Intervalos", items: ["2m", "2M", "3m", "3M", "4J", "5J", "6M", "7m", "8J"], accuracy: 73 },
  { id: "chords", name: "Acordes", items: ["maior", "menor", "dim", "aum", "7", "maj7", "m7", "m7b5"], accuracy: 49 },
  { id: "scales", name: "Escalas", items: ["maior", "menor nat.", "dórico", "frígio", "lídio", "mixolídio"], accuracy: 41 },
  { id: "rhythms", name: "Ritmos", items: ["semínimas", "colcheias", "tercinas", "semicolcheias", "síncopa"], accuracy: 76 },
  { id: "progressions", name: "Progressões", items: ["I–V–vi–IV", "ii–V–I", "I–IV–V", "vi–IV–I–V"], accuracy: 33 },
  { id: "melodies", name: "Melodias", items: ["3 notas", "4 notas", "5 notas", "frase completa"], accuracy: 38 },
];

function EarPage() {
  const [drill, setDrill] = useState(drills[0]);
  const [answer, setAnswer] = useState<string | null>(null);
  const [target, setTarget] = useState(drills[0].items[0]);
  const [score, setScore] = useState({ hit: 0, total: 0 });

  const play = () => {
    const next = drill.items[Math.floor(Math.random() * drill.items.length)];
    setTarget(next);
    setAnswer(null);
  };

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Prática", "Treino de Ouvido"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-auto bg-border lg:grid-cols-[220px_1fr_260px]">
        <Panel title="Módulos" bodyClassName="p-0">
          {drills.map((d) => (
            <button
              key={d.id}
              onClick={() => {
                setDrill(d);
                setTarget(d.items[0]);
                setAnswer(null);
              }}
              className={cn(
                "block w-full border-b border-border/60 px-2 py-1.5 text-left text-xs",
                drill.id === d.id ? "bg-surface" : "text-muted-foreground hover:text-foreground",
              )}
            >
              <div className="flex justify-between">
                <span>{d.name}</span>
                <span className="num text-2xs">{d.accuracy}%</span>
              </div>
              <Meter value={d.accuracy} tone={d.accuracy > 70 ? "ok" : "info"} />
            </button>
          ))}
        </Panel>

        <Panel title={`Exercício · ${drill.name}`}>
          <button
            onClick={play}
            className="mb-3 border border-border bg-surface px-3 py-1.5 text-xs hover:border-signal hover:text-signal"
          >
            Tocar estímulo
          </button>
          <div className="grid grid-cols-3 gap-1 md:grid-cols-4">
            {drill.items.map((i) => (
              <button
                key={i}
                onClick={() => {
                  setAnswer(i);
                  setScore((s) => ({ hit: s.hit + (i === target ? 1 : 0), total: s.total + 1 }));
                }}
                className={cn(
                  "border border-border bg-surface/50 py-2 text-xs hover:border-border-strong",
                  answer === i && (i === target ? "border-ok text-ok" : "border-destructive text-destructive"),
                )}
              >
                {i}
              </button>
            ))}
          </div>
          {answer && (
            <p className="mt-3 text-xs">
              {answer === target ? (
                <span className="text-ok">Correto.</span>
              ) : (
                <span className="text-warn">Era {target}.</span>
              )}
            </p>
          )}
        </Panel>

        <Panel title="Desempenho">
          <Row label="Acertos" value={`${score.hit}/${score.total}`} />
          <Row label="Precisão" value={`${score.total ? Math.round((score.hit / score.total) * 100) : 0}%`} />
          <p className="mt-3 text-2xs text-muted-foreground">
            Rotina sugerida: 4 min intervalos · 3 min acordes · 3 min ditado melódico. Cante antes de responder.
          </p>
        </Panel>
      </div>
    </div>
  );
}
