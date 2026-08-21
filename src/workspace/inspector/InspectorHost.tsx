import { useInstruments } from "@/shared/api/home";
import { useSongs } from "@/shared/api/repertoire";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { ContextSection } from "./sections/ContextSection";
import { NotesSection } from "./sections/NotesSection";
import { SongSection } from "./sections/SongSection";

export function InspectorHost() {
  const { addNote, instrument, notes, removeNote } = useWorkspace();
  const instruments = useInstruments().data ?? [];
  const songs = useSongs().data ?? [];
  const currentInstrument = instruments.find((item) => item.id === instrument);
  const currentSong = songs.find(
    (song) => song.instrument === instrument && song.status === "learning",
  );

  return (
    <aside
      aria-label="Inspetor contextual"
      className="flex h-full min-h-0 flex-col gap-px bg-border"
    >
      <ContextSection instrument={currentInstrument} />
      <SongSection song={currentSong} />
      <NotesSection notes={notes} addNote={addNote} removeNote={removeNote} />
    </aside>
  );
}
