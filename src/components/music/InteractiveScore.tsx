import { Pause, Play, Repeat2 } from "lucide-react";
import { useEffect, useMemo, useRef, useState } from "react";

function parseNotes(value?: string) {
  return (value ?? "C4,D4,E4,F4")
    .split(/[;,]/)
    .map((note) => note.trim())
    .filter((note) => /^[A-G](#|b)?\d$/.test(note));
}

function frequency(note: string) {
  const match = note.match(/^([A-G])(#|b)?(\d)$/)!;
  const positions: Record<string, number> = { C: 0, D: 2, E: 4, F: 5, G: 7, A: 9, B: 11 };
  const midi =
    (Number(match[3]) + 1) * 12 +
    positions[match[1]] +
    (match[2] === "#" ? 1 : match[2] === "b" ? -1 : 0);
  return 440 * 2 ** ((midi - 69) / 12);
}

export function InteractiveScore({ notes: source, bpm = 80 }: { notes?: string; bpm?: number }) {
  const notes = useMemo(() => parseNotes(source), [source]);
  const scoreRef = useRef<HTMLDivElement>(null);
  const [playing, setPlaying] = useState(false);
  const [cursor, setCursor] = useState(0);
  const cursorRef = useRef(0);
  const [speed, setSpeed] = useState(1);
  const [loop, setLoop] = useState(true);

  useEffect(() => {
    if (!scoreRef.current) return;
    let active = true;
    import("vexflow").then(({ Accidental, Formatter, Renderer, Stave, StaveNote, Voice }) => {
      if (!active || !scoreRef.current) return;
      scoreRef.current.innerHTML = "";
      const width = Math.max(520, scoreRef.current.clientWidth);
      const renderer = new Renderer(scoreRef.current, Renderer.Backends.SVG);
      renderer.resize(width, 120);
      const context = renderer.getContext();
      const stave = new Stave(10, 12, width - 20).addClef("treble");
      stave.setContext(context).draw();
      const rendered = notes.map((note) => {
        const match = note.match(/^([A-G])(#|b)?(\d)$/)!;
        const staveNote = new StaveNote({
          keys: [`${match[1].toLowerCase()}/${match[3]}`],
          duration: "q",
        });
        if (match[2]) staveNote.addModifier(new Accidental(match[2]), 0);
        return staveNote;
      });
      const voice = new Voice({ numBeats: rendered.length, beatValue: 4 }).addTickables(rendered);
      new Formatter().joinVoices([voice]).format([voice], width - 100);
      voice.draw(context, stave);
    });
    return () => {
      active = false;
    };
  }, [notes]);

  useEffect(() => {
    if (!playing) return;
    const play = () => {
      const context = new AudioContext();
      const oscillator = context.createOscillator();
      const gain = context.createGain();
      oscillator.frequency.value = frequency(notes[cursorRef.current]);
      gain.gain.setValueAtTime(0.12, context.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.0001, context.currentTime + 0.35);
      oscillator.connect(gain).connect(context.destination);
      oscillator.start();
      oscillator.stop(context.currentTime + 0.36);
      setCursor((value) => {
        const next = value < notes.length - 1 ? value + 1 : loop ? 0 : value;
        cursorRef.current = next;
        if (!loop && value >= notes.length - 1) setPlaying(false);
        return next;
      });
    };
    play();
    const timer = window.setInterval(play, 60000 / bpm / speed);
    return () => window.clearInterval(timer);
  }, [bpm, loop, notes, playing, speed]);

  if (!notes.length) return null;

  return (
    <div className="border border-border bg-white text-black">
      <div className="relative overflow-auto">
        <div ref={scoreRef} className="min-w-[520px]" />
        <div
          className="pointer-events-none absolute top-5 h-20 w-px bg-red-600"
          style={{ left: `${Math.max(7, 9 + (cursor / Math.max(1, notes.length - 1)) * 82)}%` }}
        />
      </div>
      <div className="flex items-center gap-2 border-t border-black/15 bg-neutral-100 px-2 py-1 text-black">
        <button
          onClick={() => setPlaying((value) => !value)}
          className="flex size-7 items-center justify-center border border-black/20"
        >
          {playing ? <Pause className="size-3" /> : <Play className="size-3" />}
        </button>
        <select
          value={speed}
          onChange={(event) => setSpeed(Number(event.target.value))}
          className="h-7 border border-black/20 bg-white px-1 text-xs"
        >
          <option value={0.5}>50%</option>
          <option value={0.75}>75%</option>
          <option value={1}>100%</option>
        </select>
        <button
          onClick={() => setLoop((value) => !value)}
          className={`flex h-7 items-center gap-1 border px-2 text-xs ${loop ? "border-red-500" : "border-black/20"}`}
        >
          <Repeat2 className="size-3" />
          Loop
        </button>
        <span className="ml-auto font-mono text-xs">{bpm} BPM</span>
      </div>
    </div>
  );
}
