import type { LucideIcon } from "lucide-react";
import {
  Activity,
  BookOpen,
  Boxes,
  Ear,
  Gauge,
  GitBranch,
  ListChecks,
  Music2,
  Timer,
} from "lucide-react";

export interface NavLeaf {
  label: string;
  path: string;
  icon: LucideIcon;
  hint?: string;
}

export interface NavGroup {
  label: string;
  items: NavLeaf[];
}

export const navTree: NavGroup[] = [
  {
    label: "Estacao",
    items: [
      { label: "Inicio", path: "/", icon: Activity, hint: "O que praticar agora" },
      { label: "Sessao", path: "/sessao", icon: Timer, hint: "Modo foco" },
    ],
  },
  {
    label: "Aprender",
    items: [
      { label: "Biblioteca", path: "/biblioteca", icon: BookOpen, hint: "Conteudos de teoria" },
      { label: "Skill Tree", path: "/skills", icon: GitBranch, hint: "Mapa de habilidades" },
    ],
  },
  {
    label: "Praticar",
    items: [
      { label: "Repertorio", path: "/repertorio", icon: Music2, hint: "Musicas e secoes" },
      { label: "Exercicios", path: "/exercicios", icon: ListChecks, hint: "Banco por tecnica" },
      { label: "Ear Training", path: "/ouvido", icon: Ear, hint: "Reconhecimento" },
      { label: "Metrônomo", path: "/metronomo", icon: Gauge, hint: "BPM e presets" },
    ],
  },
  {
    label: "Criar",
    items: [{ label: "Projetos", path: "/projetos", icon: Boxes, hint: "Riffs e ideias" }],
  },
];

export const navFlat: NavLeaf[] = navTree.flatMap((g) => g.items);

export function titleForPath(path: string): string {
  const exact = navFlat.find((i) => i.path === path);
  if (exact) return exact.label;
  if (path.startsWith("/biblioteca/")) return "Biblioteca";
  if (path.startsWith("/repertorio/")) return "Repertorio";
  if (path.startsWith("/projetos/")) return "Projeto";
  return path;
}
