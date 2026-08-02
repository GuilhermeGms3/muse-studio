import { useNavigate } from "@tanstack/react-router";
import {
  CommandDialog,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import { navigationRegistry } from "@/workspace/navigation/registry";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";
import { useExercises } from "@/shared/api/exercises";
import { useLibrary } from "@/shared/api/library";
import { useSkills } from "@/shared/api/learning";
import { useSongs } from "@/shared/api/repertoire";
import { Database } from "lucide-react";

export function CommandPalette() {
  const { paletteOpen, setPaletteOpen, instrument } = useWorkspace();
  const navigate = useNavigate();
  const library = useLibrary().data ?? [];
  const songs = useSongs().data ?? [];
  const exercises = useExercises(instrument).data ?? [];
  const skills = useSkills(instrument).data ?? [];
  const modules = navigationRegistry.filter(
    (item, index, entries) =>
      entries.findIndex((candidate) => candidate.path === item.path) === index,
  );

  const go = (to: string) => {
    setPaletteOpen(false);
    navigate({ to });
  };

  return (
    <CommandDialog open={paletteOpen} onOpenChange={setPaletteOpen}>
      <CommandInput placeholder="Buscar módulos, conteúdos, músicas, exercícios e habilidades..." />
      <CommandList className="max-h-[420px]">
        <CommandEmpty>Nada encontrado.</CommandEmpty>
        <CommandGroup heading="Módulos">
          <CommandItem value="dados backup restaurar midi importar" onSelect={() => go("/dados")}>
            <Database className="size-3.5" />
            Dados e integrações
            <span className="ml-auto text-2xs text-text-muted">Backup, arquivos e MIDI</span>
          </CommandItem>
          {modules.map((item) => (
            <CommandItem
              key={item.path}
              value={`modulo ${item.label}`}
              onSelect={() => go(item.path)}
            >
              <item.icon className="size-3.5" />
              {item.label}
              <span className="ml-auto text-2xs text-text-muted">{item.hint}</span>
            </CommandItem>
          ))}
        </CommandGroup>
        <CommandGroup heading="Biblioteca">
          {library.map((item) => (
            <CommandItem
              key={item.id}
              value={`lib ${item.friendlyTitle} ${item.technicalName} ${item.category}`}
              onSelect={() => go(`/biblioteca/${item.id}`)}
            >
              {item.friendlyTitle}
              <span className="ml-auto text-2xs text-text-muted">{item.technicalName}</span>
            </CommandItem>
          ))}
        </CommandGroup>
        <CommandGroup heading="Repertório">
          {songs.map((song) => (
            <CommandItem
              key={song.id}
              value={`song ${song.title} ${song.artist}`}
              onSelect={() => go(`/repertorio/${song.id}`)}
            >
              {song.title}
              <span className="ml-auto text-2xs text-text-muted">{song.artist}</span>
            </CommandItem>
          ))}
        </CommandGroup>
        <CommandGroup heading="Habilidades">
          {skills.map((skill) => (
            <CommandItem
              key={skill.id}
              value={`skill ${skill.friendlyTitle} ${skill.technicalName} ${skill.domain}`}
              onSelect={() => go("/skills")}
            >
              {skill.technicalName}
              <span className="ml-auto text-2xs text-text-muted">{skill.domain}</span>
            </CommandItem>
          ))}
        </CommandGroup>
        <CommandGroup heading="Exercícios">
          {exercises.map((exercise) => (
            <CommandItem
              key={exercise.id}
              value={`ex ${exercise.name} ${exercise.technique}`}
              onSelect={() => go("/exercicios")}
            >
              {exercise.name}
              <span className="ml-auto text-2xs text-text-muted">{exercise.technique}</span>
            </CommandItem>
          ))}
        </CommandGroup>
      </CommandList>
    </CommandDialog>
  );
}
