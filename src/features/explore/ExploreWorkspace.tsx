import { Link } from "@tanstack/react-router";
import { useState } from "react";
import {
  ArrowRight,
  BookOpen,
  Boxes,
  Ear,
  Gauge,
  ListChecks,
  Search,
  Settings2,
  Sparkles,
} from "lucide-react";
import { useExercises } from "@/shared/api/exercises";
import { useLibrary } from "@/shared/api/library";
import { useSkills } from "@/shared/api/learning";
import { QueryState } from "@/shared/ui/query/QueryState";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

export function ExploreWorkspace() {
  const { instrument } = useWorkspace();
  const library = useLibrary();
  const exercises = useExercises(instrument);
  const skills = useSkills(instrument);
  const [search, setSearch] = useState("");
  if (!library.data || !exercises.data || !skills.data)
    return <QueryState error={library.error ?? exercises.error ?? skills.error} />;
  const groups = [
    {
      title: "Conteúdos",
      description: `${library.data.length} materiais de teoria e técnica`,
      to: "/biblioteca" as const,
      icon: BookOpen,
    },
    {
      title: "Exercícios livres",
      description: `${exercises.data.length} práticas disponíveis para o instrumento ativo`,
      to: "/exercicios" as const,
      icon: ListChecks,
    },
    {
      title: "Habilidades",
      description: `${skills.data.length} registros legados de habilidade`,
      to: "/skills" as const,
      icon: Sparkles,
    },
    {
      title: "Treino de ouvido",
      description: "Percepção e reconhecimento musical",
      to: "/ouvido" as const,
      icon: Ear,
    },
    {
      title: "Projetos",
      description: "Ideias, riffs e composições",
      to: "/projetos" as const,
      icon: Boxes,
    },
    {
      title: "Metrônomo",
      description: "Ferramenta de pulso para uso contextual",
      to: "/metronomo" as const,
      icon: Gauge,
    },
    {
      title: "Dados e integrações",
      description: "Importação, exportação e preferências técnicas",
      to: "/dados" as const,
      icon: Settings2,
    },
  ];
  const visibleGroups = groups.filter((group) =>
    `${group.title} ${group.description}`
      .toLocaleLowerCase("pt-BR")
      .includes(search.toLocaleLowerCase("pt-BR")),
  );
  return (
    <div className="mx-auto w-full max-w-6xl px-4 py-6 md:px-8 md:py-8">
      <header className="grid gap-5 border-b border-border pb-5 lg:grid-cols-[minmax(0,1fr)_360px] lg:items-end">
        <span className="label-tech">Escolha livre</span>
        <h1 className="mt-1 text-2xl font-medium tracking-tight md:text-3xl">Explorar</h1>
        <p className="mt-2 max-w-2xl text-sm text-text-muted">
          Biblioteca, prática livre, teoria e ferramentas continuam disponíveis sem disputar o foco
          da sua Jornada.
        </p>
        <label className="block">
          <span className="label-tech">Buscar no estúdio</span>
          <span className="mt-2 flex min-h-11 items-center gap-2 border-b border-border-strong focus-within:border-signal">
            <Search className="size-4 text-text-muted" />
            <input
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              className="min-w-0 flex-1 bg-transparent text-sm outline-none"
              placeholder="Teoria, prática, percepção…"
            />
          </span>
        </label>
      </header>
      <div className="mt-2 divide-y divide-border border-y border-border">
        {visibleGroups.map((group, index) => {
          const Icon = group.icon;
          return (
            <Link
              key={group.title}
              to={group.to}
              className="group grid min-h-20 grid-cols-[30px_minmax(0,1fr)_auto] items-center gap-4 py-4 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring md:grid-cols-[42px_180px_minmax(0,1fr)_auto]"
            >
              <span className="num hidden text-2xs text-text-muted md:block">
                {String(index + 1).padStart(2, "0")}
              </span>
              <span className="flex items-center gap-3 text-sm font-medium">
                <Icon className="size-4" />
                <span>{group.title}</span>
              </span>
              <span className="text-xs leading-relaxed text-text-muted">{group.description}</span>
              <ArrowRight className="size-4 text-text-muted group-hover:text-signal" />
            </Link>
          );
        })}
        {visibleGroups.length === 0 ? (
          <p className="py-10 text-center text-sm text-text-muted">
            Nenhuma área corresponde à busca.
          </p>
        ) : null}
      </div>
    </div>
  );
}
