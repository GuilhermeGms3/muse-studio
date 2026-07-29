const NOTE_NAMES = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"];

export function Fretboard({
  highlight = [],
  frets = 15,
  label,
}: {
  highlight?: number[];
  frets?: number;
  label?: string;
}) {
  const strings = ["e", "B", "G", "D", "A", "E"];
  const markers = [3, 5, 7, 9, 12, 15];
  return (
    <div className="overflow-x-auto">
      {label && <div className="label-tech mb-1">{label}</div>}
      <div className="min-w-[520px] border border-border bg-rail p-2">
        {strings.map((s, si) => (
          <div key={s} className="flex items-center">
            <span className="num w-4 text-2xs text-muted-foreground">{s}</span>
            {Array.from({ length: frets }).map((_, f) => {
              const on = si === 5 && highlight.includes(f);
              return (
                <div
                  key={f}
                  className="relative h-5 flex-1 border-r border-border/70"
                  style={{ minWidth: 30 }}
                >
                  <div className="absolute inset-x-0 top-1/2 h-px bg-border-strong/70" />
                  {on && (
                    <div className="absolute left-1/2 top-1/2 size-3 -translate-x-1/2 -translate-y-1/2 rounded-full bg-signal" />
                  )}
                </div>
              );
            })}
          </div>
        ))}
        <div className="flex">
          <span className="w-4" />
          {Array.from({ length: frets }).map((_, f) => (
            <span
              key={f}
              className="num flex-1 text-center text-2xs text-muted-foreground"
              style={{ minWidth: 30, opacity: markers.includes(f) ? 1 : 0.35 }}
            >
              {f}
            </span>
          ))}
        </div>
      </div>
    </div>
  );
}

export function KeyboardDiagram({
  highlight = [],
  octaves = 2,
  label,
}: {
  highlight?: number[];
  octaves?: number;
  label?: string;
}) {
  const whiteOffsets = [0, 2, 4, 5, 7, 9, 11];
  const blackOffsets = [1, 3, 6, 8, 10];
  const whites = Array.from({ length: octaves }).flatMap((_, o) =>
    whiteOffsets.map((n) => n + o * 12),
  );
  return (
    <div>
      {label && <div className="label-tech mb-1">{label}</div>}
      <div className="relative h-24 w-full max-w-[420px] border border-border bg-rail">
        <div className="flex h-full">
          {whites.map((n) => (
            <div
              key={n}
              className="relative flex flex-1 items-end justify-center border-r border-border last:border-r-0"
              style={{
                background: highlight.includes(n % 12) ? "var(--color-info)" : "var(--color-surface-2)",
              }}
            >
              <span className="num pb-1 text-2xs text-background/0 mix-blend-normal text-muted-foreground">
                {NOTE_NAMES[n % 12]}
              </span>
            </div>
          ))}
        </div>
        <div className="pointer-events-none absolute inset-0 flex">
          {whites.map((n, i) => {
            const next = n + 1;
            const hasBlack = blackOffsets.includes(next % 12);
            return (
              <div key={n} className="relative flex-1">
                {hasBlack && i < whites.length - 1 && (
                  <div
                    className="absolute -right-[14%] top-0 h-[60%] w-[28%] border border-border"
                    style={{
                      background: highlight.includes(next % 12)
                        ? "var(--color-signal)"
                        : "var(--color-rail)",
                    }}
                  />
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
