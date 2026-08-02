import { useNavigate } from "@tanstack/react-router";
import { ArrowLeft, ArrowRight, Check, Headphones, Music2, Timer } from "lucide-react";
import { useRef, useState } from "react";
import { completeDiagnostic, type InstrumentId } from "@/lib/music-api";
import { cn } from "@/shared/utils/cn";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

const instruments: { id: InstrumentId; name: string; detail: string }[] = [
  { id: "guitar", name: "Guitarra", detail: "Palheta, tecnica e repertorio" },
  { id: "acoustic", name: "Violao", detail: "Acordes, levadas e fingerstyle" },
  { id: "keys", name: "Teclado", detail: "Leitura, harmonia e independencia" },
  { id: "drums", name: "Bateria", detail: "Grooves, coordenacao e viradas" },
];

function parseList(value: string) {
  return value
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

function playPair(direction: "up" | "down" | "same") {
  const context = new AudioContext();
  const start = context.currentTime + 0.05;
  const frequencies =
    direction === "up"
      ? [261.63, 329.63]
      : direction === "down"
        ? [329.63, 261.63]
        : [293.66, 293.66];
  frequencies.forEach((frequency, index) => {
    const oscillator = context.createOscillator();
    const gain = context.createGain();
    oscillator.frequency.value = frequency;
    oscillator.type = "triangle";
    gain.gain.setValueAtTime(0.0001, start + index * 0.55);
    gain.gain.exponentialRampToValueAtTime(0.16, start + index * 0.55 + 0.02);
    gain.gain.exponentialRampToValueAtTime(0.0001, start + index * 0.55 + 0.42);
    oscillator.connect(gain).connect(context.destination);
    oscillator.start(start + index * 0.55);
    oscillator.stop(start + index * 0.55 + 0.45);
  });
}

export function DiagnosticPage() {
  const navigate = useNavigate();
  const { instrument: currentInstrument, setInstrument } = useWorkspace();
  const [step, setStep] = useState(0);
  const [instrument, chooseInstrument] = useState<InstrumentId>(currentInstrument);
  const [level, setLevel] = useState("beginner");
  const [sessionMinutes, setSessionMinutes] = useState(45);
  const [genres, setGenres] = useState("Rock, Blues");
  const [artists, setArtists] = useState("");
  const [songs, setSongs] = useState("");
  const taps = useRef<number[]>([]);
  const [tapCount, setTapCount] = useState(0);
  const [earAnswers, setEarAnswers] = useState(0);
  const [earRound, setEarRound] = useState(0);
  const [earTarget, setEarTarget] = useState<"up" | "down" | "same">("up");
  const [technique, setTechnique] = useState(1);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const techniqueOptions =
    instrument === "drums"
      ? [
          "Ainda estou conhecendo as peças, postura e pegada.",
          "Toco ritmos simples, mas perco o pulso ou embolo mãos e pés.",
          "Seguro grooves básicos, mas quase não sei fazer viradas.",
          "Toco com metrônomo, faço viradas simples e volto no tempo.",
          "Tenho coordenação consistente e quero refinar dinâmica e linguagem.",
        ]
      : [
          "Ainda estou aprendendo postura, notas ou acordes básicos.",
          "Consigo tocar exercícios simples, mas perco o pulso.",
          "Toco músicas fáceis e controlo movimentos básicos.",
          "Consigo estudar com metrônomo e corrigir erros sozinho.",
          "Tenho técnica consistente e quero refinar musicalidade.",
        ];

  const rhythmScore = (() => {
    if (taps.current.length < 5) return 35;
    const intervals = taps.current.slice(1).map((time, index) => time - taps.current[index]);
    const average = intervals.reduce((sum, value) => sum + value, 0) / intervals.length;
    const deviation = Math.sqrt(
      intervals.reduce((sum, value) => sum + (value - average) ** 2, 0) / intervals.length,
    );
    return Math.max(20, Math.min(100, Math.round(100 - (deviation / average) * 180)));
  })();

  const tap = () => {
    const next = [...taps.current, performance.now()].slice(-12);
    taps.current = next;
    setTapCount(next.length);
  };

  const startEarRound = () => {
    const options = ["up", "down", "same"] as const;
    const target = options[Math.floor(Math.random() * options.length)];
    setEarTarget(target);
    playPair(target);
  };

  const answerEar = (answer: "up" | "down" | "same") => {
    if (answer === earTarget) setEarAnswers((value) => value + 1);
    const nextRound = earRound + 1;
    setEarRound(nextRound);
    if (nextRound < 5) setTimeout(startEarRound, 250);
  };

  const finish = async () => {
    setSaving(true);
    setError(null);
    try {
      await completeDiagnostic({
        instrument,
        level,
        sessionMinutes,
        favoriteGenres: parseList(genres),
        favoriteArtists: parseList(artists),
        favoriteSongs: parseList(songs),
        rhythmScore,
        earScore: Math.round((earAnswers / 5) * 100),
        techniqueScore: technique * 20,
      });
      setInstrument(instrument);
      navigate({ to: "/" });
    } catch (reason) {
      setError((reason as Error).message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <main className="flex h-full min-h-0 flex-col bg-panel">
      <header className="flex h-10 shrink-0 items-center justify-between border-b border-border bg-rail px-4">
        <span className="label-tech">Diagnostico inicial</span>
        <span className="num text-2xs text-muted-foreground">{step + 1}/4</span>
      </header>
      <div className="min-h-0 flex-1 overflow-auto">
        <section className="mx-auto max-w-2xl px-5 py-8">
          {step === 0 && (
            <>
              <h1 className="text-xl font-semibold">Vamos encontrar seu ponto de partida.</h1>
              <p className="mt-1 text-xs text-muted-foreground">
                Sem prova longa. Isso apenas evita aulas faceis ou avancadas demais.
              </p>
              <fieldset className="mt-6">
                <legend className="label-tech">Instrumento principal</legend>
                <div className="mt-2 grid gap-px bg-border md:grid-cols-4">
                  {instruments.map((item) => (
                    <button
                      key={item.id}
                      onClick={() => chooseInstrument(item.id)}
                      className={cn(
                        "min-h-20 bg-surface p-3 text-left",
                        instrument === item.id && "outline outline-1 outline-signal",
                      )}
                    >
                      <Music2 className="size-4 text-signal" />
                      <strong className="mt-2 block text-xs">{item.name}</strong>
                      <span className="text-2xs text-muted-foreground">{item.detail}</span>
                    </button>
                  ))}
                </div>
              </fieldset>
              <div className="mt-5 grid gap-3 md:grid-cols-2">
                <label className="text-xs">
                  Nivel
                  <select
                    value={level}
                    onChange={(event) => setLevel(event.target.value)}
                    className="mt-1 h-9 w-full border border-border bg-surface px-2"
                  >
                    <option value="beginner">Iniciante</option>
                    <option value="intermediate">Intermediario</option>
                    <option value="advanced">Avancado</option>
                  </select>
                </label>
                <label className="text-xs">
                  Tempo normal de pratica
                  <select
                    value={sessionMinutes}
                    onChange={(event) => setSessionMinutes(Number(event.target.value))}
                    className="mt-1 h-9 w-full border border-border bg-surface px-2"
                  >
                    {[20, 30, 45, 60, 90].map((value) => (
                      <option key={value} value={value}>
                        {value} minutos
                      </option>
                    ))}
                  </select>
                </label>
                <label className="text-xs">
                  Estilos favoritos
                  <input
                    value={genres}
                    onChange={(event) => setGenres(event.target.value)}
                    className="mt-1 h-9 w-full border border-border bg-surface px-2"
                  />
                </label>
                <label className="text-xs">
                  Artistas favoritos
                  <input
                    value={artists}
                    onChange={(event) => setArtists(event.target.value)}
                    placeholder="Separe por virgulas"
                    className="mt-1 h-9 w-full border border-border bg-surface px-2"
                  />
                </label>
                <label className="text-xs md:col-span-2">
                  Musicas que gostaria de tocar
                  <input
                    value={songs}
                    onChange={(event) => setSongs(event.target.value)}
                    placeholder="Separe por virgulas"
                    className="mt-1 h-9 w-full border border-border bg-surface px-2"
                  />
                </label>
              </div>
            </>
          )}

          {step === 1 && (
            <div className="text-center">
              <Timer className="mx-auto size-5 text-signal" />
              <h1 className="mt-3 text-xl font-semibold">Mantenha um pulso confortavel.</h1>
              <p className="mt-1 text-xs text-muted-foreground">
                Toque no botao de 8 a 12 vezes, tentando manter a mesma distancia.
              </p>
              <button
                onClick={tap}
                className="num mx-auto mt-8 flex size-32 items-center justify-center rounded-full border border-signal bg-surface text-2xl active:bg-signal active:text-signal-foreground"
              >
                {tapCount || "TAP"}
              </button>
              <p className="mt-4 text-2xs text-muted-foreground">
                {tapCount < 8 ? `Faltam ${8 - tapCount} toques` : "Amostra suficiente."}
              </p>
            </div>
          )}

          {step === 2 && (
            <div className="text-center">
              <Headphones className="mx-auto size-5 text-signal" />
              <h1 className="mt-3 text-xl font-semibold">A segunda nota sobe ou desce?</h1>
              <p className="mt-1 text-xs text-muted-foreground">
                Sao cinco comparacoes curtas. Use “igual” quando a altura nao mudar.
              </p>
              <button
                onClick={startEarRound}
                className="mx-auto mt-7 flex size-20 items-center justify-center rounded-full border border-border bg-surface hover:border-signal"
              >
                <Music2 className="size-6 text-signal" />
              </button>
              <div className="mx-auto mt-6 grid max-w-sm grid-cols-3 gap-1">
                {(
                  [
                    ["up", "Sobe"],
                    ["down", "Desce"],
                    ["same", "Igual"],
                  ] as const
                ).map(([value, label]) => (
                  <button
                    key={value}
                    disabled={earRound >= 5}
                    onClick={() => answerEar(value)}
                    className="h-10 border border-border bg-surface text-xs hover:border-signal disabled:opacity-40"
                  >
                    {label}
                  </button>
                ))}
              </div>
              <p className="num mt-4 text-2xs text-muted-foreground">{earRound}/5 respostas</p>
            </div>
          )}

          {step === 3 && (
            <>
              <Check className="size-5 text-signal" />
              <h1 className="mt-3 text-xl font-semibold">Como o instrumento se sente hoje?</h1>
              <p className="mt-1 text-xs text-muted-foreground">
                Escolha a descricao mais proxima. O plano vai se corrigir conforme voce pratica.
              </p>
              <div className="mt-6 divide-y divide-border border-y border-border">
                {techniqueOptions.map((label, index) => (
                  <label
                    key={label}
                    className="flex cursor-pointer items-center gap-3 bg-surface px-3 py-3 text-xs hover:bg-surface-2"
                  >
                    <input
                      type="radio"
                      name="technique"
                      checked={technique === index + 1}
                      onChange={() => setTechnique(index + 1)}
                    />
                    {label}
                  </label>
                ))}
              </div>
              {error && <p className="mt-3 text-xs text-destructive">{error}</p>}
            </>
          )}
        </section>
      </div>
      <footer className="flex h-14 shrink-0 items-center justify-between border-t border-border bg-rail px-4">
        <button
          disabled={step === 0}
          onClick={() => setStep((value) => value - 1)}
          className="inline-flex h-9 items-center gap-2 px-3 text-xs disabled:opacity-30"
        >
          <ArrowLeft className="size-4" />
          Anterior
        </button>
        {step < 3 ? (
          <button
            disabled={(step === 1 && tapCount < 8) || (step === 2 && earRound < 5)}
            onClick={() => {
              setStep((value) => value + 1);
              if (step === 1) setTimeout(startEarRound, 300);
            }}
            className="inline-flex h-9 items-center gap-2 border border-signal bg-signal px-4 text-xs font-semibold text-signal-foreground disabled:opacity-40"
          >
            Continuar
            <ArrowRight className="size-4" />
          </button>
        ) : (
          <button
            disabled={saving}
            onClick={finish}
            className="inline-flex h-9 items-center gap-2 border border-signal bg-signal px-4 text-xs font-semibold text-signal-foreground disabled:opacity-40"
          >
            {saving ? "Montando trilha..." : "Montar minha trilha"}
            <Check className="size-4" />
          </button>
        )}
      </footer>
    </main>
  );
}
