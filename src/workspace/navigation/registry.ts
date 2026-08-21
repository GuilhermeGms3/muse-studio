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
  SearchCheck,
  Settings2,
  Target,
  AudioLines,
  Wrench,
} from "lucide-react";
import type {
  MacroContextDefinition,
  MacroContextId,
  NavigationEntry,
  NavigationGroup,
} from "./types";

export const macroContexts: MacroContextDefinition[] = [
  { id: "home", label: "Guiado", icon: Home, description: "Hoje e continuidade" },
  { id: "practice", label: "Prática", icon: Target, description: "Tocar e treinar livremente" },
  { id: "learning", label: "Aprender", icon: BrainCircuit, description: "Jornada e conhecimento" },
  { id: "library", label: "Música", icon: Library, description: "Repertório e descoberta" },
  { id: "compose", label: "Criar", icon: PenTool, description: "Projetos, riffs e ideias" },
  { id: "review", label: "Evoluir", icon: History, description: "Histórico e evidências" },
  { id: "tools", label: "Ferramentas", icon: Wrench, description: "Utilitários e dados" },
];

export const navigationRegistry: NavigationEntry[] = [
  {
    id: "home.today",
    context: "home",
    label: "Hoje",
    path: "/",
    icon: Activity,
    hint: "O que praticar agora",
  },
  {
    id: "home.journey",
    context: "home",
    label: "Jornada",
    path: "/jornada",
    matchPaths: ["/missoes/"],
    icon: GitBranch,
    hint: "Caminho guiado ativo",
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
    id: "practice.workspace",
    context: "practice",
    label: "Prática",
    path: "/pratica",
    icon: Target,
    hint: "Missão, exercício ou Studio",
  },
  {
    id: "practice.studio",
    context: "practice",
    label: "Studio",
    path: "/studio",
    matchPaths: ["/studio/"],
    icon: AudioLines,
    hint: "Loop, backing e takes",
  },
  {
    id: "practice.exercises",
    context: "practice",
    label: "Exercícios",
    path: "/exercicios",
    icon: ListChecks,
    hint: "Banco por técnica",
  },
  {
    id: "practice.ear",
    context: "practice",
    label: "Treino de ouvido",
    path: "/ouvido",
    icon: Ear,
    hint: "Reconhecimento",
  },
  {
    id: "practice.metronome",
    context: "practice",
    label: "Metrônomo",
    path: "/metronomo",
    icon: Gauge,
    hint: "BPM e presets",
  },
  {
    id: "learning.journey",
    context: "learning",
    label: "Jornada",
    path: "/jornada",
    matchPaths: ["/missoes/"],
    icon: GitBranch,
    hint: "Progressão musical guiada",
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
    id: "library.songs",
    context: "library",
    label: "Músicas",
    path: "/musicas",
    icon: Music2,
    hint: "Repertório em desenvolvimento",
  },
  {
    id: "library.explore",
    context: "library",
    label: "Explorar",
    path: "/explorar",
    icon: SearchCheck,
    hint: "Descoberta livre por contexto",
  },
  {
    id: "compose.projects",
    context: "compose",
    label: "Projetos",
    path: "/projetos",
    matchPaths: ["/projetos/"],
    icon: Boxes,
    hint: "Riffs e ideias",
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
    id: "review.history",
    context: "review",
    label: "Histórico musical",
    path: "/historico",
    icon: History,
    hint: "Sessões, tentativas e evidências",
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
  if (pathname === "/jornada" || pathname.startsWith("/missoes/")) return "learning";
  if (pathname === "/musicas" || pathname.startsWith("/musicas/")) return "library";
  if (pathname === "/historico") return "review";
  if (pathname === "/explorar") return "library";
  if (pathname === "/diagnostico") return "home";
  if (pathname.startsWith("/exercicios/")) return "practice";
  if (
    ["/pratica", "/sessao", "/plano", "/exercicios", "/ouvido", "/metronomo"].includes(pathname)
  ) {
    return "practice";
  }
  if (pathname === "/studio" || pathname.startsWith("/studio/")) return "practice";
  if (pathname === "/skills" || pathname === "/mapa") return "learning";
  if (pathname === "/biblioteca" || pathname.startsWith("/biblioteca/")) return "library";
  if (pathname === "/projetos" || pathname.startsWith("/projetos/")) return "compose";
  if (pathname === "/diario") return "review";
  if (pathname === "/dados") return "tools";
  return "home";
}

export function titleForRegisteredPath(pathname: string): string {
  const titles: Record<string, string> = {
    "/": "Home",
    "/jornada": "Jornada",
    "/musicas": "Músicas",
    "/historico": "Histórico",
    "/explorar": "Explorar",
    "/diagnostico": "Diagnóstico",
    "/pratica": "Prática",
    "/studio": "Studio",
    "/plano": "Prática",
    "/diario": "Diário",
    "/biblioteca": "Biblioteca",
    "/skills": "Jornada",
    "/mapa": "Mapa do Conhecimento",
    "/exercicios": "Exercícios",
    "/ouvido": "Ear Training",
    "/metronomo": "Metrônomo",
    "/projetos": "Projetos",
    "/dados": "Dados e Integrações",
  };
  if (titles[pathname]) return titles[pathname];
  if (pathname.startsWith("/biblioteca/")) return "Biblioteca";
  if (pathname.startsWith("/missoes/")) return "Missão";
  if (pathname.startsWith("/exercicios/")) return "Exercício";
  if (pathname.startsWith("/musicas/")) return "Música";
  if (pathname.startsWith("/projetos/")) return "Projeto";
  return pathname;
}
