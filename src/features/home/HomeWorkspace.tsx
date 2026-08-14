import { Link } from "@tanstack/react-router";
import type { ComponentType } from "react";
import {
  ArrowRight,
  BookOpen,
  Clock3,
  Compass,
  Ear,
  Focus,
  Gauge,
  Music2,
  PenTool,
  Play,
  RotateCcw,
  SearchCheck,
} from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import type {
  CoachHome,
  CoachRecommendation,
  InstrumentId,
  LearningStage,
} from "@/shared/api/contracts";
import { useHomeData, usePreferences } from "@/shared/api/home";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { selectHomeFocus } from "@/features/product-model";

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

export function Home() {
  const { instrument } = useWorkspace();
  const homeQuery = useHomeData(instrument);
  const preferencesQuery = usePreferences();

  if (!preferencesQuery.data || !homeQuery.data) {
    if (preferencesQuery.error || homeQuery.error)
      return <HomeError error={preferencesQuery.error ?? homeQuery.error} />;
    return <HomeLoading />;
  }
  if (!preferencesQuery.data.onboardingCompleted) return <DiagnosticStart />;

  const home = homeQuery.data;
  const coach = home.coach;
  const experience = home.learningExperience;
  const activeExperience = experience && experience.status !== "COMPLETED" ? experience : undefined;
  const focus = selectHomeFocus(home);
  const recommendation = coach?.recommendations.find(
    (item) => item.kind !== "REVIEW" && item.kind !== "REVALIDATION",
  );
  const reviews =
    coach?.recommendations.filter(
      (item) => item.kind === "REVIEW" || item.kind === "REVALIDATION",
    ) ?? [];

  const profileInstrument = instrumentLabels[coach?.profile.instrument ?? instrument];
  const activeRecommendation = activeExperience
    ? coach?.recommendations.find((item) => item.missionId === activeExperience.missionId)
    : undefined;

  return (
    <div className="mx-auto w-full max-w-7xl px-4 py-5 md:px-7 md:py-7">
      <header className="flex flex-wrap items-end justify-between gap-4 border-b border-border pb-4">
        <div>
          <span className="label-tech">Estação de hoje</span>
          <h1 className="mt-1 text-xl font-medium tracking-tight md:text-2xl">{home.greeting}</h1>
        </div>
        <div className="flex w-full flex-wrap items-center gap-3 text-2xs text-text-muted md:w-auto md:justify-end">
          <span>{profileInstrument}</span>
          <span aria-hidden="true">/</span>
          <span>{coach ? stageLabels[coach.profile.stage] : "Ponto de partida"}</span>
          {coach?.availableMinutes ? (
            <span className="num inline-flex items-center gap-1">
              <Clock3 className="size-3" /> {coach.availableMinutes} min
            </span>
          ) : null}
        </div>
      </header>

      <div className="mt-4 grid gap-px bg-border xl:grid-cols-[minmax(0,1.65fr)_minmax(260px,.7fr)]">
        <div className="min-w-0 bg-background-workspace">
          {focus === "ACTIVE_EXPERIENCE" && activeExperience ? (
            <ActiveExperience
              missionId={activeExperience.missionId}
              paused={activeExperience.status === "PAUSED"}
              title={activeRecommendation?.title}
              objective={activeRecommendation?.observableObjective}
            />
          ) : focus === "COACH_RECOMMENDATION" && recommendation ? (
            <Recommendation recommendation={recommendation} />
          ) : (
            <EvidenceNeeded coach={coach} />
          )}

          {experience?.status === "COMPLETED" && (
            <section className="border-l-2 border-ok px-5 py-4 md:px-7">
              <span className="label-tech text-ok">Missão concluída</span>
              <p className="mt-2 text-sm">
                As evidências foram preservadas. O Coach usará esse registro para recalcular o
                próximo passo.
              </p>
              <Link
                to="/jornada"
                className="mt-3 inline-flex min-h-11 items-center gap-2 text-xs text-signal hover:underline"
              >
                Ver posição na Jornada <ArrowRight className="size-3.5" />
              </Link>
            </section>
          )}

          <section
            aria-labelledby="today-program"
            className="border-t border-border px-5 py-5 md:px-7"
          >
            <div className="flex items-center justify-between gap-3">
              <h2 id="today-program" className="label-tech">
                Programa de hoje
              </h2>
              <Link to="/plano" className="text-2xs text-text-muted hover:text-signal">
                Abrir plano
              </Link>
            </div>
            {home.todayPlan.length ? (
              <ol className="mt-3 divide-y divide-border border-y border-border">
                {home.todayPlan.map((activity, index) => (
                  <li
                    key={activity.id}
                    className="grid min-h-14 grid-cols-[28px_minmax(0,1fr)_auto] items-center gap-3 py-2.5"
                  >
                    <span className="num text-2xs text-text-muted">
                      {String(index + 1).padStart(2, "0")}
                    </span>
                    <span className="min-w-0">
                      <span className="block truncate text-sm">{activity.title}</span>
                      <span className="label-tech mt-0.5 block">{activity.kind}</span>
                    </span>
                    <span className="num text-2xs text-text-muted">{activity.minutes} min</span>
                  </li>
                ))}
              </ol>
            ) : (
              <p className="mt-3 text-xs text-text-muted">
                O Coach ainda não montou atividades para hoje.
              </p>
            )}
          </section>

          {reviews.length > 0 && (
            <section
              aria-labelledby="review-title"
              className="border-t border-border px-5 py-5 md:px-7"
            >
              <h2 id="review-title" className="label-tech">
                Para revisar
              </h2>
              <div className="mt-3 divide-y divide-border border-y border-border">
                {reviews.map((item) => (
                  <ReviewRow key={`${item.kind}-${item.competencyId}`} item={item} />
                ))}
              </div>
            </section>
          )}
        </div>

        <aside className="min-w-0 bg-background-rail px-5 py-5">
          <span className="label-tech">Estúdio aberto</span>
          <p className="mt-2 text-sm leading-relaxed">
            {coach?.activeGoals[0]?.title ?? home.message}
          </p>
          <nav
            aria-label="Acesso livre ao estúdio"
            className="mt-5 divide-y divide-border border-y border-border"
          >
            <StudioLink to="/sessao" label="Prática livre" detail="Sessão em foco" icon={Gauge} />
            <StudioLink
              to="/biblioteca"
              label="Conhecimento"
              detail="Teoria e técnica"
              icon={BookOpen}
            />
            <StudioLink to="/ouvido" label="Percepção" detail="Treino de ouvido" icon={Ear} />
            <StudioLink to="/projetos" label="Criação" detail="Riffs e projetos" icon={PenTool} />
            <StudioLink to="/musicas" label="Músicas" detail="Repertório" icon={Music2} />
          </nav>
          <Link
            to="/explorar"
            className="mt-5 inline-flex min-h-11 items-center gap-2 text-xs text-text-muted hover:text-signal"
          >
            <Compass className="size-4" /> Explorar todo o estúdio
          </Link>
        </aside>
      </div>
    </div>
  );
}

