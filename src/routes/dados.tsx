import { createFileRoute } from "@tanstack/react-router";
import { Cable, Database, Download, FileAudio, FolderOpen, Upload } from "lucide-react";
import { useRef, useState } from "react";
import { Breadcrumb } from "@/components/workspace/TabBar";
import { QueryState } from "@/components/workspace/QueryState";
import {
  downloadDataFile,
  importMusicFile,
  restoreBackup,
  useDataStatus,
  type ImportedFile,
} from "@/lib/music-api";
import { useWorkspace } from "@/lib/workspace-store";

export const Route = createFileRoute("/dados")({
  head: () => ({ meta: [{ title: "Dados e integrações - Music OS" }] }),
  component: DataPage,
});

type MidiInput = {
  name?: string;
  onmidimessage: ((event: { data: Uint8Array }) => void) | null;
};
type MidiAccess = { inputs: Map<string, MidiInput> };

function saveBlob(blob: Blob, name: string) {
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement("a");
  anchor.href = url;
  anchor.download = name;
  anchor.click();
  URL.revokeObjectURL(url);
}

function DataPage() {
  const status = useDataStatus();
  const { metronome, setMetronome } = useWorkspace();
  const restoreInput = useRef<HTMLInputElement>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [imports, setImports] = useState<ImportedFile[]>([]);
  const [dragging, setDragging] = useState(false);
  const [midiDevice, setMidiDevice] = useState("Desconectado");
  const [midiEvent, setMidiEvent] = useState("—");

  if (!status.data) return <QueryState error={status.error} />;

  const exportFile = async (kind: "backup" | "journal") => {
    const blob = await downloadDataFile(kind === "backup" ? "/data/backup" : "/data/journal.csv");
    saveBlob(
      blob,
      kind === "backup" ? `music-os-backup-${Date.now()}.json` : "music-os-journal.csv",
    );
    setMessage(kind === "backup" ? "Backup exportado." : "Histórico exportado.");
  };

  const restore = async (file?: File) => {
    if (!file) return;
    try {
      const result = await restoreBackup(JSON.parse(await file.text()));
      setMessage(result.message);
    } catch (reason) {
      setMessage((reason as Error).message);
    }
  };

  const importFiles = async (files: FileList | File[]) => {
    for (const file of Array.from(files)) {
      try {
        const imported = await importMusicFile(file);
        setImports((values) => [imported, ...values]);
        setMessage(`${file.name} importado.`);
      } catch (reason) {
        setMessage((reason as Error).message);
      }
    }
  };

  const connectMidi = async () => {
    const midiNavigator = navigator as Navigator & {
      requestMIDIAccess?: () => Promise<MidiAccess>;
    };
    if (!midiNavigator.requestMIDIAccess) {
      setMidiDevice("Web MIDI indisponível");
      return;
    }
    const access = await midiNavigator.requestMIDIAccess();
    const inputs = Array.from(access.inputs.values());
    setMidiDevice(
      inputs
        .map((input) => input.name)
        .filter(Boolean)
        .join(", ") || "Nenhuma entrada",
    );
    inputs.forEach((input) => {
      input.onmidimessage = (event) => {
        const [statusByte, data1, data2] = event.data;
        const command = statusByte & 0xf0;
        if (command === 0x90 && data2 > 0) {
          setMidiEvent(`Nota ${data1} · velocidade ${data2}`);
          if (data1 === 36) setMetronome({ playing: !metronome.playing });
        }
        if (command === 0xb0 && data1 === 64 && data2 > 0) {
          setMidiEvent("Pedal · metrônomo");
          setMetronome({ playing: !metronome.playing });
        }
      };
    });
  };

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Sistema", "Dados e integrações"]} />
      <main className="min-h-0 flex-1 overflow-auto bg-panel p-4">
        <div className="mx-auto max-w-4xl">
          <section className="border border-border">
            <header className="flex items-center gap-2 border-b border-border bg-rail px-3 py-2">
              <Database className="size-4 text-signal" />
              <span className="label-tech">Dados locais</span>
            </header>
            <div className="grid gap-px bg-border md:grid-cols-[1fr_auto]">
              <div className="bg-surface p-3">
                <p className="num break-all text-xs">{status.data.dataDirectory}</p>
                <p className="mt-1 text-2xs text-muted-foreground">
                  {status.data.lessons} aulas · {status.data.exercises} exercícios ·{" "}
                  {status.data.recordings} gravações
                </p>
              </div>
              <div className="flex items-center gap-1 bg-surface p-2">
                <Action icon={Download} label="Backup" onClick={() => exportFile("backup")} />
                <Action
                  icon={Upload}
                  label="Restaurar"
                  onClick={() => restoreInput.current?.click()}
                />
                <Action icon={Download} label="CSV" onClick={() => exportFile("journal")} />
                <input
                  ref={restoreInput}
                  type="file"
                  accept=".json"
                  hidden
                  onChange={(event) => restore(event.target.files?.[0])}
                />
              </div>
            </div>
          </section>

          <section className="mt-4 border border-border">
            <header className="flex items-center gap-2 border-b border-border bg-rail px-3 py-2">
              <FolderOpen className="size-4 text-signal" />
              <span className="label-tech">Importar material</span>
            </header>
            <label
              onDragOver={(event) => {
                event.preventDefault();
                setDragging(true);
              }}
              onDragLeave={() => setDragging(false)}
              onDrop={(event) => {
                event.preventDefault();
                setDragging(false);
                importFiles(event.dataTransfer.files);
              }}
              className={`flex min-h-24 cursor-pointer items-center justify-center gap-3 bg-surface p-4 text-xs ${dragging ? "outline outline-1 outline-signal" : ""}`}
            >
              <FileAudio className="size-5 text-muted-foreground" />
              Áudio, MIDI, MusicXML ou Guitar Pro
              <input
                type="file"
                multiple
                hidden
                accept=".wav,.mp3,.ogg,.flac,.m4a,.mid,.midi,.musicxml,.xml,.mxl,.gp,.gp3,.gp4,.gp5,.gpx"
                onChange={(event) => event.target.files && importFiles(event.target.files)}
              />
            </label>
            {imports.map((item) => (
              <div
                key={item.id}
                className="flex justify-between border-t border-border px-3 py-2 text-xs"
              >
                <span>{item.name}</span>
                <span className="label-tech">{item.type}</span>
              </div>
            ))}
          </section>

          <section className="mt-4 border border-border">
            <header className="flex items-center gap-2 border-b border-border bg-rail px-3 py-2">
              <Cable className="size-4 text-signal" />
              <span className="label-tech">MIDI</span>
            </header>
            <div className="flex items-center gap-3 bg-surface p-3">
              <button
                onClick={connectMidi}
                className="h-8 border border-border px-3 text-xs hover:border-signal"
              >
                Conectar
              </button>
              <div className="min-w-0">
                <p className="truncate text-xs">{midiDevice}</p>
                <p className="num text-2xs text-muted-foreground">{midiEvent}</p>
              </div>
            </div>
          </section>
          {message && (
            <p className="mt-3 border-l border-signal pl-2 text-xs text-muted-foreground">
              {message}
            </p>
          )}
        </div>
      </main>
    </div>
  );
}

function Action({
  icon: Icon,
  label,
  onClick,
}: {
  icon: typeof Download;
  label: string;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className="inline-flex h-8 items-center gap-2 border border-border px-3 text-xs hover:border-signal"
    >
      <Icon className="size-3" />
      {label}
    </button>
  );
}
