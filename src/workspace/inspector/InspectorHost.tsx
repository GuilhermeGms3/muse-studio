import { useInstruments } from "@/shared/api/home";
import { useSkills } from "@/shared/api/learning";
import { useTodayPlan } from "@/shared/api/practice";
import { useSongs } from "@/shared/api/repertoire";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { ContextSection } from "./sections/ContextSection";
import { NotesSection } from "./sections/NotesSection";
import { SkillsSection } from "./sections/SkillsSection";
import { SongSection } from "./sections/SongSection";

export function InspectorHost() {
  const { addNote, instrument, notes, removeNote, session } = useWorkspace();
  const instruments = useInstruments().data ?? [];
  const skills = useSkills(instrument).data ?? [];
  const songs = useSongs().data ?? [];
  const todayPlan = useTodayPlan(instrument).data ?? [];
  const currentInstrument = instruments.find((item) => item.id === instrument);
  const activeSkills = skills.filter((skill) => ["practicing", "learning"].includes(skill.state));
  const currentSong = songs.find(
    (song) => song.instrument === instrument && song.status === "learning",
  );
  const remainingMinutes = todayPlan
    .filter((activity) => !activity.done)
    .reduce((sum, activity) => sum + activity.minutes, 0);

  return (
    <aside
      aria-label="Inspetor contextual"
      className="flex h-full min-h-0 flex-col gap-px bg-border"
    >
      <ContextSection
        instrument={currentInstrument}
        sessionSeconds={session.seconds}
        remainingMinutes={remainingMinutes}
      />
      <SkillsSection skills={activeSkills} />
      <SongSection song={currentSong} />
      <NotesSection notes={notes} addNote={addNote} removeNote={removeNote} />
    </aside>
  );
}
