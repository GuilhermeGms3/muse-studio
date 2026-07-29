import type { LucideIcon } from "lucide-react";
import {
  Activity,
  BookOpen,
  Boxes,
  CalendarRange,
  Ear,
  Gauge,
  GitBranch,
  ListChecks,
  Music2,
  NotebookPen,
  Network,
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
    label: "Estação",
    items: [
      { label: "Início", path: "/", icon: Activity, hint: "Estação de trabalho" },
      { label: "Sessão de Prática", path: "/sessao", icon: Timer, hint: "Cronômetro e execução" },
      { label: "Plano de Estudos", path: "/plano", icon: CalendarRange, hint: "Blocos do dia" },
      { label: "Diário", path: "/diario", icon: NotebookPen, hint: "Histórico de práticas" },
    ],
  },
  {
    label: "Conhecimento",
    items: [
      { label: "Biblioteca", path: "/biblioteca", icon: BookOpen, hint: "Teoria e conteúdos" },
      { label: "Skill Tree", path: "/skills", icon: GitBranch, hint: "Domínio real" },
      { label: "Mapa do Conhecimento", path: "/mapa", icon: Network, hint: "Grafo interligado" },
    ],
  },
  {
    label: "Prática",
    items: [
      { label: "Repertório", path: "/repertorio", icon: Music2, hint: "Músicas e seções" },
      { label: "Exercícios", path: "/exercicios", icon: ListChecks, hint: "Banco por técnica" },
      { label: "Treino de Ouvido", path: "/ouvido", icon: Ear, hint: "Reconhecimento" },
      { label: "Metrônomo", path: "/metronomo", icon: Gauge, hint: "BPM e presets" },
    ],
  },
  {
    label: "Criação",
    items: [{ label: "Projetos Musicais", path: "/projetos", icon: Boxes, hint: "Riffs e ideias" }],
  },
];

export const navFlat: NavLeaf[] = navTree.flatMap((g) => g.items);

export function titleForPath(path: string): string {
  const exact = navFlat.find((i) => i.path === path);
  if (exact) return exact.label;
  if (path.startsWith("/biblioteca/")) return "Biblioteca";
  if (path.startsWith("/repertorio/")) return "Repertório";
  if (path.startsWith("/projetos/")) return "Projeto";
  return path;
}
