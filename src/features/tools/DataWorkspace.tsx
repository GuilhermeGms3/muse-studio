import { Cable, Database, Download, FileAudio, FolderOpen, Upload } from "lucide-react";
import { useQueryClient } from "@tanstack/react-query";
import { useRef, useState } from "react";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { Breadcrumb } from "@/workspace/navigation/Breadcrumb";
import { QueryState } from "@/shared/ui/query/QueryState";
import {
  downloadDataFile,
  importMusicFile,
  restoreBackup,
  useDataStatus,
  type ImportedFile,
} from "@/lib/music-api";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

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

export function DataPage() {
  const status = useDataStatus();
  const queryClient = useQueryClient();
  const { metronome, setMetronome } = useWorkspace();
  const restoreInput = useRef<HTMLInputElement>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [messageKind, setMessageKind] = useState<"success" | "error">("success");
  const [busy, setBusy] = useState<"backup" | "journal" | "restore" | "import" | null>(null);
  const [pendingRestore, setPendingRestore] = useState<File | null>(null);
  const [imports, setImports] = useState<ImportedFile[]>([]);
  const [dragging, setDragging] = useState(false);
  const [midiDevice, setMidiDevice] = useState("Desconectado");
  const [midiEvent, setMidiEvent] = useState("—");

  if (!status.data) return <QueryState error={status.error} />;

  const exportFile = async (kind: "backup" | "journal") => {
    setBusy(kind);
    try {
      const blob = await downloadDataFile(kind === "backup" ? "/data/backup" : "/data/journal.csv");
      saveBlob(
        blob,
        kind === "backup" ? `muse-studio-backup-${Date.now()}.json` : "muse-studio-journal.csv",
      );
      setMessageKind("success");
      setMessage(kind === "backup" ? "Backup de dados exportado." : "Histórico exportado.");
    } catch (reason) {
      setMessageKind("error");
      setMessage((reason as Error).message);
    } finally {
      setBusy(null);
    }
  };

  const restore = async (file: File | null) => {
    if (!file) return;
    setBusy("restore");
    try {
      const result = await restoreBackup(JSON.parse(await file.text()));
      await queryClient.invalidateQueries();
      setMessageKind("success");
      setMessage(result.message);
    } catch (reason) {
      setMessageKind("error");
      setMessage(
        `A restauração não foi concluída. Os dados atuais foram preservados. ${(reason as Error).message}`,
      );
    } finally {
      setBusy(null);
      setPendingRestore(null);
      if (restoreInput.current) restoreInput.current.value = "";
    }
  };

  const importFiles = async (files: FileList | File[]) => {
    setBusy("import");
    for (const file of Array.from(files)) {
      try {
        const imported = await importMusicFile(file);
        setImports((values) => [imported, ...values]);
        setMessageKind("success");
        setMessage(`${file.name} importado.`);
      } catch (reason) {
        setMessageKind("error");
        setMessage((reason as Error).message);
      }
    }
    setBusy(null);
  };

  const connectMidi = async () => {
    const midiNavigator = navigator as Navigator & {
      requestMIDIAccess?: () => Promise<MidiAccess>;
    };
    if (!midiNavigator.requestMIDIAccess) {
      setMidiDevice("Web MIDI indisponível");
      return;
    }
    try {
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
          if (!event.data) return;
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
    } catch {
      setMidiDevice("Não foi possível acessar o dispositivo MIDI");
      setMessageKind("error");
      setMessage("Verifique a permissão do navegador e tente conectar o dispositivo novamente.");
    }
  };

  return (
    <div className="flex h-full min-h-0 flex-col">
      <Breadcrumb trail={["Ferramentas", "Dados e integrações"]} />
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
                <Action
                  icon={Download}
                  label={busy === "backup" ? "Exportando..." : "Backup"}
                  disabled={busy !== null}
                  onClick={() => exportFile("backup")}
                />
                <Action
                  icon={Upload}
                  label={busy === "restore" ? "Restaurando..." : "Restaurar"}
                  disabled={busy !== null}
                  onClick={() => restoreInput.current?.click()}
                />
                <Action
                  icon={Download}
                  label={busy === "journal" ? "Exportando..." : "CSV"}
                  disabled={busy !== null}
                  onClick={() => exportFile("journal")}
                />
                <input
                  ref={restoreInput}
                  type="file"
                  accept=".json"
                  hidden
                  onChange={(event) => setPendingRestore(event.target.files?.[0] ?? null)}
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
                if (busy === null) void importFiles(event.dataTransfer.files);
              }}
              aria-disabled={busy !== null}
              className={`flex min-h-24 items-center justify-center gap-3 bg-surface p-4 text-xs ${busy !== null ? "cursor-not-allowed opacity-60" : "cursor-pointer"} ${dragging ? "outline outline-1 outline-signal" : ""}`}
            >
              <FileAudio className="size-5 text-muted-foreground" />
              Áudio, MIDI, MusicXML ou Guitar Pro
              <input
                type="file"
                multiple
                hidden
                disabled={busy !== null}
                accept=".wav,.mp3,.ogg,.flac,.m4a,.mid,.midi,.musicxml,.xml,.mxl,.gp,.gp3,.gp4,.gp5,.gpx"
                onChange={(event) => event.target.files && void importFiles(event.target.files)}
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
                type="button"
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
            <p
              role={messageKind === "error" ? "alert" : "status"}
              className={`mt-3 border-l pl-2 text-xs ${messageKind === "error" ? "border-destructive text-destructive" : "border-signal text-muted-foreground"}`}
            >
              {message}
            </p>
          )}
        </div>
      </main>
      <AlertDialog
        open={pendingRestore !== null}
        onOpenChange={(open) => {
          if (!open && busy !== "restore") {
            setPendingRestore(null);
            if (restoreInput.current) restoreInput.current.value = "";
          }
        }}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Restaurar este backup?</AlertDialogTitle>
            <AlertDialogDescription>
              Registros com o mesmo identificador serão atualizados. Dados adicionais serão
              preservados, mas a operação não pode ser desfeita automaticamente.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={busy === "restore"}>Cancelar</AlertDialogCancel>
            <AlertDialogAction
              disabled={busy === "restore"}
              onClick={() => void restore(pendingRestore)}
            >
              {busy === "restore" ? "Restaurando..." : "Restaurar backup"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}

function Action({
  icon: Icon,
  label,
  onClick,
  disabled = false,
}: {
  icon: typeof Download;
  label: string;
  onClick: () => void;
  disabled?: boolean;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      className="inline-flex h-8 items-center gap-2 border border-border px-3 text-xs hover:border-signal disabled:cursor-not-allowed disabled:opacity-50"
    >
      <Icon className="size-3" />
      {label}
    </button>
  );
}
