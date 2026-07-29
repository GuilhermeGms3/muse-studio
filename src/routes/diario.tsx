import { createFileRoute } from "@tanstack/react-router";
import { Panel, Row } from "@/components/workspace/Panel";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { journal } from "@/data/practice";
import { instruments } from "@/data/skills";
import { useState } from "react";
import { cn } from "@/lib/utils";

export const Route = createFileRoute("/diario")({
  head: () => ({
    meta: [
      { title: "Diário de Prática — Music OS" },
      { name: "description", content: "Histórico completo de sessões, dificuldades e evolução." },
      { property: "og:title", content: "Diário de Prática — Music OS" },
      { property: "og:description", content: "Registro cronológico de tudo que foi estudado." },
    ],
  }),
  component: JournalPage,
});

function JournalPage() {
  const [selected, setSelected] = useState(journal[0].id);
  const entry = journal.find((j) => j.id === selected)!;
  const total = journal.length;

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Estação", "Diário"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-auto bg-border lg:grid-cols-[280px_1fr]">
        <Panel title={`Sessões (${total})`} bodyClassName="p-0">
          {journal.map((j) => (
            <button
              key={j.id}
              onClick={() => setSelected(j.id)}
              className={cn(
                "flex w-full items-center gap-2 border-b border-border/60 px-2 py-1.5 text-left text-xs",
                selected === j.id ? "bg-surface" : "hover:bg-surface/50",
              )}
            >
              <span className="num w-10 text-muted-foreground">{j.date}</span>
              <span className="num text-signal">{j.duration}</span>
              <span className="label-tech ml-auto">
                {instruments.find((i) => i.id === j.instrument)?.short}
              </span>
            </button>
          ))}
        </Panel>
        <Panel title={`Registro · ${entry.date}`}>
          <Row label="Duração" value={entry.duration} />
          <Row label="Instrumento" value={instruments.find((i) => i.id === entry.instrument)?.name} mono={false} />
          <div className="mt-3">
            <span className="label-tech">Treinei</span>
            <ul className="mt-1 space-y-0.5">
              {entry.worked.map((w) => (
                <li key={w} className="border-l border-info pl-2 text-xs">{w}</li>
              ))}
            </ul>
          </div>
          <div className="mt-3">
            <span className="label-tech">Dificuldades</span>
            <p className="border-l border-warn pl-2 text-xs text-muted-foreground">{entry.difficulties}</p>
          </div>
          <div className="mt-3">
            <span className="label-tech">Melhorias</span>
            <p className="border-l border-ok pl-2 text-xs text-muted-foreground">{entry.improvements}</p>
          </div>
        </Panel>
      </div>
    </div>
  );
}
