import { useEffect, useRef } from "react";

export function useMetronomeEngine({
  bpm,
  playing,
  beats,
  subdivision,
  onTick,
}: {
  bpm: number;
  playing: boolean;
  beats: number;
  subdivision: number;
  onTick?: (beat: number) => void;
}) {
  const beatRef = useRef(0);
  const ctxRef = useRef<AudioContext | null>(null);

  useEffect(() => {
    if (!playing) {
      beatRef.current = 0;
      return;
    }
    const interval = 60000 / bpm / subdivision;
    const AudioCtor =
      typeof window !== "undefined"
        ? (window.AudioContext ??
          (window as unknown as { webkitAudioContext?: typeof AudioContext }).webkitAudioContext)
        : undefined;
    if (AudioCtor && !ctxRef.current) ctxRef.current = new AudioCtor();
    const ctx = ctxRef.current;

    const tick = () => {
      const step = beatRef.current;
      const isDownbeat = step % (beats * subdivision) === 0;
      const isBeat = step % subdivision === 0;
      if (ctx) {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.frequency.value = isDownbeat ? 1600 : isBeat ? 1100 : 800;
        gain.gain.value = isDownbeat ? 0.25 : isBeat ? 0.16 : 0.07;
        osc.connect(gain).connect(ctx.destination);
        const now = ctx.currentTime;
        osc.start(now);
        gain.gain.exponentialRampToValueAtTime(0.0001, now + 0.05);
        osc.stop(now + 0.06);
      }
      onTick?.(Math.floor(step / subdivision) % beats);
      beatRef.current = (step + 1) % (beats * subdivision);
    };

    tick();
    const id = setInterval(tick, interval);
    return () => clearInterval(id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [bpm, playing, beats, subdivision]);
}
