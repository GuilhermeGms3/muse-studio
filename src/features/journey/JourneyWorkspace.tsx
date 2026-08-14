import { Link } from "@tanstack/react-router";
import { ArrowRight, Check, Circle, LockKeyhole, RotateCcw } from "lucide-react";
import { useJourney } from "@/shared/api/journey";
import { useInstruments } from "@/shared/api/home";
import type { JourneyCompetency } from "@/shared/api/contracts";
import { QueryState } from "@/shared/ui/query/QueryState";
import { cn } from "@/shared/utils/cn";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { journeyStatusCopy as stateCopy } from "@/features/product-model";

export function JourneyWorkspace() {
  const { instrument, setInstrument } = useWorkspace();
  const query = useJourney(instrument);
  const instruments = useInstruments();
  if (!query.data) return <QueryState error={query.error} />;
  const journey = query.data;
  const stages = journey.competencies.reduce<
    Array<{ stage: JourneyCompetency["stage"]; items: JourneyCompetency[] }>
  >((groups, competency) => {
    const current = groups.at(-1);
    if (current?.stage === competency.stage) current.items.push(competency);
    else groups.push({ stage: competency.stage, items: [competency] });
    return groups;
  }, []);

  return (
    <div className="mx-auto w-full max-w-6xl px-4 py-6 md:px-8 md:py-8">
      <header className="flex flex-wrap items-end justify-between gap-4 border-b border-border pb-5">
        <div>
          <span className="label-tech">Currículo ativo</span>
          <h1 className="mt-1 text-2xl font-medium tracking-tight md:text-3xl">Jornada</h1>
          <p className="mt-2 max-w-2xl text-sm text-text-muted">{journey.position.explanation}</p>
        </div>
        <label className="text-2xs text-text-muted">
          Instrumento
          <select
            value={instrument}
            onChange={(event) => setInstrument(event.target.value as typeof instrument)}
            className="ml-2 min-h-11 border border-border bg-surface-card px-3 text-xs text-text-primary"
          >
            {(instruments.data ?? []).map((item) => (
              <option key={item.id} value={item.id}>
                {item.name}
              </option>
            ))}
          </select>
        </label>
      </header>

      {journey.competencies.length === 0 ? (
        <div className="py-16 text-center">
          <h2 className="text-lg font-medium">
            Ainda não há um caminho curricular para este instrumento.
          </h2>
          <p className="mt-2 text-sm text-text-muted">
            Troque o instrumento ativo ou conclua o diagnóstico.
          </p>
          <Link
            to="/diagnostico"
            className="mt-5 inline-flex min-h-11 items-center border border-signal px-4 text-signal"
          >
            Fazer diagnóstico
          </Link>
        </div>
      ) : (
        <div className="mt-2">
          {stages.map((stage, index) => (
            <section
              key={stage.stage}
              aria-labelledby={`stage-${stage.stage}`}
              className="grid border-b border-border py-7 md:grid-cols-[120px_minmax(0,1fr)] md:py-10"
            >
              <div className="mb-5 md:mb-0">
                <span className="num text-4xl font-light text-text-muted md:text-5xl">
                  {String(index + 1).padStart(2, "0")}
                </span>
                <span className="label-tech mt-2 block">Capítulo</span>
              </div>
              <div>
                <h2 id={`stage-${stage.stage}`} className="text-lg font-medium md:text-xl">
                  {stageLabel(stage.stage)}
                </h2>
                <ol className="mt-4 divide-y divide-border border-y border-border">
                  {stage.items.map((competency, itemIndex) => (
                    <JourneyRow
                      key={competency.competencyId}
                      competency={competency}
                      next={journey.position.focusCompetencyId === competency.competencyId}
                      number={itemIndex + 1}
                    />
                  ))}
                </ol>
              </div>
            </section>
          ))}
        </div>
      )}
    </div>
  );
}

const stageLabel = (stage: JourneyCompetency["stage"]) =>
  ({
    first_steps: "Primeiros passos",
    beginner: "Fundamentos",
    beginner_advanced: "Fundamentos em contexto",
    early_intermediate: "Integração inicial",
    intermediate: "Desenvolvimento musical",
    upper_intermediate: "Integração avançada",
    advanced: "Autonomia e expressão",
  })[stage];

function JourneyRow({
  competency,
  next,
  number,
}: {
  competency: JourneyCompetency;
  next: boolean;
  number: number;
}) {
  const mission = competency.missions[0];
  const Icon =
    competency.status === "ESTABLISHED"
      ? Check
      : competency.status === "BLOCKED"
        ? LockKeyhole
        : competency.status === "REVIEW_DUE"
          ? RotateCcw
          : Circle;
  return (
    <li
      className={cn(
        "relative py-4 md:py-5",
        next && "pl-4 before:absolute before:inset-y-3 before:left-0 before:w-0.5 before:bg-signal",
      )}
    >
      <div className="grid grid-cols-[28px_minmax(0,1fr)] items-start gap-3 md:grid-cols-[38px_minmax(0,1fr)_auto]">
        <span
          className={cn(
            "num mt-0.5 flex items-center gap-1 text-2xs",
            competency.status === "ESTABLISHED"
              ? "border-ok text-ok"
              : competency.status === "BLOCKED"
                ? "border-border text-locked"
                : next
                  ? "border-signal text-signal"
                  : "border-border-strong text-text-muted",
          )}
        >
          <Icon className="size-3.5" aria-hidden="true" /> {String(number).padStart(2, "0")}
        </span>
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <h2 className="text-sm font-medium md:text-base">{competency.title}</h2>
            <span
              className={cn(
                "text-2xs",
                competency.status === "ESTABLISHED"
                  ? "text-ok"
                  : competency.status === "REVIEW_DUE"
                    ? "text-warn"
                    : next
                      ? "text-signal"
                      : "text-text-muted",
              )}
            >
              {next ? "Próxima missão" : stateCopy[competency.status]}
            </span>
          </div>
          <p className="mt-1 max-w-3xl text-xs leading-relaxed text-text-muted">
            {competency.reason}
          </p>
          {mission && competency.status !== "BLOCKED" && (
            <Link
              to="/missoes/$missionId"
              params={{ missionId: mission.id }}
              className="mt-3 inline-flex min-h-11 items-center gap-2 text-xs text-signal hover:underline md:hidden"
            >
              {mission.title}
              <ArrowRight className="size-3.5" />
            </Link>
          )}
        </div>
        {mission && competency.status !== "BLOCKED" ? (
          <Link
            to="/missoes/$missionId"
            params={{ missionId: mission.id }}
            className="hidden min-h-11 items-center gap-2 self-center border border-border px-4 text-xs hover:border-signal hover:text-signal md:inline-flex"
          >
            {mission.title}
            <ArrowRight className="size-3.5" />
          </Link>
        ) : null}
      </div>
    </li>
  );
}