function ActiveExperience({
  missionId,
  paused,
  title,
  objective,
}: {
  missionId: string;
  paused: boolean;
  title?: string;
  objective?: string;
}) {
  return (
    <section className="relative min-h-72 min-w-0 overflow-hidden px-5 py-7 md:px-7 md:py-9">
      <MusicStaff />
      <div className="flex items-center gap-2 text-signal">
        <Play className="size-4" />
        <span className="label-tech text-current">Continuar experiência</span>
      </div>
      <h2 className="mt-5 text-xl font-semibold md:text-2xl">
        {title ?? "Experiência em andamento"}
      </h2>
      <p className="mt-2 max-w-2xl break-words text-sm leading-relaxed text-text-muted">
        {objective ??
          (paused
            ? "Seu ponto de retomada foi preservado."
            : "A experiência está em andamento no seu perfil instrumental.")}
      </p>
      <Link
        to="/missoes/$missionId"
        params={{ missionId }}
        className="mt-6 inline-flex min-h-11 items-center gap-2 bg-signal px-5 text-sm font-semibold text-signal-foreground hover:brightness-110"
      >
        Continuar missão <ArrowRight className="size-4" />
      </Link>
    </section>
  );
}

function Recommendation({ recommendation }: { recommendation: CoachRecommendation }) {
  return (
    <section className="relative min-h-72 min-w-0 overflow-hidden px-5 py-7 md:px-7 md:py-9">
      <MusicStaff />
      <div className="flex items-center gap-2 text-signal">
        <Focus className="size-4" />
        <span className="label-tech text-current">Recomendação do Coach</span>
      </div>
      <div className="mt-5 flex flex-wrap items-center gap-2">
        {recommendation.estimatedMinutes && (
          <span className="inline-flex items-center gap-1 text-xs text-text-muted">
            <Clock3 className="size-3.5" /> {recommendation.estimatedMinutes} min
          </span>
        )}
      </div>
      <h2 className="mt-2 text-xl font-semibold md:text-2xl">{recommendation.title}</h2>
      <p className="mt-2 max-w-2xl text-sm leading-relaxed text-text-muted">
        {recommendation.observableObjective}
      </p>
      <p className="mt-5 border-l-2 border-border-strong pl-3 text-xs leading-relaxed text-text-muted">
        {recommendation.explanation}
      </p>
      {recommendation.missionId ? (
        <Link
          to="/missoes/$missionId"
          params={{ missionId: recommendation.missionId }}
          className="mt-6 inline-flex min-h-11 items-center gap-2 bg-signal px-5 text-sm font-semibold text-signal-foreground hover:brightness-110"
        >
          Começar missão <ArrowRight className="size-4" />
        </Link>
      ) : (
        <Link
          to="/jornada"
          className="mt-6 inline-flex min-h-11 items-center gap-2 bg-signal px-5 text-sm font-semibold text-signal-foreground"
        >
          Ver próximo passo <ArrowRight className="size-4" />
        </Link>
      )}
    </section>
  );
}

