import { createFileRoute } from "@tanstack/react-router";
import { EditorWorkspace } from "@/features/editor/EditorWorkspace";

export const Route = createFileRoute("/editor")({
  head: () => ({ meta: [{ title: "Editor - Muse Studio" }] }),
  component: EditorWorkspace,
});
