import { useEffect } from "react";
import { useNavigate } from "@tanstack/react-router";
import { useWorkspace } from "@/workspace/store/WorkspaceProvider";

export function useWorkspaceShortcuts() {
  const navigate = useNavigate();
  const { metronome, setMetronome, setPaletteOpen, toggleDock, toggleInspector, toggleSidebar } =
    useWorkspace();

  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => {
      const command = event.metaKey || event.ctrlKey;
      if (!command) return;
      const key = event.key.toLowerCase();

      if (key === "k") {
        event.preventDefault();
        setPaletteOpen(true);
      } else if (key === "b") {
        event.preventDefault();
        toggleSidebar();
      } else if (event.key === "\\") {
        event.preventDefault();
        toggleInspector();
      } else if (key === "m") {
        event.preventDefault();
        setMetronome({ playing: !metronome.playing });
      } else if (key === "j") {
        event.preventDefault();
        toggleDock();
      } else if (event.shiftKey && key === "p") {
        event.preventDefault();
        navigate({ to: "/pratica" });
      }
    };

    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [
    metronome.playing,
    navigate,
    setMetronome,
    setPaletteOpen,
    toggleDock,
    toggleInspector,
    toggleSidebar,
  ]);
}
