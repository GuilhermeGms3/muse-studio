import {
  Activity,
  BookOpen,
  Boxes,
  BrainCircuit,
  Database,
  Ear,
  Gauge,
  GitBranch,
  History,
  Home,
  Library,
  ListChecks,
  Music2,
  PenTool,
  RotateCcw,
  Settings2,
  Target,
  Timer,
  Wrench,
} from "lucide-react";
import type {
  MacroContextDefinition,
  MacroContextId,
  NavigationEntry,
  NavigationGroup,
} from "./types";

export const macroContexts: MacroContextDefinition[] = [
  { id: "home", label: "Home", icon: Home, description: "Estação de hoje" },
  { id: "practice", label: "Praticar", icon: Target, description: "Sessões e repertório" },
  { id: "learning", label: "Aprender", icon: BrainCircuit, description: "Habilidades e trilhas" },
  { id: "library", label: "Biblioteca", icon: Library, description: "Conteúdos e coleções" },
  { id: "compose", label: "Compor", icon: PenTool, description: "Projetos e ideias" },
  { id: "review", label: "Revisar", icon: History, description: "Histórico e progresso" },
  { id: "tools", label: "Ferramentas", icon: Wrench, description: "Utilitários e dados" },
];

export const navigationRegistry: NavigationEntry[] = [
  {
    id: "home.today",
    context: "home",
    label: "Estação de hoje",
    path: "/",
    icon: Activity,
    hint: "O que praticar agora",
    legacyGroup: "Estacao",
    legacyOrder: 0,
  },
  {
    id: "home.plan",
    context: "home",
    label: "Plano de hoje",
    path: "/plano",
    icon: ListChecks,
    hint: "Sequência de estudo",
  },
  {
    id: "home.diagnostic",
    context: "home",
    label: "Diagnóstico inicial",
    path: "/diagnostico",
    icon: Target,
    hint: "Definir ponto de partida",
  },
  {
    id: "practice.session",
    context: "practice",
    label: "Sessão",
    path: "/sessao",
    icon: Timer,
    hint: "Modo foco",
    legacyGroup: "Estacao",
    legacyOrder: 1,
  },
  {
    id: "practice.plan",
    context: "practice",
    label: "Plano de hoje",
    path: "/plano",
    icon: ListChecks,
    hint: "Atividades planejadas",
  },
  {
    id: "practice.exercises",
    context: "practice",
    label: "Exercícios",
    path: "/exercicios",
    icon: ListChecks,
    hint: "Banco por técnica",
    legacyGroup: "Praticar",
    legacyOrder: 1,
  },
  {
    id: "practice.song-workspace",
    context: "practice",
    label: "Treinar música",
    path: "/treino-musica",
    matchPaths: ["/treino-musica/"],
    icon: Music2,
    hint: "Tablatura, vídeo e playback",
  },
  {
    id: "practice.repertoire",
    context: "practice",
    label: "Repertório",
    path: "/repertorio",
    matchPaths: ["/repertorio/"],
    icon: Music2,
    hint: "Músicas e seções",
    legacyGroup: "Praticar",
    legacyOrder: 0,
  },
  {
    id: "practice.ear",
    context: "practice",
    label: "Treino de ouvido",
    path: "/ouvido",
    icon: Ear,
    hint: "Reconhecimento",
    legacyGroup: "Praticar",
    legacyOrder: 2,
  },
  {
    id: "practice.metronome",
    context: "practice",
    label: "Metrônomo",
    path: "/metronomo",
    icon: Gauge,
    hint: "BPM e presets",
    legacyGroup: "Praticar",
    legacyOrder: 3,
  },
  {
    id: "learning.skills",
    context: "learning",
    label: "Skill Tree",
    path: "/skills",
    matchPaths: ["/mapa"],
    icon: GitBranch,
    hint: "Mapa de habilidades",
    legacyGroup: "Aprender",
    legacyOrder: 1,
  },
  {
    id: "learning.library",
    context: "learning",
    label: "Conceitos em progresso",
    path: "/biblioteca",
    matchPaths: ["/biblioteca/"],
    icon: BookOpen,
    hint: "Conteúdos ligados às habilidades",
  },
  {
    id: "library.content",
    context: "library",
    label: "Conteúdos",
    path: "/biblioteca",
    matchPaths: ["/biblioteca/"],
    icon: BookOpen,
    hint: "Teoria e técnica",
    legacyGroup: "Aprender",
    legacyOrder: 0,
  },
  {
    id: "library.repertoire",
    context: "library",
    label: "Repertório",
    path: "/repertorio",
    matchPaths: ["/repertorio/"],
    icon: Music2,
    hint: "Coleção de músicas",
  },
  {
    id: "library.import",
    context: "library",
    label: "Importação",
    path: "/dados",
    icon: Database,
    hint: "Arquivos musicais",
  },
  {
    id: "compose.projects",
    context: "compose",
    label: "Projetos",
    path: "/projetos",
    matchPaths: ["/projetos/"],
    icon: Boxes,
    hint: "Riffs e ideias",
    legacyGroup: "Criar",
    legacyOrder: 0,
  },
  {
    id: "review.journal",
    context: "review",
    label: "Diário",
    path: "/diario",
    icon: History,
    hint: "Histórico de sessões",
  },
  {
    id: "review.skills",
    context: "review",
    label: "Evidências de habilidade",
    path: "/skills",
    icon: RotateCcw,
    hint: "Progresso por instrumento",
  },
  {
    id: "review.exports",
    context: "review",
    label: "Exportações",
    path: "/dados",
    icon: Database,
    hint: "Backup e diário",
  },
  {
    id: "tools.metronome",
    context: "tools",
    label: "Metrônomo",
    path: "/metronomo",
    icon: Gauge,
    hint: "Ferramenta persistente",
  },
  {
    id: "tools.data",
    context: "tools",
    label: "Dados e integrações",
    path: "/dados",
    icon: Settings2,
    hint: "Backup, importação e MIDI",
  },
];

