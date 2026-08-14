import { Link } from "@tanstack/react-router";
import { FileCheck2, Mic2, Music, NotebookPen, PlayCircle } from "lucide-react";
import type { ComponentType } from "react";
import { useLearningHistory } from "@/shared/api/journey";
import type { LearningHistoryItem } from "@/shared/api/contracts";
import { QueryState } from "@/shared/ui/query/QueryState";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

const kindMeta: Record<
  LearningHistoryItem["kind"],
  { label: string; icon: ComponentType<{ className?: string }> }
> = {
  MISSION: { label: "Missão", icon: Music },
  ATTEMPT: { label: "Tentativa", icon: PlayCircle },
  RECORDING: { label: "Gravação", icon: Mic2 },
  EVIDENCE: { label: "Evidência", icon: FileCheck2 },
  SESSION: { label: "Sessão", icon: NotebookPen },
};

export function HistoryWorkspace() {
  const { instrument } = useWorkspace();
  const query = useLearningHistory(instrument);
  if (!query.data) return <QueryState error={query.error} />;

  return (
    <div className="mx-auto w-full max-w-5xl px-4 py-6 md:px-8 md:py-8">
      <header className="border-b border-border pb-5">
        <span className="label-tech">Registros reais</span>
        <h1 className="mt-1 text-2xl font-medium tracking-tight md:text-3xl">Histórico</h1>
        <p className="mt-2 max-w-2xl text-sm text-text-muted">
          Sessões, experiências, tentativas, gravações e evidências em uma única linha do tempo.
        </p>
      </header>
      {query.data.length === 0 ? (
        <section className="py-16 text-center">
          <h2 className="text-lg font-medium">Ainda não há registros para este instrumento.</h2>
          <p className="mt-2 text-sm text-text-muted">
            Quando você concluir uma prática ou salvar uma evidência, ela aparecerá aqui.
          </p>
          <Link
            to="/"
            className="mt-5 inline-flex min-h-11 items-center border border-signal px-4 text-signal"
          >
            Voltar para Hoje
          </Link>
        </section>
      ) : (
        <ol className="mt-2 divide-y divide-border border-y border-border">
          {query.data.map((item) => (
            <HistoryRow key={item.id} item={item} />
          ))}
        </ol>
      )}
    </div>
  );
}

function HistoryRow({ item }: { item: LearningHistoryItem }) {
  const meta = kindMeta[item.kind];
  const Icon = meta.icon;
  const content = (
    <>
      <span className="grid size-9 shrink-0 place-items-center text-text-muted">
        <Icon className="size-4" />
      </span>
      <span className="min-w-0 flex-1">
        <span className="label-tech">
          {meta.label} · {formatDate(item.occurredAt)}
        </span>
        <span className="mt-1 block text-sm font-medium">{item.title}</span>
        <span className="mt-1 block text-xs leading-relaxed text-text-muted">{item.detail}</span>
      </span>
      <span className="text-2xs text-text-muted">{humanStatus(item.status)}</span>
    </>
  );
  return item.missionId ? (
    <Link
      to="/missoes/$missionId"
      params={{ missionId: item.missionId }}
      className="flex min-h-20 items-center gap-3 border-l-2 border-transparent py-4 pl-3 hover:border-signal hover:bg-surface-card/30"
    >
      {content}
    </Link>
  ) : (
    <li className="flex min-h-20 items-center gap-3 border-l-2 border-transparent py-4 pl-3">
      {content}
    </li>
  );
}

const formatDate = (value: string) =>
  new Intl.DateTimeFormat("pt-BR", { dateStyle: "medium", timeStyle: "short" }).format(
    new Date(value),
  );
const humanStatus = (value: string) =>
  ({
    COMPLETED: "Concluída",
    IN_PROGRESS: "Em andamento",
    PAUSED: "Pausada",
    PASSED: "Critério atendido",
    VALID: "Válida",
    RECORDED: "Registrada",
    SAVED: "Salva",
  })[value] ?? value.toLowerCase().replaceAll("_", " ");
