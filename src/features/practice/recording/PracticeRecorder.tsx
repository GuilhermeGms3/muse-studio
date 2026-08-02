import { useRef, useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Clock3, Mic, Save, Square, Trash2 } from "lucide-react";
import { getRecordings, recordingAudioUrl, uploadRecording } from "@/lib/music-api";

function pitchFromBuffer(buffer: Float32Array, sampleRate: number) {
  let rms = 0;
  for (const value of buffer) rms += value * value;
  rms = Math.sqrt(rms / buffer.length);
  if (rms < 0.015) return null;
  let bestOffset = -1;
  let bestCorrelation = 0;
  for (let offset = 30; offset < Math.min(1000, buffer.length / 2); offset++) {
    let correlation = 0;
    for (let index = 0; index < buffer.length - offset; index++) {
      correlation += buffer[index] * buffer[index + offset];
    }
    if (correlation > bestCorrelation) {
      bestCorrelation = correlation;
      bestOffset = offset;
    }
  }
  return bestOffset > 0 ? sampleRate / bestOffset : null;
}

function pitchInfo(hz: number) {
  const midi = Math.round(69 + 12 * Math.log2(hz / 440));
  const names = ["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"];
  const ideal = 440 * 2 ** ((midi - 69) / 12);
  const cents = Math.round(1200 * Math.log2(hz / ideal));
  return { label: `${names[(midi + 120) % 12]}${Math.floor(midi / 12) - 1}`, cents, midi };
}

