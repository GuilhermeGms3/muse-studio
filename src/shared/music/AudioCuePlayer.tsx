import { useRef, useState } from "react";
import { Play, Square } from "lucide-react";

const noteOffsets: Record<string, number> = {
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

function frequency(note: string) {
  const match = note.trim().match(/^([A-G](?:#|b)?)(-?\d)$/);
  if (!match) return 440;
  const midi = (Number(match[2]) + 1) * 12 + noteOffsets[match[1]];
  return 440 * 2 ** ((midi - 69) / 12);
}

function schedulePercussion(context: AudioContext, token: string, start: number, level: number) {
  if (token === "R") return;
  if (token === "K") {
    const oscillator = context.createOscillator();
    const gain = context.createGain();
    oscillator.type = "sine";
    oscillator.frequency.setValueAtTime(120, start);
    oscillator.frequency.exponentialRampToValueAtTime(48, start + 0.16);
    gain.gain.setValueAtTime(0.16 / level, start);
    gain.gain.exponentialRampToValueAtTime(0.0001, start + 0.22);
    oscillator.connect(gain).connect(context.destination);
    oscillator.start(start);
    oscillator.stop(start + 0.24);
    return;
  }
  const duration = token === "C" ? 0.48 : token === "H" ? 0.08 : 0.18;
  const buffer = context.createBuffer(1, context.sampleRate * duration, context.sampleRate);
  const channel = buffer.getChannelData(0);
  for (let index = 0; index < channel.length; index += 1) {
    channel[index] = (Math.random() * 2 - 1) * (1 - index / channel.length);
  }
  const source = context.createBufferSource();
  const filter = context.createBiquadFilter();
  const gain = context.createGain();
  filter.type = token === "T" ? "bandpass" : "highpass";
  filter.frequency.value = token === "H" ? 6500 : token === "C" ? 4200 : token === "T" ? 240 : 1800;
  gain.gain.value = 0.1 / level;
  source.buffer = buffer;
  source.connect(filter).connect(gain).connect(context.destination);
  source.start(start);
}

export function AudioCuePlayer({
  notes,
  label = "Ouvir exemplo",
}: {
  notes?: string;
  label?: string;
}) {
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
        const token = note.trim();
        const start = context.currentTime + groupIndex * 0.36;
        if (["K", "S", "H", "T", "C", "R"].includes(token)) {
          schedulePercussion(context, token, start, Math.max(1, group.split(",").length));
          return;
        }
        const oscillator = context.createOscillator();
        const gain = context.createGain();
        oscillator.type = "triangle";
        oscillator.frequency.value = frequency(token);
        gain.gain.setValueAtTime(0.0001, start);
        gain.gain.exponentialRampToValueAtTime(
          0.12 / Math.max(1, group.split(",").length),
          start + 0.02,
        );
        gain.gain.exponentialRampToValueAtTime(0.0001, start + 0.62);
        oscillator.connect(gain).connect(context.destination);
        oscillator.start(start);
        oscillator.stop(start + 0.65);
      });
    });
    setPlaying(true);
    window.setTimeout(stop, groups.length * 360 + 700);
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
