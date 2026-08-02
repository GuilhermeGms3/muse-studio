import { createFileRoute } from "@tanstack/react-router";
import { LibraryNodePage } from "@/features/library/LessonWorkspace";

export const Route = createFileRoute("/biblioteca/$nodeId")({
  head: () => ({ meta: [{ title: "Biblioteca - Muse Studio" }] }),
  component: LibraryNodePage,
});
