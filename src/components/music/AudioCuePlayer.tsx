import { useRef, useState } from "react";
import { Play, Square } from "lucide-react";

const noteOffsets: Record<string, number> = {
  C: 0, "C#": 1, Db: 1, D: 2, "D#": 3, Eb: 3, E: 4, F: 5,
  "F#": 6, Gb: 6, G: 7, "G#": 8, Ab: 8, A: 9, "A#": 10, Bb: 10, B: 11,
};

function frequency(note: string) {
  const match = note.trim().match(/^([A-G](?:#|b)?)(-?\d)$/);
  if (!match) return 440;
  const midi = (Number(match[2]) + 1) * 12 + noteOffsets[match[1]];
  return 440 * 2 ** ((midi - 69) / 12);
}

export function AudioCuePlayer({ notes, label = "Ouvir exemplo" }: { notes?: string; label?: string }) {
  const contextRef = useRef<AudioContext | null>(null);
  const [playing, setPlaying] = useState(false);

  const stop = () => {
    contextRef.current?.close();
    contextRef.current = null;
    setPlaying(false);
  };

  const play = () => {
    stop();
    const context = new AudioContext();
    contextRef.current = context;
    const groups = (notes || "C4,E4,G4").split(";");
    groups.forEach((group, groupIndex) => {
      group.split(",").forEach((note) => {
        const oscillator = context.createOscillator();
        const gain = context.createGain();
        const start = context.currentTime + groupIndex * 0.72;
        oscillator.type = "triangle";
        oscillator.frequency.value = frequency(note);
        gain.gain.setValueAtTime(0.0001, start);
        gain.gain.exponentialRampToValueAtTime(0.12 / Math.max(1, group.split(",").length), start + 0.02);
        gain.gain.exponentialRampToValueAtTime(0.0001, start + 0.62);
        oscillator.connect(gain).connect(context.destination);
        oscillator.start(start);
        oscillator.stop(start + 0.65);
      });
    });
    setPlaying(true);
    window.setTimeout(stop, groups.length * 720 + 100);
  };

  return (
    <button
      type="button"
      onClick={playing ? stop : play}
      className="inline-flex h-8 items-center gap-2 border border-border bg-surface px-3 text-xs hover:border-signal"
    >
      {playing ? <Square className="size-3" /> : <Play className="size-3 text-signal" />}
      {playing ? "Parar" : label}
    </button>
  );
}
