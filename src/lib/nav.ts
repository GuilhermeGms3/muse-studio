export type {
  LegacyNavLeaf as NavLeaf,
  LegacyNavGroup as NavGroup,
} from "@/workspace/navigation/legacy-adapter";
export {
  legacyNavFlat as navFlat,
  legacyNavTree as navTree,
} from "@/workspace/navigation/legacy-adapter";
export {
  contextForPath,
  macroContexts,
  navigationGroups,
  navigationRegistry,
  titleForRegisteredPath as titleForPath,
} from "@/workspace/navigation/registry";
export type {
  MacroContextDefinition,
  MacroContextId,
  NavigationEntry,
  NavigationGroup,
} from "@/workspace/navigation/types";
