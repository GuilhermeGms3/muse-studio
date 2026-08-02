import type { LucideIcon } from "lucide-react";
import { navigationRegistry } from "./registry";

export interface LegacyNavLeaf {
  label: string;
  path: string;
  icon: LucideIcon;
  hint?: string;
}

export interface LegacyNavGroup {
  label: string;
  items: LegacyNavLeaf[];
}

const legacyGroupOrder = ["Estacao", "Aprender", "Praticar", "Criar"];

export const legacyNavTree: LegacyNavGroup[] = legacyGroupOrder.map((label) => ({
  label,
  items: navigationRegistry
    .filter((entry) => entry.legacyGroup === label)
    .sort((a, b) => (a.legacyOrder ?? 0) - (b.legacyOrder ?? 0))
    .map(({ label: itemLabel, path, icon, hint }) => ({ label: itemLabel, path, icon, hint })),
}));

export const legacyNavFlat = legacyNavTree.flatMap((group) => group.items);