function targetMidi(note?: string) {
  if (!note) return null;
  const match = note.match(/^([A-G])(#|b)?(\d)$/);
  if (!match) return null;
  const names: Record<string, number> = { C: 0, D: 2, E: 4, F: 5, G: 7, A: 9, B: 11 };
  return (
    (Number(match[3]) + 1) * 12 +
    names[match[1]] +
    (match[2] === "#" ? 1 : match[2] === "b" ? -1 : 0)
  );
}

export function PracticeRecorder({
  contextType = "practice",
  contextId = "general",
  targetBpm,
  targetNote,
}: {
  contextType?: string;
  contextId?: string;
  targetBpm?: number;
  targetNote?: string;
}) {
  const queryClient = useQueryClient();
  const history = useQuery({
    queryKey: ["recordings", contextType, contextId],
    queryFn: () => getRecordings(contextType, contextId),
  });
  const mediaRef = useRef<MediaRecorder | null>(null);
  const animationRef = useRef<number | null>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const contextRef = useRef<AudioContext | null>(null);
  const previousRmsRef = useRef(0);
  const onsetTimesRef = useRef<number[]>([]);
  const pitchOffsetsRef = useRef<number[]>([]);
  const metricsRef = useRef({ bpm: 0, stability: 0, timing: 0, pitch: 0, bend: 0 });
  const startedAtRef = useRef(0);
  const [recording, setRecording] = useState(false);
  const [audioUrl, setAudioUrl] = useState<string | null>(null);
  const [pitch, setPitch] = useState("—");
  const [rhythm, setRhythm] = useState("Aguardando pulsos");
  const [timing, setTiming] = useState("Sem referência rítmica");
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const stop = () => {
    mediaRef.current?.stop();
    streamRef.current?.getTracks().forEach((track) => track.stop());
    if (animationRef.current) cancelAnimationFrame(animationRef.current);
    contextRef.current?.close().catch(() => undefined);
    setRecording(false);
  };

  const persist = async (blob: Blob) => {
    setSaving(true);
    try {
      const metrics = metricsRef.current;
      await uploadRecording(blob, {
        contextType,
        contextId,
        durationMillis: Math.round(performance.now() - startedAtRef.current),
        targetBpm,
        measuredBpm: metrics.bpm || undefined,
        timingOffsetMillis: metrics.timing || undefined,
        rhythmStability: metrics.stability || undefined,
        targetNote,
        pitchOffsetCents: metrics.pitch || undefined,
        bendStability: metrics.bend || undefined,
      });
      setSaved(true);
      queryClient.invalidateQueries({ queryKey: ["recordings", contextType, contextId] });
    } catch (reason) {
      setError((reason as Error).message);
    } finally {
      setSaving(false);
    }
  };

  const start = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      streamRef.current = stream;
      const chunks: Blob[] = [];
      const recorder = new MediaRecorder(stream);
      mediaRef.current = recorder;
      recorder.ondataavailable = (event) => chunks.push(event.data);
      recorder.onstop = () => {
        if (audioUrl) URL.revokeObjectURL(audioUrl);
        const blob = new Blob(chunks, { type: recorder.mimeType });
        setAudioUrl(URL.createObjectURL(blob));
        persist(blob);
      };
      recorder.start();

      const context = new AudioContext();
      contextRef.current = context;
      const analyser = context.createAnalyser();
      analyser.fftSize = 2048;
      context.createMediaStreamSource(stream).connect(analyser);
      const buffer = new Float32Array(analyser.fftSize);
      const wantedMidi = targetMidi(targetNote);
      const analyse = () => {
        analyser.getFloatTimeDomainData(buffer);
        const hz = pitchFromBuffer(buffer, context.sampleRate);
        if (hz) {
          const info = pitchInfo(hz);
          setPitch(`${info.label} ${info.cents >= 0 ? "+" : ""}${info.cents}c`);
          if (wantedMidi !== null) {
            const offset = Math.round((69 + 12 * Math.log2(hz / 440) - wantedMidi) * 100);
            pitchOffsetsRef.current = [...pitchOffsetsRef.current.slice(-100), offset];
            const average = Math.round(
              pitchOffsetsRef.current.reduce((sum, value) => sum + value, 0) /
                pitchOffsetsRef.current.length,
            );
            const deviation = Math.sqrt(
              pitchOffsetsRef.current.reduce((sum, value) => sum + (value - average) ** 2, 0) /
                pitchOffsetsRef.current.length,
            );
            metricsRef.current.pitch = average;
            metricsRef.current.bend = Math.max(0, Math.round(100 - deviation));
          }
        }

        let rms = 0;
        for (const sample of buffer) rms += sample * sample;
        rms = Math.sqrt(rms / buffer.length);
        const now = performance.now();
        const previousOnset = onsetTimesRef.current.at(-1) ?? 0;
        if (rms > 0.045 && previousRmsRef.current <= 0.045 && now - previousOnset > 120) {
          onsetTimesRef.current = [...onsetTimesRef.current.slice(-12), now];
          const intervals = onsetTimesRef.current
            .slice(1)
            .map((time, index) => time - onsetTimesRef.current[index]);
          if (intervals.length >= 2) {
            const average =
              intervals.reduce((sum, interval) => sum + interval, 0) / intervals.length;
            const deviation = Math.sqrt(
              intervals.reduce((sum, interval) => sum + (interval - average) ** 2, 0) /
                intervals.length,
            );
            const measured = Math.round(60000 / average);
            const stability = Math.max(0, Math.round(100 - (deviation / average) * 100));
            metricsRef.current.bpm = measured;
            metricsRef.current.stability = stability;
            setRhythm(`${measured} BPM · ${stability}% estável`);
          }
          if (targetBpm) {
            const grid = 60000 / targetBpm;
            const elapsed = now - startedAtRef.current;
            const offset = Math.round(elapsed - Math.round(elapsed / grid) * grid);
            metricsRef.current.timing = offset;
            setTiming(`${Math.abs(offset)} ms ${offset > 0 ? "atrasado" : "adiantado"}`);
          }
        }
        previousRmsRef.current = rms;
        animationRef.current = requestAnimationFrame(analyse);
      };
      onsetTimesRef.current = [];
      pitchOffsetsRef.current = [];
      metricsRef.current = { bpm: 0, stability: 0, timing: 0, pitch: 0, bend: 0 };
      startedAtRef.current = performance.now();
      setSaved(false);
      setRhythm("Aguardando pulsos");
      setTiming(targetBpm ? `Grade em ${targetBpm} BPM` : "Sem referência rítmica");
      setError(null);
      setRecording(true);
      analyse();
    } catch {
      setError("Permita o acesso ao microfone para gravar e analisar sua execução.");
    }
  };

  return (
    <div className="border border-border bg-rail p-3">
      <div className="flex items-center justify-between gap-3">
        <div>
          <span className="label-tech">Gravação e análise</span>
          <p className="num mt-1 text-sm">{recording ? pitch : "Pronto para ouvir"}</p>
          <p className="num mt-0.5 text-2xs text-muted-foreground">
            {rhythm} · {timing}
          </p>
          {targetNote && (
            <p className="text-2xs text-muted-foreground">
              Alvo: {targetNote} · estabilidade do bend {metricsRef.current.bend || 0}%
            </p>
          )}
        </div>
        <button
          type="button"
          onClick={recording ? stop : start}
          className="inline-flex h-8 items-center gap-2 border border-border bg-surface px-3 text-xs hover:border-signal"
        >
          {recording ? (
            <Square className="size-3 text-signal" />
          ) : (
            <Mic className="size-3 text-signal" />
          )}
          {recording ? "Parar" : "Gravar"}
        </button>
      </div>
      {audioUrl && (
        <div className="mt-3 flex items-center gap-2">
          <audio controls src={audioUrl} className="h-8 min-w-0 flex-1" />
          <span title={saved ? "Salva na biblioteca local" : "Salvando"}>
            <Save className={`size-4 ${saved ? "text-ok" : "text-muted-foreground"}`} />
          </span>
          <button
            type="button"
            title="Descartar reprodução atual"
            onClick={() => setAudioUrl(null)}
          >
            <Trash2 className="size-4 text-muted-foreground" />
          </button>
        </div>
      )}
      {saving && <p className="mt-2 text-2xs text-muted-foreground">Salvando na pasta local...</p>}
      {error && <p className="mt-2 text-2xs text-warn">{error}</p>}
      {(history.data?.length ?? 0) > 0 && (
        <details className="mt-3 border-t border-border pt-2">
          <summary className="flex cursor-pointer items-center gap-2 text-2xs text-muted-foreground">
            <Clock3 className="size-3" />
            Gravações anteriores ({history.data?.length})
          </summary>
          <div className="mt-2 space-y-2">
            {history.data?.slice(0, 5).map((item) => (
              <div key={item.id} className="flex items-center gap-2">
                <audio controls src={recordingAudioUrl(item)} className="h-7 min-w-0 flex-1" />
                <span className="num w-28 text-right text-2xs text-muted-foreground">
                  {item.measuredBpm ? `${item.measuredBpm} BPM` : "livre"}
                  {item.rhythmStability ? ` · ${item.rhythmStability}%` : ""}
                </span>
              </div>
            ))}
          </div>
        </details>
      )}
    </div>
  );
}
