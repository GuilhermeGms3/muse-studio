import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
  type ReactNode,
} from "react";
import type { InstrumentId, SkillState } from "@/data/types";
import { quickNotes as seedNotes, todayPlan } from "@/data/practice";

export interface OpenTab {
  path: string;
  title: string;
}

interface SessionState {
  running: boolean;
  seconds: number;
  instrument: InstrumentId;
  bpm: number;
  goal: string;
  notes: string;
  exercises: string[];
}

interface WorkspaceValue {
  instrument: InstrumentId;
  setInstrument: (id: InstrumentId) => void;
  tabs: OpenTab[];
  openTab: (tab: OpenTab) => void;
  closeTab: (path: string) => void;
  sidebarOpen: boolean;
  toggleSidebar: () => void;
  inspectorOpen: boolean;
  toggleInspector: () => void;
  paletteOpen: boolean;
  setPaletteOpen: (v: boolean) => void;
  favorites: string[];
  toggleFavorite: (path: string) => void;
  history: string[];
  notes: string[];
  addNote: (text: string) => void;
  removeNote: (text: string) => void;
  skillOverrides: Record<string, SkillState>;
  setSkillState: (id: string, state: SkillState) => void;
  blocksDone: Record<string, boolean>;
  toggleBlock: (id: string) => void;
  session: SessionState;
  startSession: (partial?: Partial<SessionState>) => void;
  pauseSession: () => void;
  resetSession: () => void;
  updateSession: (partial: Partial<SessionState>) => void;
  metronome: { bpm: number; playing: boolean; beats: number; subdivision: number };
  setMetronome: (p: Partial<{ bpm: number; playing: boolean; beats: number; subdivision: number }>) => void;
}

const WorkspaceContext = createContext<WorkspaceValue | null>(null);

const STORAGE_KEY = "music-os:workspace:v1";

interface Persisted {
  instrument: InstrumentId;
  favorites: string[];
  notes: string[];
  skillOverrides: Record<string, SkillState>;
  blocksDone: Record<string, boolean>;
}

export function WorkspaceProvider({ children }: { children: ReactNode }) {
  const [instrument, setInstrumentState] = useState<InstrumentId>("guitar");
  const [tabs, setTabs] = useState<OpenTab[]>([]);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [inspectorOpen, setInspectorOpen] = useState(true);
  const [paletteOpen, setPaletteOpen] = useState(false);
  const [favorites, setFavorites] = useState<string[]>(["/skills", "/repertorio"]);
  const [history, setHistory] = useState<string[]>([]);
  const [notes, setNotes] = useState<string[]>(seedNotes);
  const [skillOverrides, setSkillOverrides] = useState<Record<string, SkillState>>({});
  const [blocksDone, setBlocksDone] = useState<Record<string, boolean>>(() =>
    Object.fromEntries(todayPlan.filter((b) => b.done).map((b) => [b.id, true])),
  );
  const [session, setSession] = useState<SessionState>({
    running: false,
    seconds: 0,
    instrument: "guitar",
    bpm: 100,
    goal: "Alternate Picking — 122 BPM limpo",
    notes: "",
    exercises: ["ex1", "ex12"],
  });
  const [metronome, setMetronomeState] = useState({
    bpm: 100,
    playing: false,
    beats: 4,
    subdivision: 1,
  });
  const hydrated = useRef(false);

  useEffect(() => {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) {
        const p = JSON.parse(raw) as Partial<Persisted>;
        if (p.instrument) setInstrumentState(p.instrument);
        if (p.favorites) setFavorites(p.favorites);
        if (p.notes) setNotes(p.notes);
        if (p.skillOverrides) setSkillOverrides(p.skillOverrides);
        if (p.blocksDone) setBlocksDone(p.blocksDone);
      }
    } catch {
      /* ignore */
    }
    hydrated.current = true;
  }, []);

  useEffect(() => {
    if (!hydrated.current) return;
    const payload: Persisted = { instrument, favorites, notes, skillOverrides, blocksDone };
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(payload));
    } catch {
      /* ignore */
    }
  }, [instrument, favorites, notes, skillOverrides, blocksDone]);

  useEffect(() => {
    if (!session.running) return;
    const t = setInterval(() => setSession((s) => ({ ...s, seconds: s.seconds + 1 })), 1000);
    return () => clearInterval(t);
  }, [session.running]);

  const openTab = useCallback((tab: OpenTab) => {
    setTabs((prev) => (prev.some((t) => t.path === tab.path) ? prev : [...prev, tab]));
    setHistory((prev) => [tab.path, ...prev.filter((p) => p !== tab.path)].slice(0, 12));
  }, []);

  const value = useMemo<WorkspaceValue>(
    () => ({
      instrument,
      setInstrument: setInstrumentState,
      tabs,
      openTab,
      closeTab: (path) => setTabs((prev) => prev.filter((t) => t.path !== path)),
      sidebarOpen,
      toggleSidebar: () => setSidebarOpen((v) => !v),
      inspectorOpen,
      toggleInspector: () => setInspectorOpen((v) => !v),
      paletteOpen,
      setPaletteOpen,
      favorites,
      toggleFavorite: (path) =>
        setFavorites((prev) => (prev.includes(path) ? prev.filter((p) => p !== path) : [...prev, path])),
      history,
      notes,
      addNote: (text) => setNotes((prev) => [text, ...prev]),
      removeNote: (text) => setNotes((prev) => prev.filter((n) => n !== text)),
      skillOverrides,
      setSkillState: (id, state) => setSkillOverrides((prev) => ({ ...prev, [id]: state })),
      blocksDone,
      toggleBlock: (id) => setBlocksDone((prev) => ({ ...prev, [id]: !prev[id] })),
      session,
      startSession: (partial) => setSession((s) => ({ ...s, ...partial, running: true })),
      pauseSession: () => setSession((s) => ({ ...s, running: false })),
      resetSession: () => setSession((s) => ({ ...s, running: false, seconds: 0, notes: "" })),
      updateSession: (partial) => setSession((s) => ({ ...s, ...partial })),
      metronome,
      setMetronome: (p) => setMetronomeState((m) => ({ ...m, ...p })),
    }),
    [
      instrument,
      tabs,
      openTab,
      sidebarOpen,
      inspectorOpen,
      paletteOpen,
      favorites,
      history,
      notes,
      skillOverrides,
      blocksDone,
      session,
      metronome,
    ],
  );

  return <WorkspaceContext.Provider value={value}>{children}</WorkspaceContext.Provider>;
}

export function useWorkspace() {
  const ctx = useContext(WorkspaceContext);
  if (!ctx) throw new Error("useWorkspace must be used inside WorkspaceProvider");
  return ctx;
}

export function formatClock(totalSeconds: number) {
  const h = Math.floor(totalSeconds / 3600);
  const m = Math.floor((totalSeconds % 3600) / 60);
  const s = totalSeconds % 60;
  return `${String(h).padStart(2, "0")}:${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
}
