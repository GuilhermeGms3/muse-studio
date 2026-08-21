import { createFileRoute } from "@tanstack/react-router";
import { SongPage } from "@/features/repertoire/SongWorkspace";

export const Route = createFileRoute("/musicas/$songId")({
  head: () => ({ meta: [{ title: "Música - Muse Studio" }] }),
  component: SongPage,
});
