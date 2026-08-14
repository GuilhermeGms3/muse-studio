import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Pencil, Plus, Save, Trash2, X } from "lucide-react";
import {
  deleteResource,
  saveExercise,
  saveLesson,
  saveProject,
  saveSong,
  type Exercise,
  type InstrumentId,
  type LibraryContent,
  type MusicProject,
  type Song,
} from "@/lib/music-api";

type Kind = "lesson" | "exercise" | "song" | "project";
type Entity = LibraryContent | Exercise | Song | MusicProject;

function slug(value: string) {
  return value
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-|-$/g, "");
}

function empty(kind: Kind, instrument: InstrumentId): Entity {
  if (kind === "lesson")
    return {
      id: "",
      friendlyTitle: "",
      technicalName: "",
      category: "Fundamentos",
      summary: "",
      body: [],
      examples: [],
      related: [],
      level: "beginner",
      estimatedMinutes: 12,
      objectives: [],
      commonMistakes: [],
      steps: [],
    };
  if (kind === "exercise")
    return {
      id: "",
      name: "",
      technique: "",
      instrument,
      targetBpm: 100,
      currentBpm: 60,
      minBpm: 40,
      bpmStep: 4,
      minutes: 8,
      description: "",
      difficulty: 1,
      passAccuracy: 85,
      passRepetitions: 3,
      instructions: [],
      activityType: "execute",
      stage: "beginner",
      competencyIds: [],
      variations: [],
    };
  if (kind === "song")
    return {
      id: "",
      title: "",
      artist: "",
      tuning: "Standard",
      musicalKey: "C",
      bpm: 100,
      instrument,
      difficulty: 2,
      status: "backlog",
      notes: "",
      progress: 0,
      techniques: [],
      scales: [],
      sections: [],
    };
  return {
    id: "",
    name: "",
    musicalKey: "C",
    bpm: 100,
    status: "sketch",
    lyrics: "",
    ideas: [],
    references: [],
    riffs: [],
    versions: [],
  };
}

