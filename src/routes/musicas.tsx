import { createFileRoute } from "@tanstack/react-router";
import { SongsWorkspace } from "@/features/songs/SongsWorkspace";

export const Route = createFileRoute("/musicas")({
  head: () => ({ meta: [{ title: "Músicas - Muse Studio" }] }),
  component: SongsWorkspace,
});
