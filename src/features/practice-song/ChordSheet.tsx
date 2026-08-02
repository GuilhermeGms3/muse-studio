import { useEffect, useMemo, useRef, useState, type ChangeEvent } from "react";
import { ExternalLink, FileUp, Minus, Music2, Plus, Save, Settings2 } from "lucide-react";
import type { InstrumentId } from "@/shared/api/contracts";

interface ChordSheetState {
  content: string;
  transpose: number;
  capo: number;
  textScale: number;
  showDiagrams: boolean;
  showTabs: boolean;
}

const INITIAL_STATE: ChordSheetState = {
  content: "",
  transpose: 0,
  capo: 0,
  textScale: 100,
  showDiagrams: true,
  showTabs: true,
};

const NOTE_INDEX: Record<string, number> = {
  C: 0,
  "C#": 1,
  Db: 1,
  D: 2,
  "D#": 3,
  Eb: 3,
  E: 4,
  F: 5,
  "F#": 6,
  Gb: 6,
  G: 7,
  "G#": 8,
  Ab: 8,
  A: 9,
  "A#": 10,
  Bb: 10,
  B: 11,
};
const NOTES = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"];
const EXACT_CHORD =
  /^([A-G])([#b]?)(m|maj|min|sus|dim|aug)?(\d{0,2})?(add\d+)?(\([^)]*\))?(\/[A-G][#b]?)?$/;

const CHORD_SHAPES: Record<string, Array<number | "x">> = {
  C: ["x", 3, 2, 0, 1, 0],
  Cmaj7: ["x", 3, 2, 0, 0, 0],
  D: ["x", "x", 0, 2, 3, 2],
  Dm: ["x", "x", 0, 2, 3, 1],
  D7: ["x", "x", 0, 2, 1, 2],
  E: [0, 2, 2, 1, 0, 0],
  Em: [0, 2, 2, 0, 0, 0],
  E7: [0, 2, 0, 1, 0, 0],
  F: [1, 3, 3, 2, 1, 1],
  Fmaj7: ["x", "x", 3, 2, 1, 0],
  G: [3, 2, 0, 0, 0, 3],
  G7: [3, 2, 0, 0, 0, 1],
  A: ["x", 0, 2, 2, 2, 0],
  Am: ["x", 0, 2, 2, 1, 0],
  A7: ["x", 0, 2, 0, 2, 0],
  Am7: ["x", 0, 2, 0, 1, 0],
  B7: ["x", 2, 1, 2, 0, 2],
};

export function ChordSheet({
  storageKey,
  instrument,
  sourceUrl,
}: {
  storageKey: string;
  instrument: InstrumentId;
  sourceUrl: string;
}) {
  const key = `muse-studio:chord-sheet:${storageKey}`;
  const [state, setState] = useState<ChordSheetState>(INITIAL_STATE);
  const [draft, setDraft] = useState("");
  const [editing, setEditing] = useState(false);
  const [hydrated, setHydrated] = useState(false);
  const fileInput = useRef<HTMLInputElement>(null);
  const contentRoot = useRef<HTMLDivElement>(null);

  useEffect(() => {
    try {
      const saved = localStorage.getItem(key);
      if (saved) {
        const parsed = JSON.parse(saved) as Partial<ChordSheetState>;
        setState({ ...INITIAL_STATE, ...parsed });
        setDraft(parsed.content ?? "");
      } else {
        setState(INITIAL_STATE);
        setDraft("");
      }
    } catch {
      setState(INITIAL_STATE);
    }
    setEditing(false);
    setHydrated(true);
  }, [key]);

  useEffect(() => {
    if (!hydrated) return;
    localStorage.setItem(key, JSON.stringify(state));
  }, [hydrated, key, state]);

  useEffect(() => {
    if (editing) return;
    requestAnimationFrame(() => contentRoot.current?.parentElement?.scrollTo({ left: 0 }));
  }, [editing, state.content]);

  const rendered = useMemo(() => transposeSheet(state.content, state.transpose), [state]);
  const chords = useMemo(() => extractChords(rendered), [rendered]);
  const diagramChords = chords.filter((chord) => CHORD_SHAPES[chord]).slice(0, 12);

  const importFile = async (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;
    const content = await file.text();
    setDraft(content);
    setEditing(true);
    event.target.value = "";
  };

  const save = () => {
    setState((current) => ({ ...current, content: draft.trim() }));
    setEditing(false);
  };

  if (editing) {
    return (
      <div ref={contentRoot} className="p-3">
        <textarea
          value={draft}
          onChange={(event) => setDraft(event.target.value)}
          aria-label="Conteúdo da cifra"
          className="num min-h-[420px] w-full resize-y border border-border bg-background-rail p-3 text-xs leading-6 outline-none focus:border-signal focus:ring-2 focus:ring-ring"
        />
        <div className="mt-2 flex justify-end gap-2">
          <button
            type="button"
            onClick={() => {
              setDraft(state.content);
              setEditing(false);
            }}
            className="h-8 border border-border bg-surface-card px-3 text-xs hover:border-border-strong"
          >
            Cancelar
          </button>
          <button
            type="button"
            onClick={save}
            disabled={!draft.trim()}
            className="inline-flex h-8 items-center gap-2 border border-signal bg-signal px-3 text-xs text-signal-foreground disabled:opacity-40"
          >
            <Save className="size-3" aria-hidden="true" />
            Salvar cifra
          </button>
        </div>
      </div>
    );
  }

  if (!state.content) {
    return (
      <div
        ref={contentRoot}
        className="flex min-h-72 flex-col items-center justify-center gap-3 p-5 text-center"
      >
        <Music2 className="size-6 text-signal" aria-hidden="true" />
        <p className="text-sm font-medium">Cifra completa</p>
        <div className="flex flex-wrap justify-center gap-2">
          <button
            type="button"
            onClick={() => setEditing(true)}
            className="inline-flex h-8 items-center gap-2 border border-signal bg-signal px-3 text-xs text-signal-foreground"
          >
            <Plus className="size-3" aria-hidden="true" />
            Colar cifra
          </button>
          <button
            type="button"
            onClick={() => fileInput.current?.click()}
            className="inline-flex h-8 items-center gap-2 border border-border bg-surface-card px-3 text-xs hover:border-signal"
          >
            <FileUp className="size-3" aria-hidden="true" />
            Importar arquivo
          </button>
          <a
            href={sourceUrl}
            target="_blank"
            rel="noreferrer"
            className="inline-flex h-8 items-center gap-2 border border-border bg-surface-card px-3 text-xs hover:border-signal"
          >
            Buscar cifra <ExternalLink className="size-3" aria-hidden="true" />
          </a>
        </div>
        <input
          ref={fileInput}
          type="file"
          accept=".txt,.cho,.chopro,text/plain"
          onChange={importFile}
          className="hidden"
        />
      </div>
    );
  }

  return (
    <div ref={contentRoot} className="min-w-0 overflow-hidden">
      <div className="grid grid-cols-2 gap-2 border-b border-border bg-background-rail p-2 sm:flex sm:flex-wrap sm:items-center">
        <Control label="Tom">
          <StepButton label="Descer meio tom" onClick={() => setState(update("transpose", -1))}>
            <Minus className="size-3" />
          </StepButton>
          <span className="num min-w-8 text-center text-xs">{signed(state.transpose)}</span>
          <StepButton label="Subir meio tom" onClick={() => setState(update("transpose", 1))}>
            <Plus className="size-3" />
          </StepButton>
        </Control>
        <Control label="Capotraste">
          <StepButton
            label="Diminuir capotraste"
            onClick={() => setState(update("capo", -1, 0, 12))}
          >
            <Minus className="size-3" />
          </StepButton>
          <span className="num min-w-5 text-center text-xs">{state.capo}</span>
          <StepButton
            label="Aumentar capotraste"
            onClick={() => setState(update("capo", 1, 0, 12))}
          >
            <Plus className="size-3" />
          </StepButton>
        </Control>
        <label className="label-tech flex h-7 items-center justify-between gap-2 border border-border bg-surface-card px-2 sm:justify-start">
          Texto
          <select
            value={state.textScale}
            onChange={(event) =>
              setState((current) => ({ ...current, textScale: Number(event.target.value) }))
            }
            className="num bg-transparent text-xs text-text-primary outline-none"
          >
            <option value={80}>80%</option>
            <option value={100}>100%</option>
            <option value={120}>120%</option>
            <option value={140}>140%</option>
          </select>
        </label>
        <label className="flex h-7 items-center gap-2 border border-border bg-surface-card px-2 text-2xs">
          <input
            type="checkbox"
            checked={state.showTabs}
            onChange={(event) =>
              setState((current) => ({ ...current, showTabs: event.target.checked }))
            }
          />
          Tablaturas
        </label>
        {(instrument === "guitar" || instrument === "acoustic") && (
          <label className="flex h-7 items-center gap-2 border border-border bg-surface-card px-2 text-2xs">
            <input
              type="checkbox"
              checked={state.showDiagrams}
              onChange={(event) =>
                setState((current) => ({ ...current, showDiagrams: event.target.checked }))
              }
            />
            Diagramas
          </label>
        )}
        <button
          type="button"
          onClick={() => {
            setDraft(state.content);
            setEditing(true);
          }}
          title="Editar cifra"
          className="flex size-7 items-center justify-center justify-self-end border border-border bg-surface-card hover:border-signal sm:ml-auto"
        >
          <Settings2 className="size-3" aria-hidden="true" />
        </button>
      </div>

      {state.showDiagrams && diagramChords.length > 0 && (
        <div className="flex gap-3 overflow-x-auto border-b border-border p-3">
          {diagramChords.map((chord) => (
            <ChordDiagram key={chord} chord={chord} frets={CHORD_SHAPES[chord]} />
          ))}
        </div>
      )}

      <div className="overflow-auto p-4" style={{ fontSize: `${state.textScale}%` }}>
        <div className="num min-w-max text-xs leading-6">
          {rendered.split("\n").map((line, index) => {
            if (!state.showTabs && isTabLine(line)) return null;
            return (
              <div
                key={`${index}-${line}`}
                className={
                  isChordLine(line) || line.includes("[")
                    ? "whitespace-pre text-signal"
                    : "whitespace-pre text-text-primary"
                }
              >
                {line || " "}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

function Control({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="flex h-7 items-center justify-between gap-1 border border-border bg-surface-card px-1 sm:justify-start">
      <span className="label-tech px-1">{label}</span>
      {children}
    </div>
  );
}

function StepButton({
  label,
  onClick,
  children,
}: {
  label: string;
  onClick: () => void;
  children: React.ReactNode;
}) {
  return (
    <button
      type="button"
      aria-label={label}
      title={label}
      onClick={onClick}
      className="flex size-5 items-center justify-center hover:bg-surface-hover"
    >
      {children}
    </button>
  );
}

function ChordDiagram({ chord, frets }: { chord: string; frets: Array<number | "x"> }) {
  return (
    <div className="w-20 shrink-0">
      <div className="num mb-1 text-center text-2xs font-medium text-signal">{chord}</div>
      <div className="num grid grid-cols-6 text-center text-[9px] text-text-muted">
        {frets.map((fret, index) => (
          <span key={`${chord}-${index}`}>{fret === "x" ? "×" : fret === 0 ? "○" : " "}</span>
        ))}
      </div>
      <div className="relative mx-auto mt-1 h-16 w-16 border-y border-border-strong">
        {Array.from({ length: 6 }, (_, index) => (
          <span
            key={`string-${index}`}
            className="absolute inset-y-0 w-px bg-border-strong"
            style={{ left: `${index * 20}%` }}
          />
        ))}
        {Array.from({ length: 5 }, (_, index) => (
          <span
            key={`fret-${index}`}
            className="absolute inset-x-0 h-px bg-border"
            style={{ top: `${(index + 1) * 20}%` }}
          />
        ))}
        {frets.map((fret, index) =>
          typeof fret === "number" && fret > 0 ? (
            <span
              key={`dot-${index}`}
              className="absolute size-2 -translate-x-1/2 -translate-y-1/2 rounded-full bg-signal"
              style={{ left: `${index * 20}%`, top: `${(Math.min(fret, 4) - 0.5) * 20}%` }}
            />
          ) : null,
        )}
      </div>
    </div>
  );
}

function update(field: "transpose" | "capo", delta: number, min = -11, max = 11) {
  return (current: ChordSheetState): ChordSheetState => ({
    ...current,
    [field]: Math.min(max, Math.max(min, current[field] + delta)),
  });
}

function signed(value: number) {
  return value > 0 ? `+${value}` : String(value);
}

function isChord(value: string) {
  return EXACT_CHORD.test(value.replaceAll("[", "").replaceAll("]", "").replace(/[()]/g, ""));
}

function isChordLine(line: string) {
  const tokens = line.trim().split(/\s+/).filter(Boolean);
  if (!tokens.length) return false;
  const chords = tokens.filter(isChord).length;
  return chords > 0 && chords / tokens.length >= 0.5;
}

function isTabLine(line: string) {
  return /^\s*(e|B|G|D|A|E|HH|SD|BD|T\d)\|/.test(line);
}

function transposeSheet(content: string, semitones: number) {
  if (!semitones) return content;
  return content
    .split("\n")
    .map((line) => {
      if (!isChordLine(line) && !line.includes("[")) return line;
      return line.replace(
        /[A-G](?:#|b)?(?:m|maj|min|sus|dim|aug)?\d*(?:add\d+)?(?:\([^)]*\))?(?:\/[A-G](?:#|b)?)?/g,
        (chord) => transposeChord(chord, semitones),
      );
    })
    .join("\n");
}

function transposeChord(chord: string, semitones: number) {
  return chord.replace(/[A-G](?:#|b)?/g, (note) => {
    const index = NOTE_INDEX[note];
    return index === undefined ? note : NOTES[(index + semitones + 120) % 12];
  });
}

function extractChords(content: string) {
  const found = new Set<string>();
  for (const line of content.split("\n")) {
    if (!isChordLine(line) && !line.includes("[")) continue;
    for (const match of line.matchAll(/[A-G](?:#|b)?(?:m|maj|min|sus|dim|aug)?\d*/g)) {
      if (isChord(match[0])) found.add(match[0]);
    }
  }
  return [...found];
}
