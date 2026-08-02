import { useEffect, useMemo, useState } from "react";
import { Panel, Row } from "@/shared/ui/workspace/Panel";
import { QueryState } from "@/shared/ui/query/QueryState";
import { Breadcrumb } from "@/workspace/navigation/Breadcrumb";
import { useInstruments, useJournal } from "@/lib/music-api";
import { cn } from "@/shared/utils/cn";

export function JournalPage() {
  const journalQuery = useJournal();
  const instrumentsQuery = useInstruments();
  const [selected, setSelected] = useState<string | null>(null);
  const journal = useMemo(() => journalQuery.data ?? [], [journalQuery.data]);

  useEffect(() => {
    if (!selected && journal[0]) setSelected(journal[0].id);
  }, [journal, selected]);

  if (!journalQuery.data) return <QueryState error={journalQuery.error} />;
  const entry = journal.find((item) => item.id === selected) ?? journal[0];
  const instruments = instrumentsQuery.data ?? [];
  if (!entry) return <QueryState error={new Error("Nenhuma sessão registrada.")} />;

  const date = (value: string) => new Intl.DateTimeFormat("pt-BR").format(new Date(value));
  const duration = (seconds: number) =>
    `${Math.floor(seconds / 3600)}h${String(Math.floor((seconds % 3600) / 60)).padStart(2, "0")}`;

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Revisar", "Diário"]} />
      <div className="grid min-h-0 flex-1 grid-cols-1 gap-px overflow-auto bg-border lg:grid-cols-[280px_1fr]">
        <Panel title={`Sessões (${journal.length})`} bodyClassName="p-0">
          {journal.map((item) => (
            <button
              key={item.id}
              onClick={() => setSelected(item.id)}
              className={cn(
                "flex w-full items-center gap-2 border-b border-border/60 px-2 py-1.5 text-left text-xs",
                selected === item.id ? "bg-surface" : "hover:bg-surface/50",
              )}
            >
              <span className="num w-16 text-muted-foreground">{date(item.practicedAt)}</span>
              <span className="num text-signal">{duration(item.durationSeconds)}</span>
              <span className="label-tech ml-auto">
                {instruments.find((instrument) => instrument.id === item.instrument)?.shortName}
              </span>
            </button>
          ))}
        </Panel>
        <Panel title={`Registro · ${date(entry.practicedAt)}`}>
          <Row label="Duração" value={duration(entry.durationSeconds)} />
          <Row
            label="Instrumento"
            value={instruments.find((item) => item.id === entry.instrument)?.name}
            mono={false}
          />
          <div className="mt-3">
            <span className="label-tech">Treinei</span>
            {entry.worked.map((worked) => (
              <div key={worked} className="mt-0.5 border-l border-info pl-2 text-xs">
                {worked}
              </div>
            ))}
          </div>
          <div className="mt-3">
            <span className="label-tech">Dificuldades</span>
            <p className="border-l border-warn pl-2 text-xs text-muted-foreground">
              {entry.difficulties || "—"}
            </p>
          </div>
          <div className="mt-3">
            <span className="label-tech">Melhorias</span>
            <p className="border-l border-ok pl-2 text-xs text-muted-foreground">
              {entry.improvements || "—"}
            </p>
          </div>
        </Panel>
      </div>
    </div>
  );
}
