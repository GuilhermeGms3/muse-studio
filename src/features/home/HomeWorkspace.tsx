import { Link } from "@tanstack/react-router";
import {
  ArrowRight,
  BookOpen,
  Clock3,
  Focus,
  Gauge,
  ListChecks,
  Music2,
  Play,
  RefreshCcw,
  Route,
  SearchCheck,
  Target,
} from "lucide-react";
import type { ComponentType } from "react";
import type {
  CoachHome,
  CoachRecommendation,
  InstrumentId,
  LearningStage,
} from "@/shared/api/contracts";
import { Skeleton } from "@/components/ui/skeleton";
import { WorkspaceCard } from "@/shared/ui/workspace/Card";
import { StateTag } from "@/shared/ui/workspace/Panel";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { useHomeData, usePreferences } from "@/lib/music-api";

const instrumentLabels: Record<InstrumentId, string> = {
  guitar: "Guitarra",
  acoustic: "Violão",
  keys: "Teclado",
  drums: "Bateria",
};

const stageLabels: Record<LearningStage, string> = {
  first_steps: "Primeiros passos",
  beginner: "Iniciante",
  beginner_advanced: "Iniciante avançado",
  early_intermediate: "Intermediário inicial",
  intermediate: "Intermediário",
  upper_intermediate: "Intermediário avançado",
  advanced: "Avançado",
};

const intentions: Array<{
  label: string;
  description: string;
  to: "/biblioteca" | "/skills" | "/repertorio" | "/diario" | "/sessao";
  icon: ComponentType<{ className?: string }>;
}> = [
  {
    label: "Aprender algo novo",
    description: "Explorar uma ideia musical",
    to: "/biblioteca",
    icon: BookOpen,
  },
  {
    label: "Evoluir uma habilidade",
    description: "Trabalhar o caminho atual",
    to: "/skills",
    icon: Target,
  },
  {
    label: "Tocar músicas",
    description: "Continuar seu repertório",
    to: "/repertorio",
    icon: Music2,
  },
  {
    label: "Revisar",
    description: "Retomar o que pede atenção",
    to: "/diario",
    icon: RefreshCcw,
  },
  {
    label: "Treino livre",
    description: "Entrar no modo de prática",
    to: "/sessao",
    icon: Gauge,
  },
];

export function Home() {
  const { instrument, startSession } = useWorkspace();
  const homeQuery = useHomeData(instrument);
  const preferencesQuery = usePreferences();

  if (!preferencesQuery.data || !homeQuery.data) {
    if (preferencesQuery.error || homeQuery.error) {
      return <HomeError error={preferencesQuery.error ?? homeQuery.error} />;
    }
    return <HomeLoading />;
  }

  if (!preferencesQuery.data.onboardingCompleted) {
    return <DiagnosticStart />;
  }

  const home = homeQuery.data;
  const coach = home.coach;
  const firstBlock = home.todayPlan[0];

  return (
    <main className="h-full overflow-auto bg-background">
      <div className="mx-auto flex min-h-full w-full max-w-6xl flex-col gap-4 px-5 py-5">
        <header className="flex shrink-0 items-end justify-between gap-4">
          <div>
            <h1 className="text-lg font-semibold tracking-tight">{home.greeting}</h1>
            <p className="mt-0.5 text-xs text-muted-foreground">
              {coach
                ? `${instrumentLabels[coach.profile.instrument]} · ${stageLabels[coach.profile.stage]}`
                : instrumentLabels[instrument]}
            </p>
          </div>
          {coach?.availableMinutes ? (
            <span className="num flex items-center gap-1.5 text-xs text-muted-foreground">
              <Clock3 className="size-3.5" />
              {coach.availableMinutes} min disponíveis
            </span>
          ) : null}
        </header>

        <CoachCenter coach={coach} />

        <section aria-labelledby="intentions-heading">
          <div className="mb-2 flex items-center justify-between gap-3">
            <h2 id="intentions-heading" className="label-tech">
              O que você quer fazer hoje?
            </h2>
            <span className="text-2xs text-muted-foreground">Você mantém o controle da sessão</span>
          </div>
          <div className="grid gap-px bg-border sm:grid-cols-2 lg:grid-cols-5">
            {intentions.map((intention) => {
              const Icon = intention.icon;
              return (
                <Link
                  key={intention.label}
                  to={intention.to}
                  className="group flex min-h-20 items-start gap-3 bg-surface-card p-3 outline-none transition-colors hover:bg-surface-hover focus-visible:ring-1 focus-visible:ring-ring"
                >
                  <Icon className="mt-0.5 size-4 shrink-0 text-context-home group-hover:text-signal" />
                  <span className="min-w-0">
                    <span className="block text-xs font-medium">{intention.label}</span>
                    <span className="mt-1 block text-2xs leading-relaxed text-muted-foreground">
                      {intention.description}
                    </span>
                  </span>
                </Link>
              );
            })}
          </div>
        </section>

        <section className="grid min-h-0 flex-1 gap-px bg-border lg:grid-cols-[minmax(0,1.35fr)_minmax(280px,0.65fr)]">
          <WorkspaceCard className="border-0 p-4">
            <div className="flex items-center justify-between gap-3">
              <span className="label-tech">Plano de apoio</span>
              <span className="num text-xs text-muted-foreground">{home.expectedMinutes} min</span>
            </div>
            <div className="mt-3 divide-y divide-border/70 border-y border-border/70">
              {home.todayPlan.map((block) => (
                <div key={block.id} className="grid grid-cols-[64px_1fr] items-center gap-2 py-2">
                  <span className="num text-xs text-signal">{block.minutes} min</span>
                  <span className="min-w-0 truncate text-xs">{block.title}</span>
                </div>
              ))}
            </div>
            <Link
              to="/sessao"
              onClick={() =>
                startSession({
                  instrument,
                  goal: firstBlock?.title ?? "Prática guiada",
                  seconds: 0,
                  notes: "",
                })
              }
              className="mt-3 inline-flex h-9 items-center gap-2 border border-border bg-surface px-3 text-xs hover:border-signal hover:text-signal focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
            >
              <Play className="size-3.5 fill-current" />
              Iniciar plano existente
            </Link>
          </WorkspaceCard>

          <div className="grid gap-px bg-border">
            <WorkspaceCard className="border-0 p-4">
              <span className="label-tech">Continuar de onde parei</span>
              <p className="mt-3 text-sm">{home.continueFrom.title}</p>
              <p className="text-xs text-muted-foreground">{home.continueFrom.subtitle}</p>
              <Link
                to={
                  home.continueFrom.type === "song" ? "/repertorio/$songId" : "/biblioteca/$nodeId"
                }
                params={
                  home.continueFrom.type === "song"
                    ? { songId: home.continueFrom.id }
                    : { nodeId: home.continueFrom.id }
                }
                className="mt-3 inline-flex h-8 items-center gap-2 border border-border bg-surface px-3 text-xs hover:border-signal hover:text-signal focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
              >
                Continuar
                <ArrowRight className="size-3.5" />
              </Link>
            </WorkspaceCard>

            <WorkspaceCard className="border-0 p-4">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <span className="label-tech">Competência em desenvolvimento</span>
                  <p className="mt-3 text-sm">{home.currentObjective.title}</p>
                  <p className="mt-1 text-2xs text-muted-foreground">
                    {home.currentObjective.technicalName}
                  </p>
                </div>
                <StateTag state={home.currentObjective.state} />
              </div>
            </WorkspaceCard>
          </div>
        </section>
      </div>
    </main>
  );
}

