import type { LucideIcon } from "lucide-react";

export const macroContextIds = [
  "home",
  "practice",
  "learning",
  "library",
  "compose",
  "review",
  "tools",
] as const;

export type MacroContextId = (typeof macroContextIds)[number];

export interface MacroContextDefinition {
  id: MacroContextId;
  label: string;
  icon: LucideIcon;
  description: string;
}

export interface NavigationEntry {
  id: string;
  context: MacroContextId;
  label: string;
  path: string;
  icon: LucideIcon;
  hint?: string;
  matchPaths?: string[];
}

export interface NavigationGroup {
  context: MacroContextId;
  label: string;
  items: NavigationEntry[];
}
