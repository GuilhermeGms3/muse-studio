import { Bookmark, Pause, Play, Repeat2 } from "lucide-react";
import { useEffect, useMemo, useRef, useState } from "react";

export function InteractiveTab({
  tablature,
  initialBpm = 80,
}: {
  tablature: string;
  initialBpm?: number;
}) {
  const lines = useMemo(() => tablature.split("\n"), [tablature]);
  const columns = Math.max(...lines.map((line) => line.length), 1);
  const [playing, setPlaying] = useState(false);
  const [cursor, setCursor] = useState(0);
  const [loopStart, setLoopStart] = useState(0);
  const [loopEnd, setLoopEnd] = useState(columns - 1);
  const [bpm, setBpm] = useState(initialBpm);
  const [speed, setSpeed] = useState(1);
  const [countIn, setCountIn] = useState(true);
  const [difficult, setDifficult] = useState<number[]>([]);
  const audioContext = useRef<AudioContext | null>(null);

  useEffect(() => {
    if (!playing) return;
    let count = countIn ? 4 : 0;
    const tick = () => {
      const context = audioContext.current ?? new AudioContext();
      audioContext.current = context;
      const oscillator = context.createOscillator();
      const gain = context.createGain();
      oscillator.frequency.value = count > 0 ? 1000 : 620;
      gain.gain.setValueAtTime(0.08, context.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.0001, context.currentTime + 0.06);
      oscillator.connect(gain).connect(context.destination);
      oscillator.start();
      oscillator.stop(context.currentTime + 0.07);
      if (count > 0) count -= 1;
      else setCursor((value) => (value >= loopEnd ? loopStart : value + 1));
    };
    tick();
    const timer = window.setInterval(tick, 60000 / bpm / 2 / speed);
    return () => window.clearInterval(timer);
  }, [bpm, countIn, loopEnd, loopStart, playing, speed]);

  return (
    <div className="border border-border bg-rail">
      <div className="flex flex-wrap items-center gap-2 border-b border-border px-2 py-1.5">
        <button
          onClick={() => setPlaying((value) => !value)}
          title={playing ? "Pausar" : "Reproduzir"}
          className="flex size-7 items-center justify-center border border-border bg-surface"
        >
          {playing ? (
            <Pause className="size-3 text-signal" />
          ) : (
            <Play className="size-3 text-signal" />
          )}
        </button>
        <label className="label-tech flex items-center gap-1">
          BPM
          <input
            type="number"
            min={30}
            max={300}
            value={bpm}
            onChange={(event) => setBpm(Number(event.target.value))}
            className="num h-7 w-12 border border-border bg-surface px-1 text-xs text-foreground"
          />
        </label>
        <select
          value={speed}
          onChange={(event) => setSpeed(Number(event.target.value))}
          className="num h-7 border border-border bg-surface px-1 text-2xs"
        >
          <option value={0.5}>50%</option>
          <option value={0.75}>75%</option>
          <option value={1}>100%</option>
        </select>
        <label className="flex items-center gap-1 text-2xs text-muted-foreground">
          <input
            type="checkbox"
            checked={countIn}
            onChange={(event) => setCountIn(event.target.checked)}
          />
          Contagem
        </label>
        <span className="ml-auto flex items-center gap-1 text-2xs text-muted-foreground">
          <Repeat2 className="size-3" />
          {loopStart + 1}–{loopEnd + 1}
        </span>
      </div>
      <div className="overflow-auto p-3">
        <div className="num min-w-max text-xs leading-6">
          {lines.map((line, lineIndex) => (
            <div key={`${line}-${lineIndex}`} className="whitespace-pre">
              {Array.from({ length: columns }, (_, column) => (
                <button
                  key={column}
                  onClick={() => setCursor(column)}
                  onDoubleClick={() =>
                    setDifficult((values) =>
                      values.includes(column)
                        ? values.filter((value) => value !== column)
                        : [...values, column],
                    )
                  }
                  className={`inline-flex h-6 w-[0.62rem] items-center justify-center ${
                    column === cursor
                      ? "bg-signal text-signal-foreground"
                      : difficult.includes(column)
                        ? "bg-warn/20 text-warn"
                        : ""
                  }`}
                >
                  {line[column] ?? " "}
                </button>
              ))}
            </div>
          ))}
        </div>
      </div>
      <div className="flex items-center gap-2 border-t border-border px-2 py-1.5">
        <label className="label-tech">
          Início{" "}
          <input
            type="number"
            min={1}
            max={columns}
            value={loopStart + 1}
            onChange={(event) => setLoopStart(Math.min(loopEnd, Number(event.target.value) - 1))}
            className="num ml-1 h-6 w-12 border border-border bg-surface px-1 text-2xs"
          />
        </label>
        <label className="label-tech">
          Fim{" "}
          <input
            type="number"
            min={1}
            max={columns}
            value={loopEnd + 1}
            onChange={(event) => setLoopEnd(Math.max(loopStart, Number(event.target.value) - 1))}
            className="num ml-1 h-6 w-12 border border-border bg-surface px-1 text-2xs"
          />
        </label>
        <span className="ml-auto flex items-center gap-1 text-2xs text-muted-foreground">
          <Bookmark className="size-3" />
          Duplo clique marca um trecho difícil
        </span>
      </div>
    </div>
  );
}