function CoachCenter({ coach }: { coach?: CoachHome }) {
  const recommendation = coach?.recommendations[0];
  const grounded = coach?.status === "GROUNDED" && recommendation;

  return (
    <section
      aria-labelledby="coach-question"
      className="grid border border-border-context bg-surface-card lg:grid-cols-[minmax(0,1fr)_320px]"
    >
      <div className="p-5">
        <div className="flex items-center gap-2 text-context-home">
          <Focus className="size-4" />
          <span className="label-tech text-current">Coach</span>
        </div>
        <h2 id="coach-question" className="mt-3 text-2xl font-semibold tracking-tight">
          O que faço hoje?
        </h2>

        {grounded ? (
          <GroundedRecommendation recommendation={recommendation} />
        ) : (
          <UngroundedCoach coach={coach} />
        )}
      </div>

      <aside className="border-t border-border bg-panel p-4 lg:border-l lg:border-t-0">
        <span className="label-tech">Contexto da recomendação</span>
        <div className="mt-3 space-y-3">
          <ContextRow
            icon={Route}
            label="Estágio atual"
            value={coach ? stageLabels[coach.profile.stage] : "Indisponível"}
          />
          <ContextRow
            icon={Target}
            label="Objetivo ativo"
            value={coach?.activeGoals[0]?.title ?? "Nenhum objetivo declarado"}
          />
          <ContextRow
            icon={SearchCheck}
            label="Base da decisão"
            value={
              grounded
                ? `${recommendation.evidence.length} evidência(s) citada(s)`
                : "Coleta necessária"
            }
          />
        </div>
      </aside>
    </section>
  );
}

function GroundedRecommendation({ recommendation }: { recommendation: CoachRecommendation }) {
  const evidence = recommendation.evidence[0];
  return (
    <div className="mt-5">
      <div className="flex flex-wrap items-center gap-2">
        <span className="label-tech text-signal">Próximo passo recomendado</span>
        {recommendation.estimatedMinutes ? (
          <span className="num border border-border px-1.5 py-0.5 text-2xs text-muted-foreground">
            {recommendation.estimatedMinutes} min
          </span>
        ) : null}
      </div>
      <h3 className="mt-2 text-lg font-medium">{recommendation.title}</h3>
      <p className="mt-1 max-w-2xl text-xs leading-relaxed text-muted-foreground">
        {recommendation.observableObjective}
      </p>

      <div className="mt-4 border-l-2 border-context-home pl-3">
        <span className="label-tech">Por que agora?</span>
        <p className="mt-1 text-xs leading-relaxed">{evidence.observation}</p>
        <p className="mt-1 text-2xs text-muted-foreground">
          Evidência {evidence.id} · {evidence.conditions}
        </p>
      </div>

      <div className="mt-4 flex flex-wrap gap-2">
        <Link
          to="/skills"
          className="inline-flex h-10 items-center gap-2 border border-signal bg-signal px-4 text-xs font-semibold text-signal-foreground hover:brightness-110 focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
        >
          Abrir caminho recomendado
          <ArrowRight className="size-3.5" />
        </Link>
        <details className="group">
          <summary className="flex h-10 cursor-pointer list-none items-center border border-border bg-surface px-3 text-xs hover:border-border-strong">
            Ver justificativa completa
          </summary>
          <p className="mt-2 max-w-2xl border border-border bg-panel p-3 text-2xs leading-relaxed text-muted-foreground">
            {recommendation.explanation}
          </p>
        </details>
      </div>
    </div>
  );
}

