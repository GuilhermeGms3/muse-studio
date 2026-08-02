import { Link } from "@tanstack/react-router";
import { ArrowRight, Flame, Play } from "lucide-react";
import { Meter } from "@/shared/ui/workspace/Panel";
import { WorkspaceCard } from "@/shared/ui/workspace/Card";
import { QueryState } from "@/shared/ui/query/QueryState";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { useHomeData, usePreferences } from "@/lib/music-api";

export function Home() {
  const { instrument, startSession } = useWorkspace();
  const homeQuery = useHomeData(instrument);
  const preferencesQuery = usePreferences();
  if (preferencesQuery.data && !preferencesQuery.data.onboardingCompleted) {
    return (
      <main className="flex h-full items-center justify-center bg-panel p-5">
        <section className="w-full max-w-xl border-y border-border py-8">
          <span className="label-tech">Primeiro acesso</span>
          <h1 className="mt-2 text-xl font-semibold">Vamos encontrar seu ponto de partida.</h1>
          <p className="mt-2 max-w-md text-xs leading-relaxed text-muted-foreground">
            Um diagnostico curto monta sua trilha e evita conteudos faceis ou avancados demais.
          </p>
          <Link
            to="/diagnostico"
            className="mt-5 inline-flex h-10 items-center gap-2 border border-signal bg-signal px-4 text-xs font-semibold text-signal-foreground"
          >
            Fazer diagnostico
            <ArrowRight className="size-4" />
          </Link>
        </section>
      </main>
    );
  }
  if (!homeQuery.data) return <QueryState error={homeQuery.error} />;
  const home = homeQuery.data;
  const plan = home.todayPlan;
  const totalMinutes = home.expectedMinutes;
  const currentObjective = home.currentObjective;
  const objectiveState = currentObjective.state;
  const objectiveProgress = currentObjective.progress;
  const streakDays = home.streakDays;
  const firstBlock = plan[0];

  return (
    <main className="h-full overflow-auto bg-background">
      <div className="mx-auto flex min-h-full w-full max-w-5xl flex-col gap-4 px-5 py-5">
        <header className="shrink-0">
          <h1 className="text-lg font-semibold tracking-tight">{home.greeting}</h1>
          <p className="text-sm text-muted-foreground">{home.message}</p>
        </header>

        <section className="grid gap-px bg-border lg:grid-cols-[1fr_330px]">
          <WorkspaceCard className="border-0 p-4">
            <div className="mb-3 flex items-center justify-between gap-3">
              <div>
                <span className="label-tech">Sessao de Hoje</span>
                <p className="mt-1 text-sm text-muted-foreground">Tempo previsto.</p>
              </div>
              <span className="num text-2xl text-foreground">{totalMinutes} min</span>
            </div>

            <Link
              to="/sessao"
              onClick={() =>
                startSession({
                  instrument,
                  goal: firstBlock?.title ?? "Pratica guiada",
                  seconds: 0,
                  notes: "",
                })
              }
              className="flex h-16 w-full items-center justify-center gap-3 border border-signal bg-signal px-4 text-base font-semibold text-signal-foreground hover:brightness-110"
            >
              <Play className="size-5 fill-current" />
              Comecar sessao
            </Link>
          </WorkspaceCard>

          <WorkspaceCard className="border-0 p-4">
            <span className="label-tech">Sequencia</span>
            <div className="mt-3 flex items-center gap-3">
              <div className="flex size-10 items-center justify-center border border-border bg-surface">
                <Flame className="size-5 text-signal" />
              </div>
              <div>
                <p className="num text-2xl leading-none">{streakDays}</p>
                <p className="text-xs text-muted-foreground">dias consecutivos estudando</p>
              </div>
            </div>
          </WorkspaceCard>
        </section>

        <section className="grid min-h-0 flex-1 gap-px bg-border lg:grid-cols-[1fr_330px]">
          <WorkspaceCard className="border-0 p-4">
            <span className="label-tech">Plano de Hoje</span>
            <div className="mt-3 divide-y divide-border/70 border-y border-border/70">
              {plan.map((block) => (
                <div key={block.id} className="grid grid-cols-[70px_1fr] items-center py-2">
                  <span className="num text-sm text-signal">{block.minutes} min</span>
                  <span className="text-sm">{block.title}</span>
                </div>
              ))}
            </div>
          </WorkspaceCard>

          <div className="grid gap-px bg-border">
            <WorkspaceCard className="border-0 p-4">
              <span className="label-tech">Continuar de onde parei</span>
              <div className="mt-3">
                <p className="text-sm">{home.continueFrom.title}</p>
                <p className="text-xs text-muted-foreground">{home.continueFrom.subtitle}</p>
              </div>
              <Link
                to={
                  home.continueFrom.type === "song" ? "/repertorio/$songId" : "/biblioteca/$nodeId"
                }
                params={
                  home.continueFrom.type === "song"
                    ? { songId: home.continueFrom.id }
                    : { nodeId: home.continueFrom.id }
                }
                className="mt-3 inline-flex h-8 items-center gap-2 border border-border bg-surface px-3 text-xs hover:border-signal hover:text-signal"
              >
                Continuar
                <ArrowRight className="size-3.5" />
              </Link>
            </WorkspaceCard>

            <WorkspaceCard className="border-0 p-4">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <span className="label-tech">Objetivo Atual</span>
                  <p className="mt-3 text-sm">{currentObjective.title}</p>
                </div>
                <span className="num text-lg">{objectiveProgress}%</span>
              </div>
              <div className="mt-3">
                <Meter
                  value={objectiveProgress}
                  tone={objectiveState === "mastered" ? "ok" : "info"}
                />
              </div>
            </WorkspaceCard>
          </div>
        </section>
      </div>
    </main>
  );
}
