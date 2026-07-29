import { AlertTriangle, Check, Clock } from "lucide-react";
import type { LibraryContent } from "@/lib/music-api";
import { Fretboard, KeyboardDiagram } from "./Diagrams";
import { AudioCuePlayer } from "./AudioCuePlayer";
import { PracticeRecorder } from "./PracticeRecorder";
import { InteractiveScore } from "./InteractiveScore";

export function LessonRenderer({ lesson }: { lesson: LibraryContent }) {
  const diagramNotes = (lesson.diagramData ?? "").split(",").map(Number).filter(Number.isFinite);
  return (
    <article className="mx-auto max-w-4xl p-5">
      <div className="flex items-start justify-between gap-4 border-b border-border pb-4">
        <div>
          <span className="label-tech">
            {lesson.category} · {lesson.level}
          </span>
          <h1 className="mt-1 text-xl font-semibold">{lesson.friendlyTitle}</h1>
          <p className="label-tech mt-1">{lesson.technicalName}</p>
          <p className="mt-2 max-w-2xl text-xs leading-relaxed text-muted-foreground">
            {lesson.summary}
          </p>
        </div>
        <span className="num flex items-center gap-1 text-2xs text-muted-foreground">
          <Clock className="size-3" />
          {lesson.estimatedMinutes} min
        </span>
      </div>

      <section className="py-4">
        <span className="label-tech">Ao terminar, você consegue</span>
        <div className="mt-2 grid gap-2 md:grid-cols-3">
          {lesson.objectives.map((objective) => (
            <p
              key={objective}
              className="flex gap-2 border-l border-ok pl-2 text-xs leading-relaxed"
            >
              <Check className="mt-0.5 size-3 shrink-0 text-ok" />
              {objective}
            </p>
          ))}
        </div>
      </section>

      {lesson.diagramType && (
        <section className="border-y border-border py-4">
          {lesson.diagramType === "fretboard" && (
            <Fretboard highlight={diagramNotes} label="Mapa no braço" />
          )}
          {lesson.diagramType === "keyboard" && (
            <KeyboardDiagram highlight={diagramNotes} label="Notas no teclado" />
          )}
          {lesson.diagramType === "rhythm" && (
            <div>
              <span className="label-tech">Leitura rítmica</span>
              <pre className="num mt-2 overflow-auto border border-border bg-rail p-3 text-sm">
                {lesson.diagramData}
              </pre>
            </div>
          )}
        </section>
      )}

      <div className="divide-y divide-border">
        {lesson.steps.map((step, index) => (
          <section key={`${step.title}-${index}`} className="py-5">
            <span className="label-tech">
              Etapa {index + 1} de {lesson.steps.length}
            </span>
            <h2 className="mt-1 text-base font-semibold">{step.title}</h2>
            <p className="mt-2 text-[13px] leading-relaxed text-muted-foreground">
              {step.explanation}
            </p>
            {step.musicalExample && (
              <p className="num mt-3 border-l border-signal pl-3 text-xs">{step.musicalExample}</p>
            )}
            {step.notation && (
              <div className="mt-3">
                <InteractiveScore notes={step.audioNotes} />
                <pre className="num overflow-auto border-x border-b border-border bg-rail p-2 text-2xs">
                  {step.notation}
                </pre>
              </div>
            )}
            {step.tablature && (
              <pre className="num mt-3 overflow-auto bg-rail p-3 text-xs">{step.tablature}</pre>
            )}
            <div className="mt-3 flex gap-2">
              <AudioCuePlayer notes={step.audioNotes} />
            </div>
          </section>
        ))}
      </div>

      <details className="border border-border bg-rail">
        <summary className="flex cursor-pointer items-center gap-2 px-3 py-2 text-xs">
          <AlertTriangle className="size-3 text-warn" />
          Erros comuns
        </summary>
        <ul className="space-y-1 border-t border-border px-5 py-3 text-xs text-muted-foreground">
          {lesson.commonMistakes.map((mistake) => (
            <li key={mistake}>· {mistake}</li>
          ))}
        </ul>
      </details>

      <div className="mt-4">
        <PracticeRecorder
          contextType="lesson"
          contextId={lesson.id}
          targetNote={lesson.technicalName.toLowerCase().includes("bend") ? "D5" : undefined}
        />
      </div>
    </article>
  );
}