function UngroundedCoach({ coach }: { coach?: CoachHome }) {
  const noMission = coach?.status === "NO_ELIGIBLE_MISSION";
  return (
    <div className="mt-5">
      <span className="label-tech text-warn">Orientação responsável</span>
      <h3 className="mt-2 text-lg font-medium">
        {noMission
          ? "Ainda não há uma missão pronta para este contexto."
          : "Vamos observar antes de decidir."}
      </h3>
      <p className="mt-1 max-w-2xl text-xs leading-relaxed text-muted-foreground">
        {coach?.message ??
          "O Coach ainda não recebeu o contexto pedagógico necessário para recomendar sem inventar uma justificativa."}
      </p>
      <div className="mt-4 flex flex-wrap gap-2">
        <Link
          to="/diagnostico"
          className="inline-flex h-10 items-center gap-2 border border-signal bg-signal px-4 text-xs font-semibold text-signal-foreground hover:brightness-110 focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
        >
          <SearchCheck className="size-4" />
          Fazer coleta diagnóstica
        </Link>
        <Link
          to="/plano"
          className="inline-flex h-10 items-center gap-2 border border-border bg-surface px-3 text-xs hover:border-border-strong focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
        >
          <ListChecks className="size-4" />
          Ver plano existente
        </Link>
      </div>
    </div>
  );
}

function ContextRow({
  icon: Icon,
  label,
  value,
}: {
  icon: ComponentType<{ className?: string }>;
  label: string;
  value: string;
}) {
  return (
    <div className="flex items-start gap-2.5 border-b border-border/70 pb-3 last:border-0 last:pb-0">
      <Icon className="mt-0.5 size-3.5 shrink-0 text-context-home" />
      <div className="min-w-0">
        <p className="text-2xs text-muted-foreground">{label}</p>
        <p className="mt-0.5 text-xs leading-relaxed">{value}</p>
      </div>
    </div>
  );
}

function DiagnosticStart() {
  return (
    <main className="flex h-full items-center justify-center bg-panel p-5">
      <section className="w-full max-w-xl border-y border-border py-8">
        <span className="label-tech">Primeiro acesso</span>
        <h1 className="mt-2 text-xl font-semibold">Vamos encontrar seu ponto de partida.</h1>
        <p className="mt-2 max-w-md text-xs leading-relaxed text-muted-foreground">
          Uma coleta curta dá ao Coach evidências para orientar a primeira experiência.
        </p>
        <Link
          to="/diagnostico"
          className="mt-5 inline-flex h-10 items-center gap-2 border border-signal bg-signal px-4 text-xs font-semibold text-signal-foreground"
        >
          Fazer diagnóstico
          <ArrowRight className="size-4" />
        </Link>
      </section>
    </main>
  );
}

function HomeLoading() {
  return (
    <main
      className="h-full overflow-hidden bg-background p-5"
      aria-label="Carregando centro de aprendizagem"
    >
      <div className="mx-auto max-w-6xl space-y-4">
        <div className="space-y-2">
          <Skeleton className="h-5 w-28" />
          <Skeleton className="h-3 w-44" />
        </div>
        <Skeleton className="h-64 w-full rounded-none" />
        <div className="grid grid-cols-2 gap-px md:grid-cols-5">
          {Array.from({ length: 5 }).map((_, index) => (
            <Skeleton key={index} className="h-20 rounded-none" />
          ))}
        </div>
        <div className="grid gap-px lg:grid-cols-[1.35fr_0.65fr]">
          <Skeleton className="h-56 rounded-none" />
          <Skeleton className="h-56 rounded-none" />
        </div>
      </div>
    </main>
  );
}

function HomeError({ error }: { error: Error | null }) {
  return (
    <main className="flex h-full items-center justify-center bg-panel p-5 text-center">
      <section className="max-w-md border border-border bg-surface p-5">
        <span className="label-tech text-destructive">Centro de aprendizagem indisponível</span>
        <p className="mt-2 text-sm">Não foi possível carregar sua orientação de hoje.</p>
        {error ? <p className="mt-1 text-2xs text-muted-foreground">{error.message}</p> : null}
      </section>
    </main>
  );
}
