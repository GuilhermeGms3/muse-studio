import { useNavigate } from "@tanstack/react-router";
import { CommandDialog, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from "@/components/ui/command";
import { navFlat } from "@/lib/nav";
import { useWorkspace } from "@/lib/workspace-store";
import { library } from "@/data/library";
import { songs, exercises } from "@/data/practice";
import { skills } from "@/data/skills";

export function CommandPalette() {
  const { paletteOpen, setPaletteOpen } = useWorkspace();
  const navigate = useNavigate();

  const go = (to: string) => {
    setPaletteOpen(false);
    navigate({ to });
  };

  return (
    <CommandDialog open={paletteOpen} onOpenChange={setPaletteOpen}>
      <CommandInput placeholder="Buscar módulos, conteúdos, músicas, exercícios, habilidades…" />
      <CommandList className="max-h-[420px]">
        <CommandEmpty>Nada encontrado.</CommandEmpty>
        <CommandGroup heading="Módulos">
          {navFlat.map((i) => (
            <CommandItem key={i.path} value={`modulo ${i.label}`} onSelect={() => go(i.path)}>
              <i.icon className="size-3.5" />
              {i.label}
              <span className="ml-auto text-2xs text-muted-foreground">{i.hint}</span>
            </CommandItem>
          ))}
        </CommandGroup>
        <CommandGroup heading="Biblioteca">
          {library.map((n) => (
            <CommandItem key={n.id} value={`lib ${n.title} ${n.category}`} onSelect={() => go(`/biblioteca/${n.id}`)}>
              {n.title}
              <span className="ml-auto text-2xs text-muted-foreground">{n.category}</span>
            </CommandItem>
          ))}
        </CommandGroup>
        <CommandGroup heading="Repertório">
          {songs.map((s) => (
            <CommandItem key={s.id} value={`song ${s.title} ${s.artist}`} onSelect={() => go(`/repertorio/${s.id}`)}>
              {s.title}
              <span className="ml-auto text-2xs text-muted-foreground">{s.artist}</span>
            </CommandItem>
          ))}
        </CommandGroup>
        <CommandGroup heading="Habilidades">
          {skills.slice(0, 40).map((s) => (
            <CommandItem key={s.id} value={`skill ${s.name} ${s.domain}`} onSelect={() => go("/skills")}>
              {s.name}
              <span className="ml-auto text-2xs text-muted-foreground">{s.domain}</span>
            </CommandItem>
          ))}
        </CommandGroup>
        <CommandGroup heading="Exercícios">
          {exercises.map((e) => (
            <CommandItem key={e.id} value={`ex ${e.name} ${e.technique}`} onSelect={() => go("/exercicios")}>
              {e.name}
              <span className="ml-auto text-2xs text-muted-foreground">{e.technique}</span>
            </CommandItem>
          ))}
        </CommandGroup>
      </CommandList>
    </CommandDialog>
  );
}