export function CatalogEditor({
  kind,
  instrument,
  initial,
}: {
  kind: Kind;
  instrument: InstrumentId;
  initial?: Entity;
}) {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [entity, setEntity] = useState<Entity>(() =>
    initial ? structuredClone(initial) : empty(kind, instrument),
  );
  const [listText, setListText] = useState("");
  const update = (key: string, value: string | number) =>
    setEntity((current) => ({ ...current, [key]: value }) as Entity);
  const save = useMutation({
    mutationFn: async () => {
      const idSource =
        kind === "lesson"
          ? (entity as LibraryContent).technicalName
          : kind === "exercise"
            ? (entity as Exercise).name
            : kind === "song"
              ? (entity as Song).title
              : (entity as MusicProject).name;
      const prepared = { ...entity, id: entity.id || slug(idSource) } as Entity;
      if (kind === "lesson") {
        const lesson = prepared as LibraryContent;
        const lines = listText
          .split("\n")
          .map((value) => value.trim())
          .filter(Boolean);
        return saveLesson({
          ...lesson,
          body: lesson.body.length ? lesson.body : lines,
          objectives: lesson.objectives.length ? lesson.objectives : lines.slice(0, 3),
          commonMistakes: lesson.commonMistakes.length
            ? lesson.commonMistakes
            : ["Praticar rápido demais."],
          steps: lesson.steps.length
            ? lesson.steps
            : lines.map((line, index) => ({
                title: `${index + 1}. ${line.slice(0, 48)}`,
                explanation: line,
                audioNotes: "C4,E4,G4",
              })),
        });
      }
      if (kind === "exercise") {
        const exercise = prepared as Exercise;
        return saveExercise({
          ...exercise,
          instructions: exercise.instructions.length
            ? exercise.instructions
            : listText.split("\n").filter(Boolean),
        });
      }
      if (kind === "song") return saveSong(prepared as Song);
      return saveProject(prepared as MusicProject);
    },
    onSuccess: () => {
      queryClient.invalidateQueries();
      setOpen(false);
    },
  });
  const remove = useMutation({
    mutationFn: () =>
      deleteResource(
        kind === "lesson"
          ? "library"
          : kind === "exercise"
            ? "exercises"
            : kind === "song"
              ? "songs"
              : "projects",
        entity.id,
      ),
    onSuccess: () => {
      queryClient.invalidateQueries();
      setOpen(false);
    },
  });

  const name =
    kind === "lesson"
      ? "aula"
      : kind === "exercise"
        ? "exercício"
        : kind === "song"
          ? "música"
          : "projeto";
  const primary =
    kind === "lesson"
      ? (entity as LibraryContent).friendlyTitle
      : kind === "exercise"
        ? (entity as Exercise).name
        : kind === "song"
          ? (entity as Song).title
          : (entity as MusicProject).name;

  return (
    <>
      <button
        onClick={() => setOpen(true)}
        title={initial ? `Editar ${name}` : `Criar ${name}`}
        className="inline-flex h-7 items-center gap-1 border border-border bg-surface px-2 text-2xs hover:border-signal"
      >
        {initial ? <Pencil className="size-3" /> : <Plus className="size-3" />}
        {initial ? "Editar" : `Nova ${name}`}
      </button>
      {open && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4">
          <section className="max-h-[85vh] w-full max-w-xl overflow-auto border border-border bg-panel shadow-2xl">
            <header className="flex items-center justify-between border-b border-border bg-rail px-3 py-2">
              <span className="label-tech">
                {initial ? "Editar" : "Criar"} {name}
              </span>
              <button onClick={() => setOpen(false)}>
                <X className="size-4" />
              </button>
            </header>
            <div className="grid gap-3 p-4 md:grid-cols-2">
              <label className="text-2xs md:col-span-2">
                Título
                <input
                  value={primary}
                  onChange={(event) =>
                    update(
                      kind === "lesson"
                        ? "friendlyTitle"
                        : kind === "project"
                          ? "name"
                          : kind === "exercise"
                            ? "name"
                            : "title",
                      event.target.value,
                    )
                  }
                  className="mt-1 h-8 w-full border border-border bg-surface px-2 text-xs"
                />
              </label>
              {kind === "lesson" && (
                <>
                  <label className="text-2xs">
                    Nome técnico
                    <input
                      value={(entity as LibraryContent).technicalName}
                      onChange={(e) => update("technicalName", e.target.value)}
                      className="mt-1 h-8 w-full border border-border bg-surface px-2 text-xs"
                    />
                  </label>
                  <label className="text-2xs">
                    Categoria
                    <input
                      value={(entity as LibraryContent).category}
                      onChange={(e) => update("category", e.target.value)}
                      className="mt-1 h-8 w-full border border-border bg-surface px-2 text-xs"
                    />
                  </label>
                </>
              )}
              {kind === "exercise" && (
                <>
                  <label className="text-2xs">
                    Técnica
                    <input
                      value={(entity as Exercise).technique}
                      onChange={(e) => update("technique", e.target.value)}
                      className="mt-1 h-8 w-full border border-border bg-surface px-2 text-xs"
                    />
                  </label>
                  <label className="text-2xs">
                    BPM meta
                    <input
                      type="number"
                      value={(entity as Exercise).targetBpm}
                      onChange={(e) => update("targetBpm", Number(e.target.value))}
                      className="num mt-1 h-8 w-full border border-border bg-surface px-2 text-xs"
                    />
                  </label>
                </>
              )}
              {kind === "song" && (
                <>
                  <label className="text-2xs">
                    Artista
                    <input
                      value={(entity as Song).artist}
                      onChange={(e) => update("artist", e.target.value)}
                      className="mt-1 h-8 w-full border border-border bg-surface px-2 text-xs"
                    />
                  </label>
                  <label className="text-2xs">
                    BPM
                    <input
                      type="number"
                      value={(entity as Song).bpm}
                      onChange={(e) => update("bpm", Number(e.target.value))}
                      className="num mt-1 h-8 w-full border border-border bg-surface px-2 text-xs"
                    />
                  </label>
                </>
              )}
              {kind === "project" && (
                <>
                  <label className="text-2xs">
                    Tom
                    <input
                      value={(entity as MusicProject).musicalKey}
                      onChange={(e) => update("musicalKey", e.target.value)}
                      className="mt-1 h-8 w-full border border-border bg-surface px-2 text-xs"
                    />
                  </label>
                  <label className="text-2xs">
                    BPM
                    <input
                      type="number"
                      value={(entity as MusicProject).bpm}
                      onChange={(e) => update("bpm", Number(e.target.value))}
                      className="num mt-1 h-8 w-full border border-border bg-surface px-2 text-xs"
                    />
                  </label>
                </>
              )}
              <label className="text-2xs md:col-span-2">
                {kind === "lesson" ? "Resumo" : "Descrição / notas"}
                <textarea
                  value={
                    kind === "lesson"
                      ? (entity as LibraryContent).summary
                      : kind === "exercise"
                        ? (entity as Exercise).description
                        : kind === "song"
                          ? (entity as Song).notes
                          : (entity as MusicProject).lyrics
                  }
                  onChange={(e) =>
                    update(
                      kind === "lesson"
                        ? "summary"
                        : kind === "exercise"
                          ? "description"
                          : kind === "song"
                            ? "notes"
                            : "lyrics",
                      e.target.value,
                    )
                  }
                  className="mt-1 min-h-20 w-full border border-border bg-surface p-2 text-xs"
                />
              </label>
              {(kind === "lesson" || kind === "exercise") && (
                <label className="text-2xs md:col-span-2">
                  {kind === "lesson" ? "Etapas, uma por linha" : "Instruções, uma por linha"}
                  <textarea
                    value={listText}
                    onChange={(e) => setListText(e.target.value)}
                    className="mt-1 min-h-24 w-full border border-border bg-surface p-2 text-xs"
                  />
                </label>
              )}
            </div>
            <footer className="flex items-center justify-between border-t border-border bg-rail p-3">
              {initial ? (
                <button
                  onClick={() => remove.mutate()}
                  className="inline-flex items-center gap-2 text-xs text-destructive"
                >
                  <Trash2 className="size-3" />
                  Excluir
                </button>
              ) : (
                <span />
              )}
              <button
                onClick={() => save.mutate()}
                disabled={!primary || save.isPending}
                className="inline-flex h-8 items-center gap-2 bg-signal px-4 text-xs text-signal-foreground disabled:opacity-40"
              >
                <Save className="size-3" />
                Salvar
              </button>
            </footer>
          </section>
        </div>
      )}
    </>
  );
}
