import { Plus, X } from "lucide-react";
import { useState } from "react";
import { Panel } from "@/shared/ui/workspace/Panel";

export function NotesSection({
  notes,
  addNote,
  removeNote,
}: {
  notes: string[];
  addNote: (text: string) => void;
  removeNote: (text: string) => void;
}) {
  const [draft, setDraft] = useState("");
  return (
    <Panel title="Notas rápidas" className="flex-1">
      <form
        onSubmit={(event) => {
          event.preventDefault();
          if (!draft.trim()) return;
          addNote(draft.trim());
          setDraft("");
        }}
        className="mb-2 flex items-center gap-1"
      >
        <label htmlFor="quick-note" className="sr-only">
          Nova nota rápida
        </label>
        <input
          id="quick-note"
          value={draft}
          onChange={(event) => setDraft(event.target.value)}
          placeholder="Nova nota..."
          className="h-9 min-w-0 flex-1 border border-border bg-surface-card px-2 text-xs outline-none focus:border-ring"
        />
        <button
          type="submit"
          aria-label="Adicionar nota"
          title="Adicionar nota"
          className="flex size-9 shrink-0 items-center justify-center border border-border bg-surface-card hover:bg-surface-hover focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
        >
          <Plus className="size-3.5" aria-hidden="true" />
        </button>
      </form>
      <ul className="space-y-1">
        {notes.map((note) => (
          <li
            key={note}
            className="group flex min-h-8 items-start justify-between gap-2 border-l border-signal/60 pl-2 text-xs"
          >
            <span className="py-1 text-text-muted">{note}</span>
            <button
              type="button"
              onClick={() => removeNote(note)}
              aria-label={`Remover nota: ${note}`}
              title="Remover nota"
              className="flex size-7 shrink-0 items-center justify-center opacity-0 hover:bg-surface-hover focus-visible:opacity-100 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring group-hover:opacity-100"
            >
              <X className="size-3" aria-hidden="true" />
            </button>
          </li>
        ))}
      </ul>
    </Panel>
  );
}