function EvidenceNeeded({ coach }: { coach?: CoachHome }) {
  return (
    <section className="relative min-h-72 min-w-0 overflow-hidden px-5 py-7 md:px-7 md:py-9">
      <MusicStaff />
      <div className="flex items-center gap-2 text-warn">
        <SearchCheck className="size-4" />
        <span className="label-tech text-current">Precisamos observar primeiro</span>
      </div>
      <h2 className="mt-5 text-xl font-semibold md:text-2xl">
        Uma ação curta vai tornar a próxima orientação confiável.
      </h2>
      <p className="mt-2 max-w-2xl text-sm leading-relaxed text-text-muted">
        {coach?.message ??
          "Ainda não há contexto pedagógico suficiente para recomendar uma missão sem inventar uma justificativa."}
      </p>
      <Link
        to="/diagnostico"
        className="mt-6 inline-flex min-h-11 items-center gap-2 bg-signal px-5 text-sm font-semibold text-signal-foreground"
      >
        Fazer coleta diagnóstica <ArrowRight className="size-4" />
      </Link>
    </section>
  );
}

function ReviewRow({ item }: { item: CoachRecommendation }) {
  const content = (
    <>
      <RotateCcw className="size-4 text-warn" />
      <span className="min-w-0 flex-1">
        <span className="block text-sm">{item.title}</span>
        <span className="mt-0.5 block text-xs text-text-muted">{item.observableObjective}</span>
      </span>
      <ArrowRight className="size-4 text-text-muted" />
    </>
  );
  return item.missionId ? (
    <Link
      to="/missoes/$missionId"
      params={{ missionId: item.missionId }}
      className="flex min-h-14 items-center gap-3 py-3"
    >
      {content}
    </Link>
  ) : (
    <Link to="/jornada" className="flex min-h-14 items-center gap-3 py-3">
      {content}
    </Link>
  );
}

function MusicStaff() {
  return (
    <div
      className="pointer-events-none absolute inset-x-0 bottom-10 -z-0 opacity-35"
      aria-hidden="true"
    >
      {[0, 1, 2, 3, 4].map((line) => (
        <span key={line} className="block h-5 border-t border-border" />
      ))}
    </div>
  );
}

function StudioLink({
  to,
  label,
  detail,
  icon: Icon,
}: {
  to: "/sessao" | "/biblioteca" | "/ouvido" | "/projetos" | "/musicas";
  label: string;
  detail: string;
  icon: ComponentType<{ className?: string }>;
}) {
  return (
    <Link
      to={to}
      className="group flex min-h-14 items-center gap-3 py-2.5 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
    >
      <Icon className="size-4 text-text-muted group-hover:text-signal" />
      <span className="min-w-0 flex-1">
        <span className="block text-xs">{label}</span>
        <span className="block text-2xs text-text-muted">{detail}</span>
      </span>
      <ArrowRight className="size-3.5 text-text-muted" />
    </Link>
  );
}

function DiagnosticStart() {
  return (
    <div className="mx-auto flex min-h-full max-w-3xl items-center px-5 py-12">
      <section className="w-full border-y border-border py-10">
        <span className="label-tech">Primeiro acesso</span>
        <h1 className="mt-3 text-2xl font-semibold">Vamos encontrar seu ponto de partida.</h1>
        <p className="mt-2 text-sm text-text-muted">
          Uma coleta curta dá ao Coach evidências para orientar a primeira experiência.
        </p>
        <Link
          to="/diagnostico"
          className="mt-6 inline-flex min-h-11 items-center gap-2 bg-signal px-5 font-semibold text-signal-foreground"
        >
          Fazer diagnóstico <ArrowRight className="size-4" />
        </Link>
      </section>
    </div>
  );
}

function HomeLoading() {
  return (
    <div className="mx-auto max-w-6xl space-y-5 px-4 py-10 md:px-8">
      <Skeleton className="h-8 w-48 rounded-none" />
      <div className="grid gap-5 lg:grid-cols-[1fr_280px]">
        <Skeleton className="h-80 rounded-none" />
        <Skeleton className="h-64 rounded-none" />
      </div>
    </div>
  );
}

function HomeError({ error }: { error: Error | null }) {
  return (
    <div className="flex min-h-full items-center justify-center p-5 text-center">
      <section className="max-w-md border border-border bg-surface-card p-6">
        <span className="label-tech text-destructive">Hoje está indisponível</span>
        <p className="mt-3 text-sm">Não foi possível carregar sua orientação.</p>
        {error && <p className="mt-2 text-xs text-text-muted">{error.message}</p>}
      </section>
    </div>
  );
}
