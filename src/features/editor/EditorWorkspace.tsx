import type { ReactNode } from "react";
import { CatalogEditor } from "@/shared/catalog/CatalogEditor";
import { useExercises, useLibrary, useProjects, useSongs } from "@/lib/music-api";
import { QueryState } from "@/shared/ui/query/QueryState";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

export function EditorWorkspace() {
  const { instrument } = useWorkspace();
  const library = useLibrary();
  const exercises = useExercises(instrument);
  const songs = useSongs(instrument);
  const projects = useProjects();

  if (import.meta.env.VITE_EDITORIAL_TOOLS !== "true") {
    return (
      <main className="mx-auto max-w-2xl p-8">
        <span className="label-tech">Ferramenta editorial</span>
        <h1 className="mt-2 text-xl font-semibold">Editor desativado</h1>
        <p className="mt-2 text-sm text-muted-foreground">
          Ative VITE_EDITORIAL_TOOLS=true em desenvolvimento. Esta interface não faz parte da
          experiência do aluno.
        </p>
      </main>
    );
  }

  if (!library.data || !exercises.data || !songs.data || !projects.data) {
    return <QueryState error={library.error ?? exercises.error ?? songs.error ?? projects.error} />;
  }

  return (
    <main className="mx-auto max-w-6xl p-5">
      <header className="border-b border-border pb-4">
        <span className="label-tech">Desenvolvimento</span>
        <h1 className="mt-1 text-xl font-semibold">Editor do catálogo</h1>
        <p className="mt-2 text-xs text-muted-foreground">
          Operações editoriais separadas do fluxo de aprendizagem.
        </p>
      </header>
      <div className="mt-5 grid gap-4 lg:grid-cols-2">
        <EditorGroup title="Aulas" create={<CatalogEditor kind="lesson" instrument={instrument} />}>
          {library.data.map((item) => (
            <EditorRow key={item.id} label={item.friendlyTitle}>
              <CatalogEditor kind="lesson" instrument={instrument} initial={item} />
            </EditorRow>
          ))}
        </EditorGroup>
        <EditorGroup
          title="Exercícios"
          create={<CatalogEditor kind="exercise" instrument={instrument} />}
        >
          {exercises.data.map((item) => (
            <EditorRow key={item.id} label={item.name}>
              <CatalogEditor kind="exercise" instrument={instrument} initial={item} />
            </EditorRow>
          ))}
        </EditorGroup>
        <EditorGroup title="Músicas" create={<CatalogEditor kind="song" instrument={instrument} />}>
          {songs.data.map((item) => (
            <EditorRow key={item.id} label={`${item.title} — ${item.artist}`}>
              <CatalogEditor kind="song" instrument={instrument} initial={item} />
            </EditorRow>
          ))}
        </EditorGroup>
        <EditorGroup
          title="Projetos"
          create={<CatalogEditor kind="project" instrument={instrument} />}
        >
          {projects.data.map((item) => (
            <EditorRow key={item.id} label={item.name}>
              <CatalogEditor kind="project" instrument={instrument} initial={item} />
            </EditorRow>
          ))}
        </EditorGroup>
      </div>
    </main>
  );
}

function EditorGroup({
  title,
  create,
  children,
}: {
  title: string;
  create: ReactNode;
  children: ReactNode;
}) {
  return (
    <section className="border border-border bg-surface">
      <header className="flex min-h-10 items-center justify-between border-b border-border px-3">
        <h2 className="label-tech">{title}</h2>
        {create}
      </header>
      <div className="max-h-80 divide-y divide-border overflow-auto">{children}</div>
    </section>
  );
}

function EditorRow({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="flex min-h-10 items-center justify-between gap-3 px-3 py-1.5 text-xs">
      <span className="truncate">{label}</span>
      {children}
    </div>
  );
}