export const navigationGroups: NavigationGroup[] = macroContexts.map((context) => ({
  context: context.id,
  label: context.label,
  items: navigationRegistry.filter((entry) => entry.context === context.id),
}));

export function entryMatchesPath(entry: NavigationEntry, pathname: string) {
  return (
    entry.path === pathname || entry.matchPaths?.some((path) => pathname.startsWith(path)) === true
  );
}

export function contextForPath(pathname: string): MacroContextId {
  if (pathname === "/") return "home";
  if (pathname === "/diagnostico") return "home";
  if (["/sessao", "/plano", "/exercicios", "/ouvido", "/metronomo"].includes(pathname)) {
    return "practice";
  }
  if (pathname === "/treino-musica" || pathname.startsWith("/treino-musica/")) return "practice";
  if (pathname === "/skills" || pathname === "/mapa") return "learning";
  if (pathname === "/biblioteca" || pathname.startsWith("/biblioteca/")) return "library";
  if (pathname === "/repertorio" || pathname.startsWith("/repertorio/")) return "practice";
  if (pathname === "/projetos" || pathname.startsWith("/projetos/")) return "compose";
  if (pathname === "/diario") return "review";
  if (pathname === "/dados") return "tools";
  return "home";
}

export function titleForRegisteredPath(pathname: string): string {
  const titles: Record<string, string> = {
    "/": "Home",
    "/diagnostico": "Diagnóstico",
    "/sessao": "Sessão",
    "/plano": "Plano de Hoje",
    "/diario": "Diário",
    "/biblioteca": "Biblioteca",
    "/skills": "Skill Tree",
    "/mapa": "Mapa do Conhecimento",
    "/treino-musica": "Treinar música",
    "/repertorio": "Repertório",
    "/exercicios": "Exercícios",
    "/ouvido": "Ear Training",
    "/metronomo": "Metrônomo",
    "/projetos": "Projetos",
    "/dados": "Dados e Integrações",
  };
  if (titles[pathname]) return titles[pathname];
  if (pathname.startsWith("/biblioteca/")) return "Biblioteca";
  if (pathname.startsWith("/treino-musica/")) return "Treino musical";
  if (pathname.startsWith("/repertorio/")) return "Repertório";
  if (pathname.startsWith("/projetos/")) return "Projeto";
  return pathname;
}
