import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import type { InstrumentId } from "@/shared/api/contracts";
import { consolidateWorkspaceTabs } from "@/features/product-model";
import {
  isWorkspaceTab,
  tabKey,
  withTabDefaults,
  type WorkspaceTab,
} from "@/workspace/tabs/tab-model";

export type OpenTab = WorkspaceTab;

interface WorkspaceValue {
  theme: "dark" | "light";
  toggleTheme: () => void;
  instrument: InstrumentId;
  setInstrument: (id: InstrumentId) => void;
  tabs: OpenTab[];
  openTab: (tab: OpenTab) => void;
  closeTab: (key: string) => void;
  sidebarOpen: boolean;
  toggleSidebar: () => void;
  inspectorOpen: boolean;
  toggleInspector: () => void;
  dockOpen: boolean;
  toggleDock: () => void;
  paletteOpen: boolean;
  setPaletteOpen: (value: boolean) => void;
  notes: string[];
  addNote: (text: string) => void;
  removeNote: (text: string) => void;
  metronome: { bpm: number; playing: boolean; beats: number; subdivision: number };
  setMetronome: (
    partial: Partial<{ bpm: number; playing: boolean; beats: number; subdivision: number }>,
  ) => void;
}

const WorkspaceContext = createContext<WorkspaceValue | null>(null);

const LEGACY_STORAGE_KEY = "music-os:workspace:v1";
const STORAGE_KEY = "muse-studio:workspace:v2";
const STORAGE_VERSION = 2;

interface PersistedWorkspace {
  version: typeof STORAGE_VERSION;
  instrument: InstrumentId;
  notes: string[];
  tabs: WorkspaceTab[];
  sidebarOpen: boolean;
  inspectorOpen: boolean;
  dockOpen: boolean;
  theme?: "dark" | "light";
}

function readPersistedWorkspace(): Partial<PersistedWorkspace> | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      const parsed = JSON.parse(raw) as Partial<PersistedWorkspace>;
      if (parsed.version === STORAGE_VERSION) {
        return {
          ...parsed,
          tabs: Array.isArray(parsed.tabs)
            ? consolidateWorkspaceTabs(parsed.tabs.filter(isWorkspaceTab)).map(withTabDefaults)
            : [],
        };
      }
    }

    const legacyRaw = localStorage.getItem(LEGACY_STORAGE_KEY);
    if (!legacyRaw) return null;
    const legacy = JSON.parse(legacyRaw) as {
      instrument?: InstrumentId;
      notes?: string[];
    };
    return {
      instrument: legacy.instrument,
      notes: Array.isArray(legacy.notes) ? legacy.notes : undefined,
    };
  } catch {
    return null;
  }
}

export function WorkspaceProvider({ children }: { children: ReactNode }) {
  const [theme, setTheme] = useState<"dark" | "light">("dark");
  const [instrument, setInstrumentState] = useState<InstrumentId>("guitar");
  const [tabs, setTabs] = useState<OpenTab[]>([]);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [inspectorOpen, setInspectorOpen] = useState(false);
  const [dockOpen, setDockOpen] = useState(false);
  const [paletteOpen, setPaletteOpen] = useState(false);
  const [notes, setNotes] = useState<string[]>([]);
  const [hydrated, setHydrated] = useState(false);
  const [metronome, setMetronomeState] = useState({
    bpm: 100,
    playing: false,
    beats: 4,
    subdivision: 1,
  });

  useEffect(() => {
    const persisted = readPersistedWorkspace();
    if (persisted?.instrument) setInstrumentState(persisted.instrument);
    if (persisted?.notes) setNotes(persisted.notes);
    if (persisted?.tabs) setTabs(persisted.tabs);
    if (typeof persisted?.sidebarOpen === "boolean") setSidebarOpen(persisted.sidebarOpen);
    if (typeof persisted?.inspectorOpen === "boolean") setInspectorOpen(persisted.inspectorOpen);
    if (typeof persisted?.dockOpen === "boolean") setDockOpen(persisted.dockOpen);
    if (persisted?.theme === "light" || persisted?.theme === "dark") setTheme(persisted.theme);
    setHydrated(true);
  }, []);

  useEffect(() => {
    if (!hydrated) return;
    const payload: PersistedWorkspace = {
      version: STORAGE_VERSION,
      instrument,
      notes,
      tabs,
      sidebarOpen,
      inspectorOpen,
      dockOpen,
      theme,
    };
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
    } catch {
      // Local persistence is optional; the in-memory workspace remains usable.
    }
  }, [dockOpen, hydrated, inspectorOpen, instrument, notes, sidebarOpen, tabs, theme]);

  const openTab = useCallback((incoming: OpenTab) => {
    const tab = withTabDefaults(incoming);
    setTabs((current) => {
      const key = tabKey(tab);
      const existingIndex = current.findIndex((candidate) => tabKey(candidate) === key);
      if (existingIndex < 0) return [...current, tab];
      return current.map((candidate, index) =>
        index === existingIndex ? { ...candidate, ...tab } : candidate,
      );
    });
  }, []);

  const value = useMemo<WorkspaceValue>(
    () => ({
      theme,
      toggleTheme: () => setTheme((value) => (value === "dark" ? "light" : "dark")),
      instrument,
      setInstrument: setInstrumentState,
      tabs,
      openTab,
      closeTab: (key) =>
        setTabs((current) => current.filter((tab) => tabKey(tab) !== key && tab.path !== key)),
      sidebarOpen,
      toggleSidebar: () => setSidebarOpen((value) => !value),
      inspectorOpen,
      toggleInspector: () => setInspectorOpen((value) => !value),
      dockOpen,
      toggleDock: () => setDockOpen((value) => !value),
      paletteOpen,
      setPaletteOpen,
      notes,
      addNote: (text) => setNotes((current) => [text, ...current]),
      removeNote: (text) => setNotes((current) => current.filter((note) => note !== text)),
      metronome,
      setMetronome: (partial) => setMetronomeState((current) => ({ ...current, ...partial })),
    }),
    [
      dockOpen,
      inspectorOpen,
      instrument,
      metronome,
      notes,
      openTab,
      paletteOpen,
      sidebarOpen,
      tabs,
      theme,
    ],
  );

  return <WorkspaceContext.Provider value={value}>{children}</WorkspaceContext.Provider>;
}

export function useWorkspace() {
  const context = useContext(WorkspaceContext);
  if (!context) throw new Error("useWorkspace must be used inside WorkspaceProvider");
  return context;
}
